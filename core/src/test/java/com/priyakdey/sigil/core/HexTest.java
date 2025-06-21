package com.priyakdey.sigil.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Priyak Dey
 */
@DisplayName("Hex Utility Tests")
class HexTest {

    @Nested
    @DisplayName("toHexString() Tests")
    class ToHexStringTests {

        @Test
        @DisplayName("Should convert empty byte array to empty string")
        void shouldConvertEmptyByteArray() {
            byte[] empty = new byte[0];
            String result = Hex.toHexString(empty);
            assertEquals("", result);
        }

        @Test
        @DisplayName("Should convert single byte to hex string")
        void shouldConvertSingleByte() {
            byte[] singleByte = {(byte) 0xFF};
            String result = Hex.toHexString(singleByte);
            assertEquals("FF", result);
        }

        @Test
        @DisplayName("Should convert example byte array to DEADBEEF")
        void shouldConvertExampleByteArray() {
            byte[] data = {(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF};
            String result = Hex.toHexString(data);
            assertEquals("DEADBEEF", result);
        }

        @ParameterizedTest
        @DisplayName("Should convert various byte arrays to correct hex strings")
        @MethodSource("provideByteArraysAndExpectedHex")
        void shouldConvertVariousByteArrays(byte[] input, String expected) {
            String result = Hex.toHexString(input);
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should handle all possible byte values (0x00 to 0xFF)")
        void shouldHandleAllByteValues() {
            byte[] allBytes = new byte[256];
            for (int i = 0; i < 256; i++) {
                allBytes[i] = (byte) i;
            }
            String result = Hex.toHexString(allBytes);

            // Verify length
            assertEquals(512, result.length()); // 256 bytes * 2 hex chars each

            // Verify first few and last few bytes
            assertTrue(result.startsWith("000102"));
            assertTrue(result.endsWith("CFDFEFF"));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for null input")
        void shouldThrowExceptionForNullInput() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> Hex.toHexString(null)
            );
            assertEquals("Source array cannot be null", exception.getMessage());
        }

        static Stream<Arguments> provideByteArraysAndExpectedHex() {
            return Stream.of(
                    Arguments.of(new byte[]{0x00}, "00"),
                    Arguments.of(new byte[]{0x0F}, "0F"),
                    Arguments.of(new byte[]{(byte) 0xF0}, "F0"),
                    Arguments.of(new byte[]{0x12, 0x34}, "1234"),
                    Arguments.of(new byte[]{(byte) 0xAB, (byte) 0xCD, (byte) 0xEF}, "ABCDEF"),
                    Arguments.of(new byte[]{0x00, (byte) 0xFF, 0x7F, (byte) 0x80}, "00FF7F80")
            );
        }
    }

    @Nested
    @DisplayName("fromHexString() Tests")
    class FromHexStringTests {

        @Test
        @DisplayName("Should convert empty string to empty byte array")
        void shouldConvertEmptyString() {
            byte[] result = Hex.fromHexString("");
            assertArrayEquals(new byte[0], result);
        }

        @Test
        @DisplayName("Should reject hex strings with internal whitespace")
        void shouldRejectWhitespaceInHex() {
            assertThrows(IllegalArgumentException.class, () -> Hex.fromHexString("DE AD"));
        }

        @Test
        @DisplayName("Should convert single hex pair to single byte")
        void shouldConvertSingleHexPair() {
            byte[] result = Hex.fromHexString("FF");
            assertArrayEquals(new byte[]{(byte) 0xFF}, result);
        }

        @Test
        @DisplayName("Should convert DEADBEEF to example byte array")
        void shouldConvertDeadbeefString() {
            byte[] result = Hex.fromHexString("DEADBEEF");
            byte[] expected = {(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF};
            assertArrayEquals(expected, result);
        }

        @ParameterizedTest
        @DisplayName("Should convert various hex strings to correct byte arrays")
        @MethodSource("provideHexStringsAndExpectedBytes")
        void shouldConvertVariousHexStrings(String input, byte[] expected) {
            byte[] result = Hex.fromHexString(input);
            assertArrayEquals(expected, result);
        }

        @Test
        @DisplayName("Should handle both uppercase and lowercase hex characters")
        void shouldHandleBothCases() {
            String upperCase = "ABCDEF";
            String lowerCase = "abcdef";
            String mixedCase = "AbCdEf";

            byte[] expectedBytes = {(byte) 0xAB, (byte) 0xCD, (byte) 0xEF};

            assertArrayEquals(expectedBytes, Hex.fromHexString(upperCase));
            assertArrayEquals(expectedBytes, Hex.fromHexString(lowerCase));
            assertArrayEquals(expectedBytes, Hex.fromHexString(mixedCase));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for null input")
        void shouldThrowExceptionForNullInput() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> Hex.fromHexString(null)
            );
            assertEquals("Hex string must not be null and must have an even length", exception.getMessage());
        }

