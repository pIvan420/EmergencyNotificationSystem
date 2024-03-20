package ru.pIvan.EmergencyNotificationSystem.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pIvan.EmergencyNotificationSystem.models.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
}