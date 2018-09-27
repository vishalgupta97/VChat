package com.vishal.vchat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.vishal.vchat.R;
import com.vishal.vchat.adapter.ChatRoomsAdapter;
import com.vishal.vchat.app.EndPoints;
import com.vishal.vchat.app.MyApplication;
import com.vishal.vchat.db.DBHelper;
import com.vishal.vchat.helper.SimpleDividerItemDecoration;
import com.vishal.vchat.model.ChatRoom;
import com.vishal.vchat.model.Group;
import com.vishal.vchat.model.Message;
import com.vishal.vchat.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by MUJ on 29-Mar-17.
 */

public class RoomList extends Fragment {
    private List<ChatRoom> chatRoomArrayList;
    private ChatRoomsAdapter mAdapter;
    private RecyclerView recyclerView;
    private BroadcastReceiver mReceiver;
    public RoomList()
    {

    }
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);

    }
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        View v=inflater.inflate(R.layout.activity_roomlist,container,false);
        Context context=getActivity().getApplicationContext();
        chatRoomArrayList= DBHelper.getInstance(context).getAllRooms();
        mAdapter=new ChatRoomsAdapter(context,chatRoomArrayList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(context);
        recyclerView=(RecyclerView)v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new ChatRoomsAdapter.RecyclerTouchListener(context,recyclerView,new ChatRoomsAdapter.ClickListener(){
            public void onClick(View view, int position){
                ChatRoom room=chatRoomArrayList.get(position);
                room.setUnreadCount(0);
                mAdapter.notifyDataSetChanged();
                Intent intent=new Intent(getActivity(),ChatRoomActivity.class);
                intent.putExtra("chat_room_id",room.getId());
                intent.putExtra("type",room.getType());
                intent.putExtra("name",room.getName());
                startActivity(intent);
            }
            public void onLongClick(View view,int pos){

            }
        }));
        mReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("add room"))
                    addRoom(intent);
                else  if(intent.getAction().equals("push notify"))
                    handlePush(intent);
            }
        };
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("add room");
        intentFilter.addAction("push notify");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver,intentFilter);
        return v;
    }
    private void addRoom(Intent intent)
    {
        ChatRoom room=new ChatRoom(intent.getIntExtra("id",0),intent.getStringExtra("name"),"0",0,intent.getBooleanExtra("type",false));
        chatRoomArrayList.add(room);
        mAdapter.notifyDataSetChanged();
    }
    private void addUser(String response,int userId)
    {
        String name=response.substring(0,response.indexOf(";"));
        String number=response.substring(response.indexOf(";")+1);
        Intent intent1=new Intent("add user");
        intent1.putExtra("name",name);
        intent1.putExtra("number",number);
        intent1.putExtra("id",userId);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent1);
        DBHelper.getInstance(getActivity()).addUser("("+userId+",\""+name+"\",\""+number+"\")");
    }
    private void handlePush(final Intent intent2)
    {
        final int chatRoomId=Integer.parseInt(intent2.getStringExtra("from"));
        final String message=intent2.getStringExtra("message");
        String type=intent2.getStringExtra("type");
        final int userId=Integer.parseInt(message.substring(0,message.indexOf(";")));
        for(ChatRoom chatRoom:chatRoomArrayList)
            if(chatRoom.getId()==chatRoomId)
            {
                DBHelper.getInstance(getActivity()).addMessage(new Message(0,message,"",chatRoomId));
                chatRoom.setUnreadCount(chatRoom.getUnreadCount()+1);
                mAdapter.notifyDataSetChanged();
                return;
            }
        if(DBHelper.getInstance(getActivity()).getNameofUser(userId).equals(""))
        {
            String endp=EndPoints.GET_USER.replace("_ID_",userId+"");
            StringRequest sr = new StringRequest(Request.Method.POST, endp, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    addUser(response,userId);
                    handlePush(intent2);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("getUser", error.toString());
                }
            });
            MyApplication.getmInstance().addToRequestQueue(sr);
            return;
        }
        if(type.equals("single"))
        {
        ChatRoom room=new ChatRoom(chatRoomId,DBHelper.getInstance(getActivity()).getNameofUser(userId),"0",0,false);
        DBHelper.getInstance(getActivity()).addRoom(room);
        DBHelper.getInstance(getActivity()).updateUser(userId,chatRoomId);
        Intent intent1=new Intent("update user");
        intent1.putExtra("id",room.getId());
        intent1.putExtra("user_id",userId);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent1);
        chatRoomArrayList.add(room);
        DBHelper.getInstance(getActivity()).addMessage(new Message(0,message,"",chatRoomId));
        room.setUnreadCount(1);
        mAdapter.notifyDataSetChanged();
        }
        else {
            String endp = EndPoints.GET_GROUP.replace("_ID_", chatRoomId + "");
            StringRequest sr = new StringRequest(Request.Method.POST, endp, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    addGroup(chatRoomId, message, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("getGroup", error.toString());
                }
            });
            MyApplication.getmInstance().addToRequestQueue(sr);
        }
    }
    public void addGroup(int chatRoomId,String message,String response)
    {
        StringTokenizer st=new StringTokenizer(response,";");
        int gid=Integer.parseInt(st.nextToken());
        int createdby=Integer.parseInt(st.nextToken());
        String name=st.nextToken();
        String status=st.nextToken();
        String ids=st.nextToken();
        Group group=new Group(gid,chatRoomId,createdby,name,status,DBHelper.getInstance(getActivity()).getNameOfUsers("("+ids.substring(1)+")"),"");
        ChatRoom room=new ChatRoom(chatRoomId,group.getName(),"",0,true);
        DBHelper.getInstance(getActivity()).addGroup(group);
        DBHelper.getInstance(getActivity()).addRoom(room);
        chatRoomArrayList.add(room);
        mAdapter.notifyDataSetChanged();
    }
}
