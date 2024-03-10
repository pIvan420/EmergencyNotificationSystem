package ru.pIvan.EmergencyNotificationSystem.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.pIvan.EmergencyNotificationSystem.services.NotifiedUserService;

@Controller
@RequestMapping("/user")
public class NotifiedUserController {

    private final NotifiedUserService notifiedUserService;

    @Autowired
    public NotifiedUserController(NotifiedUserService notifiedUserService) {
        this.notifiedUserService = notifiedUserService;
    }
}