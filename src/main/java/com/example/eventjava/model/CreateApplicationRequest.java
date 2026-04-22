package com.example.eventjava.model;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class CreateApplicationRequest {

    private String firstName;
    private String lastName;
    private String email;

    public CreateApplicationRequest() {
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
}
