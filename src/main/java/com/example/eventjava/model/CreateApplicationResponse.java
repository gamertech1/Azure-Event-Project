package com.example.eventjava.model;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class CreateApplicationResponse {

    private String appId;
    private String status;
    private String reason;

    public CreateApplicationResponse() {
    }

    public CreateApplicationResponse(String appId, String status, String reason) {
        this.appId = appId;
        this.status = status;
        this.reason = reason;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
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
