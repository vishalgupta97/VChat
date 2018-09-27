package com.vishal.vchat.fcm;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by MUJ on 26-Mar-17.
 */

public class IDListener extends FirebaseInstanceIdService {
    public void onTokenRefresh(){
        String newToken= FirebaseInstanceId.getInstance().getToken();
        Intent intent=new Intent(this,FcmIntent.class);
        startService(intent);
    }
}
