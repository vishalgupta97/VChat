package com.vishal.vchat.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.vishal.vchat.model.User;

/**
 * Created by MUJ on 05-Dec-16.
 */

public class MyPreferenceManager {
    private String TAG=MyPreferenceManager.class.getSimpleName();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE=0;
    private static final String PREF_NAME="vchat";
    private static final String KEY_USER_ID="user_id";
    private static final String KEY_USER_NAME="user_name";
    private static final String KEY_USER_NUMBER="user_number";
    private static final String KEY_USER_PNAME="user_pname";
    private static final String KEY_USER_STATUS="user_status";
    private static final String KEY_USER_PICTURE="user_picture";
    private static final String KEY_NOTIFICATIONS="notifications";
    public MyPreferenceManager(Context context){
        this._context=context;
        pref=_context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor=pref.edit();
    }
    public SharedPreferences.Editor getEditor(){
        return editor;
    }
    public int getId()
    {
        if(pref.getString(KEY_USER_NUMBER,null)!=null) {
           return pref.getInt(KEY_USER_ID,0);
        }
        return 0;
    }
    public User getUser(){
        if(pref.getString(KEY_USER_NUMBER,null)!=null)
        {
            String name,number,p_name,status,picture;
            int id=pref.getInt(KEY_USER_ID,0);
            name=pref.getString(KEY_USER_NAME,null);
            number=pref.getString(KEY_USER_NUMBER,null);
            p_name=pref.getString(KEY_USER_PNAME,null);
            status=pref.getString(KEY_USER_STATUS,null);
            picture=pref.getString(KEY_USER_PICTURE,null);
            User user=new User(id,0,name,number,p_name,status,picture);
            return user;
        }
        return null;
    }
    public void addUser(String name,String number)
    {
        editor.putString(KEY_USER_NAME,name);
        editor.putString(KEY_USER_NUMBER,number);
        editor.commit();
    }
    public void  addUser(String pname,String status,String picture)
    {
        editor.putString(KEY_USER_PNAME,pname);
        editor.putString(KEY_USER_STATUS,status);
        editor.putString(KEY_USER_PICTURE,picture);
        editor.commit();
    }
    public String getNotification(){
        return pref.getString(KEY_NOTIFICATIONS,null);
    }
    public void addNotification(String notification){
        String oldNotification=getNotification();
        if(oldNotification!=null){
            oldNotification+="|"+notification;
        } else {
            oldNotification=notification;
        }
        editor.putString(KEY_NOTIFICATIONS,oldNotification);
        editor.commit();
    }
    public void clear(){
        editor.clear();
        editor.commit();
    }
}
