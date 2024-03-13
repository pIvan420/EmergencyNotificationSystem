package ru.pIvan.EmergencyNotificationSystem.util.validator;

public class InvalidFileFormatException extends Exception{
    public InvalidFileFormatException(String message) {
        super("Unsupported file extension: " + message);
    }
}