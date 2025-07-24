package com.example.coursework.data.local.util;

public interface OnMessageSentListener {

    void onSuccess();
    void onFailure(String error);
}
