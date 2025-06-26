package com.priyakdey.sigil.jwa.hmac;

import com.priyakdey.sigil.core.Hex;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("HMAC using SHA-256 Tests")
class HS256AlgorithmTest {


    /**
     * Test vectors from <a href="https://datatracker.ietf.org/doc/html/rfc4231#section-4">RFC 4231</a>
     */
    static Stream<Arguments> testVectors() {
        return Stream.of(
                Arguments.of("key = 20 bytes",
                        "0x0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b",
                        "0x4869205468657265",
                        "0xb0344c61d8db38535ca8afceaf0bf12b881dc200c9833da726e9376c2e32cff7"),
                Arguments.of("key = key shorter than the length of the HMAC output",
                        "0x4a656665",
                        "0x7768617420646f2079612077616e7420666f72206e6f7468696e673f",
                        "0x5bdcc146bf60754e6a042426089575c75a003f089d2739839dec58b964ec3843"),
                Arguments.of("key = combined length of key + data that is larger than 64 bytes",
                        "0xaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                        "0xdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd",
                        "0x773ea91e36800e46854db8ebd09181a72959098b3ef8c122d9635514ced565fe"),
                Arguments.of("key = combined length of key and data that is larger than 64 bytes",
                        "0x0102030405060708090a0b0c0d0e0f10111213141516171819",
                        "0xcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcd",
                        "0x82558a389a443c0ea4cc819899f2083a85f0faa3e578f8077a2e3ff46729665b"),
                Arguments.of("key = key larger than 128 bytes",
                        "0xaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                        "0x54657374205573696e67204c6172676572205468616e20426c6f636b2d53697a65204b6579202d2048617368204b6579204669727374",
                        "0x60e431591ee0b67f0d8a26aacbf5b77f8e0bc6213728c5140546040f0ee37f54"),
                Arguments.of("key = key and data larger than 128 bytes",
                        "0xaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                        "0x5468697320697320612074657374207573696e672061206c6172676572207468616e20626c6f636b2d73697a65206b657920616e642061206c6172676572207468616e20626c6f636b2d73697a6520646174612e20546865206b6579206e6565647320746f20626520686173686564206265666f7265206265696e6720757365642062792074686520484d414320616c676f726974686d2e",
                        "0x9b09ffa71b942fcb27635fbcd5b0e944bfdc63644f0713938a7f51535c3a35e2")
        );
    }

    @ParameterizedTest(name = "sign:: {index} - {0}")
    @MethodSource("testVectors")
    void test_sign(String testName, String keyHex, String dataHex, String expectedHex) {
        byte[] key = Hex.to(keyHex);
        byte[] data = Hex.to(dataHex);

        byte[] sign = new HS256Algorithm().sign(data, key);

        String actualHex = Hex.from(sign);

        assertEquals(expectedHex.toLowerCase(), actualHex.toLowerCase(),
                String.format("Test %s failed: expected %s but got %s", testName, expectedHex,
                        actualHex));
    }

    @Test
    @DisplayName("Algorithm Name Test")
    void test_algorithmName() {
        HS256Algorithm algorithm = new HS256Algorithm();
        String expectedName = "HS256";
        String actualName = algorithm.algorithmName();

        assertEquals(expectedName, actualName,
                String.format("Expected algorithm name '%s' but got '%s'", expectedName, actualName));
    }
}