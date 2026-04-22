package com.example.eventjava.service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.example.eventjava.model.Application;
import com.example.eventjava.model.CreateApplicationRequest;
import com.example.eventjava.model.CreateApplicationResponse;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ApplicationService {

    private final Map<String, Application> applications = new ConcurrentHashMap<>();
    @Inject
    private EventGridService eventGridService;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int APP_ID_LENGTH = 20;
    private final SecureRandom random = new SecureRandom();

    /**
     * Update the status of an application by appId.
     *
     * @param appId Application ID
     * @param status New status
     * @return true if updated, false if not found
     */
    public boolean updateApplicationStatus(String appId, String status) {
        Application app = applications.get(appId);
        if (app != null) {
            app.setStatus(status);
            return true;
        }
        return false;
    }

    /**
     * Restores an application from DLQ message data if it doesn't already
     * exist. Used by DLQ reprocessor to recreate applications lost after
     * restart.
     */
    public void restoreApplication(String appId, String firstName, String lastName, String email, String status, String reason) {
        applications.computeIfAbsent(appId, id -> new Application(id, firstName, lastName, email, status, reason));
    }

    public CreateApplicationResponse createApplication(CreateApplicationRequest request) {
        String appId = generateAppId();
        Application app = new Application(appId, request.getFirstName(), request.getLastName(), request.getEmail(), "ACTIVE", "IN_PROCESS");
        applications.put(appId, app);
        return new CreateApplicationResponse(appId, "ACTIVE", "IN_PROCESS");
    }

    public Application closeApplication(String appId) {
        Application app = applications.get(appId);
        if (app != null) {
            if ("ACTIVE".equals(app.getStatus())) {
                // Publish ApplicationActive event (current state)
                try {
                    eventGridService.publishEvent(
                            "application/" + appId,
                            "ApplicationActive",
                            app
                    );
                } catch (Exception e) {
                    System.err.println("Failed to publish ACTIVE event to Azure Event Grid: " + e.getMessage());
                }
                // Update status to CLOSED
                app.setStatus("CLOSED");
                app.setReason("COMPLETE");
                // Publish ApplicationClosed event (new state)
                try {
                    eventGridService.publishEvent(
                            "application/" + appId,
                            "ApplicationClosed",
                            app
                    );
                } catch (Exception e) {
                    System.err.println("Failed to publish CLOSED event to Azure Event Grid: " + e.getMessage());
                }
            }
        }
        return app;
    }

    private String generateAppId() {
        StringBuilder sb = new StringBuilder(APP_ID_LENGTH);
        for (int i = 0; i < APP_ID_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public Application getApplication(String appId) {
        return applications.get(appId);
    }
}
