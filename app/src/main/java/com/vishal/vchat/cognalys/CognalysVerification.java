package com.vishal.vchat.cognalys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.vishal.vchat.app.MyApplication;

public class CognalysVerification extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String mobile = intent.getStringExtra("mobilenumber");
		String app_user_id = intent.getStringExtra("app_user_id");
		Toast.makeText(context,"Verified Mobile : "+ mobile, Toast.LENGTH_SHORT)
				.show();
		Toast.makeText(context, "Verified App User ID : "+app_user_id, Toast.LENGTH_SHORT)
				.show();
		SharedPreferences.Editor editor=MyApplication.getmInstance().getPrefManager().getEditor();
		editor.putString("user_number",mobile);
		editor.putString("cognalys_app_id",app_user_id);

	}

}