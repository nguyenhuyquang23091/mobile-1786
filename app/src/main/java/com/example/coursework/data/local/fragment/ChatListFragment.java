package com.example.coursework.data.local.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.coursework.R;
import com.example.coursework.data.local.adapter.ConversationAdapter;
import com.example.coursework.data.local.entities.Conversation;
import com.example.coursework.data.local.implementation.ChatRepositoryImplementation;
import com.example.coursework.data.local.repository.ChatRepository;
import com.example.coursework.data.local.util.OnConversationsReceivedListener;
import com.example.coursework.databinding.FragmentChatListBinding;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment implements ConversationAdapter.OnConversationClickListener {

    private FragmentChatListBinding binding;
    private ConversationAdapter conversationAdapter;
    private ChatRepository chatRepository;
    private ListenerRegistration conversationListener;
    private List<Conversation> conversationList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupRepository();

    }

    private void setupRecyclerView() {
        conversationList = new ArrayList<>();
        conversationAdapter = new ConversationAdapter(conversationList, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerViewConversations.setLayoutManager(layoutManager);
        binding.recyclerViewConversations.setAdapter(conversationAdapter);
    }

    private void setupRepository() {
        chatRepository = new ChatRepositoryImplementation(requireActivity().getApplication());
    }

    private void startListeningForConversations() {
        showLoading(true);
        
        // Add debug logging
        Log.d("ChatListFragment", "Starting to listen for conversations...");

        conversationListener = chatRepository.getConversations(new OnConversationsReceivedListener() {
            @Override
            public void onConversationsReceived(List<Conversation> conversations) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        conversationAdapter.updateConversations(conversations);

                        if (conversations.isEmpty()) {
                            showEmptyState(true);
                        } else {
                            showEmptyState(false);
                        }
                    });
                }
            }

            @Override
            public void onConversationFailure(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showEmptyState(true);
                        Toast.makeText(getContext(), "Failed to load conversations: " + error,
                                Toast.LENGTH_SHORT).show();
                        Log.d("Error", error);
                    });
                }
            }
        });
    }

    private void showLoading(boolean show) {
        binding.shimmerLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            binding.shimmerLayout.startShimmer();
        } else {
            binding.shimmerLayout.stopShimmer();
        }
        binding.recyclerViewConversations.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState(boolean show) {
        binding.emptyStateLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.recyclerViewConversations.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onConversationClick(Conversation conversation) {
        // Navigate to ChatFragment with conversationId
        NavController navController = Navigation.findNavController(requireView());
        Bundle args = new Bundle();
        args.putString("conversationId", conversation.id);
        navController.navigate(R.id.action_chatListFragment_to_chatFragment, args);
    }

    @Override
    public void onResume() {
        super.onResume();
        startListeningForConversations();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (conversationListener != null) {
            conversationListener.remove();
            conversationListener = null;
        }

        binding = null;
    }
}
