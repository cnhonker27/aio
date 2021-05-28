package org.rand.ws.chat;

import java.util.List;

public class WsUser {
    private String userId;
    private String userName;
    private String nick;
    private String avatar;
    private String onlineStatus;
    private List<WsGroup> groups;
    private List<WsUser> users;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public List<WsGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<WsGroup> groups) {
        this.groups = groups;
    }

    public List<WsUser> getUsers() {
        return users;
    }

    public void setUsers(List<WsUser> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "WsUser{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", nick='" + nick + '\'' +
                ", avatar='" + avatar + '\'' +
                ", onlineStatus='" + onlineStatus + '\'' +
                ", groups=" + groups +
                ", users=" + users +
                '}';
    }
}
