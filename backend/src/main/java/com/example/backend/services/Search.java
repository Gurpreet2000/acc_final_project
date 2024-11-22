package com.example.backend.services;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileReader;
import java.nio.file.*;
import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEndOfWord = false;
    Map<String, List<Integer>> documentPositions = new HashMap<>(); // Document -> List of Line Numbers
}

class InvertedIndexTrie {
    private TrieNode root = new TrieNode();

    public void addWord(String word, String document, int lineNumber) {
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            current.children.putIfAbsent(c, new TrieNode());
            current = current.children.get(c);
        }
        current.isEndOfWord = true;
        current.documentPositions.putIfAbsent(document, new ArrayList<>());
        current.documentPositions.get(document).add(lineNumber);
    }

    public Map<String, List<Integer>> search(String word) {
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            if (!current.children.containsKey(c)) {
                return new HashMap<>();
            }
            current = current.children.get(c);
        }
        return current.isEndOfWord ? current.documentPositions : new HashMap<>();
    }
}

public class Search {
    public static void main(String[] args) {
        String directoryPath = "./data"; // Directory containing CSV files
        InvertedIndexTrie invertedIndex = new InvertedIndexTrie();
        Map<String, String[]> fileHeaders = new HashMap<>();
        Map<String, List<String[]>> fileRows = new HashMap<>();

        System.out.println("Building the inverted index Trie. Please wait...");

        try {
            Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".csv"))
                    .forEach(path -> {
                        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(path.toString())).build()) {
                            String documentId = path.toString();

                            // Read headers
                            String[] headers = csvReader.readNext();
                            if (headers == null) {
                                System.out.println("File " + documentId + " is empty or missing headers.");
                                return;
                            }
                            fileHeaders.put(documentId, headers);

                            List<String[]> rows = new ArrayList<>();
                            String[] row;
                            int lineNumber = 0;

                            while ((row = csvReader.readNext()) != null) {
                                lineNumber++;
                                rows.add(row);

                                // Add words to the Trie
                                String rowContent = String.join(" ", row).toLowerCase();
                                String[] words = rowContent.split("\\W+"); // Normalize content into words
                                for (String word : words) {
                                    if (!word.isEmpty()) {
                                        invertedIndex.addWord(word, documentId, lineNumber);
                                    }
                                }
                            }
                            fileRows.put(documentId, rows);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

            System.out.println("Inverted index Trie has been built successfully!");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\nEnter a word to search (or type 'exit' to quit): ");
                String query = scanner.nextLine().trim().toLowerCase();

                if (query.equals("exit")) {
                    System.out.println("Exiting the program. Goodbye!");
                    break;
                }

                Map<String, List<Integer>> searchResults = invertedIndex.search(query);
                JSONArray outputJson = new JSONArray();

                if (searchResults.isEmpty()) {
                    System.out.println("No results found for '" + query + "'.");
                } else {
                    searchResults.forEach((documentId, lineNumbers) -> {
                        String[] headers = fileHeaders.get(documentId);
                        List<String[]> rows = fileRows.get(documentId);

                        for (int lineNumber : lineNumbers) {
                            if (lineNumber - 1 < rows.size()) { // Adjust for 0-based index
                                JSONObject rowJson = new JSONObject();
                                rowJson.put("document", documentId);
                                String[] row = rows.get(lineNumber - 1);

                                for (int i = 0; i < headers.length && i < row.length; i++) {
                                    rowJson.put(headers[i], row[i]);
                                }

                                outputJson.put(rowJson);
                            }
                        }
                    });

                    System.out.println(outputJson.toString(4)); // Pretty-print JSON
                }
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
