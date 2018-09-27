package com.vishal.vchat.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;
import com.vishal.vchat.R;
import com.vishal.vchat.db.DBHelper;

import java.util.List;

/**
 * Created by MUJ on 13-Nov-17.
 */

public class IncomingCallActivity extends AppCompatActivity{
    Call call;
    TextView message;
    String name;
    Button accept,reject;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_call_incoming);
        message=(TextView)findViewById(R.id.callmessage);
        accept=(Button)findViewById(R.id.call_accept);
        reject=(Button)findViewById(R.id.call_reject);
        call=MainActivity.incomingCall;
        name=DBHelper.getInstance(getApplicationContext()).getNameofUser(Integer.parseInt(call.getCallId()));
        message.setText("Incoming Call "+name);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(accept.getText().equals("Accept")) {
                    call.answer();
                    accept.setText("Hangup");
                    reject.setVisibility(View.GONE);
                }
                else
                {
                    call.hangup();
                }
            }
        });
        call.addCallListener(new CallListener() {
            @Override
            public void onCallProgressing(Call call) {

            }

            @Override
            public void onCallEstablished(Call call) {
                message.setText("Established");
            }

            @Override
            public void onCallEnded(Call call) {
                MainActivity.incomingCall=null;
                finish();
            }

            @Override
            public void onShouldSendPushNotification(Call call, List<PushPair> list) {

            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call.hangup();
                MainActivity.incomingCall=null;
                finish();
            }
        });
    }
}
