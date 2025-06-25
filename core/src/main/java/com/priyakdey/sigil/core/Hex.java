package com.priyakdey.sigil.core;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Utility class for encoding and decoding data in hexadecimal string format.
 *
 * <p>
 * This class provides static methods to:
 * <ul>
 *     <li>Convert a byte array to an uppercase hexadecimal string prefixed with {@code "0x"}.</li>
 *     <li>Convert a string to its UTF-8 encoded byte representation and then to hexadecimal.</li>
 *     <li>Convert a valid hexadecimal string (with or without {@code "0x"}) back into a byte array.</li>
 * </ul>
 *
 * <p>
 * The hex strings produced use uppercase letters (A–F) and two characters per byte. The input hex
 * strings are case-insensitive and must be of even length (excluding the optional {@code "0x"} prefix).
 * </p>
 *
 * <p>This class is immutable and thread-safe.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * byte[] data = { (byte) 0xAB, (byte) 0xCD };
 * String hex = Hex.from(data); // "0xABCD"
 *
 * String encoded = Hex.from("Hi"); // "0x4869"
 *
 * byte[] decoded = Hex.to("0xABCD"); // { (byte) 0xAB, (byte) 0xCD }
 * }</pre>
 *
 * @author Priyak Dey
 */
public final class Hex {

    // TODO: replace with a more efficient lookup array for performance
    private static final Map<Character, Integer> HEX_CHAR_TO_INT = Map.ofEntries(
            Map.entry('0', 0), Map.entry('1', 1),
            Map.entry('2', 2), Map.entry('3', 3),
            Map.entry('4', 4), Map.entry('5', 5),
            Map.entry('6', 6), Map.entry('7', 7),
            Map.entry('8', 8), Map.entry('9', 9),
            Map.entry('A', 10), Map.entry('B', 11),
            Map.entry('C', 12), Map.entry('D', 13),
            Map.entry('E', 14), Map.entry('F', 15),
            Map.entry('a', 10), Map.entry('b', 11),
            Map.entry('c', 12), Map.entry('d', 13),
            Map.entry('e', 14), Map.entry('f', 15)
    );

    /**
     * Private constructor to prevent instantiation.
     */
    private Hex() {
    }

    /**
     * Converts a byte array into its uppercase hexadecimal string representation,
     * prefixed with {@code "0x"}.
     *
     * @param bytes the byte array to convert
     * @return a hex string such as {@code "0xDEADBEEF"}
     */
    public static String from(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2 + 2);
        sb.append("0x");

        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }

        return sb.toString();
    }

    /**
     * Converts a UTF-8 string into its byte representation and returns the
     * hexadecimal string of those bytes, prefixed with {@code "0x"}.
     *
     * @param string the input string
     * @return the hexadecimal representation of the UTF-8 encoded string
     */
    public static String from(String string) {
        return from(string.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Converts a hexadecimal string into its byte array representation.
     * Accepts optional {@code "0x"} prefix. The string must be of even length
     * (excluding prefix) and consist only of valid hexadecimal digits.
     *
     * @param hex the input hex string (e.g., {@code "0xDEADBEEF"} or {@code "deadbeef"})
     * @return the decoded byte array
     * @throws IllegalArgumentException if the input is null, has odd length, or contains invalid characters
     */
    public static byte[] to(String hex) {
        if (hex == null || hex.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex string: " + hex);
        }

        int length = hex.length();
        int bytesLength = length / 2;
        int i = 0;

        // Handle optional "0x" prefix
        if (hex.startsWith("0x")) {
            bytesLength -= 1;
            i = 2;
        }

        byte[] bytes = new byte[bytesLength];
        int cursor = 0;

        while (i < length) {
            char highNibble = hex.charAt(i);
            char lowerNibble = hex.charAt(i + 1);

            if (!HEX_CHAR_TO_INT.containsKey(highNibble) || !HEX_CHAR_TO_INT.containsKey(lowerNibble)) {
                throw new IllegalArgumentException("Invalid hex string: " + hex);
            }

            int high = HEX_CHAR_TO_INT.get(highNibble) << 4;
            int lower = HEX_CHAR_TO_INT.get(lowerNibble);
            bytes[cursor++] = (byte) (high | lower);
            i += 2;
        }

        return bytes;
    }
}
