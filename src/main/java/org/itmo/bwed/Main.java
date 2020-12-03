package org.itmo.bwed;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        String path = "D:/ИТМО new/теоринф/calgarycorpus/book2";
        Instant start = Instant.now();

        char[] file = Reader.readFileToCharArray(path);
        System.out.println(Duration.between(start, Instant.now()).toMillis());


        //get all cycled strings
        start = Instant.now();
        Map<CharArray, Integer> positions = new HashMap<>();
        BWElement[] cycledStrings = getBWTransform(file, positions);
        System.out.println(Duration.between(start, Instant.now()).toMillis());


        //sort all strings
        start = Instant.now();
        char[] lastChars = new char[cycledStrings.length];
        {
            List<String> str = new ArrayList<>();
            for (int i = 0; i < file.length; i++) {
                str.add(String.valueOf(cycledStrings[i].getBeginning().getArray()));
            }
            str.sort(Comparator.comparing(String::toString));
            System.out.println(Duration.between(start, Instant.now()).toMillis());
            for (int i = 0; i < file.length; i++) {
                lastChars[i] = cycledStrings[positions.get(new CharArray(str.get(i).toCharArray()))].getLast();
            }
        }
        System.out.println(Duration.between(start, Instant.now()).toMillis());

        //count RLE
        ArrayList<Integer> rleSeries = getRleSeries(lastChars);
        IntSummaryStatistics statistics = rleSeries.stream().mapToInt(n -> n).summaryStatistics();
        System.out.println("Count: " + statistics.getCount());
        System.out.println("Min: " + statistics.getMin());
        System.out.println("Max: " + statistics.getMax());
        System.out.println("Avg: " + statistics.getAverage());
        System.out.println("Median: " + (rleSeries.size() % 2 == 0 ? (rleSeries.get(rleSeries.size() / 2) - rleSeries.get(rleSeries.size() / 2 - 1)) / 2 : rleSeries.get((int) rleSeries.size() / 2)));

        /*start = Instant.now();
        for (int i = 0; i < file.length; i++) {
            System.out.println(cycledStrings[positions.get(new CharArray(str.get(i).toCharArray()))].getFirst() + " " + cycledStrings[positions.get(new CharArray(str.get(i).toCharArray()))].getLast() );
        }
        System.out.println(Duration.between(start, Instant.now()).toMillis());*/


        /*List<Integer> a = Reader.readFileInt(path);
        System.out.println(Duration.between(start, Instant.now()).toMillis());
        start = Instant.now();


        List<Character> c = Reader.readFileChar(path);
        System.out.println(Duration.between(start, Instant.now()).toMillis());
        start = Instant.now();*/


    }

    private static BWElement[] getBWTransform(char[] file, Map<CharArray, Integer> positions) {
        BWElement[] cycledStrings = new BWElement[file.length];
        Set<CharArray> usedBeginnings = new HashSet<>();
        BWElement elem = new BWElement(file[0], file[file.length - 1], 0, new CharArray(new char[]{file[0]}));
        usedBeginnings.add(new CharArray(new char[]{file[0]}));
        positions.put(new CharArray(new char[]{file[0]}), 0);
        cycledStrings[0] = elem;
        int counter;
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

                    System.out.println(Arrays.toString(lastFoundBeginning.getArray()) + " " + Arrays.toString(currBeginning.getArray()));
                }
            }
            usedBeginnings.add(currBeginning);
            positions.put(currBeginning, i);
            cycledStrings[i] = new BWElement(file[i], file[i - 1], i, new CharArray(currBeginning.getArray()));
        }

        return cycledStrings;
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

    private static char[] getSubArray(char[] source, int n, int shift) {
        char[] result = new char[shift];
        if (n + shift - n >= 0) {
            System.arraycopy(source, n, result, 0, n + shift - n);
        }
        return result;
    }

}
