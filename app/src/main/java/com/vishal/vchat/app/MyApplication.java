package com.vishal.vchat.app;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.vishal.vchat.helper.MyPreferenceManager;

/**
 * Created by MUJ on 05-Dec-16.
 */

public class MyApplication extends Application {
    public static final String TAG=MyApplication.class.getSimpleName();
    public static int userId=0;
    private static MyApplication mInstance;
    private RequestQueue mRequestQueue;
    private MyPreferenceManager pref;
    public void onCreate(){
        super.onCreate();
        mInstance=this;
        userId=getPrefManager().getId();
    }
    public static synchronized MyApplication getmInstance(){
        return mInstance;
    }
    public MyPreferenceManager getPrefManager(){
        if(pref==null)
            pref=new MyPreferenceManager(this);
        return pref;
    }
    public RequestQueue getRequestQueue(){
        if(mRequestQueue==null)
            mRequestQueue= Volley.newRequestQueue(getApplicationContext());
        return mRequestQueue;
    }
    public <T> void addToRequestQueue(Request<T>  req,String tag)
    {
        req.setTag(TextUtils.isEmpty(tag)?TAG:tag);
        getRequestQueue().add(req);
    }
    public <T> void addToRequestQueue(Request<T> req){
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
    public void cancelPendingRequest(Object tag) {
        if (mRequestQueue != null)
            mRequestQueue.cancelAll(tag);
    }
}
