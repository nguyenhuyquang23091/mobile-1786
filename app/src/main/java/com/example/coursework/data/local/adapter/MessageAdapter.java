package com.example.coursework.data.local.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursework.data.local.entities.Message;
import com.example.coursework.databinding.ListMessageReceivedBinding;
import com.example.coursework.databinding.ListMessageSentBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messages = new ArrayList<>();
    private String currentUserId;

    private final int TYPE_SENT = 1;
    private final int TYPE_RECEIVED = 2;


    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        this.currentUserId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
    }

    public void updateMessages(List<Message> newMessages) {
        this.messages.clear();
        this.messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    private static String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_SENT) {
            ListMessageSentBinding binding = ListMessageSentBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new MessageSentViewHolder(binding);
        } else  {
            ListMessageReceivedBinding binding = ListMessageReceivedBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new MessageReceiveViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if(holder instanceof MessageSentViewHolder){
           ((MessageSentViewHolder) holder).bind(message);
        } else if (holder instanceof MessageReceiveViewHolder) {
            ((MessageReceiveViewHolder) holder).bind(message);
        }

    }



    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        return message.senderId.equals(currentUserId) ? TYPE_SENT : TYPE_RECEIVED;

    }

    static class MessageSentViewHolder extends  RecyclerView.ViewHolder{
        private final ListMessageSentBinding binding;

        public MessageSentViewHolder(@NonNull ListMessageSentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        private void bind(Message message){
            binding.messageText.setText(message.message);
            binding.messageTime.setText(formatTimestamp(message.timestamp));
        }
    }
    static class MessageReceiveViewHolder extends RecyclerView.ViewHolder{
        private ListMessageReceivedBinding binding;
       public MessageReceiveViewHolder(@NonNull ListMessageReceivedBinding binding){
           super(binding.getRoot());
           this.binding = binding;
       }

       private void bind(Message message){
           binding.messageText.setText(message.message);
           binding.messageTime.setText(formatTimestamp(message.timestamp));
       }
    }
}
