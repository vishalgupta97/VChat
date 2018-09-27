package com.vishal.vchat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.vishal.vchat.R;
import com.vishal.vchat.adapter.ChatRoomThreadAdapter;
import com.vishal.vchat.adapter.GChatRoomThreadAdapter;
import com.vishal.vchat.app.EndPoints;
import com.vishal.vchat.app.MyApplication;
import com.vishal.vchat.db.DBHelper;
import com.vishal.vchat.model.ChatRoom;
import com.vishal.vchat.model.Message;

/**
 * Created by MUJ on 12-Dec-16.
 */

public class ChatRoomActivity extends AppCompatActivity {
    private String TAG = ChatRoomActivity.class.getSimpleName();
    public  int chatRoomId;
    private RecyclerView recyclerView;
    private ChatRoomThreadAdapter mAdapter;
    private GChatRoomThreadAdapter mAdapter1;
    private ArrayList<Message> messageArrayList;
    private BroadcastReceiver mRegistrationBroadcastReciever;
    private EditText inputMessage;
    private Button btnSend;
    String title;
    int userId,groupid;
    boolean type;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        //Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        inputMessage=(EditText)findViewById(R.id.message);
        btnSend=(Button)findViewById(R.id.btn_send);
        Intent intent=getIntent();
        chatRoomId=intent.getIntExtra("chat_room_id",0);
        title=intent.getStringExtra("name");
        userId=intent.getIntExtra("user_id",0);
        type=intent.getBooleanExtra("type",false);
        if(type)
            groupid=Integer.parseInt(DBHelper.getInstance(getApplicationContext()).getGroupFromChatid(chatRoomId));
        else
        if(userId==0)
            userId=Integer.parseInt(DBHelper.getInstance(getApplicationContext()).getUserFromChatid(chatRoomId));
        getSupportActionBar().setTitle(title);
        //getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        messageArrayList=new ArrayList<>();
        int selfUserId=MyApplication.userId;
        if(type) {
            Map<Integer,String> idname=DBHelper.getInstance(getApplicationContext()).getNameFromGroupId(groupid);
            mAdapter1 = new GChatRoomThreadAdapter(this, messageArrayList,selfUserId,idname);
            recyclerView.setAdapter(mAdapter1);
        }
        else {
            mAdapter = new ChatRoomThreadAdapter(this, messageArrayList, selfUserId);
            recyclerView.setAdapter(mAdapter);
        }
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mRegistrationBroadcastReciever=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("push notify")){
                    handlePushNotificaton(intent);
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReciever,new IntentFilter("push notify"));
         btnSend.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 sendMessage();
             }
         });
        if(chatRoomId!=0)
        fetchChatThread();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!type)
        getMenuInflater().inflate(R.menu.single,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_call: Intent intent=new Intent(ChatRoomActivity.this,OutgoingCallActivity.class);
                intent.putExtra("id",userId);
                intent.putExtra("title",title);
                startActivity(intent);
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    protected  void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReciever,new IntentFilter("push notify"));

    }
    protected void onPause(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReciever);
        super.onPause();
    }
    private void handlePushNotificaton(Intent intent){
        if(Integer.parseInt(intent.getStringExtra("from"))==chatRoomId)
        {
            messageArrayList.add(new Message(0,intent.getStringExtra("message"),"0",chatRoomId));
            if(type) {
            mAdapter1.notifyDataSetChanged();
            if (mAdapter1.getItemCount() > 1)
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter1.getItemCount() - 1);
        }
            else
            {
                mAdapter.notifyDataSetChanged();
                if (mAdapter.getItemCount() > 1)
                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
            }
        }
    }
    private void sendMessage(){
        if(chatRoomId==0) {
            String endp=EndPoints.ADD_ROOM.replace("_FROM_",MyApplication.userId+"").replace("_TO_",userId+"");
            StringRequest sr=new StringRequest(Request.Method.POST,endp, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    addRoom(response.substring(0,response.length()-1));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("addRoom",error.toString());
                }
            });
            MyApplication.getmInstance().addToRequestQueue(sr);
        }
        else {
            if(type)
                groupid=Integer.parseInt(DBHelper.getInstance(getApplicationContext()).getGroupFromChatid(chatRoomId));
            else
            if(userId==0)
                userId=Integer.parseInt(DBHelper.getInstance(getApplicationContext()).getUserFromChatid(chatRoomId));
            final Message message = new Message(0, MyApplication.userId + ";" + inputMessage.getText().toString(), "0", chatRoomId);
            messageArrayList.add(message);
            inputMessage.setText("");
            if(type) {
                mAdapter1.notifyDataSetChanged();
                if (mAdapter1.getItemCount() > 1)
                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter1.getItemCount() - 1);
            }
            else
            {
                mAdapter.notifyDataSetChanged();
                if (mAdapter.getItemCount() > 1)
                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
            }
            String endp;
            if(type)
            endp = EndPoints.CHAT_ROOM_GMESSAGE.replace("_FROM_", chatRoomId + "").replace("_TO_",groupid + "");
            else
            endp = EndPoints.CHAT_ROOM_MESSAGE.replace("_FROM_", chatRoomId + "").replace("_TO_",userId+ "");
            System.out.println(endp + " " + message.getMessage());
            StringRequest sr = new StringRequest(Request.Method.POST, endp, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("sendMessage", response.substring(0, response.length() - 1));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("sendMessage", error.toString());
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("message", message.getMessage());
                    return params;
                }
            };
            MyApplication.getmInstance().addToRequestQueue(sr);
            DBHelper.getInstance(getApplicationContext()).addMessage(message);
        }
    }
    private void addRoom(String response)
    {
        ChatRoom room=new ChatRoom(Integer.parseInt(response),title,"0",0,false);
        chatRoomId=room.getId();
        DBHelper.getInstance(getApplicationContext()).addRoom(room);
        DBHelper.getInstance(getApplicationContext()).updateUser(userId,chatRoomId);
        Intent intent=new Intent("add room");
        intent.putExtra("id",room.getId());
        intent.putExtra("name",room.getName());
        intent.putExtra("type",room.getType());
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        Intent intent1=new Intent("update user");
        intent1.putExtra("id",room.getId());
        intent1.putExtra("user_id",userId);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);
        sendMessage();

    }
    private void fetchChatThread(){
        List<Message> messages=DBHelper.getInstance(getApplicationContext()).getAllMessages(chatRoomId);
        messageArrayList.addAll(messages);
        if(type) {
            mAdapter1.notifyDataSetChanged();
            if (mAdapter1.getItemCount() > 1)
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter1.getItemCount() - 1);
        }
        else
        {
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getItemCount() > 1)
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
        }
    }
}