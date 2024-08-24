package com.pethoemilia.client.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pethoemilia.client.R;
import com.pethoemilia.client.entity.Group;
import com.pethoemilia.client.entity.GroupSession;
import com.pethoemilia.client.entity.Message;
import com.pethoemilia.client.entity.UserSession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Message> messages = new ArrayList<>();

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_recycler_row, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messages.get(position);

        // Assume that the UserSession.getUserId() returns the ID of the current user.
        long currentUserId = UserSession.getUser().getId();

        // Check if the current user is the sender or receiver.
        if (message.getSender().getId() == currentUserId) {
            holder.senderLayout.setVisibility(View.VISIBLE);
            holder.receiverLayout.setVisibility(View.GONE);
            holder.senderChat.setText(message.getContent());
        } else {
            holder.senderLayout.setVisibility(View.GONE);
            holder.receiverLayout.setVisibility(View.VISIBLE);
            holder.receiverChat.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        LinearLayout senderLayout;
        LinearLayout receiverLayout;
        TextView senderChat;
        TextView receiverChat;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            senderLayout = itemView.findViewById(R.id.sender_layout);
            receiverLayout = itemView.findViewById(R.id.receiver_layout);
            senderChat = itemView.findViewById(R.id.sender_chat);
            receiverChat = itemView.findViewById(R.id.receiver_chat);
        }
    }
}
