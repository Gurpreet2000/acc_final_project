package com.example.backend.utils;

import java.io.IOException;
import java.nio.file.*;
import java.util.function.Consumer;

public class FileUtils {

    /**
     * Reads all files in a given directory and applies the specified action to each file.
     *
     * @param directoryPath the path to the directory containing files
     * @param action        the action to perform on each file
     */
    public static void readFiles(String directoryPath, Consumer<Path> action) {
        try {
            Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".csv"))
                    .forEach(action);
        } catch (IOException e) {
            System.err.println("Error while reading files: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
