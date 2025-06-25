package com.priyakdey.sigil.common;

import com.priyakdey.sigil.core.Hex;
import org.junit.jupiter.params.provider.Arguments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CAVSParser {

    private final File file;

    public CAVSParser(String filePath) {
        URL resource = getClass().getClassLoader().getResource(filePath);
        if (resource == null) {
            throw new IllegalArgumentException("File not found on classpath: " + filePath);
        }

        try {
            this.file = new File(resource.toURI());
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Illegal URI Syntax: ", ex);
        }
    }

    public Stream<Arguments> parse() {
        List<String> lines = readLines(file);
        List<Arguments> arguments = new ArrayList<>();

        // skip the line which is probably digest size: [L = 32]
        for (int i = 1; i < lines.size(); i += 3) {
            String lengthLine = lines.get(i);
            String msgLine = lines.get(i + 1);
            String mdLine = lines.get(i + 2);

            if (!lengthLine.startsWith("Len") || !msgLine.startsWith("Msg") || !mdLine.startsWith("MD")) {
                throw new IllegalArgumentException("Invalid test vector format near line " + (i + 1));
            }

            int bitLength = Integer.parseInt(lengthLine.split("=")[1].trim());
            String hexMsg = msgLine.split("=")[1].trim();
            String hexDigest = mdLine.split("=")[1].trim();

            byte[] msgBytes;
            if (bitLength == 0) {
                msgBytes = new byte[0];
            } else {
                msgBytes = Hex.to("0x" + hexMsg.toUpperCase());

                int byteLength = bitLength / 8;
                if (msgBytes.length > byteLength) {
                    byte[] trimmed = new byte[byteLength];
                    System.arraycopy(msgBytes, 0, trimmed, 0, byteLength);
                    msgBytes = trimmed;
                }
            }

            arguments.add(Arguments.of("Len = " + bitLength, msgBytes, "0x" + hexDigest.toUpperCase()));
        }

        return arguments.stream();
    }

    private List<String> readLines(File file) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    lines.add(line);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error reading file: " + file, ex);
        }
        return lines;
    }
}
