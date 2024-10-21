package com.example.devkin.services;

import com.example.devkin.dtos.ExecuteDto;
import com.example.devkin.dtos.ExecutionRequestsDto;
import com.example.devkin.dtos.ExecutionResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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

    // A method to add requests to the queue
    public void submitExecutionRequest(ExecutionRequestsDto request) {
        executionQueue.add(request);
        processNextRequest(); // Process the next request if available
    }

    // A method to process requests one by one
    private void processNextRequest() {
        if (isExecuting.compareAndSet(false, true)) {
            // Process the request at the front of the queue
            ExecutionRequestsDto request = executionQueue.poll();

            if (request != null) {
                CompletableFuture.runAsync(() -> {
                    try {
                        String result = executionService.executeCode(request.getCodeRequest().getLanguage(), request.getCodeRequest().getCode());
                        messagingTemplate.convertAndSend("/topic/status/" + request.getSessionId(),
                                new ExecutionResultDto("COMPLETED", result));
                    } catch (Exception e) {
                        messagingTemplate.convertAndSend("/topic/status/" + request.getSessionId(),
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