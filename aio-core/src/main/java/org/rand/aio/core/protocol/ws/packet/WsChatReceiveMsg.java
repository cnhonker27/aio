package org.rand.aio.core.protocol.ws.packet;

import java.io.Serializable;

/**
 * @Author rand
 * @Date 2020/12/20 23:44
 * @Version 1.0
 */
public class WsChatReceiveMsg implements Serializable {

    private String msgId;

    private String targetId;

    private String senderId;

    private String context;

    private String contextType;

    private String msgType;

    private String groupChat;

    private String wsChatMsgCommand;

    private String token;

    private String createTime;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "WsChatReceiveMsg{" +
                "msgId='" + msgId + '\'' +
                ", targetId='" + targetId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", context='" + context + '\'' +
                ", contextType='" + contextType + '\'' +
                ", msgType='" + msgType + '\'' +
                ", groupChat='" + groupChat + '\'' +
                ", wsChatMsgCommand='" + wsChatMsgCommand + '\'' +
                ", token='" + token + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
