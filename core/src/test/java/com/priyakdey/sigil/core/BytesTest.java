package com.priyakdey.sigil.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Bytes Utility Tests")
class BytesTest {

    // ================================
    // CONCAT TESTS
    // ================================

    static Stream<Arguments> concatValidData() {
        return Stream.of(
                Arguments.of("basic concatenation",
                        new byte[][]{new byte[]{1}, new byte[]{2, 3}, new byte[]{4}},
                        new byte[]{1, 2, 3, 4}),
                Arguments.of("all empty arrays",
                        new byte[][]{new byte[]{}, new byte[]{}, new byte[]{}},
                        new byte[]{}),
                Arguments.of("mixed empty and non-empty",
                        new byte[][]{new byte[]{10, 20}, new byte[]{}, new byte[]{30}},
                        new byte[]{10, 20, 30}),
                Arguments.of("single array",
                        new byte[][]{new byte[]{1, 2, 3}},
                        new byte[]{1, 2, 3}),
                Arguments.of("no arguments",
                        new byte[][]{},
                        new byte[]{})
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("concatValidData")
    void testConcatValid(String description, byte[][] input, byte[] expected) {
        byte[] result = Bytes.concat(input);
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("concat() should throw IllegalArgumentException for null array")
    void testConcatWithNullArray() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Bytes.concat(new byte[]{1}, null, new byte[]{2}));
        assertEquals("Array cannot be null", exception.getMessage());
    }

    // ================================
    // CONSTANT TIME EQUALS TESTS
    // ================================

    @Test
    @DisplayName("constantTimeEquals() basic functionality")
    void testConstantTimeEquals() {
        // Equal arrays
        assertTrue(Bytes.constantTimeEquals(new byte[]{1, 2, 3}, new byte[]{1, 2, 3}));
        assertTrue(Bytes.constantTimeEquals(new byte[]{}, new byte[]{}));

        // Different arrays
        assertFalse(Bytes.constantTimeEquals(new byte[]{1, 2, 3}, new byte[]{1, 2, 4}));
        assertFalse(Bytes.constantTimeEquals(new byte[]{1, 2}, new byte[]{1, 2, 3}));

        // Same reference
        byte[] arr = new byte[]{1, 2, 3};
        assertTrue(Bytes.constantTimeEquals(arr, arr));

        // Null cases
        assertTrue(Bytes.constantTimeEquals(null, null));
        assertFalse(Bytes.constantTimeEquals(null, new byte[]{1}));
        assertFalse(Bytes.constantTimeEquals(new byte[]{1}, null));
    }

    // ================================
    // XOR TESTS
    // ================================

    @Test
    @DisplayName("xor() basic functionality")
    void testXor() {
        assertArrayEquals(new byte[]{0, 3, 0},
                Bytes.xor(new byte[]{1, 2, 3}, new byte[]{1, 1, 3}));

        assertArrayEquals(new byte[]{},
                Bytes.xor(new byte[]{}, new byte[]{}));

        // XOR with itself should be all zeros
        byte[] data = {5, 10, 15};
        assertArrayEquals(new byte[]{0, 0, 0}, Bytes.xor(data, data));
    }

    @Test
    @DisplayName("xor() should throw for different length arrays")
    void testXorDifferentLengths() {
        assertThrows(IllegalArgumentException.class, () ->
                Bytes.xor(new byte[]{1, 2}, new byte[]{1, 2, 3}));
    }

    // ================================
    // UTF-8 STRING CONVERSION TESTS
    // ================================

    @Test
    @DisplayName("UTF-8 string conversions")
    void testUTF8StringConversions() {
        String original = "Hello, 世界! 🌍";
        byte[] bytes = Bytes.fromUTF8String(original);
        String converted = Bytes.toUTF8String(bytes);

        assertEquals(original, converted);
        assertArrayEquals(original.getBytes(StandardCharsets.UTF_8), bytes);
    }

    @Test
    @DisplayName("UTF-8 conversions with empty string")
    void testUTF8EmptyString() {
        assertArrayEquals(new byte[]{}, Bytes.fromUTF8String(""));
        assertEquals("", Bytes.toUTF8String(new byte[]{}));
    }

    @Test
    @DisplayName("UTF-8 conversions should throw for null input")
    void testUTF8NullInputs() {
        assertThrows(IllegalArgumentException.class, () ->
                Bytes.fromUTF8String(null));
        assertThrows(IllegalArgumentException.class, () ->
                Bytes.toUTF8String(null));
    }

    // ================================
    // SLICE TESTS
    // ================================

    static Stream<Arguments> sliceValidData() {
        return Stream.of(
                Arguments.of("middle slice", new byte[]{1, 2, 3, 4, 5}, 1, 4, new byte[]{2, 3, 4}),
                Arguments.of("full slice", new byte[]{1, 2, 3}, 0, 3, new byte[]{1, 2, 3}),
                Arguments.of("single element", new byte[]{1, 2, 3}, 1, 2, new byte[]{2}),
                Arguments.of("from beginning", new byte[]{1, 2, 3}, 0, 2, new byte[]{1, 2}),
                Arguments.of("to end", new byte[]{1, 2, 3}, 2, 3, new byte[]{3})
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("sliceValidData")
    void testSliceValid(String description, byte[] src, int from, int end, byte[] expected) {
        assertArrayEquals(expected, Bytes.slice(src, from, end));
    }

    @Test
    @DisplayName("slice() should throw for invalid inputs")
    void testSliceInvalidInputs() {
        byte[] arr = {1, 2, 3};

        // Null array
        assertThrows(IllegalArgumentException.class, () ->
                Bytes.slice(null, 0, 1));

        // Negative from
        assertThrows(IllegalArgumentException.class, () ->
                Bytes.slice(arr, -1, 2));

        // End beyond array length
        assertThrows(IllegalArgumentException.class, () ->
                Bytes.slice(arr, 0, 4));

        // from >= end
        assertThrows(IllegalArgumentException.class, () ->
                Bytes.slice(arr, 2, 1));
        assertThrows(IllegalArgumentException.class, () ->
                Bytes.slice(arr, 1, 1)); // from == end should throw
    }

    // ================================
    // COPY TESTS
    // ================================

    @Test
    @DisplayName("copy() full array")
    void testCopyFull() {
        byte[] original = {1, 2, 3, 4};
        byte[] copy = Bytes.copy(original);

        assertArrayEquals(original, copy);
        assertNotSame(original, copy);

        // Verify independence
        copy[0] = 99;
        assertEquals(1, original[0]);
    }

    @Test
    @DisplayName("copy() with length")
    void testCopyWithLength() {
        byte[] original = {1, 2, 3, 4};

        assertArrayEquals(new byte[]{1, 2}, Bytes.copy(original, 2));
        assertArrayEquals(new byte[]{}, Bytes.copy(original, 0));
        assertArrayEquals(original, Bytes.copy(original, 4));
    }

    @Test
    @DisplayName("copy() to destination array")
    void testCopyToDestination() {
        byte[] src = {1, 2, 3};
        byte[] dest = new byte[3];

        Bytes.copy(src, dest);
        assertArrayEquals(src, dest);
        assertNotSame(src, dest);
    }

    @Test
    @DisplayName("copy() should throw for invalid inputs")
    void testCopyInvalidInputs() {
        byte[] arr = {1, 2, 3};

        // Null inputs
        assertThrows(IllegalArgumentException.class, () -> Bytes.copy(null));
        assertThrows(IllegalArgumentException.class, () -> Bytes.copy(null, 2));
        assertThrows(IllegalArgumentException.class, () -> Bytes.copy(arr, null));

        // Invalid length
        assertThrows(IllegalArgumentException.class, () -> Bytes.copy(arr, -1));
        assertThrows(IllegalArgumentException.class, () -> Bytes.copy(arr, 4));

        // Different lengths for destination copy
        assertThrows(IllegalArgumentException.class, () ->
                Bytes.copy(arr, new byte[2]));
    }

    // ================================
    // RANDOM TESTS
    // ================================

    @Test
    @DisplayName("random() generates arrays of correct length")
    void testRandom() {
        SecureRandom rng = new SecureRandom();

        byte[] random5 = Bytes.random(5, rng);
        assertEquals(5, random5.length);

        byte[] random10 = Bytes.random(10, rng);
        assertEquals(10, random10.length);

        // Two calls should produce different results (with very high probability)
        byte[] random1 = Bytes.random(16, rng);
        byte[] random2 = Bytes.random(16, rng);
        assertFalse(Arrays.equals(random1, random2));
    }

    @Test
    @DisplayName("random() should throw for invalid inputs")
    void testRandomInvalidInputs() {
        SecureRandom rng = new SecureRandom();

        assertThrows(IllegalArgumentException.class, () -> Bytes.random(0, rng));
        assertThrows(IllegalArgumentException.class, () -> Bytes.random(-1, rng));
        assertThrows(IllegalArgumentException.class, () -> Bytes.random(5, null));
    }

    // ================================
    // REPEAT TESTS
    // ================================

    @ParameterizedTest
    @ValueSource(bytes = {0, 1, -1, 127, -128})
    @DisplayName("repeat() creates correct arrays")
    void testRepeat(byte value) {
        byte[] result = Bytes.repeat(value, 5);
        assertEquals(5, result.length);
        for (byte b : result) {
            assertEquals(value, b);
        }
    }

    @Test
    @DisplayName("repeat() should throw for invalid length")
    void testRepeatInvalidLength() {
        assertThrows(IllegalArgumentException.class, () -> Bytes.repeat((byte) 1, 0));
        assertThrows(IllegalArgumentException.class, () -> Bytes.repeat((byte) 1, -1));
    }

    // ================================
    // REVERSE TESTS
    // ================================

    @Test
    @DisplayName("reverse() functionality")
    void testReverse() {
        assertArrayEquals(new byte[]{3, 2, 1}, Bytes.reverse(new byte[]{1, 2, 3}));
        assertArrayEquals(new byte[]{1}, Bytes.reverse(new byte[]{1}));
        assertArrayEquals(new byte[]{}, Bytes.reverse(new byte[]{}));

        // Even length
        assertArrayEquals(new byte[]{4, 3, 2, 1}, Bytes.reverse(new byte[]{1, 2, 3, 4}));
    }

    @Test
    @DisplayName("reverse() should throw for null input")
    void testReverseNullInput() {
        assertThrows(IllegalArgumentException.class, () -> Bytes.reverse(null));
    }

    // ================================
    // PADDING TESTS
    // ================================

    static Stream<Arguments> paddingData() {
        return Stream.of(
                Arguments.of("left pad shorter", new byte[]{1, 2}, 5, (byte) 0, new byte[]{0, 0, 0, 1, 2}),
                Arguments.of("left pad exact length", new byte[]{1, 2, 3}, 3, (byte) 0, new byte[]{1, 2, 3}),
                Arguments.of("left pad truncate", new byte[]{1, 2, 3, 4}, 2, (byte) 0, new byte[]{1, 2}),
                Arguments.of("right pad shorter", new byte[]{1, 2}, 5, (byte) 0, new byte[]{1, 2, 0, 0, 0}),
                Arguments.of("right pad exact length", new byte[]{1, 2, 3}, 3, (byte) 0, new byte[]{1, 2, 3}),
                Arguments.of("right pad truncate", new byte[]{1, 2, 3, 4}, 2, (byte) 0, new byte[]{1, 2})
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("paddingData")
    void testLeftPad(String description, byte[] src, int length, byte pad, byte[] expected) {
        if (description.contains("left")) {
            assertArrayEquals(expected, Bytes.leftPad(src, length, pad));
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("paddingData")
    void testRightPad(String description, byte[] src, int length, byte pad, byte[] expected) {
        if (description.contains("right")) {
            assertArrayEquals(expected, Bytes.rightPad(src, length, pad));
        }
    }

    @Test
    @DisplayName("padding with different pad values")
    void testPaddingWithDifferentValues() {
        byte[] src = {1, 2};

        assertArrayEquals(new byte[]{-1, -1, 1, 2}, Bytes.leftPad(src, 4, (byte) -1));
        assertArrayEquals(new byte[]{1, 2, 127, 127}, Bytes.rightPad(src, 4, (byte) 127));
    }

    @Test
    @DisplayName("padding should throw for invalid inputs")
    void testPaddingInvalidInputs() {
        byte[] arr = {1, 2, 3};

        // Null array
        assertThrows(IllegalArgumentException.class, () ->
                Bytes.leftPad(null, 5, (byte) 0));
        assertThrows(IllegalArgumentException.class, () ->
                Bytes.rightPad(null, 5, (byte) 0));

        // Negative length
        assertThrows(IllegalArgumentException.class, () ->
                Bytes.leftPad(arr, -1, (byte) 0));
        assertThrows(IllegalArgumentException.class, () ->
                Bytes.rightPad(arr, -1, (byte) 0));
    }

    @Test
    @DisplayName("padding with zero length")
    void testPaddingZeroLength() {
        byte[] src = {1, 2, 3};
        assertArrayEquals(new byte[]{}, Bytes.leftPad(src, 0, (byte) 0));
        assertArrayEquals(new byte[]{}, Bytes.rightPad(src, 0, (byte) 0));
    }

    // ================================
    // INTEGRATION TESTS
    // ================================

    @Test
    @DisplayName("Integration test: complex operations")
    void testComplexOperations() {
        // Create data, manipulate it through multiple operations
        byte[] original = "Hello".getBytes(StandardCharsets.UTF_8);

        // Copy, reverse, pad, slice
        byte[] copied = Bytes.copy(original);
        byte[] reversed = Bytes.reverse(copied);
        byte[] padded = Bytes.leftPad(reversed, 10, (byte) '*');
        byte[] sliced = Bytes.slice(padded, 2, 8);
        byte[] final1 = Bytes.rightPad(sliced, 8, (byte) '!');

        assertEquals(8, final1.length);

        // Verify the operations don't affect original
        assertEquals("Hello", new String(original, StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Performance test: large arrays")
    void testLargeArrayPerformance() {
        // Test with reasonably large arrays to ensure no performance issues
        byte[] large1 = Bytes.repeat((byte) 1, 10000);
        byte[] large2 = Bytes.repeat((byte) 2, 10000);

        byte[] concatenated = Bytes.concat(large1, large2);
        assertEquals(20000, concatenated.length);
        assertEquals(1, concatenated[0]);
        assertEquals(2, concatenated[10000]);

        byte[] xored = Bytes.xor(large1, large2);
        assertEquals(10000, xored.length);
        assertEquals(3, xored[0]); // 1 XOR 2 = 3
    }
}