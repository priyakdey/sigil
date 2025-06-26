package com.priyakdey.sigil.core;

import java.util.Arrays;

/**
 * A resizable, byte buffer for efficiently building sequences of bytes.
 * <p>
 * Supports appending single bytes, arrays of bytes, integers, and longs in both
 * big-endian and little-endian formats. Automatically grows its internal storage
 * as needed.
 * </p>
 *
 * <p><b>Note:</b> Future versions may include APIs for random access, mutation, or removal.
 * This class currently focuses on linear appending and streaming use cases.</p>
 *
 * <pre>{@code
 * ByteBuffer buffer = new ByteBuffer(16);
 * buffer.append((byte) 0x42);
 * buffer.append(123456789L, ByteOrder.LITTLE_ENDIAN);
 * String hex = buffer.toString(); // returns hex-encoded representation
 * }</pre>
 *
 * @author Priyak Dey
 * @see ByteOrder
 */
public class ByteBuffer {

    private static final int INT_LENGTH_IN_BYTES = 4;
    private static final int LONG_LENGTH_IN_BYTES = 8;

    private static final int DEFAULT_INIT_CAPACITY = 9; // 512 bytes

    private byte[] buffer;
    private int size;
    private int capacity;


    /**
     * Constructs a new {@code ByteBuffer} with the given initial capacity.
     *
     * @param capacity the initial byte capacity of the buffer
     * @throws IllegalArgumentException if capacity is not positive
     */
    public ByteBuffer(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative");
        }

