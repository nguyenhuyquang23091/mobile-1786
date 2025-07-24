package com.example.coursework.data.local.repository;

import com.example.coursework.data.local.util.OnMessagesReceivedListener;
import com.google.firebase.firestore.ListenerRegistration;

public interface ChatRepository {
    ListenerRegistration getMessage(String conversationId, OnMessagesReceivedListener listener);


}
