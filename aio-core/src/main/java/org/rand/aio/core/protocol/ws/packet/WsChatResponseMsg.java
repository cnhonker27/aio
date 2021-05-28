package org.rand.aio.core.protocol.ws.packet;

import com.google.gson.Gson;

/**
 * @Author rand
 * @Date 2020/12/20 23:44
 * @Version 1.0
 */
public class WsChatResponseMsg {

    private String msgId;

    private String nick;

    private String targetId;

    private String senderId;

    private String context;

    private String contextType;

    private String msgType;

    private String groupChat;

    private String wsChatMsgCommand;

    private String receiveType;

    private String receiveStatus="2000";

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getContextType() {
        return contextType;
    }

    public void setContextType(String contextType) {
        this.contextType = contextType;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getGroupChat() {
        return groupChat;
    }

    public void setGroupChat(String groupChat) {
        this.groupChat = groupChat;
    }

    public String getWsChatMsgCommand() {
        return wsChatMsgCommand;
    }

    public void setWsChatMsgCommand(String wsChatMsgCommand) {
        this.wsChatMsgCommand = wsChatMsgCommand;
    }

    public String getReceiveType() {
        return receiveType;
    }

    public void setReceiveType(String receiveType) {
        this.receiveType = receiveType;
    }

    public String getReceiveStatus() {
        return receiveStatus;
    }

    public void setReceiveStatus(String receiveStatus) {
        this.receiveStatus = receiveStatus;
    }

    @Override
    public String toString() {
        return "WsChatResponseMsg{" +
                "msgId='" + msgId + '\'' +
                ", nick='" + nick + '\'' +
                ", targetId='" + targetId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", context='" + context + '\'' +
                ", contextType='" + contextType + '\'' +
                ", msgType='" + msgType + '\'' +
                ", groupChat='" + groupChat + '\'' +
                ", wsChatMsgCommand='" + wsChatMsgCommand + '\'' +
                ", receiveType='" + receiveType + '\'' +
                ", receiveStatus='" + receiveStatus + '\'' +
                '}';
    }

    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
