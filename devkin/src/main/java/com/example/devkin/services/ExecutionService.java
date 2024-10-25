package com.example.devkin.services;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ExecutionService {

    public String executeCode(String code) throws Exception {
        // Check if the provided code is empty
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Code cannot be empty");
        }

        // Write Python code to a temporary file
        String fileName = writeCodeToFile(code);

        // Log file creation and content for debugging
        System.out.println("Temporary file created: " + fileName);
        System.out.println("File content:\n" + code);

        // Build the Docker command to run Python code
        String[] dockerCommand = buildDockerCommand(fileName);

        // Log the Docker command for debugging
        System.out.println("Executing Docker command: " + String.join(" ", dockerCommand));

        // Use ProcessBuilder to run the Docker command
        ProcessBuilder processBuilder = new ProcessBuilder(dockerCommand);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // Capture the output from the execution
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        // Wait for the process to complete and check for errors
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            return "Execution failed with exit code " + exitCode + ": " + output.toString();
        }

        return output.toString();
    }

    private String writeCodeToFile(String code) throws IOException {
        // Create a temporary file for the Python code
        String fileName = "C:\\Users\\ibrah\\OneDrive\\Documents\\Atypon Java\\Final Project\\code_" + UUID.randomUUID().toString() + ".py"; // Change this to a valid temp directory on your Windows system
        Files.write(Paths.get(fileName), code.getBytes());
        return fileName;
    }

    private String[] buildDockerCommand(String fileName) {
        // Normalize Windows path to Linux format for Docker
        String normalizedPath = fileName.replace("\\", "/").replace("C:", "/c");

        // Docker command to run Python code
        return new String[]{
                "docker", "run", "--rm",
                "-v", normalizedPath + ":/tmp/code.py", // Mount the file inside the container
                "python", "python3", "/tmp/code.py"     // Use python3 explicitly to run the script
        };
    }


    private void cleanupFiles(String fileName) {
        // Optional cleanup logic to remove temp files after execution
        try {
            Files.deleteIfExists(Paths.get(fileName));
        } catch (IOException e) {
            // Log exception
            e.printStackTrace();
        }
    }
}