        @ParameterizedTest
        @DisplayName("Should throw IllegalArgumentException for odd length strings")
        @ValueSource(strings = {"A", "ABC", "ABCDE", "1234567"})
        void shouldThrowExceptionForOddLengthStrings(String oddLengthString) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> Hex.fromHexString(oddLengthString)
            );
            assertEquals("Hex string must not be null and must have an even length", exception.getMessage());
        }

        @ParameterizedTest
        @DisplayName("Should throw IllegalArgumentException for invalid hex characters")
        @ValueSource(strings = {"GH", "XY", "1G", "Z0", "  ", "!@", "++", "--"})
        void shouldThrowExceptionForInvalidHexCharacters(String invalidHexString) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> Hex.fromHexString(invalidHexString)
            );
            assertTrue(exception.getMessage().startsWith("Invalid hex character in string:"));
            assertTrue(exception.getMessage().contains(invalidHexString));
        }

        static Stream<Arguments> provideHexStringsAndExpectedBytes() {
            return Stream.of(
                    Arguments.of("00", new byte[]{0x00}),
                    Arguments.of("0F", new byte[]{0x0F}),
                    Arguments.of("F0", new byte[]{(byte) 0xF0}),
                    Arguments.of("1234", new byte[]{0x12, 0x34}),
                    Arguments.of("ABCDEF", new byte[]{(byte) 0xAB, (byte) 0xCD, (byte) 0xEF}),
                    Arguments.of("00FF7F80", new byte[]{0x00, (byte) 0xFF, 0x7F, (byte) 0x80}),
                    Arguments.of("0123456789ABCDEF", new byte[]{0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF})
            );
        }
    }

    @Nested
    @DisplayName("Round-trip Tests")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class RoundTripTests {

        @ParameterizedTest
        @DisplayName("Should maintain data integrity in round-trip conversions")
        @MethodSource("provideByteArraysForRoundTrip")
        void shouldMaintainDataIntegrityInRoundTrip(byte[] original) {
            String hexString = Hex.toHexString(original);
            byte[] roundTrip = Hex.fromHexString(hexString);
            assertArrayEquals(original, roundTrip);
        }

        @ParameterizedTest
        @DisplayName("Should maintain string format in round-trip conversions")
        @MethodSource("provideHexStringsForRoundTrip")
        void shouldMaintainStringFormatInRoundTrip(String original) {
            byte[] byteArray = Hex.fromHexString(original);
            String roundTrip = Hex.toHexString(byteArray);
            assertEquals(original.toUpperCase(), roundTrip);
        }

        Stream<Arguments> provideByteArraysForRoundTrip() {
            return Stream.of(
                    Arguments.of(new byte[0]),
                    Arguments.of(new byte[]{0x00}),
                    Arguments.of(new byte[]{(byte) 0xFF}),
                    Arguments.of(new byte[]{(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF}),
                    Arguments.of(new byte[]{0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, (byte) 0xFF})
            );
        }

        Stream<Arguments> provideHexStringsForRoundTrip() {
            return Stream.of(
                    Arguments.of(""),
                    Arguments.of("00"),
                    Arguments.of("FF"),
                    Arguments.of("DEADBEEF"),
                    Arguments.of("0123456789ABCDEF"),
                    Arguments.of("00112233445566778899AABBCCDDEEFF")
            );
        }
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle maximum single byte value")
        void shouldHandleMaxSingleByteValue() {
            byte[] maxByte = {(byte) 0xFF};
            String hex = Hex.toHexString(maxByte);
            assertEquals("FF", hex);

            byte[] parsed = Hex.fromHexString(hex);
            assertArrayEquals(maxByte, parsed);
        }

        @Test
        @DisplayName("Should handle minimum single byte value")
        void shouldHandleMinSingleByteValue() {
            byte[] minByte = {(byte) 0x00};
            String hex = Hex.toHexString(minByte);
            assertEquals("00", hex);

            byte[] parsed = Hex.fromHexString(hex);
            assertArrayEquals(minByte, parsed);
        }

        @Test
        @DisplayName("Should handle large byte arrays")
        void shouldHandleLargeByteArrays() {
            byte[] largeArray = new byte[1000];
            for (int i = 0; i < largeArray.length; i++) {
                largeArray[i] = (byte) (i % 256);
            }

            String hex = Hex.toHexString(largeArray);
            assertEquals(2000, hex.length()); // 1000 bytes * 2 hex chars each

            byte[] parsed = Hex.fromHexString(hex);
            assertArrayEquals(largeArray, parsed);
        }

        @Test
        @DisplayName("Should handle mixed case hex strings")
        void shouldHandleMixedCaseHexStrings() {
            String mixedCase = "DeAdBeEf";
            byte[] expected = {(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF};
            byte[] result = Hex.fromHexString(mixedCase);
            assertArrayEquals(expected, result);
        }
    }
}