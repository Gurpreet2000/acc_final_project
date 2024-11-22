package com.example.backend.type;

import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEndOfWord = false;
    Map<String, List<Integer>> documentPositions = new HashMap<>(); // Document -> List of Line Numbers
}

public class InvertedIndexTrie {
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
}
