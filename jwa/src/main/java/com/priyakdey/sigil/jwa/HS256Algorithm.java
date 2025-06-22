package com.priyakdey.sigil.jwa;

import com.priyakdey.sigil.core.Bytes;
import com.priyakdey.sigil.core.crypto.digest.Digest;

/**
 * Implementation of the HS256 algorithm, which applies HMAC with SHA-256.
 *
 * <p>
 * This class follows the algorithm defined in
 * <a href="https://www.rfc-editor.org/rfc/rfc7518.html#section-3.2">RFC 7518 Section 3.2</a>
 * for "HS256" — HMAC using SHA-256. The internal SHA-256 hash function is provided
 * by {@link Digest}, and the construction follows the HMAC pattern specified in
 * <a href="https://www.rfc-editor.org/rfc/rfc2104.html">RFC 2104</a>.
 * </p>
 *
 * <p>
 * The block size for SHA-256 is 512 bits (64 bytes). If the key is longer than the block size,
 * it is hashed and then right-padded. If it's shorter, it's right-padded directly.
 * </p>
 *
 * <p>
 * The inner and outer pads (IPAD and OPAD) are standard constants used for HMAC:
 * 0x36 and 0x5C respectively, repeated to match the block size.
 * </p>
 *
 * @author Priyak Dey
 * @see HMAC
 * @see <a href="https://www.rfc-editor.org/rfc/rfc2104.html">RFC 2104: HMAC: Keyed-Hashing for Message Authentication</a>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc7518.html#section-3.2">RFC 7518: JSON Web Algorithms - Section 3.2 (HMAC with SHA-2)</a>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc7515.html">RFC 7515: JSON Web Signature (JWS)</a>
 */
public final class HS256Algorithm implements HMAC {

    /**
     * The hash function used for HMAC: SHA-256.
     */
    private static final Digest HASH_FN = Digest.SHA256;

    /**
     * SHA-256 block size in bytes (512 bits).
     */
    private static final int BLOCK_SIZE = 64;

    /**
     * Inner pad constant (0x36) repeated to block size.
     */
    private static final byte[] IPAD = Bytes.repeat((byte) 0x36, BLOCK_SIZE);

    /**
     * Outer pad constant (0x5C) repeated to block size.
     */
    private static final byte[] OPAD = Bytes.repeat((byte) 0x5C, BLOCK_SIZE);

    /**
     * Default constructor for HS256Algorithm.
     */
    public HS256Algorithm() {
    }

    /**
     * Returns the JOSE-standardized name of this algorithm.
     *
     * @return "HS256"
     */
    @Override
    public String getName() {
        return "HS256";
    }

    /**
     * Signs the given message using the HMAC-SHA256 algorithm with the provided key.
     * <p>
     * The process involves:
     * <ol>
     *   <li>Normalizing the key to match the block size</li>
     *   <li>Computing the inner hash: <code>H((K ^ ipad) || message)</code></li>
     *   <li>Computing the final HMAC: <code>H((K ^ opad) || inner_hash)</code></li>
     * </ol>
     *
     * @param message the message to authenticate
     * @param key     the secret key
     * @return the HMAC-SHA256 signature
     */
    @Override
    public byte[] sign(byte[] message, byte[] key) {
        byte[] normalizedKey = normalizeKey(key);

        byte[] s0 = Bytes.xor(normalizedKey, IPAD);
        s0 = Bytes.append(s0, message);
        byte[] s0Hash = HASH_FN.digest(s0);

        byte[] s1 = Bytes.xor(normalizedKey, OPAD);
        s1 = Bytes.append(s1, s0Hash);

        return HASH_FN.digest(s1);
    }

    /**
     * Normalizes the HMAC key to the block size of the hash function.
     * <p>
     * If the key is:
     * <ul>
     *     <li>shorter than the block size — it is padded with zeroes</li>
     *     <li>equal to the block size — it is used directly</li>
     *     <li>longer than the block size — it is hashed and then padded</li>
     * </ul>
     * </p>
     *
     * @param key the input HMAC key
     * @return a normalized key of size equal to the block size
     */
    private byte[] normalizeKey(byte[] key) {
        int diff = key.length - BLOCK_SIZE;
        int sign = (diff >> 31) | ((-diff) >>> 31); // -1, 0, or 1

        // @formatter:off
        return switch (sign) {
            case -1 -> Bytes.rightPad(key, BLOCK_SIZE, (byte) 0x00);
            case 0  -> key;
            case 1  -> Bytes.rightPad(HASH_FN.digest(key), BLOCK_SIZE, (byte) 0x00);
            default -> throw new IllegalStateException("WTF");    // just to satisfy the switch exhaustiveness, will never happen
        };
        // @formatter:on
    }
}
