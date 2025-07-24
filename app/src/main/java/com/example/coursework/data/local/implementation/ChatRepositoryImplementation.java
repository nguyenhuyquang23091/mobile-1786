package com.example.coursework.data.local.implementation;

import android.app.Application;

import com.example.coursework.data.local.repository.ChatRepository;
import com.example.coursework.data.local.repository.firebaseRepository.ChatFireBaseRepository;
import com.example.coursework.data.local.util.ConnectivityCheck;
import com.example.coursework.data.local.util.OnMessagesReceivedListener;

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
    public void getMessage(String conversationId, OnMessagesReceivedListener listener) {
        if(isConnected()){
            chatFireBaseRepository.getMessage(conversationId, listener);
        } else {
            if (listener != null ){
                listener.onMessageFailure("No internet Connection");
            }
        }
    }
}
