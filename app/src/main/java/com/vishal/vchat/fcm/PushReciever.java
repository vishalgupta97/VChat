package com.vishal.vchat.fcm;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vishal.vchat.app.MyApplication;
import com.vishal.vchat.db.DBHelper;

import java.util.Map;

/**
 * Created by MUJ on 26-Mar-17.
 */

public class PushReciever extends FirebaseMessagingService{
    public void onMessageReceived(RemoteMessage Rmessage)
    {
        super.onMessageReceived(Rmessage);
        Map<String,String> data=Rmessage.getData();
        String from=data.get("from1");
        String type=data.get("type1");
        String message=data.get("message");
        System.out.println("message "+from+" "+type+" "+message);
        if(Integer.parseInt(message.substring(0,message.indexOf(";")))==MyApplication.userId)
            return;
        Intent pushNotification=new Intent("push notify");
        pushNotification.putExtra("from",from);
        pushNotification.putExtra("message",message);
        pushNotification.putExtra("type",type);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

    }
}
