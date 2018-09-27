package com.vishal.vchat.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by MUJ on 25-Apr-17.
 */

public class Group implements Serializable {
    String name,status,picture;
    Map<Integer,String> idname;
    int id,roomid,createdby;
    public Group(){}
    public Group(int id,int roomid,int createdby,String name,String status,Map<Integer,String> idname,String picture){
        this.id=id;
        this.name=name;
        this.idname=idname;
        this.status=status;
        this.picture=picture;
        this.roomid=roomid;
        this.createdby=createdby;
    }

    public int getCreatedby() {
        return createdby;
    }

    public void setCreatedby(int createdby) {
        this.createdby = createdby;
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


    public String getStatus() {
        return status;
    }

    public String getPicture() {
        return picture;
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

    public Map<Integer,String> getUserids() {
        return idname;
    }

    public void setUserids( Map<Integer,String> idname) {
        this.idname=idname;
    }
}
