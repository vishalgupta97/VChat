package com.vishal.vchat.activity;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.vishal.vchat.R;
import com.vishal.vchat.app.EndPoints;
import com.vishal.vchat.app.MyApplication;
import com.vishal.vchat.cognalys.CheckNetworkConnection;
import com.vishal.vchat.cognalys.VerifyMobile;

import java.util.HashMap;
import java.util.Map;

public class CognalysActivity extends ActionBarActivity {
	Button test;
	EditText mobilenum,name;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_cognalys);
		test = (Button) findViewById(R.id.test);
		mobilenum = (EditText) findViewById(R.id.editText1);
		name=(EditText)findViewById(R.id.Name);
		test.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String mobile = "+91"+ mobilenum.getText().toString();
				Intent in = new Intent(CognalysActivity.this, VerifyMobile.class);

				in.putExtra("app_id", "b9606eba4e574d058bdef15");
				in.putExtra("access_token","dd9ff3dedd3995ccc751ca07f5ca00bd0ef573ec");
				in.putExtra("mobile", mobile);
				in.putExtra("name",name.getText());
				if (mobile.length() == 0) {
					mobilenum.setError("Please enter mobile number");
				} else {
					if (CheckNetworkConnection
							.isConnectionAvailable(getApplicationContext())) {
						//startActivityForResult(in, VerifyMobile.REQUEST_CODE);
						sendData();
					} else {
						Toast.makeText(getApplicationContext(),
								"no internet connection", Toast.LENGTH_SHORT)
								.show();
					}
				}

			}
		});
	}

	public void sendData()
	{
		StringRequest srq=new StringRequest(Request.Method.POST, EndPoints.NEW_USER, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				response=response.substring(0,response.length()-1);
				MyApplication.userId=Integer.parseInt(response);
				MyApplication.getmInstance().getPrefManager().getEditor().putInt("user_id",MyApplication.userId);
				MyApplication.getmInstance().getPrefManager().getEditor().commit();
				Log.d("sendData",MyApplication.userId+"");
				finish();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		}){
			protected Map<String,String> getParams(){
				Map<String,String> params=new HashMap<String, String>();
				params.put("name",name.getText().toString());
				params.put("number",mobilenum.getText().toString());
				params.put("fcmid", FirebaseInstanceId.getInstance().getToken());
				return  params;
			};};
		MyApplication.getmInstance().addToRequestQueue(srq);
		MyApplication.getmInstance().getPrefManager().addUser(name.getText().toString(),mobilenum.getText().toString());
		Intent i = new Intent(CognalysActivity.this, ProfileSetup.class);
		i.putExtra("mobilenumber", mobilenum.getText().toString());
		startActivity(i);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);

		if (arg0 == VerifyMobile.REQUEST_CODE) {
			String message = arg2.getStringExtra("message");
			int result = arg2.getIntExtra("result", 0);

			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
					.show();
			Toast.makeText(getApplicationContext(), "" + result,
					Toast.LENGTH_SHORT).show();

		}
	}

}
