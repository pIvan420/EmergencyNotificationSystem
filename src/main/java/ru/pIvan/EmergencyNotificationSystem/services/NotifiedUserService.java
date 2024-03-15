package ru.pIvan.EmergencyNotificationSystem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pIvan.EmergencyNotificationSystem.models.NotifiedUser;
import ru.pIvan.EmergencyNotificationSystem.repositories.NotifiedUserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class NotifiedUserService {

    private final NotifiedUserRepository notifiedUserRepository;

    @Autowired
    public NotifiedUserService(NotifiedUserRepository notifiedUserRepository) {
        this.notifiedUserRepository = notifiedUserRepository;
    }

    @Transactional
    public void save(Set<NotifiedUser> users){
        notifiedUserRepository.saveAll(users);
    }

    public List<NotifiedUser> getAll(){
        return notifiedUserRepository.findAll();
    }
    public Optional<NotifiedUser> show(String phoneNumber){
        return notifiedUserRepository.findByPhoneNumber(phoneNumber);
    }
}