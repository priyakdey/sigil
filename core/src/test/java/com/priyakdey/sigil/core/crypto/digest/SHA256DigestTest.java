package com.priyakdey.sigil.core.crypto.digest;

import com.priyakdey.sigil.core.Bytes;
import com.priyakdey.sigil.core.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class SHA256DigestTest {

    private SHA256Digest sigilDigest;
    private org.bouncycastle.crypto.digests.SHA256Digest bcDigest;

    @BeforeEach
    void setUp() {
        sigilDigest = Digest.SHA256;
        bcDigest = new org.bouncycastle.crypto.digests.SHA256Digest();
    }

    static Stream<Arguments> sha256InputCases() {
        return Stream.of(
                Arguments.of("empty string", ""),
                Arguments.of("single character", "a"),
                Arguments.of("canonical abc", "abc"),
                Arguments.of("alphanumeric", "abc123XYZ"),
                Arguments.of("whitespace only", "     "),
                Arguments.of("trailing newline", "abc\n"),
                Arguments.of("emoji", "hello 👋"),
                Arguments.of("unicode math", "π≈3.14"),
                Arguments.of("long repeated a", "a".repeat(1000)),
                Arguments.of("english pangram", "The quick brown fox jumps over the lazy dog"),
                Arguments.of("pangram + period", "The quick brown fox jumps over the lazy dog."),
                Arguments.of("hex string literal", "deadbeefcafebabe"),
                Arguments.of("binary-like string", "0000111100001111"),
                Arguments.of("html snippet", "<div class='test'>value</div>"),
                Arguments.of("json object", "{\"key\": \"value\"}"),
                Arguments.of("java method snippet", "public static void main(String[] args)"),
                Arguments.of("repeating pattern", "abababababababababab"),
                Arguments.of("high entropy symbols", "Xz92!@#)(*<>{}|~^&$%-=+"),
                Arguments.of("null keyword", "null"),
                Arguments.of("true keyword", "true"),
                Arguments.of("false keyword", "false"),
                Arguments.of("case sensitivity test 1", "Hello"),
                Arguments.of("case sensitivity test 2", "hello"),
                Arguments.of("numeric", "1234567890"),
                Arguments.of("zip header mock", "PK\u0003\u0004"),
                Arguments.of("exactly 55 bytes", "x".repeat(55)),
                Arguments.of("exactly 56 bytes", "x".repeat(56)),
                Arguments.of("exactly 64 bytes", "x".repeat(64)),
                Arguments.of("empty JSON", "{}"),
                Arguments.of("git blob mock", "blob 14\u0000hello world\n")
        );
    }

    @ParameterizedTest(name = "digest::{index}")
    @MethodSource(value = "sha256InputCases")
    void digest(String inputAsString) {
        byte[] input = Bytes.fromUTF8String(inputAsString);

        byte[] expected = new byte[bcDigest.getDigestSize()];
        bcDigest.update(input, 0, input.length);
        bcDigest.doFinal(expected, 0);

        byte[] actual = sigilDigest.digest(input);

        assertArrayEquals(expected, actual, () -> String.format(
                "Failed test case for input `%s`: expected %s but got %s%n", inputAsString,
                Hex.repr(expected), Hex.repr(actual)));
    }

}