        this.buffer = new byte[capacity];
        this.size = 0;
        this.capacity = capacity;
    }

    /**
     * Constructs a new {@code ByteBuffer} by repeating the given byte value
     * for a specified number of times.
     *
     * <p>For example, calling {@code repeat((byte) 0xFF, 4)} will produce
     * a {@code ByteBuffer} of length 4, with each byte set to {@code 0xFF}.</p>
     *
     * @param b      the byte value to be repeated
     * @param length the number of times to repeat the byte
     * @return a new {@code ByteBuffer} filled with the specified byte value
     * @throws IllegalArgumentException if {@code length} is negative
     */
    public static ByteBuffer repeat(byte b, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Length cannot be negative");
        }

        ByteBuffer buffer = new ByteBuffer(length);
        for (int i = 0; i < length; i++) {
            buffer.append(b);
        }

        return buffer;
    }

    /**
     * Returns a new {@code ByteBuffer} representing the result of
     * a byte-wise XOR operation between two buffers of the same length.
     *
     * <p>Each resulting byte is calculated as:
     * <pre>{@code
     * result[i] = a[i] ^ b[i];
     * }</pre>
     *
     * @param a the first {@code ByteBuffer}
     * @param b the second {@code ByteBuffer}
     * @return a new {@code ByteBuffer} where each byte is the XOR of the corresponding bytes from {@code a} and {@code b}
     * @throws IllegalArgumentException if {@code a} or {@code b} is {@code null} or if the buffers have different lengths
     */
    public static ByteBuffer xor(ByteBuffer a, ByteBuffer b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Buffers cannot be null");
        }

        if (a.length() != b.length()) {
            throw new IllegalArgumentException("Buffers must have the same size for XOR operation");
        }

        int length = a.size;
        ByteBuffer xor = new ByteBuffer(length);
        for (int i = 0; i < length; i++) {
            xor.append((byte) (a.getAsUInt(i) ^ b.getAsUInt(i)));
        }

        return xor;
    }

    /**
     * Appends a single byte to the end of the buffer.
     *
     * @param b the byte to append
     */
    public void append(byte b) {
        if (size >= capacity) {
            ensureCapacity(size + 1);
        }

        buffer[size++] = b;
    }

    /**
     * Appends the given byte array to the buffer.
     *
     * @param bytes the byte array to append
     * @throws IllegalArgumentException if the byte array is null or empty
     */
    public void append(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("Bytes cannot be null");
        }

        int bytesLength = bytes.length;
        int newSize = size + bytesLength;
        if (newSize > capacity) {
            ensureCapacity(newSize);
        }

        System.arraycopy(bytes, 0, buffer, size, bytesLength);
        size = newSize;
    }

    /**
     * Appends a 64-bit long value to the buffer using the specified byte order.
     *
     * @param value the long value to append
     * @param order the byte order to use (big-endian or little-endian)
     */
    public void append(long value, ByteOrder order) {
        int newSize = size + LONG_LENGTH_IN_BYTES;
        if (newSize > capacity) {
            ensureCapacity(newSize);
        }

        if (order == ByteOrder.BIG_ENDIAN) {
            appendAsBigEndian(value, LONG_LENGTH_IN_BYTES);
        } else {
            appendAsLittleEndian(value, LONG_LENGTH_IN_BYTES);
        }
    }

    /**
     * Appends a 32-bit integer value to the buffer using the specified byte order.
     *
     * @param value the integer value to append
     * @param order the byte order to use (big-endian or little-endian)
     */
    public void append(int value, ByteOrder order) {
        int newLength = size + INT_LENGTH_IN_BYTES;
        if (newLength >= capacity) {
            ensureCapacity(newLength);
        }

        if (order == ByteOrder.BIG_ENDIAN) {
            appendAsBigEndian(value, INT_LENGTH_IN_BYTES);
        } else {
            appendAsLittleEndian(value, INT_LENGTH_IN_BYTES);
        }
    }

    /**
     * Returns the current number of bytes written to the buffer.
     *
     * @return the length of the written buffer content
     */
    public int length() {
        return size;
    }

    /**
     * Returns the unsigned integer value of the byte at the specified offset.
     * <p>
     * This method treats the byte at the given index as an unsigned 8-bit value (0 to 255).
     *
     * @param offset the index from which to read the byte
     * @return the unsigned integer representation of the byte (0–255)
     * @throws IndexOutOfBoundsException if the offset is out of bounds
     */
    public int getAsUInt(int offset) {
        if (offset < 0 || offset >= size) {
            throw new IndexOutOfBoundsException("Offset out of bounds: " + offset);
        }

        return buffer[offset] & 0xFF; // Return as unsigned byte
    }

    /**
     * Returns a copy of the buffer containing only the written bytes.
     *
     * @return a byte array of size {@code length()} representing the current content
     */
    public byte[] bytes() {
        byte[] bytes = new byte[this.size];
        System.arraycopy(this.buffer, 0, bytes, 0, this.size);
        return bytes;
    }

    /**
     * Returns the hexadecimal string representation of the current buffer content.
     * Only the written portion (from index 0 to {@code length()}) is included.
     *
     * @return a hex-encoded string of the current buffer
     */
    @Override
    public String toString() {
        return Hex.from(Arrays.copyOfRange(buffer, 0, size));
    }

    /**
     * Appends the given value in big-endian byte order.
     */
    private void appendAsBigEndian(long value, int numBytes) {
        for (int i = numBytes - 1; i >= 0; i--) {
            buffer[size++] = (byte) ((value >>> (8 * i)) & 0xFF);
        }
    }

    /**
     * Appends the given value in little-endian byte order.
     */
    private void appendAsLittleEndian(long value, int numBytes) {
        for (int i = 0; i < numBytes; i++) {
            buffer[size++] = (byte) ((value >>> (8 * i)) & 0xFF);
        }
    }

    /**
     * Ensures the internal byte array has enough capacity to hold {@code newLength} bytes.
     * If not, the buffer capacity is grown to the next power of two for the new length.
     * If {@code newLength} is zero, it initializes the buffer to a default size if not already set.
     * Defaults to a minimum initial capacity of 512 bits, taking into consideration that
     * the most used SHA-256 digest requires at least 512 bits (64 bytes) for processing.
     *
     * @param newLength the required minimum capacity
     */
    private void ensureCapacity(int newLength) {
        if (newLength == 0) {
            if (capacity == 0) {
                buffer = new byte[DEFAULT_INIT_CAPACITY];
                capacity = DEFAULT_INIT_CAPACITY;
            }
            return;
        }

        capacity = Integer.highestOneBit(newLength) << 1;
        buffer = Arrays.copyOf(buffer, capacity);
    }
}
