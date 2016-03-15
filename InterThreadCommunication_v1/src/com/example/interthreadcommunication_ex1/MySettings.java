package com.example.interthreadcommunication_ex1;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class MySettings {
	private static final String TAG = "MySettings";
	private SharedPreferences mSettings;
	private static MySettings instance = null;
	
	private MySettings(Context context) {
		Log.i(TAG, "[MySettings] New MySettings");
		this.mSettings = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public static MySettings getInstance(Context context){
		if (instance == null) {
			Log.i(TAG, "[MySettings] getInstance = Instance Does Not Exist");
			instance = new MySettings(context);
			return instance;
		}
		Log.i(TAG, "[MySettings] getInstance = Instance Exists");
		return instance;
	}

	public boolean isServiceRunning() {
		Log.i(TAG, "[MySettings] isServiceRunning = " + String.valueOf(mSettings.getBoolean("service_running", false)));
		return mSettings.getBoolean("service_running", false);
	}

	public void setServiceRunning(boolean isRunning) {
		Log.i(TAG, "[MySettings] setServiceRunning = " + String.valueOf(isRunning));
		SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean("service_running", isRunning);
        editor.commit();
	}
    
	
}
