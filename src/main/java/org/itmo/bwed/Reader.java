package org.itmo.bwed;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Reader {

    public static ArrayList<Integer> readFileInt(String path) {
        try {
            ArrayList<Integer> result = new ArrayList<>();
            FileReader fr = new FileReader(path);
            int c = fr.read();
            while (c != -1) {
                result.add(c);
                c = fr.read();
            }
            return result;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static ArrayList<Character> readFileChar(String path) {
        try {
            ArrayList<Character> result = new ArrayList<>();
            FileReader fr = new FileReader(path);
            int c = fr.read();
            while (c != -1) {
                result.add((char) c);
                c = fr.read();
            }
            return result;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static char[] readFileToCharArray(String path) {
        try {
            byte[] bytes = Files.readAllBytes(Path.of(path));
            char[] chars = new char[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                chars[i] = (char) (bytes[i] & 0xFF);
            }
            return chars;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String readFileToBinaryString(String path) {
        char [] file = readFileToCharArray(path);
        StringBuilder result = new StringBuilder();
        for (char c : file) {
            result.append("00000000".substring(Integer.toBinaryString(c).length()));
            result.append(Integer.toBinaryString(c));
        }
        return result.toString();
    }
}
