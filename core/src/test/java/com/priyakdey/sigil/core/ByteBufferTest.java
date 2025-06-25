package com.priyakdey.sigil.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("ByteBuffer Tests")
class ByteBufferTest {

    @Test
    @DisplayName("Constructor with invalid capacity")
    void test_invalid_capacity() {
        assertThrows(IllegalArgumentException.class, () -> new ByteBuffer(-1),
                "Creating ByteBuffer with negative capacity should throw IllegalArgumentException");
    }

    @Test
    @DisplayName("Append Single Byte when not at capacity")
    void testAppendSingleByte_NotAtCapacity() {
        ByteBuffer buffer = new ByteBuffer(4);
        buffer.append((byte) 0xAB);

        assertEquals(1, buffer.length(),
                "Buffer length should be 1 after appending a single byte");

        String expected = "0xAB";
        String actual = buffer.toString();
        assertEquals(expected, actual, () -> String.format("Expected: %s, Actual: %s", expected,
                actual));
    }

    @Test
    @DisplayName("Append Single Byte when at capacity")
    void testAppendSingleByte_AtCapacity() {
        ByteBuffer buffer = new ByteBuffer(4);
        buffer.append((byte) 0xAB);
        buffer.append((byte) 0xAB);
        buffer.append((byte) 0xAB);
        buffer.append((byte) 0xAB);

        buffer.append((byte) 0xDE);

        assertEquals(5, buffer.length(),
                "Buffer length should be 5 after appending a single byte when buffer is full");

        String expected = "0xABABABABDE";
        String actual = buffer.toString();

        assertEquals(expected, actual, () -> String.format("Expected: %s, Actual: %s", expected,
                actual));
    }

    @Test
    @DisplayName("Append Bytes when not at capacity")
    void testAppendBytes_NotAtCapacity() {
        ByteBuffer buffer = new ByteBuffer(8);
        byte[] bytes = {0x01, 0x02, 0x03, 0x04};
        buffer.append(bytes);

        assertEquals(4, buffer.length(),
                "Buffer length should be 4 after appending 4 bytes");

        String expected = "0x01020304";
        String actual = buffer.toString();
        assertEquals(expected, actual, () -> String.format("Expected: %s, Actual: %s", expected,
                actual));
    }

    @Test
    @DisplayName("Append Bytes when at capacity")
    void testAppendBytes_AtCapacity() {
        ByteBuffer buffer = new ByteBuffer(8);
        byte[] bytes1 = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
        buffer.append(bytes1);

        byte[] bytes2 = {0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11, 0x12, 0x13, 0x14};
        buffer.append(bytes2);

        assertEquals(20, buffer.length(), "Buffer length should be 20");

        String expected = "0x0102030405060708090A0B0C0D0E0F1011121314";
        String actual = buffer.toString();
        assertEquals(expected, actual, () -> String.format("Expected: %s, Actual: %s", expected,
                actual));
    }

    @Test
    @DisplayName("Append Null Bytes array")
    void testAppendNullBytesArray() {
        ByteBuffer buffer = new ByteBuffer(8);
        assertThrows(IllegalArgumentException.class, () -> buffer.append(null),
                "Appending null bytes array should throw IllegalArgumentException");
    }

    @Test
    @DisplayName("Append Long in Big-Endian when buffer at capacity")
    void testAppendLong_BigEndian_AtCapacity() {
        ByteBuffer buffer = new ByteBuffer(10);
        buffer.append(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A});

        long value = 1L << 43;
        buffer.append(value, ByteOrder.BIG_ENDIAN);

        assertEquals(18, buffer.length(),
                "Buffer length should be 18 after appending a long value in big-endian order");

        String expected = "0x0102030405060708090A0000080000000000";
        String actual = buffer.toString();
        assertEquals(expected, actual, () -> String.format("Expected: %s, Actual: %s", expected,
                actual));
    }

    @Test
    @DisplayName("Append Long in Big-Endian when buffer not at capacity")
    void testAppendLong_BigEndian_NotAtCapacity() {
        ByteBuffer buffer = new ByteBuffer(10);
        buffer.append(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05});

        long value = 8796093022218L;    // 0x8000000000A
        buffer.append(value, ByteOrder.BIG_ENDIAN);

        assertEquals(13, buffer.length(),
                "Buffer length should be 13 after appending a long value in big-endian order");

        String expected = "0x0102030405000008000000000A";
        String actual = buffer.toString();
        assertEquals(expected, actual, () -> String.format("Expected: %s, Actual: %s", expected,
                actual));
    }

    @Test
    @DisplayName("Append Long in Little-Endian when buffer at capacity")
    void testAppendLong_LittleEndian_AtCapacity() {
        ByteBuffer buffer = new ByteBuffer(10);
        buffer.append(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A});

        long value = (long) 1 << 43;
        buffer.append(value, ByteOrder.LITTLE_ENDIAN);

        assertEquals(18, buffer.length(),
                "Buffer length should be 18 after appending a long value in big-endian order");

        String expected = "0x0102030405060708090A0000000000080000";
        String actual = buffer.toString();
        assertEquals(expected, actual, () -> String.format("Expected: %s, Actual: %s", expected,
                actual));
    }

    @Test
    @DisplayName("Append Long in Little-Endian when buffer not at capacity")
    void testAppendLong_LittleEndian_NotAtCapacity() {
        ByteBuffer buffer = new ByteBuffer(10);
        buffer.append(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05});

        long value = 8796093022218L;    // 0x8000000000A
        buffer.append(value, ByteOrder.LITTLE_ENDIAN);

        assertEquals(13, buffer.length(),
                "Buffer length should be 13 after appending a long value in big-endian order");

        String expected = "0x01020304050A00000000080000";
        String actual = buffer.toString();
        assertEquals(expected, actual, () -> String.format("Expected: %s, Actual: %s", expected,
                actual));
    }


}