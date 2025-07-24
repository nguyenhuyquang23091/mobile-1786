package com.example.coursework.data.local.repository.firebaseRepository;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.coursework.data.local.entities.Message;
import com.example.coursework.data.local.util.OnMessageSentListener;
import com.example.coursework.data.local.util.OnMessagesReceivedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
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
    public ListenerRegistration getMessage(String conversationId, OnMessagesReceivedListener listener){
      return db.collection("conversations")
                .document(conversationId).collection("messages")
                .orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {
                    if(error != null ){
                        Log.w(TAG, "Listen failed", error);
                        listener.onMessageFailure(error.getMessage());
                        return;
                    }
                    List<Message> messages = new ArrayList<>();
                    if(value != null ){
                        messages = value.toObjects(Message.class);
                    }
                    if (listener != null) {
                        listener.onMessagesReceived(messages);
                    }
                });
    }
    public void sendMessage(String conversationId, String messageText, OnMessageSentListener listener){
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser == null ){
            listener.onFailure("User is not authenticated");
        }
        String currenrUserId = currentUser.getUid();
        String currentUserEmail = currentUser.getEmail();
        Message message = new Message(
                conversationId,
                currenrUserId,
                "Huy Quang",
                "Admin",
                messageText,
                "Android"
        );
        db.collection("conversations").document(conversationId).collection("messages")
                .add(message).addOnSuccessListener(documentReference -> {
                    Log.d("Message sent successfully", "Message sent successfully");
                    if(listener != null ){
                        listener.onSuccess();
                    }
                }).addOnFailureListener(e -> {
                    Log.d("Message sent failed", "Message sent failed");
                    if(listener != null ){
                        listener.onFailure(e.getMessage());
                    }
                });
    }

    public void createConversation




}





