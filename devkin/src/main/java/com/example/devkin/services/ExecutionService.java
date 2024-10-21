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
        // Write code to a temporary file based on language (e.g., .java, .py)
        String fileName = writeCodeToFile(language, code);

        // Build the Docker command using the appropriate image for the language
        String[] dockerCommand = buildDockerCommand(language, fileName);

        // Use ProcessBuilder to run the Docker command
        ProcessBuilder processBuilder = new ProcessBuilder(dockerCommand);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // Capture the output from the execution
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();  // Wait for the process to complete
            return output.toString();
        } finally {
            // Clean up temporary files (optional)
            cleanupFiles(fileName);
        }
    }

    private String writeCodeToFile(String language, String code) throws IOException {
        // Logic to write the code to a temp file based on language
        String fileName = "/tmp/code_" + UUID.randomUUID().toString();
        if (language.equals("java")) {
            fileName += ".java";
        } else if (language.equals("python")) {
            fileName += ".py";
        }
        Files.write(Paths.get(fileName), code.getBytes());
        return fileName;
    }

    private String[] buildDockerCommand(String language, String fileName) {
        // Build the Docker command based on the language
        if (language.equals("java")) {
            return new String[]{"docker", "run", "--rm", "-v", fileName + ":/tmp/code.java", "openjdk", "javac", "/tmp/code.java && java /tmp/Code"};
        } else if (language.equals("python")) {
            return new String[]{"docker", "run", "--rm", "-v", fileName + ":/tmp/code.py", "python", "python", "/tmp/code.py"};
        }
        return new String[]{};
    }

    private void cleanupFiles(String fileName) {
        // Optional cleanup logic to remove temp files after execution
        try {
            Files.deleteIfExists(Paths.get(fileName));
        } catch (IOException e) {
            // Log exception
        }
    }
}

