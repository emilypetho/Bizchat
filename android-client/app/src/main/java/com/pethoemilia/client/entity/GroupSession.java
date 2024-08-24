package com.pethoemilia.client.entity;

public class GroupSession {

    private static Group g;

    // Setter method for the group ID
    public static void setGroup(Group group) {
        g = group;
    }

    // Getter method for the group ID
    public static Group getGroup() {
        return g;
    }
}
