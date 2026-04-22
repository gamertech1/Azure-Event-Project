package com.example.eventjava.service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceiverClient;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.azure.messaging.servicebus.models.SubQueue;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class DLQReprocessorService {

    private final String connectionString = "";
    private final String queueName = "eventtest";

    @Inject
    ApplicationService applicationService;

    /**
     * Peeks all messages in the DLQ without consuming them. Use this to inspect
     * what's in the DLQ.
     */
    public List<String> peekDLQ() {
        List<String> messages = new ArrayList<>();
        try (ServiceBusReceiverClient receiver = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .receiver()
                .queueName(queueName)
                .subQueue(SubQueue.DEAD_LETTER_QUEUE)
                .buildClient()) {

            receiver.peekMessages(100).forEach(msg -> {
                String body = msg.getBody().toString();
                System.out.println("[DLQReprocessor] Peeked DLQ message id=" + msg.getMessageId() + " body=" + body);
                messages.add(body);
            });

            System.out.println("[DLQReprocessor] Total messages in DLQ: " + messages.size());
        } catch (Exception e) {
            System.err.println("[DLQReprocessor] Failed to peek DLQ: " + e.getMessage());
            e.printStackTrace();
        }
        return messages;
    }

    /**
     * Reprocesses all messages in the DLQ. Successfully processed messages are
     * COMPLETED (removed from DLQ). Failed messages are ABANDONED (remain in
     * DLQ).
     *
     * @return summary of reprocessing results
     */
    public DLQReprocessResult reprocess() {
        int successCount = 0;
        int failCount = 0;
        List<String> errors = new ArrayList<>();

        try (ServiceBusReceiverClient receiver = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .receiver()
                .queueName(queueName)
                .subQueue(SubQueue.DEAD_LETTER_QUEUE)
                .buildClient()) {

            Iterable<ServiceBusReceivedMessage> messages = receiver.receiveMessages(100, Duration.ofSeconds(10));

            for (ServiceBusReceivedMessage message : messages) {
                String body = message.getBody().toString();
                System.out.println("[DLQReprocessor] Reprocessing DLQ message id=" + message.getMessageId());
                System.out.println("[DLQReprocessor] Dead letter reason: " + message.getDeadLetterReason());
                System.out.println("[DLQReprocessor] Dead letter description: " + message.getDeadLetterErrorDescription());
                System.out.println("[DLQReprocessor] Body: " + body);

                try {
                    com.fasterxml.jackson.databind.JsonNode node
                            = new com.fasterxml.jackson.databind.ObjectMapper().readTree(body);

                    // Handle Event Grid envelope (appId/status inside "data")
                    com.fasterxml.jackson.databind.JsonNode dataNode = node.has("data") ? node.get("data") : node;

                    if (dataNode.get("appId") == null || dataNode.get("status") == null) {
                        String err = "Message id=" + message.getMessageId() + " missing appId or status";
                        System.err.println("[DLQReprocessor] " + err);
                        errors.add(err);
                        receiver.abandon(message);
                        failCount++;
                        continue;
                    }

                    String appId = dataNode.get("appId").asText();
                    String status = dataNode.get("status").asText();

                    // If app not in store (e.g. after restart), recreate it from message data
                    String firstName = dataNode.has("firstName") ? dataNode.get("firstName").asText() : "Unknown";
                    String lastName = dataNode.has("lastName") ? dataNode.get("lastName").asText() : "Unknown";
                    String email = dataNode.has("email") ? dataNode.get("email").asText() : "unknown@unknown.com";
                    String reason = dataNode.has("reason") ? dataNode.get("reason").asText() : "";
                    applicationService.restoreApplication(appId, firstName, lastName, email, status, reason);

                    boolean updated = applicationService.updateApplicationStatus(appId, status);
                    if (updated) {
                        receiver.complete(message);
                        System.out.println("[DLQReprocessor] SUCCESS - completed message for appId=" + appId + " status=" + status);
                        successCount++;
                    } else {
                        String err = "appId=" + appId + " could not be restored or updated - scheduling retry in 1 hour";
                        System.err.println("[DLQReprocessor] FAILED - " + err);
                        errors.add(err);
                        scheduleRetry(receiver, message);
                        failCount++;
                    }

                } catch (Exception e) {
                    String err = "Exception processing message id=" + message.getMessageId() + ": " + e.getMessage() + " - scheduling retry in 1 hour";
                    System.err.println("[DLQReprocessor] " + err);
                    errors.add(err);
                    scheduleRetry(receiver, message);
                    failCount++;
                }
            }

        } catch (Exception e) {
            System.err.println("[DLQReprocessor] Failed to connect to DLQ: " + e.getMessage());
            e.printStackTrace();
            errors.add("Connection error: " + e.getMessage());
        }

        System.out.println("[DLQReprocessor] Reprocessing complete. Success=" + successCount + " Failed=" + failCount);
        return new DLQReprocessResult(successCount, failCount, errors);
    }

    /**
     * Completes the DLQ message (removes it from DLQ) and schedules a new copy
     * to the main queue to be delivered after 1 hour.
     */
    private void scheduleRetry(ServiceBusReceiverClient receiver, ServiceBusReceivedMessage dlqMessage) {
        try (ServiceBusSenderClient sender = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .queueName(queueName)
                .buildClient()) {

            ServiceBusMessage retryMessage = new ServiceBusMessage(dlqMessage.getBody());
            retryMessage.setMessageId(dlqMessage.getMessageId());
            retryMessage.setContentType(dlqMessage.getContentType());

            OffsetDateTime scheduledTime = OffsetDateTime.now().plusHours(1);
            sender.scheduleMessage(retryMessage, scheduledTime);
            receiver.complete(dlqMessage);
            System.out.println("[DLQReprocessor] Scheduled retry for message id=" + dlqMessage.getMessageId() + " at " + scheduledTime);
        } catch (Exception e) {
            System.err.println("[DLQReprocessor] Failed to schedule retry for message id=" + dlqMessage.getMessageId() + ": " + e.getMessage());
            receiver.abandon(dlqMessage);
        }
    }

    /**
     * Result summary of a reprocessing run.
     */
    public static class DLQReprocessResult {

        public final int successCount;
        public final int failCount;
        public final List<String> errors;

        public DLQReprocessResult(int successCount, int failCount, List<String> errors) {
            this.successCount = successCount;
            this.failCount = failCount;
            this.errors = errors;
        }

        @Override
        public String toString() {
            return "DLQReprocessResult{successCount=" + successCount
                    + ", failCount=" + failCount
                    + ", errors=" + errors + "}";
        }
    }
}
