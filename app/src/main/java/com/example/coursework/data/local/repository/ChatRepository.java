package com.example.coursework.data.local.repository;

import com.example.coursework.data.local.util.OnConversationListener;
import com.example.coursework.data.local.util.OnConversationsReceivedListener;
import com.example.coursework.data.local.util.OnMessageSentListener;
import com.example.coursework.data.local.util.OnMessagesReceivedListener;
import com.google.firebase.firestore.ListenerRegistration;

public interface ChatRepository {
    ListenerRegistration getMessage(String conversationId, OnMessagesReceivedListener listener);
    void sendMessage(String conversationId, String messageText, OnMessageSentListener listener);
    void createConversation(String userId, OnConversationListener listener);
    ListenerRegistration getConversations(OnConversationsReceivedListener listener);
}
