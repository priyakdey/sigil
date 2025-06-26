package com.priyakdey.sigil.core.crypto.digest;

import com.priyakdey.sigil.common.CAVSParser;
import com.priyakdey.sigil.core.Hex;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("SHA-256 Digest Test")
class SHA256DigestTest {

    static Stream<Arguments> sha256ShortMsgTestVectors() {
        CAVSParser parser = new CAVSParser("shabytetestvectors/SHA256ShortMsg.rsp");
        return parser.parse();
    }

    static Stream<Arguments> sha256LongMsgTestVectors() {
        CAVSParser parser = new CAVSParser("shabytetestvectors/SHA256LongMsg.rsp");
        return parser.parse();
    }

    @ParameterizedTest(name = "computeDigest-short-msg: {0}")
    @MethodSource("sha256ShortMsgTestVectors")
    void test_computeDigest_shortMsg(String testName, byte[] message, String expectedAsHex) {
        SHA256Digest hasher = new SHA256Digest();
        byte[] digest = hasher.computeDigest(message).return_and_reset();

        assertEquals(32, digest.length, "Digest length should be 32 bytes");

        String actualHex = Hex.from(digest);
        assertEquals(expectedAsHex.toLowerCase(), actualHex.toLowerCase(),
                () -> String.format("Digest mismatch for testcase '%s': expected %s, got %s",
                        testName, expectedAsHex, actualHex));
    }

    @ParameterizedTest(name = "computeDigest-long-msg: {0}")
    @MethodSource("sha256LongMsgTestVectors")
    void test_computeDigest_longMsg(String testName, byte[] message, String expectedAsHex) {
        SHA256Digest hasher = new SHA256Digest();
        byte[] digest = hasher.computeDigest(message).return_and_reset();

        assertEquals(32, digest.length, "Digest length should be 32 bytes");

        String actualHex = Hex.from(digest);
        assertEquals(expectedAsHex.toLowerCase(), actualHex.toLowerCase(),
                () -> String.format("Digest mismatch for testcase '%s': expected %s, got %s",
                        testName, expectedAsHex, actualHex));
    }

}