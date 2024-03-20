package ru.pIvan.EmergencyNotificationSystem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pIvan.EmergencyNotificationSystem.kafka.KafkaProducer;
import ru.pIvan.EmergencyNotificationSystem.models.Message;
import ru.pIvan.EmergencyNotificationSystem.models.NotifiedUser;
import ru.pIvan.EmergencyNotificationSystem.models.PhoneMessage;
import ru.pIvan.EmergencyNotificationSystem.repositories.PhoneMessageRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PhoneMessageService {

    private final PhoneMessageRepository phoneMessageRepository;
    private final KafkaProducer kafkaProducer;

    @Autowired
    public PhoneMessageService(PhoneMessageRepository phoneMessageRepository, KafkaProducer kafkaProducer) {
        this.phoneMessageRepository = phoneMessageRepository;
        this.kafkaProducer = kafkaProducer;
    }


    @Async
    @Transactional
    public void saveAndNotify(Message message, List<NotifiedUser> notifyUsers){
        List<PhoneMessage> phoneMessages = notifyUsers
                .stream()
                .map(phone -> new PhoneMessage(message, phone, true))
                .toList();

        phoneMessageRepository.saveAll(phoneMessages);

        for(NotifiedUser notifyUser: notifyUsers)
            kafkaProducer.send(message.getMessage(), notifyUser.getPhoneNumber());
    }

}