package com.vishal.vchat.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.vishal.vchat.R;
import com.vishal.vchat.app.EndPoints;
import com.vishal.vchat.app.MyApplication;
import com.vishal.vchat.db.DBHelper;
import com.vishal.vchat.model.ChatRoom;
import com.vishal.vchat.model.Group;
import com.vishal.vchat.model.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MUJ on 23-Apr-17.
 */

public class GroupProfile extends AppCompatActivity {
    ImageView profilepic;
    String imgString;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupprofile);
        profilepic=(ImageView)findViewById(R.id.imageView2);
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),1);
            }
        });
        Button enter=(Button)findViewById(R.id.Create);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
            }
        });
    }
    public void sendData()
    {
        final String profileName=((EditText)findViewById(R.id.editText6)).getText().toString();
        final String status=((EditText)findViewById(R.id.editText7)).getText().toString();
        final String ids=getIntent().getStringExtra("ids");
        String endP= EndPoints.NEW_GROUP.replace("_ID_",MyApplication.userId+"");
        StringRequest srq=new StringRequest(Request.Method.POST,endP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                addGroup(response,profileName,status,ids);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Profile",error.toString());
            }
        }){
            protected Map<String,String> getParams(){
                Map<String,String> params=new HashMap<String, String>();
                params.put("name",profileName);
                params.put("status",status);
                params.put("userids",ids);
                return  params;
            }
        };
        MyApplication.getmInstance().addToRequestQueue(srq);
    }
    private void addGroup(String response,String name,String status,String ids)
    {
        int groupid=Integer.parseInt(response.substring(0,response.indexOf(",")));
        int chatroomid=Integer.parseInt(response.substring(response.indexOf(",")+1,response.length()-1));
        Map<Integer,String> x=DBHelper.getInstance(getApplicationContext()).getNameOfUsers(ids);
        Group group=new Group(groupid,chatroomid,MyApplication.userId,name,status,x,"");
        DBHelper.getInstance(getApplicationContext()).addGroup(group);
        ChatRoom chatRoom=new ChatRoom(chatroomid,name,"0",0,true);
        DBHelper.getInstance(getApplicationContext()).addRoom(chatRoom);
        Intent intent=new Intent("add room");
        intent.putExtra("id",chatRoom.getId());
        intent.putExtra("name",chatRoom.getName());
        intent.putExtra("type",chatRoom.getType());
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        String endP=EndPoints.CHAT_ROOM_GMESSAGE.replace("_FROM_", chatroomid + "").replace("_TO_",groupid + "");
        StringRequest srq=new StringRequest(Request.Method.POST,endP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Message",response);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Profile",error.toString());
            }
        }){
            protected Map<String,String> getParams(){
                Map<String,String> params=new HashMap<>();
                params.put("message",MyApplication.userId+";Welcome to group");
                return  params;
            }
        };
        MyApplication.getmInstance().addToRequestQueue(srq);
    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null)
        {
            Uri uri=data.getData();
            try
            {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                profilepic.setImageBitmap(bitmap);
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                byte[] imageBytes=baos.toByteArray();
                imgString= Base64.encodeToString(imageBytes,Base64.DEFAULT);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}

