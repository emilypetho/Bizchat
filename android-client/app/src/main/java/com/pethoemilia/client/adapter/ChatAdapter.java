package com.pethoemilia.client.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pethoemilia.client.R;
import com.pethoemilia.client.entity.Group;
import com.pethoemilia.client.entity.Message;
import com.pethoemilia.client.entity.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Message> messages = new ArrayList<>();
    private Context context; // Context változó hozzáadása

    // Konstruktor a Context paraméterrel
    public ChatAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_recycler_row, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messages.get(position);

        User user = getUserFromSharedPreferences();
        if (user != null) {
            long userId = user.getId();
            if (message.getSender().getId() == userId) {
                holder.senderLayout.setVisibility(View.VISIBLE);
                holder.receiverLayout.setVisibility(View.GONE);
                holder.senderChat.setText(message.getContent());
            } else {
                holder.senderLayout.setVisibility(View.GONE);
                holder.receiverLayout.setVisibility(View.VISIBLE);
                holder.receiverChat.setText(message.getContent());
            }
        } else {
            // Handle the case where user is not available
            Log.e("ChatAdapter", "User not found in SharedPreferences");
        }
    }

    private User getUserFromSharedPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user", null);
        if (userJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(userJson, User.class);
        }
        return null; // Return null if no user found
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

