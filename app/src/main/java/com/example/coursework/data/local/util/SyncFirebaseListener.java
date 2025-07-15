package com.example.coursework.data.local.util;


public interface SyncFirebaseListener {
    void syncFailure(String errorMessage);
    void syncSuccess();
}
