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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vishal.vchat.R;
import com.vishal.vchat.adapter.ChatRoomsAdapter;
import com.vishal.vchat.db.DBHelper;
import com.vishal.vchat.helper.SimpleDividerItemDecoration;
import com.vishal.vchat.adapter.ContactsAdapter;
import com.vishal.vchat.model.ChatRoom;
import com.vishal.vchat.model.Message;
import com.vishal.vchat.model.User;

import java.util.List;

/**
 * Created by MUJ on 29-Mar-17.
 */

public class ContactList extends Fragment {
    protected List<User> usersArrayList;
    protected ContactsAdapter mAdapter;
    private RecyclerView recyclerView;
    private BroadcastReceiver broadcastReceiver;
    public ContactList()
    {

    }
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
    }
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        View v=inflater.inflate(R.layout.activity_contactlist,container,false);
        Context context=getActivity().getApplicationContext();
        usersArrayList= DBHelper.getInstance(context).getAllUsers();
        mAdapter=new ContactsAdapter(context,usersArrayList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(context);
        recyclerView=(RecyclerView)v.findViewById(R.id.recycler_view_contact);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new ContactsAdapter.RecyclerTouchListener(context,recyclerView,new ContactsAdapter.ClickListener(){
            public void onClick(View view, int position){
                User user=usersArrayList.get(position);
                Intent intent=new Intent(getActivity(),ChatRoomActivity.class);
                intent.putExtra("chat_room_id",user.getRoomid());
                intent.putExtra("user_id",user.getId());
                intent.putExtra("name",user.getName());
                intent.putExtra("type",false);
                startActivity(intent);
            }
            public void onLongClick(View view,int pos){

            }
        }));
        broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("update user"))
                    updateUser(intent);
                else if(intent.getAction().equals("add user"))
                    addUser(intent);
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,new IntentFilter("update user"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,new IntentFilter("add user"));
        return v;
    }
    public void  addUser(Intent intent)
    {
        String name=intent.getStringExtra("name");
        String number=intent.getStringExtra("number");
        int userid=intent.getIntExtra("id",0);
        User user=new User(userid,0,name,number,"","","");
        usersArrayList.add(user);
        mAdapter.notifyDataSetChanged();
    }
    public  void updateUser(Intent intent)
    {
        int userId=intent.getIntExtra("user_id",0);
        for (int i=0;i<usersArrayList.size();i++)
            if(usersArrayList.get(i).getId()==userId)
            {
                usersArrayList.get(i).setRoomid(intent.getIntExtra("id",0));
                break;
            }
        mAdapter.notifyDataSetChanged();
    }

}
