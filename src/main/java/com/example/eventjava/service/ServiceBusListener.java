package com.example.eventjava.service;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;

import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ServiceBusListener {

    private final String connectionString = "";
    private final String queueName = "eventtest";

    @Inject
    ApplicationService applicationService;

    @EventListener
    public void start(StartupEvent event) {
        try {
            System.out.println("[ServiceBusListener] Initializing connection to queue: " + queueName);
            ServiceBusProcessorClient processorClient = new ServiceBusClientBuilder()
                    .connectionString(connectionString)
                    .processor()
                    .queueName(queueName)
                    .processMessage(context -> {
                        String body = context.getMessage().getBody().toString();
                        System.out.println("[ServiceBusListener] Received message: " + body);
                        try {
                            com.fasterxml.jackson.databind.JsonNode node
                                    = new com.fasterxml.jackson.databind.ObjectMapper().readTree(body);
                            // Messages are Event Grid envelopes: appId/status are inside "data"
                            com.fasterxml.jackson.databind.JsonNode dataNode = node.has("data") ? node.get("data") : node;
                            if (dataNode.get("appId") == null || dataNode.get("status") == null) {
                                System.err.println("[ServiceBusListener] Message missing 'appId' or 'status' fields. Raw body: " + body);
                                return;
                            }
                            String appId = dataNode.get("appId").asText();
                            String status = dataNode.get("status").asText();
                            boolean updated = applicationService.updateApplicationStatus(appId, status);
                            System.out.println("[ServiceBusListener] Updated appId=" + appId + " status=" + status + " success=" + updated);
                        } catch (Exception e) {
                            System.err.println("[ServiceBusListener] Failed to process message: " + e.getMessage());
                            e.printStackTrace();
                        }
                    })
                    .processError(errorContext -> {
                        System.err.println("[ServiceBusListener] Transport error: " + errorContext.getException().getMessage());
                        errorContext.getException().printStackTrace();
                    })
                    .buildProcessorClient();
            processorClient.start();
            System.out.println("[ServiceBusListener] Service Bus listener started successfully on queue: " + queueName);
        } catch (Exception e) {
            System.err.println("[ServiceBusListener] FAILED to start listener: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
