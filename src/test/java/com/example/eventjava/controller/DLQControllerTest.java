package com.example.eventjava.controller;

import com.example.eventjava.service.DLQReprocessorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DLQControllerTest {

    @Mock
    private DLQReprocessorService dlqReprocessorService;

    @InjectMocks
    private DLQController dlqController;

    @Test
    void peek_returnsMessagesAndCount() {
        List<String> messages = Arrays.asList("msg1", "msg2");
        when(dlqReprocessorService.peekDLQ()).thenReturn(messages);

        Map<String, Object> result = dlqController.peek();

        assertEquals(2, result.get("count"));
        assertEquals(messages, result.get("messages"));
    }

    @Test
    void peek_returnsEmptyWhenNoMessages() {
        when(dlqReprocessorService.peekDLQ()).thenReturn(Collections.emptyList());

        Map<String, Object> result = dlqController.peek();

        assertEquals(0, result.get("count"));
        assertTrue(((List<?>) result.get("messages")).isEmpty());
    }

    @Test
    void reprocess_returnsSuccessAndFailCounts() {
        DLQReprocessorService.DLQReprocessResult mockResult
                = new DLQReprocessorService.DLQReprocessResult(3, 1, Collections.singletonList("error1"));
        when(dlqReprocessorService.reprocess()).thenReturn(mockResult);

        Map<String, Object> result = dlqController.reprocess();

        assertEquals(3, result.get("successCount"));
        assertEquals(1, result.get("failCount"));
        assertEquals(Collections.singletonList("error1"), result.get("errors"));
    }

    @Test
    void reprocess_returnsZeroCountsOnEmptyQueue() {
        DLQReprocessorService.DLQReprocessResult mockResult
                = new DLQReprocessorService.DLQReprocessResult(0, 0, Collections.emptyList());
        when(dlqReprocessorService.reprocess()).thenReturn(mockResult);

        Map<String, Object> result = dlqController.reprocess();

        assertEquals(0, result.get("successCount"));
        assertEquals(0, result.get("failCount"));
        assertTrue(((List<?>) result.get("errors")).isEmpty());
    }
}
