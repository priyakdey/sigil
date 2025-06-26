package com.priyakdey.sigil.jwa.hmac;

/**
 * Represents an HMAC (Hash-Based Message Authentication Code) algorithm implementation.
 *
 * <p>HMAC is a construction for creating a message authentication code (MAC) by combining a
 * secret key and a hash function (such as SHA-256). It ensures both the integrity and
 * authenticity of a message by producing a fixed-length signature that can be verified
 * by any party possessing the same secret key.</p>
 *
 * <strong>Responsibilities</strong>
 * <ul>
 *   <li>Provide a well-defined name for the HMAC variant (e.g., {@code HS256}).</li>
 *   <li>Accept input data and a secret key to produce a signature (MAC) in the form of a byte array.</li>
 * </ul>
 *
 * <strong>Sealed Hierarchy</strong>
 * <p>This interface is sealed and can only be implemented by authorized classes within this package,
 * such as {@link HS256Algorithm}. This allows for a constrained, well-controlled set of HMAC implementations,
 * making the design both extensible and secure.</p>
 *
 * <strong>Thread-safety</strong>
 * <p>Implementations must be thread-safe for concurrent invocations of {@link #sign(byte[], byte[])}
 * and {@link #algorithmName()}. State (if any) should be isolated per call, making instances safe
 * for multithreaded environments, unless stated otherwise by the implementor.</p>
 *
 * <strong>Example Usage</strong>
 * <pre>{@code
 * HMAC hmac = new HS256Algorithm();
 * byte[] signature = hmac.sign(data, secretKey);
 * System.out.println("HMAC Signature: " + Hex.from(signature));
 * }</pre>
 *
 * <strong>References</strong>
 * <ul>
 *   <li><a href="https://datatracker.ietf.org/doc/html/rfc2104">RFC 2104 - HMAC: Keyed-Hashing for Message Authentication</a></li>
 *   <li><a href="https://en.wikipedia.org/wiki/HMAC">Wikipedia - HMAC</a></li>
 * </ul>
 *
 * @see HS256Algorithm
 * @author Priyak Dey
 */
public sealed interface HMAC permits HS256Algorithm {

    /**
     * Returns the algorithm name of this HMAC implementation.
     *
     * <p>Common examples: {@code HS256}, {@code HS384}, {@code HS512}.
     *
     * @return the identifier for this HMAC algorithm.
     */
    String algorithmName();


    /**
     * Computes the HMAC signature of the given data using the provided secret key.
     *
     * <p>Both {@code data} and {@code key} must be non-null, and {@code key}
     * must be of an appropriate length for the HMAC variant. The method returns
     * the computed signature as a byte array.</p>
     *
     * @param data the message data to be signed
     * @param key  the secret key used for signing
     * @return the computed HMAC signature
     * @throws NullPointerException     if {@code data} or {@code key} is {@code null}
     * @throws IllegalArgumentException if the key is invalid for the specific HMAC variant
     */
    byte[] sign(byte[] data, byte[] key);

}
