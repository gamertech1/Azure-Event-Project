package com.example.eventjava.controller;

import com.example.eventjava.model.Application;
import com.example.eventjava.model.CreateApplicationRequest;
import com.example.eventjava.model.CreateApplicationResponse;
import com.example.eventjava.service.ApplicationService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationControllerTest {

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private ApplicationController controller;

    @Test
    void createApplication_returnsOkWithResponse() {
        CreateApplicationRequest request = new CreateApplicationRequest();
        CreateApplicationResponse expected = new CreateApplicationResponse("APP123", "ACTIVE", "IN_PROCESS");
        when(applicationService.createApplication(request)).thenReturn(expected);

        HttpResponse<CreateApplicationResponse> response = controller.createApplication(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(expected, response.body());
    }

    @Test
    void closeApplication_returnsOkWhenFound() {
        Application app = new Application("APP123", "John", "Doe", "j@d.com", "CLOSED", "COMPLETE");
        when(applicationService.closeApplication("APP123")).thenReturn(app);

        HttpResponse<Application> response = controller.closeApplication("APP123");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(app, response.body());
    }

    @Test
    void closeApplication_returnsNotFoundWhenMissing() {
        when(applicationService.closeApplication("MISSING")).thenReturn(null);

        HttpResponse<Application> response = controller.closeApplication("MISSING");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void getApplicationStatus_returnsOkWithStatusWhenFound() {
        Application app = new Application("APP123", "John", "Doe", "j@d.com", "ACTIVE", "IN_PROCESS");
        when(applicationService.getApplication("APP123")).thenReturn(app);

        HttpResponse<?> response = controller.getApplicationStatus("APP123");

        assertEquals(HttpStatus.OK, response.getStatus());
        ApplicationController.StatusResponse body = (ApplicationController.StatusResponse) response.body();
        assertNotNull(body);
        assertEquals("ACTIVE", body.getStatus());
        assertEquals("IN_PROCESS", body.getReason());
    }

    @Test
    void getApplicationStatus_returnsNotFoundWhenMissing() {
        when(applicationService.getApplication("MISSING")).thenReturn(null);

        HttpResponse<?> response = controller.getApplicationStatus("MISSING");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void updateApplicationStatus_returnsOkWhenUpdated() {
        ApplicationController.StatusUpdateRequest req = new ApplicationController.StatusUpdateRequest();
        req.setAppId("APP123");
        req.setStatus("SUSPENDED");
        when(applicationService.updateApplicationStatus("APP123", "SUSPENDED")).thenReturn(true);

        HttpResponse<?> response = controller.updateApplicationStatus(req);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void updateApplicationStatus_returnsNotFoundWhenMissing() {
        ApplicationController.StatusUpdateRequest req = new ApplicationController.StatusUpdateRequest();
        req.setAppId("MISSING");
        req.setStatus("CLOSED");
        when(applicationService.updateApplicationStatus("MISSING", "CLOSED")).thenReturn(false);

        HttpResponse<?> response = controller.updateApplicationStatus(req);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void updateApplicationStatusByPath_returnsOkWhenUpdated() {
        when(applicationService.updateApplicationStatus("APP123", "CLOSED")).thenReturn(true);

        HttpResponse<?> response = controller.updateApplicationStatusByPath("APP123", "CLOSED");

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void updateApplicationStatusByPath_returnsNotFoundWhenMissing() {
        when(applicationService.updateApplicationStatus("MISSING", "CLOSED")).thenReturn(false);

        HttpResponse<?> response = controller.updateApplicationStatusByPath("MISSING", "CLOSED");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }
}
