package com.example.coursework.data.local.implementation;

import android.app.Application;

import com.example.coursework.data.local.repository.ChatRepository;
import com.example.coursework.data.local.repository.firebaseRepository.ChatFireBaseRepository;
import com.example.coursework.data.local.util.ConnectivityCheck;
import com.example.coursework.data.local.util.OnConversationListener;
import com.example.coursework.data.local.util.OnConversationsReceivedListener;
import com.example.coursework.data.local.util.OnMessageSentListener;
import com.example.coursework.data.local.util.OnMessagesReceivedListener;
import com.google.firebase.firestore.ListenerRegistration;

public class ChatRepositoryImplementation implements ChatRepository {

    private ChatFireBaseRepository chatFireBaseRepository;
    private ConnectivityCheck connectivityCheck;


    public ChatRepositoryImplementation(Application application) {
        this.chatFireBaseRepository = new ChatFireBaseRepository();
        this.connectivityCheck = new ConnectivityCheck(application);
        this.connectivityCheck.RegisterNetworkCallback();
    }

    public boolean isConnected(){
        return connectivityCheck.isConnected();
    }
    @Override
    public ListenerRegistration getMessage(String conversationId, OnMessagesReceivedListener listener) {
        if(isConnected()){
            return chatFireBaseRepository.getMessage(conversationId, listener);
        } else {
            if (listener != null ){
                listener.onMessageFailure("No internet Connection");
            }
            return null;
        }
    }

    @Override
    public void sendMessage(String conversationId, String messageText, OnMessageSentListener listener) {
        if(isConnected()){
            chatFireBaseRepository.sendMessage(conversationId, messageText, listener);
        } else {
            if(listener != null ){
                listener.onFailure("No internet connected");
            }
        }

    }

    @Override
    public void createConversation(String userId, OnConversationListener listener) {
        if(isConnected()){
            chatFireBaseRepository.createConversation(userId, listener);
        } else {
            if(listener != null ){
                listener.onFailure("No internet connected");
            }
        }

    }

    @Override
    public ListenerRegistration getConversations(OnConversationsReceivedListener listener) {
        if(isConnected()){
            return chatFireBaseRepository.getConversations(listener);
        } else {
            if(listener != null ){
                listener.onConversationFailure("No internet connection available");
            }
            return null;
        }
    }
    }

