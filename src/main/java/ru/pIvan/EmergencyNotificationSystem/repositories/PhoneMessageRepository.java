package ru.pIvan.EmergencyNotificationSystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pIvan.EmergencyNotificationSystem.models.PhoneMessage;

public interface PhoneMessageRepository extends JpaRepository<PhoneMessage, String> {
}