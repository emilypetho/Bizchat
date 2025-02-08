package com.pethoemilia.client.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pethoemilia.client.MyConst;
import com.pethoemilia.client.R;
import com.pethoemilia.client.entity.Group;
import com.pethoemilia.client.entity.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> groups = new ArrayList<>();
    private User user = new User();


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private OnItemClickListener listener;

    public GroupAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
        notifyDataSetChanged();
    }

//    public void setGroups(List<Group> newGroups) {
//        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new GroupDiffCallback(groups, newGroups));
//
//        groups.clear();
//        groups.addAll(newGroups);
//        diffResult.dispatchUpdatesTo(this);
//    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new GroupViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groups.get(position);

        // Ha két tag van a csoportban, a név a másik felhasználóra változik
        if (group.getUsers().size() == 2) {
            for (User member : group.getUsers()) {
                if (!member.getId().equals(user.getId())) {
                    holder.groupName.setText(member.getName());
                    break;
                }
            }
        } else {
            holder.groupName.setText(group.getName());
            holder.image.setImageResource(R.drawable.group);
        }

        // Üzenet kezelés
        if (group.getMessages() != null && !group.getMessages().isEmpty()) {
            if (group.getUsers().size() == 2) {
                holder.lastmessage.setText(group.lastMessage().getContent());
            } else {
                holder.lastmessage.setText(group.lastMessage().getSender().getName() + ": " + group.lastMessage().getContent());
            }
            Long timestamp = group.getLastMessageTimestamp();
            if (timestamp != null) {
                Date messageDate = new Date(timestamp);
                Date currentDate = new Date();

                SimpleDateFormat sameDayFormat = new SimpleDateFormat("HH:mm");
                SimpleDateFormat differentDayFormat = new SimpleDateFormat("MMM dd");

                SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
                String messageDay = dayFormat.format(messageDate);
                String currentDay = dayFormat.format(currentDate);

                if (messageDay.equals(currentDay)) {
                    holder.msgtime.setText(sameDayFormat.format(messageDate));
                } else {
                    holder.msgtime.setText(differentDayFormat.format(messageDate));
                }
            } else {
                holder.msgtime.setText("Nincs időpont");
            }
        } else {
            holder.lastmessage.setText("Még nincs üzenet.");
            holder.msgtime.setText(" ");
        }
    }


    @Override
    public int getItemCount() {
        return groups.size();
    }

    public List<Group> getGroups() {
        return groups;
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        TextView lastmessage;
        TextView msgtime;
        ImageView image;

        public GroupViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            groupName = itemView.findViewById(R.id.groupName);
            lastmessage = itemView.findViewById(R.id.lastmessage);
            msgtime = itemView.findViewById(R.id.msgtime);
            image = itemView.findViewById(R.id.image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}