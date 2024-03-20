package ru.pIvan.EmergencyNotificationSystem.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, KafkaMessage> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, KafkaMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String message, String phone_number){
        KafkaMessage kafkaMessage = new KafkaMessage(message, phone_number);
        kafkaTemplate.send("kafka_test", kafkaMessage);
    }
}