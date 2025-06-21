package com.priyakdey.sigil.core;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Utility class for various byte array operations.
 * Provides low-level manipulation functions used in cryptographic and data transformation contexts.
 * All methods are null-safe unless otherwise documented.
 *
 * @author Priyak Dey
 */
public class Bytes {

    private Bytes() {
    }

    /**
     * Concatenates multiple byte arrays into a single array.
     *
     * @param arrays the byte arrays to concatenate
     * @return a new byte array containing all input arrays in order
     * @throws IllegalArgumentException if any array is {@code null}
     */
    public static byte[] concat(byte[]... arrays) {
        int length = 0;
        for (byte[] array : arrays) {
            if (array == null) {
                throw new IllegalArgumentException("Array cannot be null");
            }
            length += array.length;
        }

        byte[] buffer = new byte[length];
        int cursor = 0;

        for (byte[] array : arrays) {
            System.arraycopy(array, 0, buffer, cursor, array.length);
            cursor += array.length;
        }

        return buffer;
    }

    /**
     * Compares two byte arrays in constant time to prevent timing attacks.
     *
     * @param a first byte array
     * @param b second byte array
     * @return true if both arrays are equal in length and contents
     */
    public static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a == b) return true; // handles null case and same reference
        if (a == null || b == null || a.length != b.length) return false;

        int match = 0;
        for (int i = 0; i < a.length; i++) {
            match |= (a[i] ^ b[i]) & 0xFF;
        }

        return match == 0;
    }

    /**
     * Performs a byte-wise XOR of two arrays of equal length.
     *
     * @param a first byte array
     * @param b second byte array
     * @return a new byte array with XOR of a[i] and b[i]
     * @throws IllegalArgumentException if arrays are of unequal length
     */
    public static byte[] xor(byte[] a, byte[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Arrays must be of the same length");
        }

        byte[] buffer = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            buffer[i] = (byte) (a[i] ^ b[i]);
        }
        return buffer;
    }

    /**
     * Converts a byte array to a UTF-8 string.
     *
     * @param bytes byte array to convert
     * @return UTF-8 encoded string
     * @throws IllegalArgumentException if {@code bytes} is {@code null}
     */
    public static String toUTF8String(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("Source array cannot be null");
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Converts a UTF-8 string to a byte array.
     *
     * @param str string to convert
     * @return byte array of UTF-8 encoded characters
     * @throws IllegalArgumentException if {@code str} is {@code null}
     */
    public static byte[] fromUTF8String(String str) {
        if (str == null) {
            throw new IllegalArgumentException("Source string cannot be null");
        }

        return str.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Extracts a subarray (slice) from the input byte array.
     *
     * @param src  the source array
     * @param from starting index (inclusive)
     * @param end  ending index (exclusive)
     * @return new byte array with elements from `from` (inclusive) to `end` (exclusive)
     * @throws IllegalArgumentException if {@code src} is {@code null}, or indices are out of bounds or invalid
     */
    public static byte[] slice(byte[] src, int from, int end) {
        if (src == null || from < 0 || end > src.length || from >= end) {
            throw new IllegalArgumentException("Invalid slice range");
        }

        byte[] buffer = new byte[end - from];
        System.arraycopy(src, from, buffer, 0, end - from);
        return buffer;
    }

    /**
     * Returns a full copy of the given byte array.
     *
     * @param src the source array
     * @return a new byte array copy
     * @throws IllegalArgumentException if {@code src} is {@code null}
     */
    public static byte[] copy(byte[] src) {
        if (src == null) {
            throw new IllegalArgumentException("Source array cannot be null");
        }

        byte[] buffer = new byte[src.length];
        System.arraycopy(src, 0, buffer, 0, src.length);
        return buffer;
    }

    /**
     * Returns a copy of the first {@code length} bytes of the source array.
     *
     * @param src    the source array
     * @param length number of bytes to copy
     * @return partial copy of src
     * @throws IllegalArgumentException if {@code src} is {@code null}, or {@code length} is invalid
     */
    public static byte[] copy(byte[] src, int length) {
        if (src == null || length < 0 || length > src.length) {
            throw new IllegalArgumentException("Invalid slice range");
        }

        byte[] buffer = new byte[length];
        System.arraycopy(src, 0, buffer, 0, length);
        return buffer;
    }

    /**
     * Copies the full content of src to dest. Both arrays must be of equal length.
     *
     * @param src  source array
     * @param dest destination array
     * @throws IllegalArgumentException if {@code src} or {@code dest} is {@code null}, or their lengths differ
     */
    public static void copy(byte[] src, byte[] dest) {
        if (src == null || dest == null || src.length != dest.length) {
            throw new IllegalArgumentException("Source and destination arrays must be of the same length");
        }

        System.arraycopy(src, 0, dest, 0, src.length);
    }

    /**
     * Generates a random byte array using the provided {@link SecureRandom}.
     *
     * @param length number of random bytes
     * @param rng    SecureRandom instance
     * @return random byte array of given length
     * @throws IllegalArgumentException if {@code length} is non-positive or is {@code rng} is {@code null}
     */
    public static byte[] random(int length, SecureRandom rng) {
        if (length <= 0 || rng == null) {
            throw new IllegalArgumentException("Length must be greater than zero");
        }

        byte[] buffer = new byte[length];
        rng.nextBytes(buffer);
        return buffer;
    }

    /**
     * Creates a byte array filled with the same byte repeated.
     *
     * @param value  byte to repeat
     * @param length length of the resulting array
     * @return repeated byte array
     * @throws IllegalArgumentException if {@code length} is non-positive
     */
    public static byte[] repeat(byte value, int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than zero");
        }

        byte[] buffer = new byte[length];
        Arrays.fill(buffer, value);
        return buffer;
    }

    /**
     * Reverses the order of bytes in the given array.
     *
     * @param src byte array to reverse
     * @return new array with reversed byte order
     * @throws IllegalArgumentException if {@code src} is {@code null}
     */
    public static byte[] reverse(byte[] src) {
        if (src == null) {
            throw new IllegalArgumentException("Source array cannot be null");
        }

        byte[] buffer = new byte[src.length];
        for (int i = 0; i < src.length; i++) {
            buffer[i] = src[src.length - 1 - i];
        }

        return buffer;
    }

    /**
     * Pads the byte array from the left to match the specified length.
     * If already at or above length, truncates to {@code length}.
     *
     * @param src    source array
     * @param length desired total length
     * @param pad    byte to pad with
     * @return padded or truncated array
     * @throws IllegalArgumentException if {@code src} is {@code null}, or {@code length} is negative
     */
    public static byte[] leftPad(byte[] src, int length, byte pad) {
        if (src == null || length < 0) {
            throw new IllegalArgumentException("Source array cannot be null and length must be non-negative");
        }

        if (src.length >= length) {
            return copy(src, length);
        }

        byte[] buffer = new byte[length];
        int padLength = length - src.length;
        Arrays.fill(buffer, 0, padLength, pad);
        System.arraycopy(src, 0, buffer, padLength, src.length);

        return buffer;
    }

    /**
     * Pads the byte array from the right to match the specified length.
     * If already at or above length, truncates to {@code length}.
     *
     * @param src    source array
     * @param length desired total length
     * @param pad    byte to pad with
     * @return padded or truncated array
     * @throws IllegalArgumentException if {@code src} is {@code null}, or {@code length} is negative
     */
    public static byte[] rightPad(byte[] src, int length, byte pad) {
        if (src == null || length < 0) {
            throw new IllegalArgumentException("Source array cannot be null and length must be non-negative");
        }

        if (src.length >= length) {
            return copy(src, length);
        }

        byte[] buffer = new byte[length];
        System.arraycopy(src, 0, buffer, 0, src.length);
        Arrays.fill(buffer, src.length, length, pad);

        return buffer;
    }

    /**
     * Appends one byte to the input byte array.
     *
     * @param src source array
     * @param b   byte to append
     * @return new byte array containing src followed by byte
     * @throws IllegalArgumentException if {@code src} or {@code append} is {@code null}
     */
    public static byte[] append(byte[] src, byte b) {
        if (src == null) {
            throw new IllegalArgumentException("Source and append arrays cannot be null");
        }

        byte[] buffer = new byte[src.length + 1];
        System.arraycopy(src, 0, buffer, 0, src.length);
        buffer[buffer.length - 1] = b;

        return buffer;
    }

    /**
     * Appends one byte array to another.
     *
     * @param src    source array
     * @param append array to append
     * @return new byte array containing src followed by append
     * @throws IllegalArgumentException if {@code src} or {@code append} is {@code null}
     */
    public static byte[] append(byte[] src, byte[] append) {
        if (src == null || append == null) {
            throw new IllegalArgumentException("Source and append arrays cannot be null");
        }

        byte[] buffer = new byte[src.length + append.length];
        System.arraycopy(src, 0, buffer, 0, src.length);
        System.arraycopy(append, 0, buffer, src.length, append.length);

        return buffer;
    }

    /**
     * Converts an array of integers to a byte array in big-endian order.
     * Each integer is split into 4 bytes, starting with the most significant byte.
     *
     * @param values the array of integers to convert
     * @return a byte array containing the big-endian representation of the input integers
     * @throws IllegalArgumentException if the input array is null
     */
    public static byte[] intsToBytesBigEndian(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("Input array cannot be null");
        }

        byte[] result = new byte[values.length * 4];
        for (int i = 0; i < values.length; i++) {
            int val = values[i];
            result[i * 4] = (byte) ((val >>> 24) & 0xFF);
            result[i * 4 + 1] = (byte) ((val >>> 16) & 0xFF);
            result[i * 4 + 2] = (byte) ((val >>> 8) & 0xFF);
            result[i * 4 + 3] = (byte) (val & 0xFF);
        }
        return result;
    }


    /**
     * Converts a long value to a big-endian byte array of the given size.
     *
     * @param value the long value to convert
     * @param size  number of bytes to use (should be {@code <= 8})
     * @return big-endian byte array
     * @throws IllegalArgumentException if {@code size} is not between 1 and 8
     */
    public static byte[] toBigEndianBytes(long value, int size) {
        if (size < 1 || size > 8) {
            throw new IllegalArgumentException("Size must be between 1 and 8");
        }

        byte[] buffer = new byte[size];
        for (int i = 0; i < size; i++) {
            buffer[size - i - 1] = (byte) ((value >> ((i) * 8)) & 0xFF);
        }

        return buffer;
    }
}
