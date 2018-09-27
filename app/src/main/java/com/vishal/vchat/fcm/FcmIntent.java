package com.vishal.vchat.fcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.vishal.vchat.app.EndPoints;
import com.vishal.vchat.app.MyApplication;
import com.vishal.vchat.model.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MUJ on 26-Mar-17.
 */

public class FcmIntent  extends IntentService{

    public FcmIntent()
    {
        super(FcmIntent.class.getSimpleName());
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        registerFcm();
    }
    private void registerFcm()
    {
        final String token= FirebaseInstanceId.getInstance().getToken();
        User user= MyApplication.getmInstance().getPrefManager().getUser();
        if(user==null)return;
        String endPoint= EndPoints.FCMID.replace("_ID_",user.getId()+"");
        StringRequest strReq=new StringRequest(Request.Method.POST, endPoint, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("registerFCM",response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("registerFCM",error.getMessage());
            }
        }){
            protected Map<String,String> getParams(){
                Map<String,String> params=new HashMap<String, String>();
                params.put("fcmid",token);
                return  params;
            }
        };
        MyApplication.getmInstance().addToRequestQueue(strReq);
    }
}
