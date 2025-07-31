package com.example.coursework.data.local.entities;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.util.List;

public class Conversation {

    @Exclude
    public String id;
    public List<String> users;
    public Timestamp createdAt;
    public String lastMessage;


    public Conversation() {
    }

    public Conversation(String id, List<String> users, long createdAt, String lastMessage) {
        this.id = id;
        this.users = users;
        this.createdAt = Timestamp.now();
        this.lastMessage = "Conversation started";
    }
}
