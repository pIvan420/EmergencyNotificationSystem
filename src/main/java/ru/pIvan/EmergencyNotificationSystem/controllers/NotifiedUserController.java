package ru.pIvan.EmergencyNotificationSystem.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.pIvan.EmergencyNotificationSystem.services.FileHandlerService;
import ru.pIvan.EmergencyNotificationSystem.services.NotifiedUserService;
import ru.pIvan.EmergencyNotificationSystem.util.validator.InvalidFileFormatException;

import java.io.IOException;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class NotifiedUserController {

    private final NotifiedUserService notifiedUserService;
    private final FileHandlerService fileHandlerService;

    @Autowired
    public NotifiedUserController(NotifiedUserService notifiedUserService,
                                  FileHandlerService fileHandlerService) {
        this.notifiedUserService = notifiedUserService;
        this.fileHandlerService = fileHandlerService;
    }

    @PostMapping(path = "/registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> usersRegistration (@RequestParam("file") MultipartFile file){

        try {
            //Обработка файла и сохранение в БД
            FileHandlerService.ProcessingResult processingResult = fileHandlerService.processFileToNotifyUsers(file);
            notifiedUserService.save(processingResult.getCorrectPhoneNumbers());

            if (processingResult.getBadPhoneNumbers().size() >= 1) {
                String errorMessages = processingResult.getBadPhoneNumbers().stream()
                        .map(badPhoneNumber -> badPhoneNumber.getPhoneNumber() + " " + badPhoneNumber.getErrorMsg())
                        .collect(Collectors.joining("\n"));
                return ResponseEntity.ok("Added " +
                        processingResult.getCorrectPhoneNumbers().size() +
                        " phone numbers\nIncorrect phone numbers:\n" + errorMessages);
            } else {
                return ResponseEntity.ok("Added " +
                        processingResult.getCorrectPhoneNumbers().size() + " phone numbers");
            }
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("I/O error: " + e.getMessage());
        } catch (InvalidFileFormatException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}