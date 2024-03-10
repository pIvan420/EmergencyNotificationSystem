package ru.pIvan.EmergencyNotificationSystem.dto;

import jakarta.validation.constraints.NotEmpty;

public class NotifiedUserDTO {

    @NotEmpty(message = "Phone number cannot be empty")
    private String phone_number;

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    @Override
    public String toString() {
        return "NotifyUser{" +
                "phone_number='" + phone_number + '\'' +
                '}';
    }
}