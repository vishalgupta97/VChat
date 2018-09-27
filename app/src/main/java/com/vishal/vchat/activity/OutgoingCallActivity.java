package com.vishal.vchat.activity;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;
import com.vishal.vchat.R;

import java.util.List;

/**
 * Created by MUJ on 13-Nov-17.
 */

public class OutgoingCallActivity extends AppCompatActivity {
    int id;
    String name;
    SinchClient client;
    TextView message;
    Button hangup;
    Call call;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_call);
        name=getIntent().getStringExtra("title");
        id=getIntent().getIntExtra("id",0);
        client=MainActivity.client;
        call=client.getCallClient().callUser(id+"");
        call.addCallListener(new CallListener() {
            @Override
            public void onCallProgressing(Call call) {

            }

            @Override
            public void onCallEstablished(Call call) {
                    message.setText("Call Established");
                setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            }

            @Override
            public void onCallEnded(Call call) {
                setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
                finish();
            }

            @Override
            public void onShouldSendPushNotification(Call call, List<PushPair> list) {

            }
        });
        message=(TextView)findViewById(R.id.callmessage);
        hangup=(Button)findViewById(R.id.endcall);
        message.setText("Calling "+name);
        hangup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call.hangup();
            }
        });
    }
}
