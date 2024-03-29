package com.example.wander.model;

import java.util.List;

public class Group {
    private String groupName;

    private String id;
    private String groupLocation;
    private List<String> groupAdmins;
    private List<String> members;
    private float[] scores;
    private String postRef;
    private boolean requestToJoin;
    private int refreshTime;

    public Group(String id, String groupName, String groupLocation, List<String> groupAdmins, List<String> members, boolean requestToJoin) {
        this.id = id;
        this.groupName = groupName;
        this.groupAdmins = groupAdmins;
        this.members = members;
        this.groupLocation = groupLocation;
        this.requestToJoin = requestToJoin;
        this.refreshTime = 1;
    }

    // Getters
    public String getGroupName() {
        return groupName;
    }
    public String getGroupLocation() {
        return groupLocation;
    }
    public List<String> getGroupAdmins() {
        return groupAdmins;
    }
    public List<String> getMembers() {
        return members;
    }
    public boolean getRequestToJoin() {
        return requestToJoin;
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
    public void addMember(String member) {
        members.add(member);
    }
    public void removeMember(String member) {
        members.remove(member);
    }
    public void setRequestToJoin(boolean value) {
        requestToJoin = value;
    }

    public String getId() {
        return id;
    }
}
