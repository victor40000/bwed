package org.itmo.bwed;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class Writer {

    public static void writeToFile(String binary, String path) {
        byte[] bytes = new byte[binary.length() / 8];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (Integer.parseInt(binary.substring(i * 8, (i + 1) * 8), 2));
        }
        try {
            Files.write(Path.of(path), bytes);
        } catch (Exception ex) {

        }
    }

    public static void writeToFile(char[] target, String path) {
        StringBuilder buffer = new StringBuilder();
        for (char c: target) {
            buffer.append("00000000".substring(Integer.toBinaryString(c).length()));
            buffer.append(Integer.toBinaryString(c));
        }
        writeToFile(buffer.toString(), path);
    }

}
