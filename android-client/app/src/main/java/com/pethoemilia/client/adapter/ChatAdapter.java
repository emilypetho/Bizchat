package com.pethoemilia.client.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pethoemilia.client.MyConst;
import com.pethoemilia.client.R;
import com.pethoemilia.client.Repository.ChatRepository;
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
    private ChatRepository repo;
    // Konstruktor a Context paraméterrel
    public ChatAdapter(Context context) {
        this.context = context;
        repo = ChatRepository.getInstance(context);
    }

    public void translate(String message) {
        repo.translate(message, new ChatRepository.StringCallback() {
            @Override
            public void onSuccess(String res) {
                showMessageDialog(context,res);
            }
            @Override
            public void onFailure() {
                Log.e("ChatViewModel", "Összegzés sikertelen");
            }
        });
    }
    public static void showMessageDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
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
        Long timestamp = message.getTimestamp();
        Date messageDate = new Date(timestamp);
        Date currentDate = new Date();

        SimpleDateFormat sameDayFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat differentDayFormat = new SimpleDateFormat("MMM dd");

        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
//        String messageDay = dayFormat.format(messageDate);
//        String currentDay = dayFormat.format(currentDate);
//
//        if (messageDay.equals(currentDay)) {
//            holder.msgtime.setText(sameDayFormat.format(messageDate));
//        } else {
//            holder.msgtime.setText(differentDayFormat.format(messageDate));
//        }

        User user = getUserFromSharedPreferences();
        if (user != null) {
            long userId = user.getId();
            if (message.getSender().getId() == userId) {
                holder.senderLayout.setVisibility(View.VISIBLE);
                holder.receiverLayout.setVisibility(View.GONE);
                holder.senderChat.setText(message.getContent());
                holder.senderChatTime.setVisibility(View.GONE);

                // OnClickListener az idő megjelenítésére
                holder.senderChat.setOnClickListener(v -> {
                    if (holder.senderChatTime.getVisibility() == View.GONE) {
                        holder.senderChatTime.setText(sameDayFormat.format(messageDate));
                        holder.senderChatTime.setVisibility(View.VISIBLE);
                    } else {
                        holder.senderChatTime.setVisibility(View.GONE); // Ismét kattintva elrejtheted
                    }
                });
            } else {
                holder.senderLayout.setVisibility(View.GONE);
                holder.receiverLayout.setVisibility(View.VISIBLE);
                holder.receiverChat.setText(message.getContent());
                //holder.chatSenderName.setVisibility(View.GONE);
                holder.chatSenderName.setText(message.getSender().getName());
                holder.receiverChatTime.setVisibility(View.GONE);

                // OnClickListener az idő megjelenítésére
                holder.receiverChat.setOnClickListener(v -> {
                    if (holder.receiverChatTime.getVisibility() == View.GONE) {
                        holder.receiverChatTime.setText(sameDayFormat.format(messageDate));
                        holder.receiverChatTime.setVisibility(View.VISIBLE);
                    } else {
                        holder.receiverChatTime.setVisibility(View.GONE);
                    }
                });
                holder.receiverChat.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        translate(holder.receiverChat.getText().toString());
                        return false;
                    }
                });
            }
        } else {
            // Handle the case where user is not available
            Log.e("ChatAdapter", "User not found in SharedPreferences");
        }
    }

    private User getUserFromSharedPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyConst.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString(MyConst.USER, null);
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
        TextView senderChatTime;
        TextView receiverChatTime;
        TextView chatSenderName;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            senderLayout = itemView.findViewById(R.id.sender_layout);
            receiverLayout = itemView.findViewById(R.id.receiver_layout);
            senderChat = itemView.findViewById(R.id.sender_chat);
            receiverChat = itemView.findViewById(R.id.receiver_chat);
            receiverChatTime = itemView.findViewById(R.id.chat_time_receiver);
            senderChatTime = itemView.findViewById(R.id.chat_time_sender);
            chatSenderName = itemView.findViewById(R.id.chat_sender_name);
        }
    }
}

