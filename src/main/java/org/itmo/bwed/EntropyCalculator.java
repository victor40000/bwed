package org.itmo.bwed;

import java.util.*;

public class EntropyCalculator {

    public static void printEntropy(char[] chars) {
        System.out.println("Entropy H(X): " + getEntropy(chars));
        System.out.println("Entropy H(X|X): " + getConditionalEntropy(chars, 1));
        System.out.println("Entropy H(X|XX): " + getSecondOrderConditionalEntropy(chars));
    }

    public static double getEntropy(char[] chars) {
        double result = 0;
        int[] numberOfUsages = new int[256];
        Arrays.fill(numberOfUsages, 0);
        for (char c : chars) {
            numberOfUsages[c]++;
        }
        double chance = 0;
        for (int i : numberOfUsages) {
            chance = (double) i / chars.length;
            result += chance * log(chance);
        }
        return -result;
    }

    public static double getConditionalEntropy(char[] chars, int conditionLen) {
        double result = 0;
        Map<String, Integer> numberOfSequenceUsages = new HashMap<>();
        String charsStr = new String(chars);
        String currString;
        for (int i = 0; i < chars.length - conditionLen; i++) {
            currString = charsStr.substring(i, i + conditionLen + 1);
            if (numberOfSequenceUsages.get(currString) == null) {
                numberOfSequenceUsages.put(currString, 1);
            } else {
                numberOfSequenceUsages.put(currString, numberOfSequenceUsages.get(currString) + 1);
            }
        }
        int[] numberOfUsages = new int[256];
        Arrays.fill(numberOfUsages, 0);
        for (char c : chars) {
            numberOfUsages[c]++;
        }
        List<String> combinations = new ArrayList<>();
        getAllCombinations("", conditionLen - 1, combinations);
        double chance = 0;
        double conditionalEntropy;
        double conditionalChance;
        int conditionalUsages = 0;
        for (int i = 0; i < numberOfUsages.length; i++) {
            if (numberOfUsages[i] == 0) {
                continue;
            }
            chance = (double) numberOfUsages[i] / chars.length;
            conditionalEntropy = 0;
            conditionalChance = 0;
            for (String combination : combinations) {

                String currStr = combination + (char) i;
                conditionalUsages = numberOfSequenceUsages.get(currStr) == null ? 0 : numberOfSequenceUsages.get(currStr);
                conditionalChance = (double) conditionalUsages / numberOfUsages[i];
                conditionalEntropy += conditionalChance * log(conditionalChance);
            }
            result += chance * conditionalEntropy;
        }
        return -result;
    }

    public static double getSecondOrderConditionalEntropy(char[] chars) {
        double result = 0;
        Map<String, Integer> numberOf1SequenceUsages = new HashMap<>();
        Map<String, Integer> numberOf2SequenceUsages = new HashMap<>();
        Map<String, Integer> numberOf3SequenceUsages = new HashMap<>();
        String charsStr = new String(chars);
        for (int i = 0; i < chars.length - 2; i++) {
            String currString1 = charsStr.substring(i, i + 1);
            String currString2 = charsStr.substring(i, i + 2);
            String currString3 = charsStr.substring(i, i + 3);
            numberOf1SequenceUsages.merge(currString1, 1, Integer::sum);
            numberOf2SequenceUsages.merge(currString2, 1, Integer::sum);
            numberOf3SequenceUsages.merge(currString3, 1, Integer::sum);
        }

        for (int i = 0; i < 256; i++) {
            char ic = (char) i;
            if (numberOf1SequenceUsages.get("" + ic) == null) {
                continue;
            }
            int numberOf1Usg = numberOf1SequenceUsages.get("" + ic);
            double sequence1Sum = 0;
            double chance1 = (double) numberOf1Usg / chars.length;

            for (int j = 0; j < 256; j++) {
                char jc = (char) j;
                if (numberOf2SequenceUsages.get("" + jc + ic) == null) {
                    continue;
                }
                int numberOf2Usg = numberOf2SequenceUsages.get("" + jc + ic);
                double sequence2Sum = 0;
                double chance2 = (double) numberOf2Usg / numberOf1Usg;

                for (int k = 0; k < 256; k++) {
                    char kc = (char) k;
                    if (numberOf3SequenceUsages.get("" + kc + jc + ic) == null) {
                        continue;
                    }
                    int numberOf3Usg = numberOf3SequenceUsages.get("" + kc + jc + ic);
                    double chance3 = (double) numberOf3Usg / numberOf2Usg;
                    sequence2Sum += chance3 * log(chance3);
                }

                sequence1Sum += sequence2Sum * chance2;
            }

            result += sequence1Sum * chance1;
        }
        return -result;
    }

    public static void getAllCombinations(String str, int n, List<String> result) {
        for (int i = 0; i < 256; i++) {
            if (n == 0) {
                result.add(str + (char) i);
                continue;
            }
            getAllCombinations(str + (char) i, n - 1, result);
        }
    }

    private static double log(double val) {
        return val == 0 ? 0 : Math.log(val) / Math.log(2);
    }
}
