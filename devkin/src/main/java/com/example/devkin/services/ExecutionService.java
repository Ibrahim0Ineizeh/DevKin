package com.example.devkin.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ExecutionService {

    @Value("${execution.file.path}")
    private String filePath;

    public String executeCode(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Code cannot be empty");
        }
        String fileName = writeCodeToFile(code);

        System.out.println("Temporary file created: " + fileName);
        System.out.println("File content:\n" + code);

        String[] dockerCommand = buildDockerCommand(fileName);

        System.out.println("Executing Docker command: " + String.join(" ", dockerCommand));

        ProcessBuilder processBuilder = new ProcessBuilder(dockerCommand);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        int exitCode = process.waitFor();
        cleanupFiles(fileName);
        if (exitCode != 0) {
            return "Execution failed with exit code " + exitCode + ": " + output.toString();
        }

        return output.toString();
    }

    private String writeCodeToFile(String code) throws IOException {
        String fileName = filePath + "\\code_" + UUID.randomUUID().toString() + ".py";
        Files.write(Paths.get(fileName), code.getBytes());
        return fileName;
    }

    private String[] buildDockerCommand(String fileName) {
        String normalizedPath = fileName.replace("\\", "/").replace("C:", "/c");
        return new String[]{
                "docker", "run", "--rm",
                "-v", normalizedPath + ":/tmp/code.py",
                "python", "python3", "/tmp/code.py"
        };
    }


    private void cleanupFiles(String fileName) {
        try {
            Files.deleteIfExists(Paths.get(fileName));
        } catch (IOException e) {
            // Log exception
            e.printStackTrace();
        }
    }
}
