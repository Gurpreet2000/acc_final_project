package com.example.backend;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.PostConstruct;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.AutoComplete;
import com.example.backend.model.Greeting;
import com.example.backend.model.SearchQuery;
import com.example.backend.model.StorageList;
import com.example.backend.services.SpellCheck;
import com.example.backend.services.Search;

@RestController
public class Controller {
    private static final Search search = new Search();
    private static final SpellCheck spellCheck = new SpellCheck();

    @PostConstruct
    public void init() {
        // Initialize the search and build the Trie
        search.buildTrie();
        spellCheck.buildDictionary();
    }

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @GetMapping("/auto_complete")
    public AutoComplete autoComplete(@RequestParam(value = "q", defaultValue = "") String query) {
        System.out.println("Query: " + query);
        List<String> list = search.invertedIndex.autocomplete(query);
        System.out.println(list);
        return new AutoComplete(list);
    }

    @GetMapping("/storage_list")
    public StorageList storageList(@RequestParam(value = "q", defaultValue = "") String query) {
        System.out.println("Query: " + query);
        List<Object> list = Arrays.asList(search.storageSizes.toArray());
        System.out.println(list);
        return new StorageList(list);
    }

    @GetMapping("/search")
    public SearchQuery search(
            @RequestParam(value = "q", defaultValue = "") String query,
            @RequestParam(value = "minPrice", defaultValue = "") String minPrice,
            @RequestParam(value = "maxPrice", defaultValue = "") String maxPrice,
            @RequestParam(value = "minStorage", defaultValue = "") String minStorage,
            @RequestParam(value = "maxStorage", defaultValue = "") String maxStorage) {

        System.out.println("Raw Input -> Query: " + query + ", Min Price: " + minPrice + ", Max Price: " + maxPrice
                + ", Min Storage: " + minStorage + ", Max Storage: " + maxStorage);

        double minPriceVal = parseDoubleWithDefault(minPrice, 0); // Default to 0
        double maxPriceVal = parseDoubleWithDefault(maxPrice, Double.MAX_VALUE); // Default to maximum value
        double minStorageVal = parseDoubleWithDefault(minStorage, 0); // Default to 0
        double maxStorageVal = parseDoubleWithDefault(maxStorage, Double.MAX_VALUE); // Default to maximum value

        // System.out.println("Parsed Values -> Min Price: " + minPriceVal + ", Max
        // Price: " + maxPriceVal +
        // ", Min Storage: " + minStorageVal + ", Max Storage: " + maxStorageVal);

        Map<String, HashSet<Integer>> searchResultIndex = new HashMap<>();
        String string = "";

        // Handle general query
        if (!query.isEmpty()) {
            searchResultIndex = search.invertedIndex.search(query);
            System.out.println(searchResultIndex.entrySet().isEmpty());
            if (searchResultIndex.entrySet().isEmpty()) {
                string = spellCheck.findClosestWord(query);
            }
        }

        // Handle price range query
        if (minPriceVal > 0 || maxPriceVal < Double.MAX_VALUE) {
            Map<String, HashSet<Integer>> priceResults = search.invertedIndexKeyMapped.searchRange(
                    "price per month", minPriceVal, maxPriceVal);
            mergeResults(searchResultIndex, priceResults);
        }

        // Handle storage range query
        if (minStorageVal > 0 || maxStorageVal < Double.MAX_VALUE) {
            Map<String, HashSet<Integer>> storageResults = search.invertedIndexKeyMapped.searchRange(
                    "capacity", minStorageVal, maxStorageVal);
            mergeResults(searchResultIndex, storageResults);
        }

        // Convert the search results to JSON format
        List<Map<String, Object>> list = search.convertToJson(searchResultIndex);

        return new SearchQuery(list, string);
    }

    /**
     * Safely parses a string to a double with a default value if the string is
     * invalid.
     */
    private double parseDoubleWithDefault(String value, double defaultValue) {
        try {
            if (value == null || value.isEmpty()) {
                return defaultValue;
            }
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format for value: " + value + ". Using default: " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * Merges results from two search result maps.
     */
    private void mergeResults(Map<String, HashSet<Integer>> mainResults,
            Map<String, HashSet<Integer>> newResults) {
        newResults.forEach((key, value) -> mainResults.merge(key, value, (oldSet, newSet) -> {
            oldSet.addAll(newSet);
            return oldSet;
        }));
    }

}
