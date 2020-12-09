package org.itmo.bwed;

import org.apache.commons.lang.ArrayUtils;
import org.itmo.bwed.exceptions.IneffectiveTransromationException;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Encoder {

    private final static String ZERO_STRING_24 = "000000000000000000000000";
    private final static String ZERO_STRING_8 = "00000000";
    private final static String ONE_STRING_8 = "11111111";
    private static int stringPos = 0;

    public static String encode(char[] fileCh, boolean memEffective) {
        Instant start;
        char[] lastChars;
        BWElement[] cycledStrings;
        start = Instant.now();
        if (memEffective) {
            System.out.println("Using memory effective algorithm...");
            cycledStrings = getBWTransformLight(fileCh);
            lastChars = getLastCharsSortFullStringsOptimized(cycledStrings);
        } else {
            try {
                Map<CharArray, Integer> positions = new HashMap<>();
                cycledStrings = getBWTransform(fileCh, positions);
                lastChars = getLastCharsSortBeginnings(cycledStrings, positions);
            } catch (IneffectiveTransromationException ex) {
                System.out.println("Using memory effective algorithm...");
                cycledStrings = getBWTransformLight(fileCh);
                lastChars = getLastCharsSortFullStringsOptimized(cycledStrings);
            }
        }
        System.out.println(Duration.between(start, Instant.now()).toMillis() + "ms spent for BW transform and sorting");

        //count RLE
//        System.out.println("source len: " + lastChars.length);
//        System.out.println("source string number: " + stringPos);
//        getRleStatistics(lastChars);
//        getBookStampStatistics(lastChars);
        start = Instant.now();
        String result = encodeBookStamp(lastChars, stringPos);
        System.out.println(Duration.between(start, Instant.now()).toMillis() + "ms spent for encoding with Bookstamp algorithm");
        System.out.println("source len: " + lastChars.length);
        getRleStatistics(lastChars);
        System.out.println("Bookstamp len: " + result.length() / 8);
        System.out.println("Cost (bit/char): " + (double) result.length() / lastChars.length);

        System.out.println();
        return result;
    }


    private static BWElement[] getBWTransformLight(char[] file) {
        String fileStr = new String(file);
        BWElement[] cycledStrings = new BWElement[file.length];
        BWElement elem = new BWElement(file[0], file[file.length - 1], 0, new CharArray(), fileStr);
        cycledStrings[0] = elem;
        for (int i = 1; i < file.length; i++) {
            cycledStrings[i] = new BWElement(file[i], file[i - 1], i, new CharArray(), fileStr);
        }

        return cycledStrings;
    }

    private static BWElement[] getBWTransform(char[] file, Map<CharArray, Integer> positions) {
        String fileStr = new String(file);
        BWElement[] cycledStrings = new BWElement[file.length];
        Set<CharArray> usedBeginnings = new HashSet<>();
        BWElement elem = new BWElement(file[0], file[file.length - 1], 0, new CharArray(new char[]{file[0]}), fileStr);
        usedBeginnings.add(new CharArray(new char[]{file[0]}));
        positions.put(new CharArray(new char[]{file[0]}), 0);
        cycledStrings[0] = elem;
        int counter;
        char[] tempBeginning = new char[]{};
        int pointerToCurrBeginning = 0;
        for (int i = 1; i < file.length; i++) {
            CharArray currBeginning;
            counter = 1;
            if (tempBeginning.length - pointerToCurrBeginning > 2) {
                currBeginning = new CharArray(getSubArray(tempBeginning, pointerToCurrBeginning, tempBeginning.length));
                pointerToCurrBeginning++;
                counter = currBeginning.size();
            } else {
                currBeginning = new CharArray(new char[]{file[i]});
            }

            while (usedBeginnings.contains(currBeginning)) {
                currBeginning.add(file[(i + counter) % file.length]);
                counter++;
            }
            if ((counter > 10 && tempBeginning.length - pointerToCurrBeginning <= 2) ||
                    (counter - tempBeginning.length - pointerToCurrBeginning > 30)) {
                tempBeginning = getSubArray(currBeginning.getArray(), 1, currBeginning.size() - 1);
                pointerToCurrBeginning = 0;
            }
            if (counter != 1) {
                CharArray lastFoundBeginning = currBeginning.getSubArray(0, currBeginning.size() - 1);
                Integer lastPos = positions.get(lastFoundBeginning);
                if (lastPos != null) {
                    BWElement lstElem = cycledStrings[lastPos];
                    lstElem.getBeginning().add(file[(lastPos + counter - 1) % file.length]);
                    int innerCounter = 1;
                    while (lstElem.getBeginning().equals(currBeginning)) {
                        usedBeginnings.add(new CharArray(lstElem.getBeginning().getArray()));
                        lstElem.getBeginning().add(file[(lastPos + counter - 1 + innerCounter) % file.length]);
                        currBeginning.add(file[(i + counter + innerCounter - 1) % file.length]);
                        innerCounter++;
                        if (innerCounter > 2000) {
                            throw new IneffectiveTransromationException();
                        }
                    }
                    positions.remove(lastFoundBeginning);
                    positions.put(new CharArray(lstElem.getBeginning().getArray()), lastPos);
                    usedBeginnings.add(new CharArray(lstElem.getBeginning().getArray()));

//                    System.out.println(Arrays.toString(lastFoundBeginning.getArray()) + " " + Arrays.toString(currBeginning.getArray()));
                }
            }
            usedBeginnings.add(currBeginning);
            positions.put(currBeginning, i);
            cycledStrings[i] = new BWElement(file[i], file[i - 1], i, new CharArray(currBeginning.getArray()), fileStr);
//            System.out.println(i);
        }

        return cycledStrings;
    }


    private static char[] getLastCharsSortBeginnings(BWElement[] cycledStrings, Map<CharArray, Integer> positions) {
        char[] lastChars = new char[cycledStrings.length];
        List<String> str = new ArrayList<>();
        for (BWElement cycledString : cycledStrings) {
            str.add(String.valueOf(cycledString.getBeginning().getArray()));
        }
        str.sort(Comparator.comparing(String::toString));
        for (int i = 0; i < cycledStrings.length; i++) {
            lastChars[i] = cycledStrings[positions.get(new CharArray(str.get(i).toCharArray()))].getLast();
            if (cycledStrings[positions.get(new CharArray(str.get(i).toCharArray()))].getShift() == 0) {
                stringPos = i;
            }
        }
        return lastChars;
    }

    private static char[] getLastCharsSortFullStrings(BWElement[] cycledStrings) {
        char[] lastChars = new char[cycledStrings.length];
        List<BWElement> bwElements = new ArrayList<>(Arrays.asList(cycledStrings));
        bwElements.sort(Comparator.comparing(BWElement::getFullString));
        for (int i = 0; i < bwElements.size(); i++) {
            lastChars[i] = bwElements.get(i).getLast();
            if (bwElements.get(i).getShift() == 0) {
                stringPos = i;
            }
        }
        return lastChars;
    }

    private static char[] getLastCharsSortFullStringsOptimized(BWElement[] cycledStrings) {
        char[] lastChars = new char[cycledStrings.length];
        List<BWElement> bwElements = new ArrayList<>(Arrays.asList(cycledStrings));
        Collections.sort(bwElements);
        for (int i = 0; i < bwElements.size(); i++) {
            lastChars[i] = bwElements.get(i).getLast();
            if (bwElements.get(i).getShift() == 0) {
                stringPos = i;
            }
        }
        return lastChars;
    }

    private static void getRleStatistics(char[] lastChars) {
        ArrayList<Integer> rleSeries = getRleSeries(lastChars);
        rleSeries.sort(Comparator.naturalOrder());
        IntSummaryStatistics statistics = rleSeries.stream().mapToInt(n -> n).summaryStatistics();
//        System.out.println("Count: " + statistics.getCount());
//        System.out.println("Min: " + statistics.getMin());
//        System.out.println("Max: " + statistics.getMax());
//        System.out.println("Avg: " + statistics.getAverage());
//        System.out.println("Median: " + (rleSeries.size() % 2 == 0 ? (rleSeries.get(rleSeries.size() / 2) - rleSeries.get(rleSeries.size() / 2 - 1)) / 2 : rleSeries.get((int) rleSeries.size() / 2)));
        int len = 1;
        int finalLen = 0;
        int finalLenBits = 0;
        for (int i = 0; i < rleSeries.size(); i++) {
            finalLen = finalLen + (1 + String.valueOf(len).length());

            if (len == 1) {
                finalLenBits += 9;
            } else {
                finalLenBits += 9 + (Integer.toBinaryString(len).length() - 1) + (Integer.toBinaryString(len).length() - 1);
            }

            if (rleSeries.get(i) > len) {
//                System.out.println("Non-single sequence: " + rleSeries.get(i) + " at index: " + i);
                len = rleSeries.get(i);
            }
        }
//        System.out.println("Length of source sequence: " + lastChars.length);
//        System.out.println(lastChars.length);
//        System.out.println("Length of RLE-coded sequence: " + finalLen);
//        System.out.println("Length of source sequence in bits: " + cycledStrings.length * 8);
//        System.out.println("Length of encoded RLE-coded sequence: " + finalLenBits / 8);
        System.out.println("RLE len: " + finalLenBits / 8);
    }


    private static ArrayList<Integer> getRleSeries(char[] array) {
        ArrayList<Integer> rleSeries = new ArrayList<>();
        char curr;
        char last = array[0];
        int counter = 1;
        for (int i = 1; i < array.length; i++) {
            curr = array[i];
            if (curr == last) {
                counter++;
            } else {
                rleSeries.add(counter);
                counter = 1;
            }
            last = curr;
        }
        return rleSeries;
    }


    private static String encodeBookStamp(char[] array, int sourcePos) {
        StringBuilder buffer = new StringBuilder();
        String sourcePosStr = Integer.toBinaryString(sourcePos);
        buffer.append(ZERO_STRING_24.substring(sourcePosStr.length()));
        buffer.append(sourcePosStr);
        ArrayList<Integer> bookStampSeries = getBookStampSeries(array);
        for (Integer elem : bookStampSeries) {
            buffer.append(getZeroBasedMonoCode(elem));
        }
        if (buffer.length() % 8 != 0) {
            buffer.append(ONE_STRING_8.substring(buffer.length() % 8));
        }
        return buffer.toString();
    }


    private static void getBookStampStatistics(char[] array) {
        ArrayList<Integer> bookStampSeries = getBookStampSeries(array);
        int finalLenBits = 0;
        for (Integer bookStampSery : bookStampSeries) {
            if (bookStampSery == 0 || bookStampSery == 1) {
                finalLenBits += 2;
                continue;
            }
            finalLenBits += (Integer.toBinaryString(bookStampSery).length()) +
                    (Integer.toBinaryString(bookStampSery).length() - 1);
        }
//        System.out.println(array.length);
        System.out.println("Bookstamp len: " + finalLenBits / 8);
        finalLenBits = 0;
        for (Integer bookStampSery : bookStampSeries) {
            if (bookStampSery == 0) {
                finalLenBits += 1;
                continue;
            }
            if (bookStampSery == 1) {
                finalLenBits += 2;
                continue;
            }
            finalLenBits += (Integer.toBinaryString(bookStampSery).length()) +
                    (Integer.toBinaryString(bookStampSery).length());
        }
        System.out.println("0-based Bookstamp len: " + finalLenBits / 8);
    }


    private static ArrayList<Integer> getBookStampSeries(char[] array) {
        ArrayList<Integer> result = new ArrayList<>();
        char[] asci_array = new char[256];
        for (int i = 0; i < 256; i++) {
            asci_array[i] = (char) i;
        }
        char[] sequence = ArrayUtils.addAll(asci_array, array);
        char curr;
        boolean[] used = new boolean[256];
        Arrays.fill(used, false);
        for (int i = 256; i < sequence.length; i++) {
            curr = sequence[i];
            int count = 0;
            int counter = 1;
            while (sequence[i - counter] != curr) {
                if (!used[sequence[i - counter]]) {
                    used[sequence[i - counter]] = true;
                    count++;
                }
                counter++;
            }
            result.add(count);
            Arrays.fill(used, false);
        }
        return result;
    }

    private static String getMonoCode(int val) {
        if (val == 0) {
            return "00";
        }
        if (val == 1) {
            return "01";
        }
        StringBuilder str = new StringBuilder(ONE_STRING_8.substring(8 - Integer.toBinaryString(val).length() + 1));
        str.append("0");
        str.append(Integer.toBinaryString(val).substring(1));
        return str.toString();
    }

    private static String getZeroBasedMonoCode(int val) {
        if (val == 0) {
            return "0";
        }
        if (val == 1) {
            return "10";
        }
        StringBuilder str = new StringBuilder(ONE_STRING_8.substring(8 - Integer.toBinaryString(val).length()));
        str.append("0");
        str.append(Integer.toBinaryString(val).substring(1));
        return str.toString();
    }

    public static char[] getSubArray(char[] source, int begin, int end) {
        char[] result = new char[end - begin];
        System.arraycopy(source, begin, result, 0, end - begin);
        return result;
    }

    /*private static BWElement[] getBWTransformOld(char[] file, Map<CharArray, Integer> positions) {
        BWElement[] cycledStrings = new BWElement[file.length];
        Set<CharArray> usedBeginnings = new HashSet<>();
        BWElement elem = new BWElement(file[0], file[file.length - 1], 0, new CharArray(new char[]{file[0]}));
        usedBeginnings.add(new CharArray(new char[]{file[0]}));
        positions.put(new CharArray(new char[]{file[0]}), 0);
        cycledStrings[0] = elem;
        int counter;
        char[] tempBeginning = new char[]{};
        int pointerToCurrBeginning = 0;
        for (int i = 1; i < file.length; i++) {
            elem = new BWElement(file[i], file[i - 1], i, new CharArray(new char[]{file[i]}));
            cycledStrings[i] = elem;
            CharArray currBeginning = new CharArray(new char[]{file[i]});
            counter = 1;
            while (usedBeginnings.contains(currBeginning)) {
                currBeginning.add(file[(i + counter) % file.length]);
                counter++;
            }
            if (counter != 1) {
                CharArray lastFoundBeginning = currBeginning.getSubArray(0, currBeginning.size() - 1);
                Integer lastPos = positions.get(lastFoundBeginning);
                if (lastPos != null) {
                    BWElement lstElem = cycledStrings[lastPos];
                    lstElem.getBeginning().add(file[(lastPos + counter - 1) % file.length]);
                    int innerCounter = 1;
                    while (lstElem.getBeginning().equals(currBeginning)) {
                        usedBeginnings.add(new CharArray(lstElem.getBeginning().getArray()));
                        lstElem.getBeginning().add(file[(lastPos + counter - 1 + innerCounter) % file.length]);
                        currBeginning.add(file[(i + counter + innerCounter - 1) % file.length]);
                        innerCounter++;
                    }
                    positions.remove(lastFoundBeginning);
                    positions.put(new CharArray(lstElem.getBeginning().getArray()), lastPos);
                    usedBeginnings.add(new CharArray(lstElem.getBeginning().getArray()));

//                    System.out.println(Arrays.toString(lastFoundBeginning.getArray()) + " " + Arrays.toString(currBeginning.getArray()));
                }
            }
            usedBeginnings.add(currBeginning);
            positions.put(currBeginning, i);
            cycledStrings[i] = new BWElement(file[i], file[i - 1], i, new CharArray(currBeginning.getArray()));
        }

        return cycledStrings;
    }*/
}
