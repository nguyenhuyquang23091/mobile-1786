package com.example.coursework.data.local.util;

public interface OnConversationListener {
    void onSuccess(String conversationId);
    void onFailure(String errorMessage);

}
