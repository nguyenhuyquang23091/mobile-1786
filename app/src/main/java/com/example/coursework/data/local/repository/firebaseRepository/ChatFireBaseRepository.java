package com.example.coursework.data.local.repository.firebaseRepository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatFireBaseRepository {
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    public ChatFireBaseRepository() {
        this.firestore = FirebaseFirestore.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
    }




}
