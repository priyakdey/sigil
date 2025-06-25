package com.priyakdey.sigil.core;

/**
 * Specifies the byte ordering (endianness) used when encoding multi-byte
 * values like integers and longs into a {@link ByteBuffer}.
 *
 * <p>
 * Byte order determines how the bytes of a multi-byte value are laid out in memory:
 * </p>
 *
 * <ul>
 *   <li>{@code BIG_ENDIAN} – The most significant byte (MSB) comes first.</li>
 *   <li>{@code LITTLE_ENDIAN} – The least significant byte (LSB) comes first.</li>
 * </ul>
 *
 * <p>
 * This enum is inspired by {@link java.nio.ByteOrder} and is used for low-level encoding
 * control in the {@code sigil.core} package.
 * </p>
 *
 * <pre>{@code
 * ByteBuffer buffer = new ByteBuffer(16);
 * buffer.append(0x12345678, ByteOrder.BIG_ENDIAN); // stored as 12 34 56 78
 * buffer.append(0x12345678, ByteOrder.LITTLE_ENDIAN); // stored as 78 56 34 12
 * }</pre>
 *
 * @author Priyak Dey
 */
public enum ByteOrder {
    /**
     * Stores the most significant byte first (network order).
     */
    BIG_ENDIAN,

    /**
     * Stores the least significant byte first.
     */
    LITTLE_ENDIAN
}
