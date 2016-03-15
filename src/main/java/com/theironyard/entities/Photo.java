package com.theironyard.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Created by alexanderhughes on 3/15/16.
 */
@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue
    private int id;
    @ManyToOne
    private User sender;
    @ManyToOne
    private User recipient;
    @NotNull
    private String fileName;
    @NotNull
    int timeToView;
    LocalDateTime time;

    public Photo() {
    }

    public Photo(User sender, User recipient, String fileName, int timeToView) {
        this.sender = sender;
        this.recipient = recipient;
        this.fileName = fileName;
        this.timeToView = timeToView;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public int getTimeToView() {
        return timeToView;
    }

    public void setTimeToView(int timeToView) {
        this.timeToView = timeToView;
    }
}
