package com.example.coursework.data.local.util;

import com.example.coursework.data.local.entities.Message;

import java.util.List;

public interface OnMessagesReceivedListener {
    void onMessagesReceived(List<Message> messages);
    void onMessageFailure(String error);

}
