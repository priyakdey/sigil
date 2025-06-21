package com.priyakdey.sigil.core;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base64UrlTest {

    @ParameterizedTest(name = "encode::{index}")
    @CsvFileSource(resources = "/base64url.csv", numLinesToSkip = 1,
            useHeadersInDisplayName = true)
    void encode(String input, String expected) {
        String actual = Base64Url.encode(Bytes.fromUTF8String(input));

        assertEquals(expected, actual, () -> String.format("For input %s expected '%s' but got '%s'",
                input, expected, actual));
    }
}