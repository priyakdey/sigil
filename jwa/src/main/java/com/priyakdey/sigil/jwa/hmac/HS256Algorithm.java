package com.priyakdey.sigil.jwa.hmac;

import com.priyakdey.sigil.core.ByteBuffer;
import com.priyakdey.sigil.core.crypto.digest.Digest;
import com.priyakdey.sigil.core.crypto.digest.SHA256Digest;

/**
 * An implementation of the HMAC (Hash-Based Message Authentication Code) algorithm using
 * the SHA-256 hash function ("HS256").
 *
 * <h2>Overview</h2>
 * <p>HMAC is a mechanism for creating a message authentication code (MAC) by combining a secret key
 * and a hash function. The HS256 variant uses SHA-256 as its underlying hash. The result is a
 * fixed-length (32-byte) signature that can be used to verify both the integrity and authenticity
 * of a message.</p>
 *
 * <h2>Algorithm Name</h2>
 * <p>Returns {@code "HS256"} as defined by the JSON Web Algorithms (JWA) standard for HMAC
 * with SHA-256.</p>
 *
 * <h2>Key Normalization</h2>
 * Accepts secret keys of any length:
 * <ul>
 *   <li>Keys longer than the block size (64 bytes) are first hashed with SHA-256, yielding a
 *       32-byte key that is then used as the actual HMAC key.</li>
 *   <li>Keys shorter than the block size are zero-padded to 64 bytes.</li>
 * </ul>
 *
 *
 * <h2>Thread-safety</h2>
 * <p>Although the hash function used by this class is stateful across invocations,
 * instances of this class are designed for one-shot use. To compute HMAC in a
 * multithreaded environment, create separate instances for each thread or request,
 * or ensure external synchronization.</p>
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * HMAC hmac = new HS256Algorithm();
 * byte[] signature = hmac.sign(data, secretKey);
 * System.out.println(Hex.from(signature));
 * }</pre>
 *
 * <h2>References</h2>
 * <ul>
 *     <li><a href="https://www.rfc-editor.org/rfc2104">RFC 2104 - HMAC: Keyed-Hashing for Message Authentication</a></li>
 *     <li><a href="https://www.rfc-editor.org/rfc7515">RFC 7515 - JSON Web Signature (JWS)</a></li>
 * </ul>
 *
 * @author Priyak Dey
 * @see HMAC
 * @see SHA256Digest
 */
public final class HS256Algorithm implements HMAC {

    private static final String ALG_NAME = "HS256";

    private static final int BLOCK_SIZE = 64;

    private final Digest hashFn;

    /**
     * Constructs a new HMAC-SHA256 instance.
     *
     * <p>Initializes the internal SHA-256 digest implementation. Each instance can be used
     * for one or more sign operations, but is not guaranteed to be thread-safe. To use in
     * a multi-threaded context, create a separate instance per thread or request.
     */
    public HS256Algorithm() {
        this.hashFn = new SHA256Digest();
    }

    /**
     * Returns the algorithm name ("HS256") for this HMAC implementation.
     *
     * @return the name of the HMAC algorithm
     */
    @Override
    public String algorithmName() {
        return ALG_NAME;
    }

    /**
     * Computes the HMAC signature for the given data using the provided secret key.
     *
     * <p>This method applies the HMAC construction:
     * <ol>
     *     <li>Normalizes the key (hashing if longer than block size, padding if shorter).</li>
     *     <li>Combines the normalized key with the inner padding (ipad), and appends the data.</li>
     *     <li>Hashes the result, yielding the inner hash result (s0).</li>
     *     <li>Combines the normalized key with the outer padding (opad), appends the inner hash, and hashes again.</li>
     * </ol>
     *
     * @param data the data to be signed (must not be {@code null})
     * @param key  the secret key for HMAC (must not be {@code null})
     * @return the HMAC signature (32 bytes for HS256).
     * @throws NullPointerException if {@code data} or {@code key} is {@code null}
     */
    @Override
    public byte[] sign(byte[] data, byte[] key) {
        ByteBuffer normalizedKey = computeBlockSizedKey(key);

        ByteBuffer iPad = ByteBuffer.repeat((byte) 0x36, BLOCK_SIZE);
        ByteBuffer s0 = ByteBuffer.xor(normalizedKey, iPad);        // Inner padded key
        s0.append(data);
        byte[] s0Digest = hashFn.computeDigest(s0.bytes()).return_and_reset();

        ByteBuffer oPad = ByteBuffer.repeat((byte) 0x5c, BLOCK_SIZE);
        ByteBuffer s1 = ByteBuffer.xor(normalizedKey, oPad);        // Outer padded key
        s1.append(s0Digest);

        return hashFn.computeDigest(s1.bytes()).return_and_reset();
    }

    private ByteBuffer computeBlockSizedKey(byte[] key) {
        int diff = key.length - BLOCK_SIZE;
        int sign = (diff >> 31) | (-diff >>> 31);

        ByteBuffer buffer = new ByteBuffer(BLOCK_SIZE);

        switch (sign) {
            case -1, 0:
                buffer.append(key);
                break;
            default:
                byte[] digest = this.hashFn.computeDigest(key).return_and_reset();
                buffer.append(digest);
        }

        while (buffer.length() < BLOCK_SIZE) {
            buffer.append((byte) 0x0);
        }

        return buffer;
    }
}
