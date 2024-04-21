package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

// The class name should be in PascalCase, however when I change the name here and in the constructor I get the error:Class 'FileOutputStrategy' is public, should be declared in a file named 'FileOutputStrategy.java' and can no longer do mvn clean package error less, which is why I kept it like this for now
public class fileOutputStrategy implements OutputStrategy {

    private String BaseDirectory;

    public final ConcurrentHashMap<String, String> file_map = new ConcurrentHashMap<>();

    public fileOutputStrategy(String baseDirectory) {
        this.BaseDirectory = baseDirectory;
    }

    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(BaseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // FilePath should be filePath (camelCase)
        // Set the FilePath variable
        String filePath = file_map.computeIfAbsent(label, k -> Paths.get(BaseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) { // The catch block should provide more specific exception handling, rather than catching a generic Exception
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}