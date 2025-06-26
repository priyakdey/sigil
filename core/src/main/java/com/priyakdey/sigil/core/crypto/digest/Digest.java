package com.priyakdey.sigil.core.crypto.digest;

/**
 * Represents a cryptographic message digest (hash function).
 *
 * <p>Implementations of this interface compute the hash of an input message and
 * return its result. The hash is specific to the concrete implementation and must
 * conform to its respective specification (e.g., SHA-256).</p>
 *
 * <strong>State Management</strong>
 * <p>Although the digest can be reused for multiple hash operations, it maintains
 * an internal state that must be reset between invocations. This is done via
 * the {@link #return_and_reset()} method, which returns the computed hash and
 * resets the internal state to its initial state. Failing to reset will cause
 * subsequent digest calculations to produce incorrect results.</p>
 *
 * <strong>Thread-Safety</strong>
 * <p>Instances of this interface are <strong>NOT thread-safe</strong> unless
 * used with external synchronization. To safely use in multithreaded environments,
 * create separate instances for each thread, or use a higher-level context-style
 * pattern that allows incremental hash construction and finalization per-thread.</p>
 *
 * <strong>Example Usage</strong>
 * <pre>{@code
 * Digest digest = new SHA256Digest();
 * byte[] hash = digest.computeDigest("hello".getBytes(StandardCharsets.UTF_8))
 *                      .return_and_reset();
 * }</pre>
 *
 * <strong>Best Practices</strong>
 * <ul>
 *     <li>For one-shot hash calculations, create a new digest instance per call,
 *     or call {@code return_and_reset()} after every use to reset state.</li>
 *     <li>For stateful streaming input, use a context-style digest class
 *     designed for incremental updates and finalization, if available.</li>
 * </ul>
 *
 * <strong>References</strong>
 * <ul>
 *     <li><a href="https://tools.ietf.org/html/rfc6234">RFC 6234 - Secure Hash Standard (SHS)</a></li>
 *     <li><a href="https://en.wikipedia.org/wiki/SHA-2">Wikipedia - SHA-2</a></li>
 *     <li><a href="https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.180-4.pdf">NIST FIPS 180-4 - Secure Hash Standard</a></li>
 * </ul>
 *
 * @author Priyak Dey
 */
public sealed interface Digest permits SHA256Digest {

    /**
     * Computes the hash of the given input message.
     *
     * <p>The resulting hash is stored within the digest’s internal state
     * until {@link #return_and_reset()} is called, at which point the hash
     * can be retrieved and the state reset for future use.</p>
     *
     * @param message the input data to be hashed (must not be {@code null})
     * @return this digest instance, allowing method chaining
     * @throws NullPointerException if {@code message} is {@code null}
     */
    Digest computeDigest(byte[] message);

    /**
     * Returns the resulting hash of the most recent digest computation
     * and resets the internal state to its initial state.
     *
     * @return the hash result as a byte array
     */
    byte[] return_and_reset();

}
