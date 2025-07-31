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
import com.example.coursework.data.local.adapter.UserAdapter;
import com.example.coursework.data.local.entities.User;
import com.example.coursework.data.local.implementation.ChatRepositoryImplementation;
import com.example.coursework.data.local.repository.ChatRepository;
import com.example.coursework.data.local.util.OnUsersReceivedListener;
import com.example.coursework.data.local.util.OnConversationListener;
import com.example.coursework.databinding.FragmentUserListBinding;

import java.util.ArrayList;
import java.util.List;

public class ConversationFragent extends Fragment implements UserAdapter.OnUserClickListener {
    private FragmentUserListBinding binding;
    private UserAdapter userAdapter;
    private ChatRepository chatRepository;
    private List<User> userList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupRepository();

    }

    private void setupRecyclerView() {
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.usersRecyclerView.setLayoutManager(layoutManager);
        binding.usersRecyclerView.setAdapter(userAdapter);
    }

    private void setupRepository() {
        chatRepository = new ChatRepositoryImplementation(requireActivity().getApplication());
    }

    private void loadUsers() {
        showLoading(true);
        
        // Step 1: Retrieve available users
        Log.d("ConversationFragent", "Loading available users...");

        chatRepository.getUsers(new OnUsersReceivedListener() {
            @Override
            public void onUsersReceived(List<User> users) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        userAdapter.updateUsers(users);

                        if (users.isEmpty()) {
                            showEmptyState(true);
                        } else {
                            showEmptyState(false);
                        }
                    });
                }
            }

            @Override
            public void onUsersFailure(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showEmptyState(true);
                        Toast.makeText(getContext(), "Failed to load users: " + error,
                                Toast.LENGTH_SHORT).show();
                        Log.d("Error", error);
                    });
                }
            }
        });
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.usersRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState(boolean show) {
        binding.emptyStateText.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.usersRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onUserClick(User user) {
        // Step 2: Select a user and initiate a chat
        String customerId = extractUserIdFromUser(user);
        
        if (customerId == null || customerId.isEmpty()) {
            Toast.makeText(getContext(), "Unable to identify user", Toast.LENGTH_SHORT).show();
            return;
        }
        
        showLoading(true);
        
        // Step 3-5: Check existing conversation, load previous data or create new conversation
        chatRepository.createConversation(customerId, new OnConversationListener() {
            @Override
            public void onSuccess(String conversationId) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        
                        // Navigate to chat with the conversation ID
                        NavController navController = Navigation.findNavController(requireView());
                        Bundle args = new Bundle();
                        args.putString("conversationId", conversationId);
                        args.putString("userEmail", user.email);
                        navController.navigate(R.id.action_chatListFragment_to_chatFragment, args);
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(getContext(), "Failed to start conversation: " + error, 
                                Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUsers();
    }

    private String extractUserIdFromUser(User user) {
        // Use email as the user identifier since that's what we have
        return user.email;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
