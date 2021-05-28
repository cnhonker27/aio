package org.rand.ws.chat;

import java.util.List;

public class WsGroup {
    private String groupId;
    private String groupName;
    private String avatar;
    private Integer online=0;
    private List<WsUser> users;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }

    public List<WsUser> getUsers() {
        return users;
    }

    public void setUsers(List<WsUser> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "WsGroup{" +
                "groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", online=" + online +
                ", users=" + users +
                '}';
    }
}
