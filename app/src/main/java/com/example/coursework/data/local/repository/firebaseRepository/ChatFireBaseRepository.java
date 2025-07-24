package com.example.coursework.data.local.repository.firebaseRepository;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.coursework.data.local.entities.Message;
import com.example.coursework.data.local.util.OnMessagesReceivedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatFireBaseRepository {
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;


    private static final String TAG = "CHAT_REPOSITORY";

    public ChatFireBaseRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public void getMessage(String conservationId, OnMessagesReceivedListener listener){
        db.collection("conversations").document(conservationId).collection("messages")
                .orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null ){
                            Log.w(TAG, "Listen failed", error);
                            listener.onMessageFailure(error.getMessage());
                            return;
                        }
                        List<Message> messages = new ArrayList<>();
                        if(value != null ){
                            messages = value.toObjects(Message.class);
                        }
                        listener.onMessagesReceived(messages);

                    }
                });

    }









}
