package com.pethoemilia.client.entity;

import android.widget.Toast;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Group {
    private Long id;
    private String name;
    private Set<User> users = new HashSet<>();
    private List<Message> messages = new ArrayList<>();

    // Constructor, getters, and setters omitted for brevity

    public String lastMessage() {
        if (messages == null || messages.isEmpty()) {
            return "Még nincs üzenet.";
        }

        sortMessagesByTimestamp();

        Message lastMessage = messages.get(messages.size() - 1);
        return lastMessage.getContent();
    }

    public Long getLastMessageTimestamp() {
        if (messages == null || messages.isEmpty()) {
            return null;
        }

        sortMessagesByTimestamp();
        Message lastMessage = messages.get(messages.size() - 1);
        return lastMessage.getTimestamp();
    }

    private void sortMessagesByTimestamp() {
        if (messages != null) {
            Collections.sort(messages, new Comparator<Message>() {
                @Override
                public int compare(Message m1, Message m2) {
                    return m1.getTimestamp().compareTo(m2.getTimestamp());
                }
            });
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}