package com.example.coursework.data.local.repository;

import com.example.coursework.data.local.util.OnMessagesReceivedListener;

public interface ChatRepository {
    void getMessage(String conversationId, OnMessagesReceivedListener listener);

}
