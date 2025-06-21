package com.priyakdey.sigil.core;

/**
 * Utility class for encoding binary data into Base64URL format.
 * <p>
 * This implementation follows the Base64URL encoding rules as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc4648#section-5">RFC 4648 §5</a>,
 * commonly used in JWT (JSON Web Tokens) and JOSE standards.
 * Key properties:
 * <ul>
 *     <li>Uses URL-safe character set: {@code A-Z, a-z, 0-9, '-', '_'}</li>
 *     <li>Does NOT include {@code =} padding characters</li>
 *     <li>Works directly on byte arrays (UTF-8 safe)</li>
 * </ul>
 * <p>
 * Example:
 * <pre>{@code
 *     byte[] data = "Hello".getBytes(StandardCharsets.UTF_8);
 *     String encoded = Base64Url.encode(data);  // Output: SGVsbG8
 * }</pre>
 *
 * @author Priyak Dey
 */
public class Base64Url {

    private static final byte[] BASE64_TABLE = new byte[]{
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
    };


    private Base64Url() {
    }

    /**
     * Encodes a byte array into an unpadded Base64URL string.
     *
     * @param input the binary data to encode
     * @return a Base64URL-encoded string (unpadded)
     */
    public static String encode(byte[] input) {
        int cursor = 0;
        int length = input.length;

        int outputLength = (length * 8 + 5) / 6;
        byte[] buffer = new byte[outputLength];
        int outputCursor = 0;

        while (cursor + 2 < length) {
            int b0 = input[cursor] & 0xFF;
            int b1 = input[cursor + 1] & 0xFF;
            int b2 = input[cursor + 2] & 0xFF;

            int first = b0 >> 2;
            int second = ((b0 & 0x3) << 4) | ((b1 & 0xF0) >> 4);
            int third = ((b1 & 0xF) << 2) | ((b2 & 0xC0) >> 6);
            int fourth = b2 & 0x3F;

            buffer[outputCursor++] = BASE64_TABLE[first];
            buffer[outputCursor++] = BASE64_TABLE[second];
            buffer[outputCursor++] = BASE64_TABLE[third];
            buffer[outputCursor++] = BASE64_TABLE[fourth];

            cursor += 3;
        }

        if (length - cursor == 2) {
            int b0 = input[cursor] & 0xFF;
            int b1 = input[cursor + 1] & 0xFF;

            int first = b0 >> 2;
            int second = ((b0 & 0x3) << 4) | (b1 >> 4);
            int third = (b1 & 0xF) << 2;

            buffer[outputCursor++] = BASE64_TABLE[first];
            buffer[outputCursor++] = BASE64_TABLE[second];
            buffer[outputCursor] = BASE64_TABLE[third];
        } else if (length - cursor == 1) {
            int b0 = input[cursor] & 0xFF;

            int first = b0 >> 2;
            int second = (b0 & 0x3) << 4;

            buffer[outputCursor++] = BASE64_TABLE[first];
            buffer[outputCursor] = BASE64_TABLE[second];
        }

        return Bytes.toUTF8String(buffer);
    }

}
