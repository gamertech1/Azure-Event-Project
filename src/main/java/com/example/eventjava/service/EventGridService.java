package com.example.eventjava.service;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.BinaryData;
import com.azure.messaging.eventgrid.EventGridEvent;
import com.azure.messaging.eventgrid.EventGridPublisherClient;
import com.azure.messaging.eventgrid.EventGridPublisherClientBuilder;
import com.example.eventjava.model.Application;

import jakarta.inject.Singleton;

@Singleton
public class EventGridService {

    private final EventGridPublisherClient<EventGridEvent> client;

    public EventGridService() {
        // TODO: Replace with your actual endpoint and key
        String endpoint = "";
        String key = "";
        this.client = new EventGridPublisherClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(key))
                .buildEventGridEventPublisherClient();
    }

    public void publishEvent(String subject, String eventType, Object data) {
        EventGridEvent event = new EventGridEvent(subject, eventType, BinaryData.fromObject(data), "1.0");
        client.sendEvents(java.util.Collections.singletonList(event));
    }

    // Example usage in your code
    public void publishExampleEvent() {
        Application app = new Application(); // Create or fetch an Application object as needed
        EventPayload payload = new EventPayload("app123", "approved", "valid", "evt-001", app);
        publishEvent("subject", "eventType", payload);
    }
}
