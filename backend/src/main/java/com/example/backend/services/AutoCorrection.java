package com.example.backend.services;

import com.example.backend.utils.FileUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner; // Import for Scanner

public class AutoCorrection {

    private final List<String> dictionary = new ArrayList<>(); // To store all words from CSV files

    // Method to compute the edit distance between two words using dynamic
    // programming
    public static int computeEditDistance(String word1, String word2) {
        int len1 = word1.length();
        int len2 = word2.length();
        int[][] dp = new int[len1 + 1][len2 + 1];

        // Fill the DP table
        for (int i = 0; i <= len1; i++) {
            for (int j = 0; j <= len2; j++) {
                if (i == 0) {
                    dp[i][j] = j; // Insert all characters of word2
                } else if (j == 0) {
                    dp[i][j] = i; // Remove all characters of word1
                } else if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // No operation needed
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j],
                            Math.min(dp[i][j - 1], dp[i - 1][j - 1])); // Min of insert, remove, replace
                }
            }
        }
        return dp[len1][len2];
    }

    // Method to build the dictionary from CSV files
    public void buildDictionary() {
        String directoryPath = "./data"; // Directory containing CSV files
        System.out.println("Building the dictionary from CSV files...");

        try {
            FileUtils.readFiles("./data", path -> {
                try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(path.toString())).build()) {
                    String[] row;
                    while ((row = csvReader.readNext()) != null) {
                        for (String cell : row) {
                            String[] words = cell.toLowerCase().split("\\W+"); // Normalize content into words
                            for (String word : words) {
                                if (!word.isEmpty() && !dictionary.contains(word)) {
                                    dictionary.add(word);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Dictionary built with " + dictionary.size() + " unique words.");
    }

    // Method to find the closest match for a given word
    public String findClosestWord(String misspelledWord) {
        String closestWord = null;
        int minDistance = Integer.MAX_VALUE;

        for (String word : dictionary) {
            int distance = computeEditDistance(misspelledWord.toLowerCase(), word);
            if (distance < minDistance) {
                minDistance = distance;
                closestWord = word;
            }
        }

        return "Closest match: \"" + closestWord + "\", Edit Distance: " + minDistance;
    }

    // Main method to test the functionality
    public static void main(String[] args) {
        AutoCorrection autoCorrection = new AutoCorrection();
        autoCorrection.buildDictionary();

        System.out.print("Enter a misspelled word: ");
        try (Scanner scanner = new Scanner(System.in)) { // Ensure Scanner is imported
            String misspelledWord = scanner.nextLine();
            String result = autoCorrection.findClosestWord(misspelledWord);
            System.out.println(result);
        }
    }
}
