package com.priyakdey.sigil.jwa;


/**
 * Represents a cryptographic HMAC (Hash-based Message Authentication Code) algorithm.
 *
 * <p>
 * HMAC is a mechanism for message authentication using cryptographic hash functions and a secret key,
 * as specified in <a href="https://www.rfc-editor.org/rfc/rfc2104.html">RFC 2104</a>.
 * This interface defines the contract for implementing specific HMAC-based algorithms such as HS256.
 * </p>
 *
 * @author Priyak Dey
 * @see <a href="https://www.rfc-editor.org/rfc/rfc2104.html">RFC 2104: HMAC: Keyed-Hashing for Message Authentication</a>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc7518.html#section-3.2">RFC 7518 Section 3.2: JSON Web Algorithms (JWA) - HMAC with SHA-2</a>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc7515.html">RFC 7515: JSON Web Signature (JWS)</a>
 */
public sealed interface HMAC permits HS256Algorithm {

    /**
     * A default instance of the HS256 algorithm, which uses HMAC with SHA-256.
     */
    HMAC HS256 = new HS256Algorithm();

    /**
     * Returns the name of the HMAC algorithm (e.g., "HS256").
     *
     * @return the name of the algorithm
     */
    String getName();

    /**
     * Computes the HMAC signature of the given message using the provided secret key.
     *
     * @param message the input message to sign
     * @param key     the secret key used for signing
     * @return the computed HMAC signature
     * @throws IllegalArgumentException if the input or key is invalid
     */
    byte[] sign(byte[] message, byte[] key);

}
