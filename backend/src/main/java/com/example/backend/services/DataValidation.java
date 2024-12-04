package com.example.backend.services;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import com.example.backend.utils.FileUtils;

import java.nio.file.*;

public class DataValidation {

    // Validation Patterns
    private final String URL_PATTERN = "^https?://[\\w.-]+(/\\S*)?$";
    private final String CAPACITY_PATTERN = "^\\d+\\s?(GB|TB)$";
    private final String PRICE_PER_MONTH_PATTERN = "^(0|[1-9]\\d*)(\\.\\d{2})?$";
    private final String PRICE_PER_ANNUM_PATTERN = "^(0|[1-9]\\d*)(\\.\\d{2})?$";

    // Map to store invalid lines by filename
    private Map<String, List<String>> invalidLinesByFile = new HashMap<>();

    public static void main(String[] args) {
        DataValidation dv = new DataValidation();
        dv.init();
        dv.printInvalidLines();
    }

    public void init() {
        invalidLinesByFile.clear(); // Clear previous results
        FileUtils.readFiles("./backend/data", path -> {
            validate(path.toString());
        });
    }

    public void validate(String filePath) {
        System.out.println("Validating path: " + filePath);
        String fileName = Paths.get(filePath).getFileName().toString();
        List<String> fileInvalidLines = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String header = br.readLine(); // Skip the header
            if (header == null) {
                System.out.println("The file is empty.");
                return;
            }

            int lineNumber = 1; // Start from line 1 (after header)
            String line;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                String[] fields = line.split(",", -1); // Split, including empty fields
                if (fields.length < 8) { // Ensure sufficient fields
                    fileInvalidLines.add("Line " + lineNumber + ": Insufficient fields - " + line);
                    continue;
                }

                // Specific column validations
                String url = fields[7].trim();
                String capacity = fields[4].trim().replaceAll("\\s+", " ");
                String pricePerMonth = fields[2].trim().replaceAll("\\$", "");
                String pricePerAnnum = fields[3].trim().replaceAll("\\$", "");

                // Validate specific fields
                boolean isValid = validateField(url, URL_PATTERN) &&
                        validateField(capacity, CAPACITY_PATTERN) &&
                        validateField(pricePerMonth, PRICE_PER_MONTH_PATTERN) &&
                        validateField(pricePerAnnum, PRICE_PER_ANNUM_PATTERN);

                if (!isValid) {
                    fileInvalidLines.add("Line " + lineNumber + ": " + line);
                }
            }

            // If there are invalid lines for this file, add to the map
            if (!fileInvalidLines.isEmpty()) {
                invalidLinesByFile.put(fileName, fileInvalidLines);
            }

        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    // Method to print invalid lines
    public void printInvalidLines() {
        System.out.println("\n=== Invalid Lines by File ===");
        if (invalidLinesByFile.isEmpty()) {
            System.out.println("No invalid lines found.");
            return;
        }

        for (Map.Entry<String, List<String>> entry : invalidLinesByFile.entrySet()) {
            System.out.println(entry.getKey() + ":");
            for (String invalidLine : entry.getValue()) {
                System.out.println("  " + invalidLine);
            }
            System.out.println(); // Empty line between files
        }
    }

    private static boolean validateField(String field, String pattern) {
        return Pattern.matches(pattern, field);
    }
}