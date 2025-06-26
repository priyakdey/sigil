package com.priyakdey.sigil.core.crypto.digest;

import com.priyakdey.sigil.core.ByteBuffer;
import com.priyakdey.sigil.core.ByteOrder;

import java.util.Objects;

/**
 * An implementation of the SHA-256 cryptographic hash function as defined in
 * <a href="https://tools.ietf.org/html/rfc6234">RFC 6234</a> (Secure Hash Standard).
 *
 * <strong>Overview</strong>
 * <p>SHA-256 produces a fixed 256-bit (32-byte) digest for any given input. This class
 * operates as a stateful digest calculator:
 * <ul>
 *   <li>Each call to {@link #computeDigest(byte[])} processes the input and
 *       updates the internal hash state.</li>
 *   <li>To obtain the result and reset the state, call {@link #return_and_reset()}.</li>
 * </ul>
 *
 * <strong>Stateful Design</strong>
 * <p>This class maintains internal state (`hash[]`) across invocations.
 * As a result:
 * <ul>
 *     <li>It is <strong>not thread-safe</strong> and must be used from a single thread at a time,
 *     unless external synchronization is applied.</li>
 *     <li>Each digest call must be followed by {@link #return_and_reset()} before reusing the instance
 *     for another hash computation. Failing to do so will produce incorrect results.</li>
 * </ul>
 *
 * <strong>Example Usage</strong>
 * <pre>{@code
 * SHA256Digest digest = new SHA256Digest();
 * digest.computeDigest(data);
 * byte[] hash = digest.return_and_reset();
 * }</pre>
 *
 * <strong>References</strong>
 * <ul>
 *     <li><a href="https://tools.ietf.org/html/rfc6234">RFC 6234 - Secure Hash Standard (SHS)</a></li>
 *     <li><a href="https://en.wikipedia.org/wiki/SHA-2">Wikipedia - SHA-2</a></li>
 *     <li><a href="https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.180-4.pdf">NIST FIPS 180-4 - Secure Hash Standard</a></li>
 * </ul>
 *
 * <strong>Best Practices</strong>
 * <ul>
 *     <li>For one-shot hash operations, consider creating a new instance per call,
 *     or extending this class with a static convenience method that encapsulates
 *     the lifecycle of hash and reset operations.</li>
 *     <li>For long-running or stream-based hash calculations, this stateful design
 *     allows for incremental processing and finalization when combined with
 *     a future `update()` method (to be implemented).</li>
 * </ul>
 *
 * @author Priyak Dey
 */
public final class SHA256Digest implements Digest {
    /**
     * The length of the final SHA-256 output in bytes (256 bits).
     */
    private static final int DIGEST_SIZE = 32;

    /**
     * The block size used for processing input (64 bytes = 512 bits).
     */
    private static final int BLOCK_SIZE = 64;

    /**
     * Constant array of round constants (K) used in SHA-256 compression.
     * These are the first 32 bits of the fractional parts of the cube roots of the first 64 primes (2..311).
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
     * Initial hash values (H0 through H7).
     * These are the first 32 bits of the fractional parts of the square roots of the first 8 primes (2..19).
     * These values are updated in-place during computation.
     */
    private final int[] hash = {
            0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a,
            0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19
    };

    /**
     * Constructs a new SHA-256 digest instance.
     *
     * <p>This constructor initializes the internal state to the default hash values.
     * It does not take any parameters and does not retain any state between invocations.</p>
     */
    public SHA256Digest() {
    }

    // TODO: support for streaming input (not implemented yet)

