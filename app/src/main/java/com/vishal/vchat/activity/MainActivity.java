package com.vishal.vchat.activity;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.vishal.vchat.R;
import com.vishal.vchat.app.EndPoints;
import com.vishal.vchat.app.MyApplication;
import com.vishal.vchat.db.DBHelper;
import com.vishal.vchat.model.ChatRoom;
import com.vishal.vchat.model.Message;
import com.vishal.vchat.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


public class MainActivity extends AppCompatActivity {
    RoomList roomList;
    static SinchClient client;
    static Call incomingCall=null;
    ContactList contactList;
    HashMap<String,String> numbers=new HashMap<>();

    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(MyApplication.getmInstance().getPrefManager().getUser()==null){
            Intent intent=new Intent(MainActivity.this,CognalysActivity.class);
            startActivity(intent);
            finish();
        };
        setContentView(R.layout.activity_main);
        viewPager=(ViewPager)findViewById(R.id.viewpager);
        setupViewPager();
        setupSinch();
        ((TabLayout)findViewById(R.id.tabs)).setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_sync:new GetContacts().execute(null,null,null);
                return true;
            case R.id.showDB:DBHelper.getInstance(getApplicationContext()).showDB();
                return true;
            case R.id.newGroup: Intent intent=new Intent(MainActivity.this,NewGroup.class);
                startActivity(intent);
                return true;
            case R.id.addContact:Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, 1000);
                return true;
            default:return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
           final String number,name;
           Uri uri=data.getData();
           Cursor cursor=getContentResolver().query(uri,null,null,null,null);
           cursor.moveToFirst();
           number=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ","");
           final String number1=number.substring(number.length()-10);
           name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
           Log.d("Numbers List", number);
           StringRequest srq = new StringRequest(Request.Method.POST, EndPoints.SYNC_CONTACTS, new Response.Listener<String>() {
               @Override
               public void onResponse(String response) {
                   Log.d("Contacts", response);
                   addUser(response,name,number1);
               }
           }, new Response.ErrorListener() {
               @Override
               public void onErrorResponse(VolleyError error) {
                   Log.e("Contacts", error.getMessage()+"");
               }
           }) {
               protected Map<String, String> getParams() {
                   Map<String, String> params = new HashMap<String, String>();
                   params.put("list", number1);
                   return params;
               }
           };
           MyApplication.getmInstance().addToRequestQueue(srq);
           //Log.d("Contacts","Failed to pick contact");
    }

    private class GetContacts extends AsyncTask<Void,Void,Void> {
        protected Void doInBackground(Void... params) {
            ArrayList<StringBuffer> numList=new ArrayList<>();
            ContentResolver cr = getContentResolver();
            String[] projection = {ContactsContract.Contacts._ID, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
            Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
            int i=0;StringBuffer list=new StringBuffer();
            if (cur.getCount() > 0) {
                outer:while (cur.moveToNext()) {
                    String id = cur.getString(0);
                    String name = cur.getString(2);
                    String number = cur.getString(1).replace(" ", "");
                    if(number.length()>=10) {
                        number = number.substring(number.length() - 10);
                        for(int j=0;j<contactList.usersArrayList.size();j++)
                            if(number.equals(contactList.usersArrayList.get(j).getNumber()))
                                continue outer;
                        if(number.equals(MyApplication.getmInstance().getPrefManager().getUser().getNumber()))
                            continue outer;
                        numbers.put(number,name);
                        list.append(number+',');
                        i++;
                    }
                    if(i==30) {
                        numList.add(list.deleteCharAt(list.length()-1));
                        i=0;
                        list=new StringBuffer();
                    }
                }
                cur.close();
            }

                for(i=0;i<numList.size();i++)
                {
                    final String data=numList.get(i).toString();
                    Log.d("Numbers List", data);
                    StringRequest srq = new StringRequest(Request.Method.POST, EndPoints.SYNC_CONTACTS, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Contacts", response);
                            addUsers(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Contacts", error.getMessage()+"");
                        }
                    }) {
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("list", data);
                            return params;
                        }
                    };
                    MyApplication.getmInstance().addToRequestQueue(srq);
                }
            return null;
        }
    }
    private void addUsers(String response)
    {
        List<User> user=new ArrayList<>();
        StringBuffer query=new StringBuffer("");
        StringTokenizer st=new StringTokenizer(response,";");

            while (st.hasMoreTokens()) {
                String res = st.nextToken();
                if(res.length()<10)continue;
                String number = res.substring(0, res.indexOf(","));
                int id = Integer.parseInt(res.substring(res.indexOf(",") + 1));
                user.add(new User(id, 0, numbers.get(number), number, "", "", ""));
                query.append("("+ id + ",\"" + numbers.get(number) + "\",\"" + number + "\"),");
            }
            if(user.size()==0)
                return;
        try {
            contactList.usersArrayList.addAll(user);
            contactList.mAdapter.notifyDataSetChanged();
            DBHelper.getInstance(this).addUser(query.deleteCharAt(query.length()-1).toString());
        } catch (Exception e)
        {
            Log.d("addUsers",e+" "+response);
        }
    }
    private void addUser(String response,String name,String number)
    {
        User user=new User(Integer.parseInt(response.substring(response.indexOf(',')+1,response.indexOf(';'))),0,name,number,"","","");
        Log.d(name,user.getId()+"");
        contactList.usersArrayList.add(user);
        contactList.mAdapter.notifyDataSetChanged();
        DBHelper.getInstance(this).addUser("("+user.getId()+",\""+name+"\",\""+number+"\")");
    }
    private void setupViewPager()
    {
        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());
        roomList=new RoomList();
        contactList=new ContactList();
        adapter.addFragment(roomList,"CHATS");
        adapter.addFragment(contactList,"CONTACTS");
        viewPager.setAdapter(adapter);
    }
    class  ViewPagerAdapter extends FragmentPagerAdapter
    {
        private final List<Fragment> fragmentList=new ArrayList<>();
        private final List<String> fragmentTitle=new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager)
        {
            super(manager);
        }
        public Fragment getItem(int position)
        {
            return fragmentList.get(position);
        }
        public int getCount(){return  fragmentList.size();}
        public void addFragment(Fragment fragment,String title){
            fragmentList.add(fragment);
            fragmentTitle.add(title);
        }
        public CharSequence getPageTitle(int position){
            return  fragmentTitle.get(position);
        }
    }

    private void setupSinch()
    {
        client= Sinch.getSinchClientBuilder().context(getApplicationContext()).applicationKey(getString(R.string.sinch_app_key)).applicationSecret(getString(R.string.sinch_app_secret)).environmentHost("sandbox.sinch.com").userId(MyApplication.getmInstance().getPrefManager().getUser().getId()+"").build();
        client.setSupportCalling(true);
        client.setSupportActiveConnectionInBackground(true);
        client.startListeningOnActiveConnection();
        client.getCallClient().addCallClientListener(new CallClientListener() {
            @Override
            public void onIncomingCall(CallClient callClient, Call call) {
                incomingCall=call;
                Intent intent=new Intent(MainActivity.this,IncomingCallActivity.class);
                startActivity(intent);
            }
        });
        client.start();
    }
}
