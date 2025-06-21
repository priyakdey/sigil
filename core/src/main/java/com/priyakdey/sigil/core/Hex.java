package com.priyakdey.sigil.core;

/**
 * Utility class for hexadecimal encoding and decoding.
 * <p>
 * Provides methods to convert between byte arrays and hexadecimal strings.
 * All methods are stateless and thread-safe.
 * </p>
 *
 * <p>Example usage:
 * <pre>
 *     byte[] data = { (byte)0xDE, (byte)0xAD, (byte)0xBE, (byte)0xEF };
 *     String hex = Hex.toHexString(data); // "DEADBEEF"
 *     byte[] parsed = Hex.fromHexString(hex); // { -34, -83, -66, -17 }
 * </pre>
 * </p>
 *
 * @author Priyak Dey
 */
public class Hex {

    /**
     * Converts a byte array into an uppercase hexadecimal string representation.
     *  Each byte is converted to two hex digits, with leading zeros preserved.
     *  For example, {@code new byte[]{0x0A, 0x1F}} becomes {@code "0A1F"}.
     *
     * @param bytes the byte array to convert
     * @return a hex string (e.g., {@code "DEADBEEF"})
     * @throws IllegalArgumentException if {@code bytes} is {@code null}
     */
    public static String toHexString(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("Source array cannot be null");
        }

        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02X", b & 0xFF));
        }
        return sb.toString();
    }

    /**
     * Converts a hexadecimal string to a byte array.
     *
     * @param hexString the hex string to convert; must be non-null and have even length
     * @return a byte array representing the parsed binary data
     * @throws IllegalArgumentException if {@code hexString} is {@code null},
     *                                  has an odd length, or contains invalid characters
     */
    public static byte[] fromHexString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string must not be null and must have an even length");
        }

        int length = hexString.length() / 2;
        byte[] bytes = new byte[length];

        for (int i = 0; i < length; i++) {
            int highNibble = Character.digit(hexString.charAt(i * 2), 16);
            int lowNibble = Character.digit(hexString.charAt(i * 2 + 1), 16);
            if (highNibble < 0 || lowNibble < 0) {
                throw new IllegalArgumentException("Invalid hex character in string: " + hexString);
            }
            bytes[i] = (byte) ((highNibble << 4) | lowNibble);
        }
        return bytes;
    }

    /**
     * Returns a hexadecimal string representation of the byte array prefixed with {@code 0x}.
     * Delegates to {@link #toHexString(byte[])}.
     *
     * @param bytes the byte array to convert
     * @return a hex string in the format {@code 0x...}
     *
     * @throws IllegalArgumentException if {@code bytes} is {@code null}
     */
    public static String toString(byte[] bytes) {
        return "0x" + toHexString(bytes);
    }

}
