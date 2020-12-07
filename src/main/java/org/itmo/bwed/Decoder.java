package org.itmo.bwed;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Decoder {

    public static void decode(File file) {
        String path = file.getPath();
        String source = Reader.readFileToBinaryString(path);
        boolean isBookStampUsed = source.charAt(0) == '1';
        int sourcePos = Integer.parseInt(source.substring(1, 25), 2);
        List<Integer> bookStampSeries = decodeBinaryString(source.substring(25));
        char[] lastChars = getLastChars(bookStampSeries);
        char[] sourceString = getSource(lastChars, sourcePos);
        writeToFile(sourceString, Main.DECODED_PATH + file.getName() + "_decoded");
    }

    private static List<Integer> decodeBinaryString(String source) {
        List<Integer> result = new ArrayList<>();
        char[] chars = source.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '0') {
                result.add(0);
            } else if (chars[i] == '1' && chars[i + 1] == '0') {
                result.add(1);
                i++;
            } else {
                int counter = 1;
                while (chars[i + counter] == '1') {
                    counter++;
                    if (i + counter >= chars.length) {
                        return result;
                    }
                }
                result.add(Integer.parseInt("1" + source.substring(i + counter + 1, i + counter + counter), 2));
                i += 2 * counter - 1;
            }
        }

        return result;
    }

    private static char[] getLastChars(List<Integer> bookStampSeries) {
        char[] result = new char[bookStampSeries.size() + 256];
        for (int i = 0; i < 256; i++) {
            result[i] = (char) i;
        }
        boolean[] used = new boolean[256];
        Arrays.fill(used, false);
        for (int i = 256; i < result.length; i++) {
            /*if (bookStampSeries.get(i - 256) == 0) {
                result[i] = result[i - 1];
            }*/
            int counter = -1;
            int j = 1;
            while (counter != bookStampSeries.get(i - 256)) {
                if (!used[result[i - j]]) {
                    used[result[i - j]] = true;
                    counter++;
                }
                j++;
            }
            result[i] = result[i - j + 1];
            Arrays.fill(used, false);
        }
        return getSubArray(result, 256, bookStampSeries.size() + 256);
    }

    private static char[] getSource(char[] lastChars, int sourcePos) {
        char[] firstChars = Arrays.copyOf(lastChars, lastChars.length);
        Arrays.sort(firstChars);
        char[] result = new char[lastChars.length];
        int currPos = sourcePos;
        int counter = 0;
        while (counter < lastChars.length) {
            result[counter] = firstChars[currPos];
            int targetNumber = 0;
            char target = firstChars[currPos];
            int i = 0;
            while (currPos - i >= 0) {
                if (target == firstChars[currPos - i]) {
                    targetNumber++;
                } else {
                    break;
                }
                i++;
            }
            i = 0;
            while (targetNumber != 0) {
                if (lastChars[i] == target) {
                    targetNumber--;
                }
                i++;
            }
//            firstChars[currPos] = 256;
//            lastChars[currPos] = 256;
            currPos = i - 1;
            counter++;
        }
        return result;
    }

    public static void writeToFile(char[] target, String path) {
        StringBuilder buffer = new StringBuilder();
        for (char c: target) {
            buffer.append("00000000".substring(Integer.toBinaryString(c).length()));
            buffer.append(Integer.toBinaryString(c));
        }
        Writer.writeToFile(buffer.toString(), path);
    }

    public static char[] getSubArray(char[] source, int begin, int end) {
        char[] result = new char[end - begin];
        System.arraycopy(source, begin, result, 0, end - begin);
        return result;
    }
}
