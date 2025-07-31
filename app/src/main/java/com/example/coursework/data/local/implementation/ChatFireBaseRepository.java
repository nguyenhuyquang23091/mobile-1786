package com.example.coursework.data.local.implementation;

import android.util.Log;

import com.example.coursework.data.local.entities.Conversation;
import com.example.coursework.data.local.entities.Message;
import com.example.coursework.data.local.entities.User;
import com.example.coursework.data.local.util.OnConversationListener;
import com.example.coursework.data.local.util.OnMessageSentListener;
import com.example.coursework.data.local.util.OnMessagesReceivedListener;
import com.example.coursework.data.local.util.OnUsersReceivedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ChatFireBaseRepository {
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private static final String TAG = "CHAT_REPOSITORY";


    public ChatFireBaseRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
    }
     ListenerRegistration getMessage(String conversationId, OnMessagesReceivedListener listener){
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
     void sendMessage(String conversationId, String messageText, OnMessageSentListener listener){
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser == null ){
            listener.onFailure("User is not authenticated");
            return;
        }
        String currentUserId = currentUser.getUid();
        String currentUserEmail = currentUser.getEmail();
        Message message = new Message(
                conversationId,
                currentUserId,
                currentUserEmail,
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


     void createConversation(String customerEmail, OnConversationListener listener) {

    }

    void getUsers(OnUsersReceivedListener listener){
        FirebaseUser currentUser = getCurrentUser();
        if (currentUser == null) {
            listener.onUsersFailure("User is not authenticated");
            return;
        }
        CollectionReference usersReference = db.collection("users");
        Query query = usersReference.whereEqualTo("role", "customer");
        
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<User> users = new ArrayList<>();
                if (task.getResult() != null) {
                    users = task.getResult().toObjects(User.class);
                }
                listener.onUsersReceived(users);
            } else {
                String errorMessage = task.getException() != null ? 
                    task.getException().getMessage() : "Failed to retrieve users";
                listener.onUsersFailure(errorMessage);
                Log.w(TAG, "Error getting users", task.getException());
            }
        });
    }
    
    void findExistingConversation(String adminId, String customerId, OnConversationListener listener) {

    }


     FirebaseUser getCurrentUser(){
        return firebaseAuth.getCurrentUser();
    }


}





