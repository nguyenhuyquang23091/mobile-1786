package com.example.coursework.data.local.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.coursework.R;
import com.example.coursework.data.local.adapter.UserAdapter;
import com.example.coursework.data.local.entities.User;
import com.example.coursework.data.local.implementation.ChatRepositoryImplementation;
import com.example.coursework.data.local.repository.ChatRepository;
import com.example.coursework.data.local.util.OnConversationListener;
import com.example.coursework.data.local.util.OnUsersReceivedListener;
import com.example.coursework.databinding.FragmentUserListBinding;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment implements UserAdapter.OnUserClickListener {

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
        setupBackButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUsers();
    }

    private void setupRecyclerView() {
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this);
        
        binding.usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.usersRecyclerView.setAdapter(userAdapter);
    }

    private void setupRepository() {
        chatRepository = new ChatRepositoryImplementation(requireActivity().getApplication());
    }

    private void setupBackButton() {
        binding.backButton.setOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigateUp();
        });
    }

    private void loadUsers() {
        showLoading(true);
        
        // Step 1: Retrieve available users
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
                        showError("Failed to load users: " + error);
                    });
                }
            }
        });
    }

    @Override
    public void onUserClick(User user) {
        // Step 2: Select a user and initiate a chat
        // We need to get the user ID somehow - for now using email as identifier
        String customerId = extractUserIdFromUser(user);
        
        if (customerId == null) {
            showError("Unable to identify user");
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
                        Bundle args = new Bundle();
                        args.putString("conversationId", conversationId);
                        args.putString("userEmail", user.email);
                        
                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_userListFragment_to_chatFragment, args);
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showError("Failed to start conversation: " + error);
                    });
                }
            }
        });
    }

    private String extractUserIdFromUser(User user) {
        // This method should extract the actual user ID
        // For now, using email as a placeholder - you should modify this based on your User entity structure
        // If User entity has an id field, use that instead
        return user.email; // This is a temporary solution
    }

    private void showLoading(boolean show) {
        if (binding != null) {
            binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            binding.usersRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showEmptyState(boolean show) {
        if (binding != null) {
            binding.emptyStateText.setVisibility(show ? View.VISIBLE : View.GONE);
            binding.usersRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}