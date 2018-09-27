package com.vishal.vchat.model;

import java.io.Serializable;

/**
 * Created by MUJ on 09-Dec-16.
 */

public class Message implements Serializable{
    String message,createdAt;
    int id,roomid;
    public Message(){}
    public Message(int id,String message,String createdAt,int room){
        this.id=id;
        this.message=message;
        this.createdAt=createdAt;
        this.roomid=room;
    }
    public int getId(){
        return id;
    }
    public String getMessage(){
        return message;
    }
    public String getCreatedAt(){
        return createdAt;
    }
    public void setId(int id){
        this.id=id;
    }
    public void setMessage(String message){
        this.message=message;
    }
    public void setCreatedAt(String createdAt){
        this.createdAt=createdAt;
    }

    public int getRoomid() {
        return roomid;
    }

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }
}
