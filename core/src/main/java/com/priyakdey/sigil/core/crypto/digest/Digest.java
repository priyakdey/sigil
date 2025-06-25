package com.priyakdey.sigil.core.crypto.digest;

/**
 * Represents a cryptographic message digest (hash function).
 *
 * <p>Implementations of this interface must be <strong>stateless and deterministic</strong>:
 * calling {@link #computeDigest(byte[])} multiple times with the same input must always return the same result,
 * and must not retain any internal state between invocations.</p>
 *
 * <p>This abstraction allows interchangeable usage of different digest algorithms such as SHA-256,
 * SHA-1, SHA-512, etc. The returned hash is specific to the concrete implementation and must comply
 * with the respective algorithm's specification.</p>
 *
 * <p><strong>Thread-safety:</strong> Implementations are expected to be thread-safe if stateless.
 * For stateful digesting (e.g., streaming input), use a builder or context-style pattern instead.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Digest digest = new SHA256Digest();
 * byte[] hash = digest.computeDigest("hello".getBytes(StandardCharsets.UTF_8));
 * }</pre>
 *
 * @author Priyak Dey
 */
public sealed interface Digest permits SHA256Digest {

    /**
     * Computes the digest (hash) of the given message.
     *
     * @param message the input byte array to be hashed; must not be {@code null}
     * @return the resulting message digest as a byte array
     * @throws NullPointerException if {@code message} is null
     */
    byte[] computeDigest(byte[] message);

}
