package com.example.eventjava.service;

import com.example.eventjava.model.Application;
import com.example.eventjava.model.CreateApplicationRequest;
import com.example.eventjava.model.CreateApplicationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private EventGridService eventGridService;

    @InjectMocks
    private ApplicationService applicationService;

    private CreateApplicationRequest buildRequest(String firstName, String lastName, String email) {
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        return request;
    }

    @Test
    void createApplication_returnsResponseWithActiveStatus() {
        CreateApplicationResponse response = applicationService.createApplication(
                buildRequest("John", "Doe", "john@example.com"));

        assertNotNull(response);
        assertNotNull(response.getAppId());
        assertEquals(20, response.getAppId().length());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals("IN_PROCESS", response.getReason());
    }

    @Test
    void createApplication_generatesUniqueAppIds() {
        CreateApplicationResponse r1 = applicationService.createApplication(buildRequest("A", "B", "a@b.com"));
        CreateApplicationResponse r2 = applicationService.createApplication(buildRequest("C", "D", "c@d.com"));

        assertNotEquals(r1.getAppId(), r2.getAppId());
    }

    @Test
    void getApplication_returnsNullForUnknownId() {
        assertNull(applicationService.getApplication("UNKNOWN_ID"));
    }

    @Test
    void getApplication_returnsAppAfterCreate() {
        String appId = applicationService.createApplication(
                buildRequest("Jane", "Smith", "jane@example.com")).getAppId();

        Application app = applicationService.getApplication(appId);

        assertNotNull(app);
        assertEquals("Jane", app.getFirstName());
        assertEquals("Smith", app.getLastName());
        assertEquals("ACTIVE", app.getStatus());
    }

    @Test
    void updateApplicationStatus_returnsTrueAndUpdatesStatus() {
        String appId = applicationService.createApplication(
                buildRequest("A", "B", "a@b.com")).getAppId();

        boolean result = applicationService.updateApplicationStatus(appId, "SUSPENDED");

        assertTrue(result);
        assertEquals("SUSPENDED", applicationService.getApplication(appId).getStatus());
    }

    @Test
    void updateApplicationStatus_returnsFalseForUnknownId() {
        assertFalse(applicationService.updateApplicationStatus("NONEXISTENT", "CLOSED"));
    }

    @Test
    void closeApplication_changesStatusToClosedAndPublishesEvents() throws Exception {
        String appId = applicationService.createApplication(
                buildRequest("X", "Y", "x@y.com")).getAppId();

        Application app = applicationService.closeApplication(appId);

        assertNotNull(app);
        assertEquals("CLOSED", app.getStatus());
        assertEquals("COMPLETE", app.getReason());
        verify(eventGridService, times(2)).publishEvent(anyString(), anyString(), any());
    }

    @Test
    void closeApplication_returnsNullForUnknownId() {
        assertNull(applicationService.closeApplication("UNKNOWN_ID"));
        verifyNoInteractions(eventGridService);
    }

    @Test
    void closeApplication_doesNotPublishEventsWhenAlreadyClosed() {
        String appId = applicationService.createApplication(
                buildRequest("A", "B", "a@b.com")).getAppId();
        applicationService.updateApplicationStatus(appId, "CLOSED");
        clearInvocations(eventGridService);

        applicationService.closeApplication(appId);

        verifyNoInteractions(eventGridService);
    }

    @Test
    void restoreApplication_addsApplicationIfAbsent() {
        applicationService.restoreApplication("APP001", "First", "Last", "f@l.com", "ACTIVE", "IN_PROCESS");

        Application app = applicationService.getApplication("APP001");

        assertNotNull(app);
        assertEquals("APP001", app.getAppId());
        assertEquals("First", app.getFirstName());
        assertEquals("ACTIVE", app.getStatus());
    }

    @Test
    void restoreApplication_doesNotOverwriteExistingApplication() {
        applicationService.restoreApplication("APP002", "Original", "Last", "o@l.com", "ACTIVE", "IN_PROCESS");
        applicationService.restoreApplication("APP002", "Updated", "Last", "u@l.com", "CLOSED", "COMPLETE");

        Application app = applicationService.getApplication("APP002");

        assertEquals("Original", app.getFirstName());
        assertEquals("ACTIVE", app.getStatus());
    }
}
