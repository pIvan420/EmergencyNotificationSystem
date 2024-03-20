package ru.pIvan.EmergencyNotificationSystem.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.pIvan.EmergencyNotificationSystem.models.Message;
import ru.pIvan.EmergencyNotificationSystem.services.MessageService;

@Controller
@RequestMapping("/message")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> notifyUser(@RequestParam("message") String message){
        messageService.saveAndNotifyAll(new Message(message));
        return ResponseEntity.ok().body("Ok");
    }
}