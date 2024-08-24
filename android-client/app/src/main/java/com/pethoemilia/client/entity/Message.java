package com.pethoemilia.client.entity;

import java.time.LocalDateTime;

public class Message {

    private Long id;
    private String content;
    private Long timestamp;
    private Group group;
    private User sender;

    public Message(String content, Group group, User sender) {
        this.content = content;
        this.group = group;
        this.sender = sender;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }
}
