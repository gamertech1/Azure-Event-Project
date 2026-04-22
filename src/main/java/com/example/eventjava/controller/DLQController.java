package com.example.eventjava.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.eventjava.service.DLQReprocessorService;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;

@Controller("/dlq")
public class DLQController {

    @Inject
    DLQReprocessorService dlqReprocessorService;

    /**
     * GET /dlq/peek
     * Lists all messages currently in the DLQ without consuming them.
     */
    @Get("/peek")
    public Map<String, Object> peek() {
        List<String> messages = dlqReprocessorService.peekDLQ();
        Map<String, Object> response = new HashMap<>();
        response.put("count", messages.size());
        response.put("messages", messages);
        return response;
    }

    /**
     * POST /dlq/reprocess
     * Reprocesses all messages in the DLQ.
     * Successfully processed messages are removed from the DLQ.
     * Failed messages remain in the DLQ.
     */
    @Post("/reprocess")
    public Map<String, Object> reprocess() {
        DLQReprocessorService.DLQReprocessResult result = dlqReprocessorService.reprocess();
        Map<String, Object> response = new HashMap<>();
        response.put("successCount", result.successCount);
        response.put("failCount", result.failCount);
        response.put("errors", result.errors);
        return response;
    }
}
