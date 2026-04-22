package com.example.eventjava.controller;

import com.example.eventjava.model.Application;
import com.example.eventjava.model.CreateApplicationRequest;
import com.example.eventjava.model.CreateApplicationResponse;
import com.example.eventjava.service.ApplicationService;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;

@Controller("/application")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Post
    public HttpResponse<CreateApplicationResponse> createApplication(@Body CreateApplicationRequest request) {
        System.out.println("Received create application request: " + request);
        CreateApplicationResponse response = applicationService.createApplication(request);
        System.out.println("Created application response: " + response);
        return HttpResponse.ok(response);
    }

    @Put("/{appId}")
    public HttpResponse<Application> closeApplication(@PathVariable String appId) {
        System.out.println("Received close application request for appId: " + appId);
        Application app = applicationService.closeApplication(appId);
        if (app != null) {
            System.out.println("Closed application: " + app);
            return HttpResponse.ok(app);
        } else {
            System.out.println("Application not found: " + appId);
            return HttpResponse.notFound();
        }
    }

    @Get("/{appId}/status")
    public HttpResponse<?> getApplicationStatus(@PathVariable String appId) {
        Application app = applicationService.getApplication(appId);
        if (app != null) {
            return HttpResponse.ok(new StatusResponse(app.getStatus(), app.getReason()));
        } else {
            return HttpResponse.notFound();
        }
    }

    /**
     * Update application status by appId and status. Example payload: {
     * "appId": "abc123", "status": "ACTIVE" }
     */
    @Post("/status")
    public HttpResponse<?> updateApplicationStatus(@Body StatusUpdateRequest request) {
        boolean updated = applicationService.updateApplicationStatus(request.getAppId(), request.getStatus());
        if (updated) {
            return HttpResponse.ok("Status updated");
        } else {
            return HttpResponse.notFound();
        }
    }

    /**
     * Update application status by appId and status via path variables.
     * Example: POST /application/{appId}/{status}
     */
    @Post("/{appId}/{status}")
    public HttpResponse<?> updateApplicationStatusByPath(@PathVariable String appId, @PathVariable String status) {
        boolean updated = applicationService.updateApplicationStatus(appId, status);
        if (updated) {
            return HttpResponse.ok("Status updated");
        } else {
            return HttpResponse.notFound();
        }
    }

    public static class StatusResponse {

        private String status;
        private String reason;

        public StatusResponse(String status, String reason) {
            this.status = status;
            this.reason = reason;
        }

        public String getStatus() {
            return status;
        }

        public String getReason() {
            return reason;
        }
    }

    public static class StatusUpdateRequest {

        private String appId;
        private String status;

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
    }
}
