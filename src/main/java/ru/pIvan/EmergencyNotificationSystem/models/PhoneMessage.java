package ru.pIvan.EmergencyNotificationSystem.models;


import jakarta.persistence.*;

@Entity
@Table(name = "message_phone_relations")
public class PhoneMessage {

    @Id
    @Column(name = "relation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    @ManyToOne
    @JoinColumn(name = "phone_id")
    private NotifiedUser notifiedUser;

    @Column(name = "status")
    private boolean status;

    public PhoneMessage() {
    }

    public PhoneMessage(Message message, NotifiedUser notifiedUser, boolean status) {
        this.message = message;
        this.notifiedUser = notifiedUser;
        this.status = status;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public NotifiedUser getNotifiedUser() {
        return notifiedUser;
    }

    public void setNotifiedUser(NotifiedUser notifiedUser) {
        this.notifiedUser = notifiedUser;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}