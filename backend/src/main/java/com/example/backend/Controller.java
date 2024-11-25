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
import com.example.backend.services.Search;

@RestController
public class Controller {
    private static final Search search = new Search();

    @PostConstruct
    public void init() {
        // Initialize the search and build the Trie
        search.buildTrie();
    }

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @GetMapping("/search")
    public SearchQuery search(
            @RequestParam(value = "q", defaultValue = "") String query,
            @RequestParam(value = "price", defaultValue = "") String priceRange,
            @RequestParam(value = "storage", defaultValue = "") String storageRange) {

        System.out.println("Query: " + query + ", Price: " + priceRange + ", Storage: " + storageRange);

        Map<String, HashSet<Integer>> searchResultIndex = new HashMap<>();

        // Handle general query
        if (!query.isEmpty()) {
            searchResultIndex = search.invertedIndex.search(query);
        }

        // Handle price range query
        if (!priceRange.isEmpty()) {
            double[] priceBounds = extractRange(priceRange);
            if (priceBounds != null) {
                Map<String, HashSet<Integer>> priceResults = search.invertedIndex.searchRange("price per month",
                        priceBounds[0], priceBounds[1]);
                mergeResults(searchResultIndex, priceResults);
            }
        }

        // Handle storage range query
        if (!storageRange.isEmpty()) {
            double[] storageBounds = extractRange(storageRange);
            if (storageBounds != null) {
                Map<String, HashSet<Integer>> storageResults = search.invertedIndex.searchRange("capacity",
                        storageBounds[0], storageBounds[1]);
                mergeResults(searchResultIndex, storageResults);
            }
        }

        List<Map<String, Object>> list = search.convertToJson(searchResultIndex);
        System.out.println(searchResultIndex);

        return new SearchQuery(list);
    }

    @GetMapping("/auto_complete")
    public AutoComplete autoComplete(@RequestParam(value = "q", defaultValue = "") String query) {
        System.out.println("Query: " + query);
        List<String> list = search.invertedIndex.autocomplete(query);
        System.out.println(list);
        return new AutoComplete(list);
    }

    private double[] extractRange(String range) {
        Pattern pattern = Pattern.compile("\\[(\\d+\\.?\\d*),(\\d+\\.?\\d*)]");
        Matcher matcher = pattern.matcher(range);

        if (matcher.matches()) {
            double min = Double.parseDouble(matcher.group(1));
            double max = Double.parseDouble(matcher.group(2));
            return new double[] { min, max };
        }
        System.out.println("Invalid range format: " + range);
        return null; // Return null if the range is invalid
    }

    private void mergeResults(Map<String, HashSet<Integer>> mainResults,
            Map<String, HashSet<Integer>> newResults) {
        newResults.forEach((key, value) -> mainResults.merge(key, value, (oldSet, newSet) -> {
            oldSet.addAll(newSet);
            return oldSet;
        }));
    }
}
