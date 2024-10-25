package com.example.devkin.managers;

import com.example.devkin.dtos.ExecutionRequestsDto;
import com.example.devkin.dtos.ExecutionResultDto;
import com.example.devkin.services.ExecutionService;
import com.example.devkin.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ExecutionManager {

    private final BlockingQueue<ExecutionRequestsDto> executionQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean isExecuting = new AtomicBoolean(false);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ExecutionService executionService;

    @Autowired
    private FileStorageService fileStorageService;

    // A method to add requests to the queue
    public void submitExecutionRequest(ExecutionRequestsDto request) {
        executionQueue.add(request);
        processNextRequest();
    }

    // A method to process requests one by one
    private void processNextRequest() {
        if (isExecuting.compareAndSet(false, true)) {
            ExecutionRequestsDto request = executionQueue.poll();

            if (request != null) {
                CompletableFuture.runAsync(() -> {
                    try {
                        byte[] fileData = fileStorageService.getFileContents(fileStorageService.calcFilePath(request.getCodeRequest().getFileDto()));
                        String code = new String(fileData, StandardCharsets.UTF_8);
                        String result = executionService.executeCode(code);
                        messagingTemplate.convertAndSend("/topic/status/" + request.getProjectSlug(),
                                new ExecutionResultDto("COMPLETED", result));
                    } catch (Exception e) {
                        messagingTemplate.convertAndSend("/topic/status/" + request.getProjectSlug(),
                                new ExecutionResultDto("ERROR", e.getMessage()));
                    } finally {
                        isExecuting.set(false);
                        processNextRequest(); // Process the next request in the queue
                    }
                });
            } else {
                isExecuting.set(false); // No request to process
            }
        }
    }
}