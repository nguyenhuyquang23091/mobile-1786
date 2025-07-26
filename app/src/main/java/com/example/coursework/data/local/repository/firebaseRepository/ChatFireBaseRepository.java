package com.example.coursework.data.local.repository.firebaseRepository;

import android.util.Log;

import com.example.coursework.data.local.entities.Conversation;
import com.example.coursework.data.local.entities.Message;
import com.example.coursework.data.local.util.OnConversationListener;
import com.example.coursework.data.local.util.OnMessageSentListener;
import com.example.coursework.data.local.util.OnMessagesReceivedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChatFireBaseRepository {
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

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



    public void createConversation(String userId, OnConversationListener listener) {
        FirebaseUser currentUser = getCurrentUser();
        if (currentUser == null) {
            listener.onFailure("User is not authenticated");
            return;
        }

        String currentUserId = currentUser.getUid();
        List<String> users = Arrays.asList(currentUserId, userId);
        Collections.sort(users);

        String conversationId = users.get(0) + "" + users.get(1);
        DocumentReference documentReference = db.collection("conversations").document(conversationId);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // Conversation already exists, just return the ID
                    listener.onSuccess(conversationId);
                } else {
                    // Conversation doesn't exist, create it
                    Conversation newConversation = new Conversation(conversationId, users, System.currentTimeMillis(), "Conversation started");
                    documentReference.set(newConversation)
                            .addOnSuccessListener(aVoid -> listener.onSuccess(conversationId))
                            .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                }
            } else {
                listener.onFailure(task.getException().getMessage());
            }
        });

    }

    public FirebaseUser getCurrentUser(){
        return firebaseAuth.getCurrentUser();
    }




}





