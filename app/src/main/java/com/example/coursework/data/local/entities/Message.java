package com.example.coursework.data.local.entities;

public class Message {
    public String conversationId;
    public String senderId;
    public String senderName;
    public String senderRole;
    public String message;
    public long timestamp;
    public String platform;

    public Message() {
    }


    public Message(String conversationId, String senderId, String senderName,
                   String senderRole, String message, String platform) {
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderRole = senderRole;
        this.message = message;
        this.platform = platform;
        this.timestamp = System.currentTimeMillis();
    }
}
