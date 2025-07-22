package com.example.coursework.data.local.util;

import com.google.firebase.auth.FirebaseUser;

public interface AuthListener {
    void onFailure(String errorMessage);

    void onSuccess(FirebaseUser user);
}
