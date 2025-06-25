package com.priyakdey.sigil.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Hex utility Test")
class HexTest {

    static Stream<Arguments> fromByteArrayProvider() {
        return Stream.of(
                Arguments.of("Dead beef", new byte[]{(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF}, "0xDEADBEEF"),
                Arguments.of("Empty", new byte[]{}, "0x"),
                Arguments.of("All Zeroes", new byte[]{0x00, 0x00, 0x00}, "0x000000")
        );
    }

    static Stream<Arguments> fromStringProvider() {
        return Stream.of(
                Arguments.of("Ascii", "Hi", "0x4869"),
                Arguments.of("Unicode", "π", "0xCF80"),
                Arguments.of("Empty", "", "0x")
        );
    }

    static Stream<Arguments> toByteArrayProvider() {
        return Stream.of(
                Arguments.of("With prefix and uppercase", "0xABCD", new byte[]{(byte) 0xAB, (byte) 0xCD}),
                Arguments.of("With prefix and lowercase", "0xabcd", new byte[]{(byte) 0xAB, (byte) 0xCD}),
                Arguments.of("With prefix and mixed case", "1A2b3C", new byte[]{(byte) 0x1A, (byte) 0x2b, (byte) 0x3C}),
                Arguments.of("Empty hex after prefix", "0x", new byte[0]),
                Arguments.of("All zeroes", "0000", new byte[]{0x00, 0x00})
        );
    }


    @ParameterizedTest(name = "from_bytearray:{0}")
    @MethodSource("fromByteArrayProvider")
    void test_from_bytearray(String testCase, byte[] input, String expected) {
        String actual = Hex.from(input);
        assertEquals(expected, actual,
                String.format("Digest mismatch for testcase '%s': expected %s, got %s",
                        testCase, expected, actual));
    }

    @ParameterizedTest(name = "from_string:{0}")
    @MethodSource("fromStringProvider")
    void test_from_string(String testCase, String input, String expected) {
        String actual = Hex.from(input);
        assertEquals(expected, actual,
                String.format("Digest mismatch for testcase '%s': expected %s, got %s",
                        testCase, expected, actual));
    }

    @ParameterizedTest(name = "to_bytes:{0}")
    @MethodSource("toByteArrayProvider")
    void test_to_bytes(String testCase, String input, byte[] expected) {
        byte[] actual = Hex.to(input);
        assertArrayEquals(expected, actual,
                String.format("Digest mismatch for testcase '%s': expected %s, got %s", testCase,
                        Arrays.toString(expected), Arrays.toString(actual)));
    }


    @Test
    void testToHex_invalidCharacters() {
        assertThrows(IllegalArgumentException.class, () -> Hex.to("0xZZ12"));
        assertThrows(IllegalArgumentException.class, () -> Hex.to("GHIJ"));
    }

    @Test
    void testToHex_oddLength_withPrefix() {
        assertThrows(IllegalArgumentException.class, () -> Hex.to("0xABC"));
    }

    @Test
    void testToHex_oddLength_withoutPrefix() {
        assertThrows(IllegalArgumentException.class, () -> Hex.to("123"));
    }

    @Test
    void testToHex_nullInput() {
        assertThrows(IllegalArgumentException.class, () -> Hex.to(null));
    }
}
