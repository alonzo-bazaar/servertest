package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadingAFuckingFile {
    public static void main(String[] args) {
        try {
            File file = new File(ReadingAFuckingFile.class.getClassLoader().getResource("file.txt").getFile());
            System.out.println(file.getAbsolutePath());
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {
                System.out.println(scan.nextLine());
            }
        } catch (Throwable ignored) {}
    }
}
