package com.example.backend.services;

import com.example.backend.type.InvertedIndexTrie;
import com.example.backend.utils.FileUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.util.*;

public class Search {
    public InvertedIndexTrie invertedIndex = new InvertedIndexTrie();
    public InvertedIndexTrie invertedIndexKeyMapped = new InvertedIndexTrie();
    static Map<String, String[]> fileHeaders = new HashMap<>();
    static Map<String, List<String[]>> fileRows = new HashMap<>();
    public HashSet<String> storageSizes = new HashSet<>();

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

                        for (int i = 0; i < row.length; i++) {
                            String key = headers[i].toLowerCase(); // e.g., "capacity"
                            String value = row[i];

                            if (value != null && !value.isEmpty()) {
                                // Normalize capacities (e.g., "15 GB" to 15, "2 TB" to 2000)
                                if (key.equals("capacity")) {
                                    value = normalizeCapacity(value); // Convert GB/TB to numeric value
                                    storageSizes.add(value);
                                }
                            }

                            if (value != null && !value.isEmpty()) {
                                // Add full key-value pair for context-based searches
                                String indexedTerm = key + ":" + value.toLowerCase();
                                invertedIndexKeyMapped.addWord(indexedTerm, documentId, lineNumber);

                                // Add just the value for simpler searches
                                invertedIndex.addWord(row[i].toLowerCase(), documentId, lineNumber);
                                System.out.println("Value: " + value.toLowerCase());
                                System.out.println("Indexed: " + indexedTerm);
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

    private String normalizeCapacity(String value) {
        value = value.trim().toUpperCase();
        if (value.endsWith("GB")) {
            return value.replace("GB", "").trim();
        } else if (value.endsWith("TB")) {
            double tbToGb = Double.parseDouble(value.replace("TB", "").trim()) * 1024;
            return String.valueOf((int) tbToGb); // Convert TB to GB
        }
        return value; // Default case (e.g., invalid data)
    }

    public static void main(String[] args) {
        try {
            Search search = new Search();
            search.buildTrie();
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\nEnter a word to search or prefix for suggestions (type 'exit' to quit): ");
                String query = scanner.nextLine().trim().toLowerCase();

                if (query.equals("exit")) {
                    System.out.println("Exiting the program. Goodbye!");
                    break;
                }

                // Provide autocomplete suggestions
                List<String> suggestions = search.invertedIndex.autocomplete(query);
                if (!suggestions.isEmpty()) {
                    System.out.println("Autocomplete suggestions for '" + query + "': " +
                            suggestions);
                }

                // Search functionality
                Map<String, HashSet<Integer>> searchResults = search.invertedIndex.search(query);
                if (searchResults.isEmpty()) {
                    System.out.println("No results found for '" + query + "'.");
                } else {
                    System.out.println("Result '" + searchResults + "'.");

                    search.convertToJson(searchResults);
                }
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, Object>> convertToJson(Map<String, HashSet<Integer>> searchResults) {
        List<Map<String, Object>> outputList = new ArrayList<>();

        searchResults.forEach((documentId, lineNumbers) -> {
            String[] headers = fileHeaders.get(documentId);
            List<String[]> rows = fileRows.get(documentId);

            for (int lineNumber : lineNumbers) {
                if (lineNumber - 1 < rows.size()) { // Adjust for 0-based index
                    Map<String, Object> rowMap = new HashMap<>();
                    rowMap.put("id", lineNumber + "_" + documentId);
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
