package com.priyakdey.sigil.core.crypto.digest;

/**
 * Represents a cryptographic message digest algorithm interface.
 * <p>
 * This interface provides a contract for computing the hash (digest)
 * of an input byte array. A digest algorithm takes a variable-length input and produces
 * a fixed-length hash output, which can be used for verifying data integrity and authentication.
 * </p>
 *
 * <p>
 * Currently, this interface permits {@link SHA256Digest}, an implementation of the
 * SHA-256 (Secure Hash Algorithm 256-bit), as specified by the NIST FIPS 180-4 standard.
 * </p>
 *
 * @author Priyak Dey
 * @see <a href="https://www.rfc-editor.org/rfc/rfc6234.html">RFC 6234: US Secure Hash Algorithms (SHA and SHA-based HMAC and HKDF)</a>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc4634.html">RFC 4634: SHA and HMAC-SHA Implementations</a>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc3874.html">RFC 3874: SHA-256 and SHA-224 for the Internet X.509 Public Key Infrastructure</a>
 * @see <a href="https://csrc.nist.gov/publications/detail/fips/180/4/final">NIST FIPS PUB 180-4: Secure Hash Standard (SHS)</a>
 */
public sealed interface Digest permits SHA256Digest {

    /**
     * A default instance of the SHA-256 digest algorithm.
     */
    SHA256Digest SHA256 = new SHA256Digest();

    /**
     * Computes the cryptographic hash (digest) of the input message.
     *
     * @param message the input byte array to hash; must not be {@code null}
     * @return the computed hash (digest) as a byte array
     * @throws IllegalArgumentException if the input {@code message} is {@code null}
     */
    byte[] digest(byte[] message);

}
