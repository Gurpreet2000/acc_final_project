// Use: Regex
package com.example.backend.services;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import com.example.backend.utils.FileUtils;

import java.nio.file.*;

public class DataValidation {

    // Updated Validation Patterns
    private final String URL_PATTERN = "^https?://[\\w.-]+(/\\S*)?$";
    private final String CAPACITY_PATTERN = "^\\d+\\s?(GB|TB)$";
    private final String PRICE_PATTERN = "^\\$?\\d+(\\.\\d{2})?$";

    public static void main(String[] args) {
        DataValidation dv = new DataValidation();
        dv.init();
    }

    public void init() {
        FileUtils.readFiles("./data", path -> {
            validate(path.toString());
        });
    }

    public void validate(String filePath) {
        List<String[]> validEntries = new ArrayList<>();
        List<String[]> invalidEntries = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String header = br.readLine(); // Skip the header
            if (header == null) {
                System.out.println("The file is empty.");
                return;
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",", -1); // Split, including empty fields
                if (fields.length < 8) { // Ensure sufficient fields
                    invalidEntries.add(new String[] { "Invalid Row" });
                    continue;
                }

                String url = fields[7].trim();
                String capacity = fields[4].trim().replaceAll("\\s+", " ");
                String price = fields[2].trim();

                // Validate fields
                boolean isValid = validateField(url, URL_PATTERN) &&
                        validateField(capacity, CAPACITY_PATTERN) &&
                        validateField(price, PRICE_PATTERN);

                if (isValid) {
                    validEntries.add(new String[] { url, capacity, price });
                } else {
                    invalidEntries.add(new String[] { url, capacity, price });
                }
            }

            // Print validation results
            printResults("Valid Entries", validEntries);
            printResults("Invalid Entries", invalidEntries);

        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    private static boolean validateField(String field, String pattern) {
        return Pattern.matches(pattern, field);
    }

    private static void printResults(String title, List<String[]> entries) {
        System.out.println("\n=== " + title + " ===");
        if (entries.isEmpty()) {
            System.out.println("No entries found.");
        } else {
            System.out.printf("%-50s | %-10s | %-10s\n", "URL", "Capacity", "Price");
            System.out.println("------------------------------------------------------------");
            for (String[] entry : entries) {
                System.out.printf("%-50s | %-10s | %-10s\n", entry[0], entry[1], entry[2]);
            }
        }
    }
}
