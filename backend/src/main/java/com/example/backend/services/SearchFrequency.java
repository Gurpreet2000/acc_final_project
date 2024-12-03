package com.example.backend.services;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class TreeNode {
    String term; // Search term
    int count; // Frequency count of the term
    int height; // Height of the node
    TreeNode left, right; // Left and right children

    // Constructor to initialize a tree node with a term
    TreeNode(String term) {
        this.term = term;
        this.count = 1; // Start with count of 1 when first added
        this.height = 1;
    }
}

// AVL Tree class to manage search terms and maintain balance
class AVLTree {
    private TreeNode root;
    // Log to track frequency of each search term
    private Map<String, Integer> searchLog = new TreeMap<>();

    // Fetches height of a node (0 if null)
    private int nodeHeight(TreeNode node) {
        return node == null ? 0 : node.height;
    }

    // Performs a right rotation to maintain AVL balance
    private TreeNode rotateRight(TreeNode y) {
        TreeNode x = y.left;
        TreeNode T2 = x.right;

        // Perform rotation
        x.right = y;
        y.left = T2;

        // Update heights
        y.height = Math.max(nodeHeight(y.left), nodeHeight(y.right)) + 1;
        x.height = Math.max(nodeHeight(x.left), nodeHeight(x.right)) + 1;

        return x; // New root after rotation
    }

    // Performs a left rotation to maintain AVL balance
    private TreeNode rotateLeft(TreeNode x) {
        TreeNode y = x.right;
        TreeNode T2 = y.left;

        // Perform rotation
        y.left = x;
        x.right = T2;

        // Update heights
        x.height = Math.max(nodeHeight(x.left), nodeHeight(x.right)) + 1;
        y.height = Math.max(nodeHeight(y.left), nodeHeight(y.right)) + 1;

        return y; // New root after rotation
    }

    // Calculates balance factor of a node to decide on rotations
    private int balanceFactor(TreeNode node) {
        return node == null ? 0 : nodeHeight(node.left) - nodeHeight(node.right);
    }

    // Inserts a term into the AVL tree and updates its frequency
    public void insert(String term) {
        root = addNode(root, term);
    }

    // Recursive insertion method that maintains AVL balance
    private TreeNode addNode(TreeNode node, String term) {
        // If node is null, create a new node for the term
        if (node == null)
            return new TreeNode(term);

        // Check if term matches or needs to go left or right in the tree
        if (term.equals(node.term)) {
            // If term already exists, increment its count
            node.count++;
            return node;
        } else if (term.compareTo(node.term) < 0) {
            node.left = addNode(node.left, term);
        } else {
            node.right = addNode(node.right, term);
        }

        // Update the height of the node
        node.height = 1 + Math.max(nodeHeight(node.left), nodeHeight(node.right));

        // Check the balance factor and apply rotations if needed
        int balance = balanceFactor(node);

        // Left-left case
        if (balance > 1 && term.compareTo(node.left.term) < 0)
            return rotateRight(node);

        // Right-right case
        if (balance < -1 && term.compareTo(node.right.term) > 0)
            return rotateLeft(node);

        // Left-right case
        if (balance > 1 && term.compareTo(node.left.term) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Right-left case
        if (balance < -1 && term.compareTo(node.right.term) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node; // Return the (unchanged) node pointer
    }

    // Finds a term in the tree without incrementing frequency
    public int findTerm(String term) {
        TreeNode node = findNode(root, term);

        if (node == null) {
            System.out.println("Word: " + term + " not found!");
        } else {
            System.out.println("Found word: " + term + ", Frequency: " + node.count);
        }

        return node == null ? 0 : node.count;
    }

    // Searches for a term in the AVL tree
    private TreeNode findNode(TreeNode node, String term) {
        // Base case: term is found or reached end of tree
        if (node == null || node.term.equals(term))
            return node;

        // Traverse left or right depending on comparison
        if (term.compareTo(node.term) < 0)
            return findNode(node.left, term);

        return findNode(node.right, term);
    }

    // Displays top searched terms based on frequency and returns a list
    public List<Map.Entry<String, Integer>> showTopSearches(int limit) {
        // Use node traversal to collect frequencies
        Map<String, Integer> frequencyMap = new TreeMap<>();
        collectFrequencies(root, frequencyMap);

        // Stream and sort the entries
        Stream<Map.Entry<String, Integer>> sortedStream = frequencyMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()));

        // Apply limit if specified and positive, otherwise return all
        List<Map.Entry<String, Integer>> topSearches = (limit > 0 ? sortedStream.limit(limit) : sortedStream)
                .collect(Collectors.toList());

        // Print the top searches
        System.out.println("\nMost Searched Terms:");
        topSearches
                .forEach(entry -> System.out.println("Search: " + entry.getKey() + ", Frequency: " + entry.getValue()));

        return topSearches;
    }

    // Helper method to collect frequencies from the tree
    private void collectFrequencies(TreeNode node, Map<String, Integer> frequencyMap) {
        if (node == null)
            return;

        // Collect frequencies from left subtree
        collectFrequencies(node.left, frequencyMap);

        // Add current node's frequency
        frequencyMap.put(node.term, node.count);

        // Collect frequencies from right subtree
        collectFrequencies(node.right, frequencyMap);
    }
}

public class SearchFrequency {
    AVLTree searchTree = new AVLTree();
    String directory = "";
    String filePath = directory + "/searchHistory.txt";

    public SearchFrequency(String directory) {
        this.directory = directory;
        this.filePath = directory + "/searchHistory.txt";
    }

    public void init() {
        File file = new File(filePath);
        if (!file.exists())
            return;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                searchTree.insert(line.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addHistory(String word) {
        searchTree.insert(word.toLowerCase());

        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }

        try (FileWriter fileWriter = new FileWriter(filePath, true)) {
            fileWriter.append(word + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Map.Entry<String, Integer>> getSearchHistory() {
        return searchTree.showTopSearches(-1);
    }

    public int searchWord(String term) {
        return searchTree.findTerm(term);
    }

    public static void main(String[] args) {
        AVLTree searchTree = new AVLTree();

        String directory = "./backend/data";

        String filePath = directory + "/searchHistory.txt";

        File file = new File(filePath);
        if (!file.exists())
            return;

        // Reading the file to populate the tree with initial terms
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split each line and add words to the tree
                for (String word : getWords(line)) {
                    searchTree.insert(word.toLowerCase());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner input = new Scanner(System.in);

        // User input loop for dynamic search term input
        while (true) {
            System.out.print("\nEnter search term (or type 'exit' to stop): ");
            String query = input.nextLine().trim();
            if (query.equalsIgnoreCase("exit")) {
                break;
            }
            // Just find the term without incrementing count again
            searchTree.findTerm(query);
        }
        input.close();

        // Display top frequent search terms
        System.out.println("Top Searches:");
        searchTree.showTopSearches(-1);
    }

    // Extract words from a line using regular expression pattern
    private static Set<String> getWords(String text) {
        Set<String> words = new HashSet<>();
        Pattern pattern = Pattern.compile("\\b\\w+\\b"); // Matches individual words

        var matcher = pattern.matcher(text);
        while (matcher.find()) {
            words.add(matcher.group()); // Add each found word to the set
        }

        return words; // Return set of unique words
    }
}