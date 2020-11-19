package com.example.wheretostudy;

import java.util.Date;

/**
 * Created by suvampramanik on 16/12/17.
 */

public class ChatMessage {
    private String messageText;
    private long messageTime;
    private String messageUser;
    private String messageRoom;

    //required for FireBase
    public ChatMessage(){}

    public ChatMessage(String text, String user, String room){
        messageText = text;
        messageUser = user;
        messageTime = new Date().getTime();
        messageRoom = room;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getMessageRoom() {
        return messageRoom;
    }

    public void setMessageRoom(String messageRoom) {
        this.messageRoom = messageRoom;
    }
}
