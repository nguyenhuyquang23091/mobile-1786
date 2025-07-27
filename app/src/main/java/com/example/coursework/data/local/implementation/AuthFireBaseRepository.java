package com.example.coursework.data.local.implementation;

import com.example.coursework.data.local.util.AuthListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class AuthFireBaseRepository {
    private FirebaseAuth firebaseAuth;

    public AuthFireBaseRepository(){
         this.firebaseAuth = FirebaseAuth.getInstance();
    }
     void createUser(String email, String password, AuthListener listener){
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                listener.onSuccess(firebaseAuth.getCurrentUser());
            } else {
               listener.onFailure(Objects.requireNonNull(task.getException()).getMessage());
            }
        });

    }
}



