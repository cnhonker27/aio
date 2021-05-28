package org.rand.aio.core.protocol.ws.packet;

/**
 * @Author rand
 * @Date 2020/12/20 23:44
 * @Version 1.0
 */
public class WsChatMsg {

    private String senderId;

    private String receiverId;

    private String groupId;

    private String wsChatMsgCommand;

    private String wsChatType;

    private String wsChatMsgType;

    private String msgContent;

    private String extra;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getWsChatMsgCommand() {
        return wsChatMsgCommand;
    }

    public void setWsChatMsgCommand(String wsChatMsgCommand) {
        this.wsChatMsgCommand = wsChatMsgCommand;
    }

    public String getWsChatType() {
        return wsChatType;
    }

    public void setWsChatType(String wsChatType) {
        this.wsChatType = wsChatType;
    }

    public String getWsChatMsgType() {
        return wsChatMsgType;
    }

    public void setWsChatMsgType(String wsChatMsgType) {
        this.wsChatMsgType = wsChatMsgType;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "WsChatMsg{" +
                "senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", wsChatMsgCommand='" + wsChatMsgCommand + '\'' +
                ", wsChatType='" + wsChatType + '\'' +
                ", wsChatMsgType='" + wsChatMsgType + '\'' +
                ", msgContent='" + msgContent + '\'' +
                ", extra='" + extra + '\'' +
                '}';
    }
}