    /**
     * Computes the SHA-256 digest of the provided message.
     *
     * <p>The input is first padded according to the SHA-256 specification,
     * then processed in 512-bit blocks. The final hash is returned as a 32-byte array.</p>
     *
     * @param message the input byte array to hash (must not be {@code null})
     * @return the 32-byte SHA-256 hash of the input
     * @throws NullPointerException  if {@code message} is null
     * @throws IllegalStateException if an internal buffer misalignment occurs (should not happen under correct input)
     */
    @Override
    public Digest computeDigest(byte[] message) {
        Objects.requireNonNull(message, "Input message cannot be null");
        int messageLength = message.length;

        // Z = (64 - ((L + 1 + 8) % 64)) % 64
        int x = BLOCK_SIZE - 1;
        int zeroPaddingLength = (BLOCK_SIZE - (messageLength + 1 + 8) & x) & x;
        int bufferLength = messageLength + 1 + zeroPaddingLength + 8;

        if (bufferLength % BLOCK_SIZE != 0) {
            throw new IllegalStateException("Buffer length must be a multiple of the block size");
        }

        ByteBuffer buffer = new ByteBuffer(message.length);

        buffer.append(message);
        buffer.append((byte) 0x80); // Append the bit '1' to the message

        for (int i = 0; i < zeroPaddingLength; i++) {
            buffer.append((byte) 0x00); // Append '0' bits
        }

        // append message length in bits as a 64-bit big-endian long
        buffer.append(message.length * 8L, ByteOrder.BIG_ENDIAN);

        int blockCount = buffer.length() / BLOCK_SIZE;

        for (int blockIndex = 0; blockIndex < blockCount; blockIndex++) {
            processBlock(buffer, blockIndex);
        }

        return this;
    }

    /**
     * Returns the resulting hash of the most recent digest computation
     * and resets the internal state to its initial state.
     *
     * @return the hash result as a byte array
     */
    @Override
    public byte[] return_and_reset() {
        ByteBuffer digest = new ByteBuffer(DIGEST_SIZE);
        for (int h : hash) {
            digest.append(h, ByteOrder.BIG_ENDIAN);
        }

        hash[0] = 0x6a09e667;
        hash[1] = 0xbb67ae85;
        hash[2] = 0x3c6ef372;
        hash[3] = 0xa54ff53a;
        hash[4] = 0x510e527f;
        hash[5] = 0x9b05688c;
        hash[6] = 0x1f83d9ab;
        hash[7] = 0x5be0cd19;

        return digest.bytes();
    }

    private void processBlock(ByteBuffer buffer, int blockIndex) {
        int[] block = new int[BLOCK_SIZE];

        // @formatter:off
        // copy chunk into first 16 words w[0..15] of the message schedule array - big endian
        for (int t = 0; t < 16; t++) {
            int offset = blockIndex * BLOCK_SIZE + t * 4;
            block[t] = buffer.getAsUInt(offset + 0) << (8 * 3) |
                       buffer.getAsUInt(offset + 1) << (8 * 2) |
                       buffer.getAsUInt(offset + 2) << (8 * 1) |
                       buffer.getAsUInt(offset + 3) << (8 * 0);
        }

        // Load remaining 48 words w[16..63] of the message schedule array
        for (int t = 16; t < 64; t++) {
            int s0 = Integer.rotateRight(block[t - 15], 7) ^
                     Integer.rotateRight(block[t - 15], 18) ^
                     (block[t - 15] >>> 3);
            int s1 = Integer.rotateRight(block[t - 2], 17) ^
                     Integer.rotateRight(block[t - 2], 19) ^
                     (block[t - 2] >>> 10);
            block[t] = block[t - 16] + s0 + block[t - 7] + s1;
        }

        int a = hash[0], b = hash[1], c = hash[2], d = hash[3];
        int e = hash[4], f = hash[5], g = hash[6], h = hash[7];

        // Compression function main loop
        for (int t = 0; t < 64; t++) {
            int s1 = Integer.rotateRight(e, 6) ^
                     Integer.rotateRight(e, 11) ^
                     Integer.rotateRight(e, 25);
            int ch = (e & f) ^ (~e & g);
            int temp1 = h + s1 + ch + K[t] + block[t];

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
        // @formatter:on

        hash[0] = hash[0] + a;
        hash[1] = hash[1] + b;
        hash[2] = hash[2] + c;
        hash[3] = hash[3] + d;
        hash[4] = hash[4] + e;
        hash[5] = hash[5] + f;
        hash[6] = hash[6] + g;
        hash[7] = hash[7] + h;
    }

}

