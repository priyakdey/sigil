package com.priyakdey.sigil.core.crypto.digest;

import com.priyakdey.sigil.core.Bytes;

/**
 * Implementation of the SHA-256 cryptographic hash function.
 *
 * <p>
 * This class follows the Secure Hash Algorithm 256-bit variant as specified in the
 * <a href="https://csrc.nist.gov/publications/detail/fips/180/4/final">FIPS PUB 180-4</a>
 * and implemented in accordance with the structure outlined in
 * <a href="https://en.wikipedia.org/wiki/SHA-2">Wikipedia: SHA-2</a>.
 * </p>
 *
 * <p>
 * The digest is computed using a block size of 512 bits (64 bytes), where the message
 * is preprocessed with padding and its length is appended before chunk-wise processing.
 * Each chunk updates the internal hash state using bitwise operations, rotations, and constants.
 * </p>
 *
 * <p>
 * Constants {@code K} are derived from the fractional parts of the cube roots of the first 64 prime numbers,
 * as defined in the SHA-2 specification.
 * </p>
 *
 * @author Priyak Dey
 * @see Digest
 * @see <a href="https://www.rfc-editor.org/rfc/rfc6234.html">RFC 6234: US Secure Hash Algorithms (SHA and SHA-based HMAC and HKDF)</a>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc4634.html">RFC 4634: SHA and HMAC-SHA Implementations</a>
 * @see <a href="https://csrc.nist.gov/publications/detail/fips/180/4/final">FIPS PUB 180-4: Secure Hash Standard (SHS)</a>
 * @see <a href="https://en.wikipedia.org/wiki/SHA-2">Wikipedia: SHA-2</a>
 */
public final class SHA256Digest implements Digest {

    /**
     * SHA-256 round constants derived from the cube roots of the first 64 primes.
     */
    private static final int[] K = {
            0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
            0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
            0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
            0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
            0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
            0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
            0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
            0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };

    /**
     * Default constructor for SHA256Digest.
     */
    public SHA256Digest() {
    }

    /**
     * Computes the SHA-256 digest of the provided input.
     *
     * @param message the input byte array
     * @return a 32-byte hash digest
     * @throws IllegalArgumentException if the input is {@code null}
     */
    @Override
    public byte[] digest(byte[] message) {
        byte[] bytes = padMessage(message);

        // Reference: https://en.wikipedia.org/wiki/SHA-2

        int[] hash = {
                0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a,
                0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19
        };

        // Divide the buffer into chunks of 512 bits = 64 bytes
        int blocksCount = bytes.length / 64;

        for (int block = 0; block < blocksCount; block++) {
            int[] word = new int[64];

            // Load the first 16 words word[0..15] from the block (big-endian)
            // NOTE: Formatting is for looking like a pro 😜, compiler should fold these computations.
            for (int t = 0; t < 16; t++) {
                int i = block * 64 + t * 4;
                // @formatter:off
                word[t] = ((bytes[i + 0] & 0xFF) << (3 * 8)) |
                          ((bytes[i + 1] & 0xFF) << (2 * 8)) |
                          ((bytes[i + 2] & 0xFF) << (1 * 8)) |
                          ((bytes[i + 3] & 0xFF) << (0 * 8));
                // @formatter:on
            }

            // Load next 48 words into the scene - 16..63 and do rotations.
            for (int t = 16; t < 64; t++) {
                int s0 = Integer.rotateRight(word[t - 15], 7) ^
                        Integer.rotateRight(word[t - 15], 18) ^
                        (word[t - 15] >>> 3);
                int s1 = Integer.rotateRight(word[t - 2], 17) ^
                        Integer.rotateRight(word[t - 2], 19) ^
                        (word[t - 2] >>> 10);
                word[t] = word[t - 16] + s0 + word[t - 7] + s1;
            }

            int a = hash[0], b = hash[1], c = hash[2], d = hash[3];
            int e = hash[4], f = hash[5], g = hash[6], h = hash[7];

            // Compression
            for (int t = 0; t < 64; t++) {
                // TODO: apparently these can be optimized too, for later.
                int s1 = Integer.rotateRight(e, 6) ^
                        Integer.rotateRight(e, 11) ^
                        Integer.rotateRight(e, 25);
                int ch = (e & f) ^ ((~e) & g);
                int temp1 = h + s1 + ch + K[t] + word[t];

                int s0 = Integer.rotateRight(a, 2) ^
                        Integer.rotateRight(a, 13) ^
                        Integer.rotateRight(a, 22);
                int maj = (a & b) ^ (a & c) ^ (b & c);
                int temp2 = s0 + maj;

                h = g;
                g = f;
                f = e;
                e = d + temp1;
                d = c;
                c = b;
                b = a;
                a = temp1 + temp2;
            }

            // Add the compressed chunk to the current hash value
            hash[0] += a;
            hash[1] += b;
            hash[2] += c;
            hash[3] += d;
            hash[4] += e;
            hash[5] += f;
            hash[6] += g;
            hash[7] += h;
        }

        return Bytes.intsToBytesBigEndian(hash);
    }

    /**
     * Pads the input message according to SHA-256 specifications.
     * <p>
     * The padding includes a '1' bit, followed by zero bits, and ending with the
     * original message length encoded as a 64-bit big-endian integer.
     * </p>
     *
     * @param message the input message
     * @return the padded message ready for processing
     */
    private byte[] padMessage(byte[] message) {
        int messageLength = message.length;
        long messageLengthBits = ((long) messageLength) * 8;

        // pad 1 bit to the right by appending byte with value 0x80
        byte[] bytes = Bytes.append(message, (byte) 0x80);

        int currentBitLen = bytes.length * 8;
        int mod512 = currentBitLen % 512;
        int bitsTo448 = (448 - mod512 + 512) % 512;
        int zeroBytesToAdd = bitsTo448 / 8;

        // pad with zero bytes to make the length congruent to 448 mod 512
        byte[] zeroPad = Bytes.repeat((byte) 0x0, zeroBytesToAdd);
        bytes = Bytes.append(bytes, zeroPad);


        // append the length of the original message in bits as a 64-bit big-endian integer
        return Bytes.append(bytes, Bytes.toBigEndianBytes(messageLengthBits, 8));
    }
}
