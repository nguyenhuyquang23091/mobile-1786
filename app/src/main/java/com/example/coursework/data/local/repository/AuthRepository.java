package com.example.coursework.data.local.repository;

import androidx.lifecycle.LiveData;

import com.example.coursework.data.local.util.AuthListener;
import com.google.firebase.auth.FirebaseUser;

public interface AuthRepository {
    LiveData<FirebaseUser> getCurrentData();

    void signUp(String email, String password, AuthListener listener);

    void signIn(String email, String password, AuthListener listener);
    void signOut();
    void resetPassword(String email);
}
