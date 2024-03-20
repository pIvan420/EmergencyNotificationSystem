package ru.pIvan.EmergencyNotificationSystem.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class KafkaMessage {
    private String message;
    private String phone_number;

    public KafkaMessage() {
    }

    public KafkaMessage(String message, String phone_number) {
        this.message = message;
        this.phone_number = phone_number;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    @Override
    public String toString() {
        return "KafkaMessage{" +
                "message='" + message + '\'' +
                ", phone_number='" + phone_number + '\'' +
                '}';
    }
}