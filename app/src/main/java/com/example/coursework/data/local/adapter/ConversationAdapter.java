package com.example.coursework.data.local.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.data.local.entities.Conversation;
import com.example.coursework.databinding.ListItemConversationBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    private List<Conversation> conversations = new ArrayList<>();
    private String currentUserId;
    private OnConversationClickListener listener;

    public interface OnConversationClickListener {
        void onConversationClick(Conversation conversation);
    }

    public ConversationAdapter(List<Conversation> conversations, OnConversationClickListener listener) {
        this.conversations = conversations;
        this.listener = listener;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        this.currentUserId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemConversationBinding binding = ListItemConversationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ConversationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        Conversation conversation = conversations.get(position);
        holder.bind(conversation);
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public void updateConversations(List<Conversation> newConversations) {
        this.conversations.clear();
        this.conversations.addAll(newConversations);
        notifyDataSetChanged();
    }

    private static String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    private String getOtherParticipantName(Conversation conversation) {
        if (conversation.users != null && conversation.users.size() >= 2) {
            for (String userId : conversation.users) {
                if (!userId.equals(currentUserId)) {
                    // For now, return a generic name. In a real app, you'd fetch user details
                    return "User " + userId.substring(0, Math.min(6, userId.length()));
                }
            }
        }
        return "Unknown User";
    }

    private String getAvatarText(String participantName) {
        if (participantName != null && !participantName.isEmpty()) {
            return participantName.substring(0, 1).toUpperCase();
        }
        return "U";
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {
        private final ListItemConversationBinding binding;

        public ConversationViewHolder(@NonNull ListItemConversationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Conversation conversation) {
            String participantName = getOtherParticipantName(conversation);
            
            binding.participantName.setText(participantName);
            binding.lastMessage.setText(conversation.lastMessage != null ? conversation.lastMessage : "No messages yet");
            binding.timeText.setText(formatTimestamp(conversation.createdAt));
            binding.avatarText.setText(getAvatarText(participantName));

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onConversationClick(conversation);
                }
            });
        }
    }
}