package com.vishal.vchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.vishal.vchat.R;
import com.vishal.vchat.adapter.ContactsAdapter;
import com.vishal.vchat.adapter.NewGroupAdapter;
import com.vishal.vchat.app.MyApplication;
import com.vishal.vchat.db.DBHelper;
import com.vishal.vchat.helper.SimpleDividerItemDecoration;
import com.vishal.vchat.model.User;

import java.util.List;

/**
 * Created by MUJ on 23-Apr-17.
 */

public class NewGroup extends AppCompatActivity {
    List<User> userList;
    protected NewGroupAdapter mAdapter;
    boolean arr[];
    private RecyclerView recyclerView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newgroup);
        userList= DBHelper.getInstance(getApplicationContext()).getAllUsers();
        arr=new boolean[userList.size()];
        mAdapter=new NewGroupAdapter(getApplicationContext(),userList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView=(RecyclerView)findViewById(R.id.recycler_new_group);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new NewGroupAdapter.RecyclerTouchListener(getApplicationContext(),recyclerView,new NewGroupAdapter.ClickListener(){
            public void onClick(View view, int position){
                CheckBox t=(CheckBox)view.findViewById(R.id.newGroup_box);

                if(t.isActivated())
                {
                    arr[position]=false;
                    t.setActivated(false);
                }
                else
                {
                    arr[position]=true;
                    t.setActivated(true);
                }
            }
            public void onLongClick(View view,int pos){

            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String id="(";
        for(int i=0;i<arr.length;i++)
            if(arr[i])
                id+=userList.get(i).getId()+",";
        id=id.substring(0,id.length()-1)+")";
        Intent intent=new Intent(NewGroup.this,GroupProfile.class);
        intent.putExtra("ids",id);
        startActivity(intent);
        finish();
        return true;
    }
}
