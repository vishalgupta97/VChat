package com.vishal.vchat.model;

import java.io.Serializable;

/**
 * Created by MUJ on 09-Dec-16.
 */

public class User implements Serializable {
    String name,number,pname,status,picture;
    int id,roomid;
    public User(){}
    public User(int id,int roomid,String name,String number,String pname,String status,String picture){
        this.id=id;
        this.name=name;
        this.number=number;
        this.pname=pname;
        this.status=status;
        this.picture=picture;
        this.roomid=roomid;
    }

    public int getRoomid() {
        return roomid;
    }

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName(){
        return name;
    }
    public String getNumber(){
        return number;
    }

    public String getPname() {
        return pname;
    }

    public String getStatus() {
        return status;
    }

    public String getPicture() {
        return picture;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
    public void setName(String name){
        this.name=name;
    }
    public void setNumber(String number){
        this.number=number;
    }
}
