package com.example.backend.services;

import com.example.backend.type.InvertedIndexTrie;
import com.example.backend.utils.FileUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.util.*;

public class Search {
    public InvertedIndexTrie invertedIndex = new InvertedIndexTrie();
    static Map<String, String[]> fileHeaders = new HashMap<>();
    static Map<String, List<String[]>> fileRows = new HashMap<>();

    public void buildTrie() {

        System.out.println("Building the inverted index Trie. Please wait...");

        try {
            FileUtils.readFiles("./data", path -> {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void main(String[] args) {
        try {
            buildTrie();
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\nEnter a word to search or prefix for suggestions (type 'exit' to quit): ");
                String query = scanner.nextLine().trim().toLowerCase();

                if (query.equals("exit")) {
                    System.out.println("Exiting the program. Goodbye!");
                    break;
                }

                // Provide autocomplete suggestions
                List<String> suggestions = invertedIndex.autocomplete(query);
                if (!suggestions.isEmpty()) {
                    System.out.println("Autocomplete suggestions for '" + query + "': " + suggestions);
                }

                // Search functionality
                Map<String, List<Integer>> searchResults = invertedIndex.search(query);
                if (searchResults.isEmpty()) {
                    System.out.println("No results found for '" + query + "'.");
                } else {
                    convertToJson(searchResults);
                }
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, Object>> convertToJson(Map<String, List<Integer>> searchResults) {
        List<Map<String, Object>> outputList = new ArrayList<>();

        searchResults.forEach((documentId, lineNumbers) -> {
            String[] headers = fileHeaders.get(documentId);
            List<String[]> rows = fileRows.get(documentId);

            for (int lineNumber : lineNumbers) {
                if (lineNumber - 1 < rows.size()) { // Adjust for 0-based index
                    Map<String, Object> rowMap = new HashMap<>();
                    rowMap.put("document", documentId);
                    String[] row = rows.get(lineNumber - 1);

                    for (int i = 0; i < headers.length && i < row.length; i++) {
                        rowMap.put(headers[i], row[i]);
                    }

                    outputList.add(rowMap);
                }
            }
        });

        // System.out.println(outputJson.toString(4)); // Pretty-print JSON

        return outputList;
    }
}
