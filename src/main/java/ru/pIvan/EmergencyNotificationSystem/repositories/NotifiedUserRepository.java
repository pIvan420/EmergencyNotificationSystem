package ru.pIvan.EmergencyNotificationSystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pIvan.EmergencyNotificationSystem.models.NotifiedUser;

import java.util.Optional;

@Repository
public interface NotifiedUserRepository extends JpaRepository<NotifiedUser, String> {
    Optional<NotifiedUser> findByPhoneNumber(String number);
}