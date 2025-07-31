package com.example.coursework.data.local.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.coursework.data.local.adapter.MessageAdapter;
import com.example.coursework.data.local.entities.Message;
import com.example.coursework.data.local.implementation.ChatRepositoryImplementation;
import com.example.coursework.data.local.repository.ChatRepository;
import com.example.coursework.data.local.util.OnMessageSentListener;
import com.example.coursework.data.local.util.OnMessagesReceivedListener;
import com.example.coursework.databinding.FragmentChatBinding;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private MessageAdapter messageAdapter;
    private ChatRepository chatRepository;
    private ListenerRegistration messageListener;
    private List<Message> messageList;
    private String conversationId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Get conversationId from arguments
        if (getArguments() != null) {
            conversationId = getArguments().getString("conversationId", "default_conversation");
        } else {
            conversationId = "default_conversation";
        }
        
        setupRecyclerView();
        setupRepository();
        setupSendButton();
        setupBackButton();

    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true); // Show latest messages at bottom
        
        binding.messagesRecyclerView.setLayoutManager(layoutManager);
        binding.messagesRecyclerView.setAdapter(messageAdapter);
    }

    private void setupRepository() {
        chatRepository = new ChatRepositoryImplementation(requireActivity().getApplication());
    }

    private void setupSendButton() {
        binding.sendButton.setOnClickListener(v -> {
            String messageText = binding.messageInput.getText().toString().trim();
            
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
                binding.messageInput.setText(""); // Clear input field
            }
        });
    }

    private void setupBackButton() {
        binding.backButton.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(requireView()).navigateUp();
        });
    }

    private void startListeningForMessages() {
        messageListener = chatRepository.getMessage(conversationId, new OnMessagesReceivedListener() {
            @Override
            public void onMessagesReceived(List<Message> messages) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        messageAdapter.updateMessages(messages);
                        
                        // Scroll to bottom to show latest message
                        if (!messages.isEmpty()) {
                            binding.messagesRecyclerView.scrollToPosition(messages.size() - 1);
                        }
                    });
                }
            }

            @Override
            public void onMessageFailure(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Failed to load messages: " + error, 
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void sendMessage(String messageText) {
        chatRepository.sendMessage(conversationId, messageText, new OnMessageSentListener() {
            @Override
            public void onSuccess() {
                // Message sent successfully - real-time listener will updateYogaCourse UI
            }

            @Override
            public void onFailure(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Failed to send message: " + error, 
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        startListeningForMessages();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        // Remove the message listener to prevent memory leaks
        if (messageListener != null) {
            messageListener.remove();
            messageListener = null;
        }
        
        binding = null;

    }

}