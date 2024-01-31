package com.example.farmconnect;

public class MessageModel {

    private String msgContent;
    private String msgType;
    private String msgTime;
    private String msgSender;
    private String msgUser;


    private boolean isSelected = false;

    public MessageModel(String msgContent, String msgType, String msgTime, String msgSender, String msgUser) {
        this.msgContent = msgContent;
        this.msgType = msgType;
        this.msgTime = msgTime;
        this.msgSender = msgSender;
        this.msgUser = msgUser;
    }

    public String getMsgUser() {
        return msgUser;
    }

    public void setMsgUser(String msgUser) {
        this.msgUser = msgUser;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public String getMsgType() {
        return msgType;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public String getMsgSender() {
        return msgSender;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
