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

    public String executeCode(String language, String code) throws Exception {
        // Check if the provided code is empty
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Code cannot be empty");
        }

        // Write code to a temporary file based on language (e.g., .java, .py)
        String fileName = writeCodeToFile(language, code);

        // Log file creation and content for debugging
        System.out.println("Temporary file created: " + fileName);
        System.out.println("File content:\n" + code);

        // Build the Docker command using the appropriate image for the language
        String[] dockerCommand = buildDockerCommand(language, fileName);

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
        int exitCode = process.waitFor();  // Wait for the process to complete
        if (exitCode != 0) {
            return "Execution failed with exit code " + exitCode + ": " + output.toString();
        }
        return output.toString();
    }

    private String writeCodeToFile(String language, String code) throws IOException {
        // Logic to write the code to a temp file based on language
        String fileName = "C:\\temp\\code_" + UUID.randomUUID().toString(); // Change this to a valid temp directory on your Windows system
        if (language.equals("java")) {
            fileName += ".java";
        } else if (language.equals("python")) {
            fileName += ".py";
        }
        Files.write(Paths.get(fileName), code.getBytes());
        return fileName;
    }

    private String[] buildDockerCommand(String language, String fileName) {
        // Normalize Windows path to Linux format for Docker
        String normalizedPath = fileName.replace("\\", "/").replace("C:", "/c");

        // Build the Docker command based on the language
        if (language.equalsIgnoreCase("java")) {
            return new String[]{
                    "docker", "run", "--rm",
                    "-v", normalizedPath + ":/tmp/code.java",
                    "openjdk",
                    "sh", "-c", "javac /tmp/code.java && java -cp /tmp Code"
            };
        } else if (language.equalsIgnoreCase("python")) {
            return new String[]{
                    "docker", "run", "--rm",
                    "-v", normalizedPath + ":/tmp/code.py",
                    "python", "/tmp/code.py"
            };
        }
        return new String[]{};
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
