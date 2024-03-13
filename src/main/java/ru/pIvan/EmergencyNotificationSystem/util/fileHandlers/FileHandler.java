package ru.pIvan.EmergencyNotificationSystem.util.fileHandlers;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileHandler {
    public List<String> process(MultipartFile file) throws IOException;
}