package ru.pIvan.EmergencyNotificationSystem.util.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.pIvan.EmergencyNotificationSystem.models.NotifiedUser;
import ru.pIvan.EmergencyNotificationSystem.services.NotifiedUserService;

@Component
public class NotifiedUserValidator implements Validator {
    private final NotifiedUserService notifyUserService;

    @Autowired
    public NotifiedUserValidator(NotifiedUserService notifyUserService) {
        this.notifyUserService = notifyUserService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return NotifiedUser.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NotifiedUser notifyUser = (NotifiedUser) target;

        if(notifyUserService.show(notifyUser.getPhoneNumber()).isPresent()){
            errors.rejectValue("phoneNumber", "",
                    "This number is already included in the alert");
        }
    }
}