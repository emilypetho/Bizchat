package com.pethoemilia.client.entity;

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

//    private Boolean isGroup;

    // Constructor, getters, and setters omitted for brevity
    public Message lastMessage() {
        if (messages == null || messages.isEmpty()) {
            return null;
        }

        sortMessagesByTimestamp();

        Message lastMessage = messages.get(messages.size() - 1);
        return lastMessage;
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

//    public Boolean getFlag() {
//        return isGroup;
//    }

//    public void setFlag(Boolean group) {
//        isGroup = group;
//    }
}