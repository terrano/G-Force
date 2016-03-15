package com.example.interthreadcommunication_ex1;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class JumpList extends Activity{
	
	private static final String TAG = "JumpList";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "[ACTIVITY] onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jumplist);
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "[ACTIVITY] onDestroy");
		super.onDestroy();
	}
	
}
