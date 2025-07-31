package com.example.coursework.data.local.implementation;

import com.example.coursework.data.local.entities.User;
import com.example.coursework.data.local.service.FirebaseMessage;
import com.example.coursework.data.local.util.AuthListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
public class AuthFireBaseRepository {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    public AuthFireBaseRepository(){
         this.firebaseAuth = FirebaseAuth.getInstance();
         this.db = FirebaseFirestore.getInstance();
    }
     void createUser(String email, String password, AuthListener listener){
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                listener.onSuccess(firebaseAuth.getCurrentUser());

                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    User user = new User(
                            email,
                            "admin"
                    );

                    db.collection("users")
                            .document(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())
                            .set(user).addOnSuccessListener(aVoid -> {listener.onSuccess(currentUser);})
                            .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                }

            } else {
               listener.onFailure(Objects.requireNonNull(task.getException()).getMessage());
            }
        });

    }

    void signInWithEmailAndPassword(String email, String password, AuthListener listener) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    listener.onSuccess(currentUser);
                } else {
                    listener.onFailure("Sign in failed: User is null");
                }
            } else {
                listener.onFailure(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }
}



