package com.example.wander.model;

import java.util.List;

public class Group {
    private String groupId;
    private String groupName;
    private String groupLocation;
    private List<String> groupAdmins;
    private List<String> members;
    private float[] scores;

    public Group(String groupId, String groupName, String groupLocation, List<String> groupAdmins) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupAdmins = groupAdmins;
        this.groupLocation = groupLocation;
    }

    // Getters
    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }
    public String getGroupLocation() {
        return groupLocation;
    }

    public List<String> getGroupAdmins() {
        return groupAdmins;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    // Setters
    public void setGroupLocation(String groupLocation) {
        this.groupLocation = groupLocation;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupAdmins(List<String> groupAdmins) {
        this.groupAdmins = groupAdmins;
    }

}
