package com.example.backend.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class SearchFrequency {

    // Extracts a list of individual words from the specified file path
    private static List<String> readWordsFromFile(String filePath) {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splitWords = line.toLowerCase().split("[^a-zA-Z0-9]+");
                Collections.addAll(words, splitWords);
            }
        } catch (IOException e) {
            System.err.println("Failed to read file: " + e.getMessage());
        }
        words.removeIf(String::isEmpty); // Remove any empty entries
        return words;
    }

    // Populates a map with the frequency of each word
    private static Map<String, Integer> buildFrequencyMap(List<String> wordList) {
        Map<String, Integer> wordFrequency = new HashMap<>();
        wordList.forEach(word -> wordFrequency.merge(word, 1, Integer::sum));
        return wordFrequency;
    }

    // Retrieves a sorted list of map entries based on frequency in descending order
    private static List<Entry<String, Integer>> getSortedFrequencies(Map<String, Integer> wordFrequency) {
        return wordFrequency.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .toList();
    }

    // Displays the specified number of top words by frequency
    private static void printTopFrequencies(List<Entry<String, Integer>> sortedEntries, int count) {
        System.out.println("Top " + count + " frequently used words:");
        sortedEntries.stream().limit(count)
                .forEach(entry -> System.out.println(entry.getKey() + " : " + entry.getValue()));
    }

    public List<Entry<String, Integer>> getList(String path) {
        // Aggregate all words from each file
        List<String> wordList = new ArrayList<>();
        wordList.addAll(readWordsFromFile(path));

        // Generate word frequency map
        Map<String, Integer> frequencyMap = buildFrequencyMap(wordList);

        // Get sorted list of word frequencies
        List<Entry<String, Integer>> sortedFrequencies = getSortedFrequencies(frequencyMap);

        return sortedFrequencies;
    }

    public void addHistory(String path, String word) {
        File file = new File(path);
        if (!file.exists())
            file.getParentFile().mkdirs();

        try (FileWriter fileWriter = new FileWriter(path, true)) {
            fileWriter.append(word + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        SearchFrequency sf = new SearchFrequency();
        // Define input files
        String[] filePaths = { "./backend/data/google-drive.csv" };

        int topN = 10;
        Arrays.stream(filePaths).forEach(path -> printTopFrequencies(sf.getList(path), topN));

    }
}
