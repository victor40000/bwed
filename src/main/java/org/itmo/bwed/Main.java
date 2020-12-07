package org.itmo.bwed;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        String path = "D:/ИТМО new/теоринф/calgarycorpus/pic";
        String path_test = "D:/ИТМО new/теоринф/calgarycorpus/test";
        /*File folder = new File("D:/ИТМО new/теоринф/calgarycorpus/");
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (!fileEntry.isDirectory()) {
                System.out.println(fileEntry.getName());
                if (fileEntry.getPath().equals("D:\\ИТМО new\\теоринф\\calgarycorpus\\pic")) {
                    continue;
                }
                Encoder.encode(fileEntry.getPath());
            }
        }*/


//        Encoder.encode(path);
//        Encoder.encode(path_test);
        byte[] bytes = new byte[1];
        bytes[0] = Byte.parseByte("-1111111", 2);
        try {
            Files.write(Path.of("/encoded/encoded"), bytes);
        } catch (Exception ex) {

        }

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
