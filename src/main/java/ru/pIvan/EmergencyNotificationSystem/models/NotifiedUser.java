package ru.pIvan.EmergencyNotificationSystem.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.util.Objects;

@Entity
@Table(name = "phone_numbers")
public class NotifiedUser {
    @Id
    @Column(name = "phone_number")
    @NotEmpty(message = "Phone number cannot be empty")
    private String phoneNumber;

    public NotifiedUser() {
    }

    public NotifiedUser(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "NotifyUser{" +
                "phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}