package com.example.coursework.data.local.util;

import com.example.coursework.data.local.entities.Conversation;

import java.util.List;

public interface OnConversationsReceivedListener {
    void onConversationsReceived(List<Conversation> conversations);
    void onConversationFailure(String error);
}