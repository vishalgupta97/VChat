package com.vishal.vchat.model;

import java.io.Serializable;

/**
 * Created by MUJ on 09-Dec-16.
 */

public class ChatRoom implements Serializable{
    String name,timestamp;
    int unreadCount,id;
    boolean type;
    public ChatRoom(){

    }
    public ChatRoom(int id,String name,String timestamp,int unreadCount,boolean type)
    {
        this.id=id;
        this.name=name;
        this.timestamp=timestamp;
        this.unreadCount=unreadCount;
        this.type=type;
    }

    public void setType(boolean type) {
        this.type = type;
    }
    public boolean getType()
    {
        return type;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
