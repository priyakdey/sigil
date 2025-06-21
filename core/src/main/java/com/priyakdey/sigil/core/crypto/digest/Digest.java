package com.priyakdey.sigil.core.crypto.digest;

/**
 * @author Priyak Dey
 */
public sealed interface Digest permits SHA256Digest {

    SHA256Digest SHA256 = new SHA256Digest();

    /**
     * Computes the digest of the input message.
     *
     * @param message input message to hash
     * @return the hash (digest) of the input
     * @throws IllegalArgumentException if message is null
     */
    byte[] digest(byte[] message);

}
