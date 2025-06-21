package com.priyakdey.sigil.jwa;

import com.priyakdey.sigil.core.Hex;
import org.bouncycastle.crypto.macs.HMac;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("HS265 Algorithm Tests")
class HS256AlgorithmTest {

    private final HMAC HS256_ALGORITHM = HMAC.HS256;

    private HMac HS256_ALGORITHM_BC;

    @BeforeEach
    void setUp() {
        org.bouncycastle.crypto.digests.SHA256Digest digest = new org.bouncycastle.crypto.digests.SHA256Digest();
        HS256_ALGORITHM_BC = new HMac(digest);
    }

    static Stream<Arguments> testData() {
        // Test data from RFC 2104
        // Ref: https://datatracker.ietf.org/doc/html/rfc4231#section-4.1
        // Skip test case 5 from rfc, since truncation is not supported for jwt,
        // and we don't support either.
        return Stream.of(
                Arguments.of("4869205468657265", "0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b", "b0344c61d8db38535ca8afceaf0bf12b881dc200c9833da726e9376c2e32cff7"),
                Arguments.of("7768617420646f2079612077616e7420666f72206e6f7468696e673f", "4a656665", "5bdcc146bf60754e6a042426089575c75a003f089d2739839dec58b964ec3843"),
                Arguments.of("dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "773ea91e36800e46854db8ebd09181a72959098b3ef8c122d9635514ced565fe"),
                Arguments.of("cdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcdcd", "0102030405060708090a0b0c0d0e0f10111213141516171819", "82558a389a443c0ea4cc819899f2083a85f0faa3e578f8077a2e3ff46729665b"),
                Arguments.of("54657374205573696e67204c6172676572205468616e20426c6f636b2d53697a65204b6579202d2048617368204b6579204669727374", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "60e431591ee0b67f0d8a26aacbf5b77f8e0bc6213728c5140546040f0ee37f54"),
                Arguments.of("5468697320697320612074657374207573696e672061206c6172676572207468616e20626c6f636b2d73697a65206b657920616e642061206c6172676572207468616e20626c6f636b2d73697a6520646174612e20546865206b6579206e6565647320746f20626520686173686564206265666f7265206265696e6720757365642062792074686520484d414320616c676f726974686d2e", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "9b09ffa71b942fcb27635fbcd5b0e944bfdc63644f0713938a7f51535c3a35e2")
        );
    }


    /**
     * These tests are based on the test cases from RFC 2104.
     *
     * @param hexMsg               Input message in hex format
     * @param hexKey               Input key in hex format
     * @param expectedHexSignature Expected signature in hex format
     */
    @ParameterizedTest(name = "sign_test_case_rfc_{index}")
    @MethodSource("testData")
    void sign_rfc(String hexMsg, String hexKey, String expectedHexSignature) {
        byte[] message = Hex.fromHexString(hexMsg);
        byte[] key = Hex.fromHexString(hexKey);

        byte[] signature = HS256_ALGORITHM.sign(message, key);

        assertEquals(expectedHexSignature.toUpperCase(), Hex.toHexString(signature), "Signature mismatch");
    }

    /**
     * These tests are run against the same test data as RFC 2104, but expected is from the output
     * of the BC library.
     *
     * @param hexMsg               Input message in hex format
     * @param hexKey               Input key in hex format
     */
    @ParameterizedTest(name = "sign_test_case_bc_{index}")
    @MethodSource("testData")
    void sign_bc(String hexMsg, String hexKey) {
        byte[] message = Hex.fromHexString(hexMsg);
        byte[] key = Hex.fromHexString(hexKey);
        byte[] signature = HS256_ALGORITHM.sign(message, key);

        String expectedHexSignature = generateSignUsingBC(message, key);

        assertEquals(expectedHexSignature, Hex.toHexString(signature), "Signature mismatch");
    }

    private String generateSignUsingBC(byte[] message, byte[] key) {
        // Generate the signature using Bouncy Castle's HMAC
        HS256_ALGORITHM_BC.init(new org.bouncycastle.crypto.params.KeyParameter(key));
        HS256_ALGORITHM_BC.update(message, 0, message.length);
        byte[] expectedSignature = new byte[HS256_ALGORITHM_BC.getMacSize()];
        HS256_ALGORITHM_BC.doFinal(expectedSignature, 0);
        return Hex.toHexString(expectedSignature);
    }

}