package com.example.eventjava.model;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Application {

    private String appId;
    private String firstName;
    private String lastName;
    private String email;
    private String status;
    private String reason;

    // Constructors, getters, setters
    public Application() {
    }

    public Application(String appId, String firstName, String lastName, String email, String status, String reason) {
        this.appId = appId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.status = status;
        this.reason = reason;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
