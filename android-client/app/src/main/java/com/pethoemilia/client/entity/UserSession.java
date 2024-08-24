package com.pethoemilia.client.entity;

public class UserSession {
    private static User u;

    public static void setUser(User user) {
        u = user;
    }

    public static User getUser() {
        return u;
    }
}
