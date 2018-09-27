package com.vishal.vchat.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.vishal.vchat.model.ChatRoom;
import com.vishal.vchat.model.Group;
import com.vishal.vchat.model.Message;
import com.vishal.vchat.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by MUJ on 01-Mar-17.
 */

public class DBHelper extends SQLiteOpenHelper{
    private static final String DB_NAME="vchat";
    private static final int DB_VERSION=1;
    private static DBHelper sInstance;
    public DBHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db){
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String Friend_Table="CREATE TABLE user (id INTEGER PRIMARY KEY,roomid INTEGER REFRENCES room,name VARCHAR(30),number VARCHAR(10),pname VARCHAR(30),status VARCHAR(100),picture VARCHAR(20))";
        String Room_Table="CREATE TABLE room (id INTEGER PRIMARY KEY,name VARCHAR(30),timestamp TIMESTAMP,unreadCount INTEGER(10),type INTEGER(1))";
        String Message_Table="CREATE TABLE message(id INTEGER PRIMARY KEY,roomid INTEGER REFRENCES room,message VARCHAR(255),createdat TIMESTAMP)";
        String Groups_Table="CREATE TABLE groups(id INTEGER PRIMARY KEY,roomid INTEGER REFRENCES room,name VARCHAR(30),picture VARCHAR(20),status VARCHAR(100),createdat TIMESTAMP,createdby INTEGER REFRENCES user)";
        String GMembers_Table="CREATE TABLE gmembers(id INTEGER PRIMARY KEY,groupid INTEGER REFRENCES groups,userid INTEGER REFRENCES user)";
        db.execSQL(Room_Table);
        db.execSQL(Friend_Table);
        db.execSQL(Message_Table);
        db.execSQL(Groups_Table);
        db.execSQL(GMembers_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public static synchronized DBHelper getInstance(Context context){
        if(sInstance==null)
            sInstance=new DBHelper(context.getApplicationContext());
        return sInstance;
    }

    public void deleteMessages(){
        SQLiteDatabase db=getWritableDatabase();
        db.delete("message",null,null);
    }

    public void addUser(String values){
        SQLiteDatabase db=getWritableDatabase();
        String query="INSERT into user(id,name,number) VALUES"+values+";";
        db.beginTransaction();
        try{
            db.execSQL(query);
            db.setTransactionSuccessful();
        }
        catch (Exception e){
            Log.d("addUser",e.toString());
        } finally {
            db.endTransaction();
        }
    }

    public void updateUser(int id,int roomid)
    {
        SQLiteDatabase db=getWritableDatabase();
        String query="UPDATE user set roomid="+roomid+" where id="+id+";";
        db.beginTransaction();
        try
        {
            db.execSQL(query);
            db.setTransactionSuccessful();
        }
        catch (Exception e)
        {
            Log.d("updateUser",e.toString());
        }
        finally {
            db.endTransaction();
        }
    }

    public long addRoom(ChatRoom room)
    {
        SQLiteDatabase db=getWritableDatabase();
        long id=0;
        db.beginTransaction();
        try{
            ContentValues values=new ContentValues();
            values.put("id",room.getId());
            values.put("name",room.getName());
            values.put("timestamp",room.getTimestamp());
            values.put("type",room.getType()?1:0);
            id=db.insertOrThrow("room",null,values);
            db.setTransactionSuccessful();
        }
        catch (Exception e){
            Log.d("addRoom",e.toString());
        } finally {
            db.endTransaction();
        }
        return id;
    }

    public void addMessage(Message message){
        SQLiteDatabase db=getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values=new ContentValues();
            values.put("roomid",message.getRoomid());
            values.put("message",message.getMessage());
            values.put("createdat",message.getCreatedAt());
            db.insertOrThrow("message",null,values);
            db.setTransactionSuccessful();
        }
        catch (Exception e){
            Log.e("addMessage",e.toString());
        } finally {
            db.endTransaction();
        }
    }
    public List<ChatRoom> getAllRooms(){
        List<ChatRoom> rooms=new ArrayList<>();
        String query="SELECT * FROM room;";
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        try{
            if(cursor.moveToFirst()){
                do {
                    boolean type=cursor.getInt(cursor.getColumnIndex("type"))==1?true:false;
                    ChatRoom room=new ChatRoom(cursor.getInt(0),cursor.getString(1),cursor.getString(2),0,type);
                    rooms.add(room);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d("getRooms",e.toString());
        } finally {
            if(cursor!=null&&!cursor.isClosed())
                cursor.close();
        }
        return rooms;
    }

    public List<User> getAllUsers()
    {
        List<User> users=new ArrayList<>();
        String query="SELECT * from user"+";";
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        try{
            if(cursor.moveToFirst()){
                do {
                    User user=new User(cursor.getInt(0),cursor.getInt(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6));
                    users.add(user);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d("getUsers",e.toString());
        } finally {
            if(cursor!=null&&!cursor.isClosed())
                cursor.close();
        }
        return users;
    }

    public List<Message> getAllMessages(int room){
        List<Message> messages=new ArrayList<>();
        String query="SELECT * FROM message where roomid="+room+";";
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        try{
            if(cursor.moveToFirst()){
                do {
                    Message message=new Message(cursor.getInt(0),cursor.getString(2),cursor.getString(3),room);
                    Log.d("message",message.getMessage()+message.getCreatedAt());
                    messages.add(message);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d("getMessages",e.toString());
        } finally {
            if(cursor!=null&&!cursor.isClosed())
                cursor.close();
        }
        return messages;
    }
    public String getUserFromChatid(int chatRoomid) {
        String query = "SELECT id from user where roomid=" + chatRoomid+";";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        try {
             if(cursor.moveToFirst())
                 return cursor.getInt(0)+"";
            else
                 return null;
        } catch (Exception e){
            Log.e("getUser",e.toString());
        } finally {
            if(cursor!=null&&!cursor.isClosed())
                cursor.close();
        }
        return null;
    }
    public String getGroupFromChatid(int chatRoomid) {
        String query = "SELECT id from groups where roomid=" + chatRoomid+";";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        try {
            if(cursor.moveToFirst())
                return cursor.getInt(0)+"";
            else
                return null;
        } catch (Exception e){
            Log.e("getGroup",e.toString());
        } finally {
            if(cursor!=null&&!cursor.isClosed())
                cursor.close();
        }
        return null;
    }
    public String getNameofUser(int userId)
    {
        String query = "SELECT name from user where id=" + userId+";";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        try {
            if(cursor.moveToFirst())
                return cursor.getString(0);
            else
                return "";
        } catch (Exception e){
            Log.e("getUser",e.toString());
        } finally {
            if(cursor!=null&&!cursor.isClosed())
                cursor.close();
        }
        return "";
    }
    public void showDB()
    {
        SQLiteDatabase db=getReadableDatabase();
        String users="SELECT * from user";
        String rooms="SELECT * from room";
        String message="SELECT * from message";
        Cursor cursor=db.rawQuery(users,null);
        Cursor cursor1=db.rawQuery(rooms,null);
        Cursor cursor2=db.rawQuery(message,null);
        try {
            if(cursor!=null&&cursor.moveToFirst())
                do {
                    System.out.println(cursor.getInt(0) + " " + cursor.getInt(1) + " " + cursor.getString(2) + " " + cursor.getString(3) + " " + cursor.getString(4) + " " + cursor.getString(5) + " " + cursor.getString(6));
                } while(cursor.moveToNext());
            if(cursor1!=null&&cursor1.moveToFirst())
                do {
                System.out.println(cursor1.getInt(0) + cursor1.getString(1));
                } while(cursor1.moveToNext());
            if(cursor2!=null&&cursor2.moveToFirst())
                do {
                System.out.println(cursor2.getInt(0) + " " + cursor2.getInt(1) + " " + cursor2.getString(2) );
                } while(cursor2.moveToNext());
        } catch (Exception e){
            Log.e("getUser",e.toString());
        } finally {
            if(cursor!=null&&!cursor.isClosed())
                cursor.close();
            if(cursor1!=null&&!cursor1.isClosed())
                cursor1.close();
            if(cursor2!=null&&!cursor2.isClosed())
                cursor2.close();
        }
    }
    public void addGroup(Group group)
    {
        SQLiteDatabase db=getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values=new ContentValues();
            values.put("id",group.getId());
            values.put("name",group.getName());
            values.put("status",group.getStatus());
            values.put("createdby",1);
            values.put("roomid",group.getRoomid());
            db.insertOrThrow("groups",null,values);
            db.setTransactionSuccessful();
        }
        catch (Exception e){
            Log.e("addMessage",e.toString());
        } finally {
            db.endTransaction();
        }
        Object y[]=group.getUserids().keySet().toArray();
        int x[]=new int[y.length];
        for(int i=0;i<y.length;i++)
            x[i]=(int)y[i];
        String val="";
        for(int i=0;i<x.length;i++)
            val+="("+group.getId()+","+x[i]+"),";
        val+="("+group.getId()+","+group.getCreatedby()+")";
        String query="INSERT into gmembers(groupid,userid) VALUES"+val+";";
        db.execSQL(query);
    }
    public Map<Integer,String> getNameOfUsers(String s)
    {
        SQLiteDatabase db=getReadableDatabase();
        Cursor c1=db.rawQuery("SELECT id,name from user where id in "+s,null);
        Map<Integer,String> x=new HashMap<>();
        if(c1!=null&&c1.moveToFirst())
        {
            do
                x.put(c1.getInt(0),c1.getString(1));
            while(c1.moveToNext());
        }
        return x;
    }
    public Map<Integer,String> getNameFromGroupId(int groupid)
    {
        SQLiteDatabase db=getReadableDatabase();
        Cursor c1=db.rawQuery("SELECT userid from gmembers where groupid="+groupid,null);
        String s="(";
        if(c1!=null&&c1.moveToFirst())
        {
            do
                s+=c1.getString(0)+",";
            while(c1.moveToNext());
        }
        return getNameOfUsers(s.substring(0,s.length()-1)+")");
    }
}
