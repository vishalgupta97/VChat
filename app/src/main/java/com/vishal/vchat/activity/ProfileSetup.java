package com.vishal.vchat.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.vishal.vchat.model.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MUJ on 27-Feb-17.
 */

public class ProfileSetup extends Activity {
    ImageView profilepic;
    String imgString;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profilepic=(ImageView)findViewById(R.id.imageView1);
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),1);
            }
        });
        Button enter=(Button)findViewById(R.id.Enter);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
            }
        });
    }
    public void sendData()
    {
        final String profileName=((EditText)findViewById(R.id.editText4)).getText().toString();
        final String status=((EditText)findViewById(R.id.editText5)).getText().toString();
        User user=MyApplication.getmInstance().getPrefManager().getUser();
        final String id=user.getId()+"";
        user.setPname(profileName);
        user.setStatus(status);
        user.setPicture(imgString);
		String endP=EndPoints.PROFILE.replace("_ID_",id);
        StringRequest srq=new StringRequest(Request.Method.POST,endP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Profile",response);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Profile",error.toString());
            }
        }){
            protected Map<String,String> getParams(){
                Map<String,String> params=new HashMap<String, String>();
                params.put("pname",profileName);
                params.put("status",status);
                //params.put("image",imgString);
                return  params;
            }
        };
        MyApplication.getmInstance().addToRequestQueue(srq);
        MyApplication.getmInstance().getPrefManager().addUser(profileName,status,imgString);
        Intent intent=new Intent(ProfileSetup.this,MainActivity.class);
        startActivity(intent);
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
