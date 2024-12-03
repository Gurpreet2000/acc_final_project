package com.example.backend.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.example.backend.utils.FileUtils;

public class FrequencyCounter {

    // Extracts a list of individual words from the specified file path
    private List<String> readWordsFromFile(String filePath) {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splitWords = line.toLowerCase().split("[^a-zA-Z0-9]+");
                words.addAll(Arrays.stream(splitWords)
                        .filter(word -> !word.isEmpty())
                        .collect(Collectors.toList()));
            }
        } catch (IOException e) {
            System.err.println("Failed to read file: " + filePath + ": " + e.getMessage());
        }
        return words;
    }

    // Populates a map with the frequency of each word
    private Map<String, Integer> buildFrequencyMap(List<String> wordList) {
        return wordList.stream()
                .collect(Collectors.groupingBy(
                        word -> word,
                        Collectors.summingInt(word -> 1)));
    }

    // Retrieves a sorted list of map entries based on frequency in descending order
    private List<Entry<String, Integer>> getSortedFrequencies(Map<String, Integer> wordFrequency) {
        return wordFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());
    }

    // Displays the specified number of top words by frequency
    private void printTopFrequencies(List<Entry<String, Integer>> sortedEntries, int count) {
        System.out.println("Top " + count + " frequently used words:");
        sortedEntries.stream()
                .limit(count)
                .forEach(entry -> System.out.println(entry.getKey() + " : " + entry.getValue()));
    }

    // Get frequency list for a single file
    public List<Entry<String, Integer>> getList(String path) {
        // Aggregate words from the file
        List<String> wordList = readWordsFromFile(path);

        // Generate word frequency map
        Map<String, Integer> frequencyMap = buildFrequencyMap(wordList);

        // Get sorted list of word frequencies
        return getSortedFrequencies(frequencyMap);
    }

    // Initialize frequency analysis for all files in a directory
    public List<Entry<String, Integer>> init(String directoryPath) {
        List<Entry<String, Integer>> aggregatedList = new ArrayList<>();

        // Use FileUtils to read files and process them
        FileUtils.readFiles(directoryPath, path -> {
            List<Entry<String, Integer>> fileList = getList(path.toString());
            aggregatedList.addAll(fileList);
        });

        // Sort and return the aggregated list
        return getSortedFrequencies(
                aggregatedList.stream()
                        .collect(Collectors.groupingBy(
                                Entry::getKey,
                                Collectors.summingInt(Entry::getValue))));
    }

    public static void main(String[] args) {

        String directoryPath = "./backend/data";
        int topN = 10;

        FrequencyCounter sf = new FrequencyCounter();
        List<Entry<String, Integer>> result = sf.init(directoryPath);

        sf.printTopFrequencies(result, topN);
    }
}