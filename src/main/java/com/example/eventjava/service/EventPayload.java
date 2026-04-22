package com.example.eventjava.service;

import com.example.eventjava.model.Application; // Add this import

public class EventPayload {

    private String appId;
    private String status;
    private String reason;
    private String eventid;
    private Application application; // Add this field

    public EventPayload(String appId, String status, String reason, String eventid, Application application) {
        this.appId = appId;
        this.status = status;
        this.reason = reason;
        this.eventid = eventid;
        this.application = application;
    }

    public String getAppId() {
        return appId;
    }

    public String getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public String getEventid() {
        return eventid;
    }

    public Application getApplication() {
        return application;
    }
}
