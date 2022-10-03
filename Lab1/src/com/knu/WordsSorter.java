package com.knu;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class WordsSorter {
    public static void main(String[] args) {
        File file = new File("words.txt");
        TreeSet<String> setOfWords = new TreeSet<>(Comparator.comparing(String::length).thenComparing(String::compareTo));
        WordsSorter.readTheWords(file, setOfWords);
        System.out.println(String.join(", ", setOfWords));
    }

    public static void readTheWords(File file, SortedSet<String> treeSet){
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String[] words = scanner.nextLine().split("[\\W\\d]");
                for (String word: words) {
                    if(!word.equals("")){
                        treeSet.add(word.substring(0, Math.min(word.length(), 30)));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
    }
}