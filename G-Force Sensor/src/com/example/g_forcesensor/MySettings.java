package com.example.g_forcesensor;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;

public class MySettings {
	
	private static SharedPreferences mSettings = null;
	private static MySettings instance = null;
	
	private MySettings(Context context) {
		mSettings = PreferenceManager.getDefaultSharedPreferences(context);
		PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
	}
	
	public static MySettings getInstance(Context context){
		if (instance == null) {
			instance = new MySettings(context);
			resetSettings();
			return instance;
		}
		return instance;
	}

	public static void resetSettings(){
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean("service_running", false);
		editor.putBoolean("shouldTheServiceBeTerminated", true);
		editor.putBoolean("acitvity", false);
		editor.commit();
	}
	
	public boolean isServiceRunning() {
		return mSettings.getBoolean("service_running", false);
	}

	public void setServiceRunning(boolean isRunning) {		
		synchronized (this) {
			SharedPreferences.Editor editor = mSettings.edit();
			editor.putBoolean("service_running", isRunning);
			editor.commit();
		}		
	}
    
	public boolean shouldTheServiceBeTerminated(){
		return mSettings.getBoolean("shouldTheServiceBeTerminated", true);
	}
	
	public void setTheServiceToBeTerminated(boolean beTerminated){
		synchronized (this) {
			SharedPreferences.Editor editor = mSettings.edit();
			editor.putBoolean("shouldTheServiceBeTerminated", beTerminated);
			editor.commit();
		}
	}
	
	public void setActivityLaunch(boolean act){
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean("activity", act);
		editor.commit();
	}
	
	public boolean getActivityLaunch(){
		return mSettings.getBoolean("activity", false);
	}
	
	public float getMax() {
		return mSettings.getFloat("max", 0);
	}

	public void setMax(float max) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putFloat("max", max);
		editor.commit();
	}

	public int getSensitivity(){
		String s = mSettings.getString("sensitivity", "Medium");		
		if (s.equals("Low")) return SensorManager.SENSOR_DELAY_NORMAL;
		if (s.equals("Medium")) return SensorManager.SENSOR_DELAY_UI;
		if (s.equals("Fast")) return SensorManager.SENSOR_DELAY_GAME;
		if (s.equals("Maximum")) return SensorManager.SENSOR_DELAY_FASTEST;
		
		return SensorManager.SENSOR_DELAY_NORMAL;
	}
	
	public int getThreshold(){
		return mSettings.getInt("threshold", 30);
	}
}
