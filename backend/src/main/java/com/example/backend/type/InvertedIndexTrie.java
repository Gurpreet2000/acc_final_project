package com.example.backend.type;

import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEndOfWord = false;
    Map<String, HashSet<Integer>> documentPositions = new HashMap<>(); // Document -> List of Line Numbers
}

public class InvertedIndexTrie {
    private TrieNode root = new TrieNode();

    public void addWord(String word, String document, int lineNumber) {
        TrieNode current = root;
        for (char c : word.toLowerCase().toCharArray()) {
            current.children.putIfAbsent(c, new TrieNode());
            current = current.children.get(c);
        }
        current.isEndOfWord = true;
        current.documentPositions.putIfAbsent(document, new HashSet<>());
        current.documentPositions.get(document).add(lineNumber);
    }

    public Map<String, HashSet<Integer>> search(String query) {
        TrieNode current = root;
        for (char c : query.toLowerCase().toCharArray()) {
            if (!current.children.containsKey(c)) {
                System.out.println("No match for query: " + query); // Debug
                return new HashMap<>();
            }
            current = current.children.get(c);
        }

        // Return results if the query matches a full term
        if (current.isEndOfWord) {
            return current.documentPositions;
        }

        System.out.println("Query found as prefix but not a complete term: " + query); // Debug
        return new HashMap<>();
    }

    // Autocomplete: Find all words with the given prefix
    public List<String> autocomplete(String prefix) {
        TrieNode current = root;
        for (char c : prefix.toCharArray()) {
            if (!current.children.containsKey(c)) {
                return Collections.emptyList();
            }
            current = current.children.get(c);
        }

        List<String> suggestions = new ArrayList<>();
        collectAllWords(current, prefix, suggestions);
        return suggestions;
    }

    // Helper function to collect all words from a given TrieNode
    private void collectAllWords(TrieNode node, String prefix, List<String> suggestions) {
        if (node.isEndOfWord) {
            suggestions.add(prefix);
        }
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            collectAllWords(entry.getValue(), prefix + entry.getKey(), suggestions);
        }
    }

    public Map<String, HashSet<Integer>> searchRange(String key, double minValue, double maxValue) {
        Map<String, HashSet<Integer>> results = new HashMap<>();

        TrieNode current = root;
        String searchPrefix = key.toLowerCase() + ":";
        for (char c : searchPrefix.toCharArray()) {
            if (!current.children.containsKey(c)) {
                return results; // No match for the prefix
            }
            current = current.children.get(c);
        }

        // Collect all terms with the given prefix
        List<String> matchedTerms = new ArrayList<>();
        collectAllWords(current, searchPrefix, matchedTerms);
        for (String term : matchedTerms) {

            String[] parts = term.split(":");
            if (parts.length == 2) {
                try {
                    // System.out.println("termValue: " + parts[1]);
                    double termValue = Double.parseDouble(parts[1]);

                    // System.out.println("minValue: " + minValue + "maxValue: " + maxValue);

                    // Check if the term's value is within the range
                    if (termValue >= minValue && termValue <= maxValue) {
                        Map<String, HashSet<Integer>> termResults = search(term);
                        System.out.println(termValue + " - " + term + " - " + termResults);
                        termResults.forEach((doc, lines) -> results.merge(doc, lines, (oldSet, newSet) -> {
                            oldSet.addAll(newSet);
                            return oldSet;
                        }));
                    }
                } catch (NumberFormatException e) {
                    // Ignore non-numeric terms
                    // e.printStackTrace();
                }
            }
        }
        return results;
    }
}
