package com.example.backend.services;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import java.io.*;
import java.util.*;
import java.nio.file.*;

// Class to represent a storage plan with OpenCSV annotations
class StoragePlan {
    String provider;
    String pricePerAnnum;
    String pricePerMonth;
    String capacity;
    String fileTypes;
    String specialFeatures;
    String platformCompatibility;
    String url;
    String contactEmail;
    String contactNumber;
    String sourceFile;

    public StoragePlan(String[] data, String sourceFile) {
        this.provider = data[0];
        this.pricePerAnnum = data[1];
        this.pricePerMonth = data[2];
        this.capacity = data[3];
        this.fileTypes = data[4];
        this.specialFeatures = data[5];
        this.platformCompatibility = data[6];
        this.url = data[7];
        this.contactEmail = data[8];
        this.contactNumber = data[9];
        this.sourceFile = sourceFile;
    }

    @Override
    public String toString() {
        return String.format("""
                Provider: %s (Source: %s)
                Price per annum: %s
                Price per month: %s
                Capacity: %s
                Special features: %s
                URL: %s
                """,
                provider,
                sourceFile,
                pricePerAnnum.isEmpty() ? "N/A" : pricePerAnnum,
                pricePerMonth.isEmpty() ? "N/A" : pricePerMonth,
                capacity,
                specialFeatures,
                url);
    }
}

// Trie node class
class TrieNode {
    Map<Character, TrieNode> children;
    List<StoragePlan> plans;

    public TrieNode() {
        children = new HashMap<>();
        plans = new ArrayList<>();
    }
}

// Trie implementation for storage plans
class StoragePlanTrie {
    private TrieNode root;

    public StoragePlanTrie() {
        root = new TrieNode();
    }

    public void insert(StoragePlan plan) {
        insertByField(plan, plan.provider.toLowerCase());
        insertByField(plan, plan.capacity.toLowerCase());
        if (!plan.pricePerMonth.isEmpty()) {
            insertByField(plan, plan.pricePerMonth.toLowerCase());
        }
        if (!plan.pricePerAnnum.isEmpty()) {
            insertByField(plan, plan.pricePerAnnum.toLowerCase());
        }
    }

    private void insertByField(StoragePlan plan, String field) {
        TrieNode current = root;

        for (char c : field.toCharArray()) {
            current.children.putIfAbsent(c, new TrieNode());
            current = current.children.get(c);
        }

        if (!current.plans.contains(plan)) {
            current.plans.add(plan);
        }
    }

    public List<StoragePlan> search(String prefix) {
        prefix = prefix.toLowerCase();
        TrieNode current = root;

        for (char c : prefix.toCharArray()) {
            if (!current.children.containsKey(c)) {
                return new ArrayList<>();
            }
            current = current.children.get(c);
        }

        Set<StoragePlan> results = new HashSet<>();
        collectPlans(current, results);
        return new ArrayList<>(results);
    }

    private void collectPlans(TrieNode node, Set<StoragePlan> results) {
        results.addAll(node.plans);
        for (TrieNode child : node.children.values()) {
            collectPlans(child, results);
        }
    }
}

// CSV file handler using OpenCSV
class CSVFileHandler {
    public static List<StoragePlan> readCSVFile(String filePath) throws IOException, CsvException {
        List<StoragePlan> plans = new ArrayList<>();
        String fileName = Paths.get(filePath).getFileName().toString();

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                .withSkipLines(1) // Skip header row
                .build()) {

            List<String[]> rows = reader.readAll();
            for (String[] row : rows) {
                plans.add(new StoragePlan(row, fileName));
            }
        }
        return plans;
    }
}

public class Search {
    private StoragePlanTrie trie;
    private List<StoragePlan> allPlans;

    public Search() {
        this.trie = new StoragePlanTrie();
        this.allPlans = new ArrayList<>();
    }

    public void loadDataFromDirectory(String directoryPath) throws IOException, CsvException {
        Files.walk(Paths.get(directoryPath))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".csv"))
                .forEach(path -> {
                    try {
                        List<StoragePlan> plans = CSVFileHandler.readCSVFile(path.toString());
                        plans.forEach(plan -> {
                            trie.insert(plan);
                            allPlans.add(plan);
                        });
                        System.out.println("Loaded " + plans.size() + " plans from " + path.getFileName());
                    } catch (IOException | CsvException e) {
                        System.err.println("Error reading file: " + path + " - " + e.getMessage());
                    }
                });
    }

    public List<StoragePlan> search(String query) {
        return trie.search(query);
    }

    // Filter plans by price range
    public List<StoragePlan> searchByPriceRange(double minPrice, double maxPrice) {
        return allPlans.stream()
                .filter(plan -> {
                    double monthlyPrice = parsePrice(plan.pricePerMonth);
                    return monthlyPrice >= minPrice && monthlyPrice <= maxPrice;
                })
                .toList();
    }

    // Helper method to parse price strings
    private double parsePrice(String price) {
        if (price == null || price.isEmpty()) {
            return 0.0;
        }
        try {
            // Extract numeric value from strings like "CA$27.99 / month"
            return Double.parseDouble(price.replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public static void main(String[] args) {
        Search searchEngine = new Search();

        try {
            // Load all CSV files from the specified directory
            searchEngine.loadDataFromDirectory("./data");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\nSearch Options:");
                System.out.println("1. Search by text");
                System.out.println("2. Search by price range");
                System.out.println("3. Exit");
                System.out.print("Choose an option (1-3): ");

                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        System.out.print("Enter search term: ");
                        String query = scanner.nextLine();
                        List<StoragePlan> results = searchEngine.search(query);
                        displayResults(results);
                        break;

                    case "2":
                        System.out.print("Enter minimum price (in CA$): ");
                        double minPrice = Double.parseDouble(scanner.nextLine());
                        System.out.print("Enter maximum price (in CA$): ");
                        double maxPrice = Double.parseDouble(scanner.nextLine());
                        List<StoragePlan> priceResults = searchEngine.searchByPriceRange(minPrice, maxPrice);
                        displayResults(priceResults);
                        break;

                    case "3":
                        System.out.println("Goodbye!");
                        return;

                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }

        } catch (IOException | CsvException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    private static void displayResults(List<StoragePlan> results) {
        if (results.isEmpty()) {
            System.out.println("No results found.");
        } else {
            System.out.println("\nFound " + results.size() + " matching plans:");
            results.forEach(System.out::println);
        }
    }
}