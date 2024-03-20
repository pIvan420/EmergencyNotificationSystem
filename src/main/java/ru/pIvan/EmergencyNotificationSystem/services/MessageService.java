package ru.pIvan.EmergencyNotificationSystem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pIvan.EmergencyNotificationSystem.models.Message;
import ru.pIvan.EmergencyNotificationSystem.models.NotifiedUser;
import ru.pIvan.EmergencyNotificationSystem.repositories.MessageRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MessageService {

    private final NotifiedUserService notifiedUserService;
    private final MessageRepository messageRepository;
    private final PhoneMessageService phoneMessageService;
    private static final int batchSize = 10000;

    @Autowired
    public MessageService(NotifiedUserService notifiedUserService, MessageRepository messageRepository, PhoneMessageService phoneMessageService) {
        this.notifiedUserService = notifiedUserService;
        this.messageRepository = messageRepository;
        this.phoneMessageService = phoneMessageService;
    }

    @Transactional
    public void saveAndNotifyAll(Message message){
        List<NotifiedUser> notifyUsers = notifiedUserService.getAll();

        save(message);

        for(int i = 0; i < notifyUsers.size(); i += batchSize){
            phoneMessageService.saveAndNotify(message,
                    notifyUsers.subList(i , Math.min(i + batchSize, notifyUsers.size())));
        }
    }

    @Transactional
    public void save(Message message){
        messageRepository.save(message);
    }

    public List<Message> getAll(){
        return messageRepository.findAll();
    }
}