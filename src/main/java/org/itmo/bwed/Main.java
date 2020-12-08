package org.itmo.bwed;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Main {

    public static final String TO_ENCODE = "to_encode/";
    public static final String ENCODED_PATH = "encoded/";
    public static final String TO_DECODE = "to_decode/";
    public static final String DECODED_PATH = "decoded/";

    public static void main(String[] args) {
        boolean memEff = false;
        for (String arg: args) {
            if (arg.equals("-memeff")) {
                memEff = true;
                break;
            }
        }
        File folder = new File(TO_ENCODE);
        System.out.println("Encoding:");
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (!fileEntry.isDirectory()) {
                System.out.println(fileEntry.getName());
                Encoder.encode(fileEntry, memEff);
            }
        }

//        Encoder.encode(test);
        System.out.println("Decoding:");
        folder = new File(TO_DECODE);
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (!fileEntry.isDirectory()) {
                System.out.println(fileEntry.getName());
                Decoder.decode(fileEntry);
            }
        }
//        Decoder.decode(new File(ENCODED_PATH));

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

}
