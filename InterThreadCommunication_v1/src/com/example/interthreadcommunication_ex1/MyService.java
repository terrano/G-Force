package com.example.interthreadcommunication_ex1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class MyService extends Service{
	
	private MySettings mySettings;
	private static String TAG = "MyService";
	private boolean isSkyDiving; 
	private double x = 0, y = 0, z = 0, g = 0, i = 0;

	////////////////////////////////////////////////////////////////////////
	//Start and Finish Service
	////////////////////////////////////////////////////////////////////////
	@Override
	public void onCreate() {
		Log.i(TAG, "[SERVICE] onCreate");
		super.onCreate();
		
		this.isSkyDiving = false;
		
		thread = new HandlerThread("[SERVICE] ServiceThread");
		thread.start();
		
		myHandlerThread = new MyHandlerThread(thread.getLooper());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "[SERVICE] onStartCommand");	
		this.mySettings = MySettings.getInstance(getApplicationContext());
		return super.onStartCommand(intent, flags, startId);
	}

	// Local Binder
	public class LocalBinder extends Binder{		
		MyService getService(){
			Log.i(TAG + "Binder", "[SERVICE] LocalBinder-getService");
			return MyService.this; 
		}
	}
	
	private boolean isMainActivityRunning;
	private HandlerThread thread;
	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "[SERVICE] onBind");		
		return new LocalBinder();
	}
	
	////////////////////////////////////////////////////////////////////////
	//Register and Unregister Main Activity
	////////////////////////////////////////////////////////////////////////
	private MainActivity mainActivity;
	public void registerActivity(MainActivity mainActivity){
		Log.i(TAG, "[SERVICE] registerActivity");
		this.mainActivity = mainActivity;
		this.isMainActivityRunning = true;
		makeNotification();
	}
	
	public void unregisterActivity(){
		Log.i(TAG, "[SERVICE] unregisterActivity");
		this.isMainActivityRunning = false;
		this.mainActivity = null;
		if ((!isMainActivityRunning) && (!isSkyDiving) && (!mySettings.isServiceRunning())) stopMyself();
	}
	////////////////////////////////////////////////////////////////////////
	
	private void stopMyself(){
		Log.i(TAG, "[SERVICE] stopMyself");
		this.stopSelf();
	}
	
	@Override
	public void onDestroy() {
		Log.i(TAG, "[SERVICE] onDestroy");
		
		Message msg = this.myHandlerThread.obtainMessage();
		msg.obj = "stop";
		this.myHandlerThread.sendMessage(msg);
		
		if (this.isSkyDiving) stopSkyDiving();
		if (this.notificationManager != null) this.notificationManager.cancel(0);
		
		this.thread.interrupt();
		this.thread.quit();
		
		this.myHandlerThread = null;
		
		super.onDestroy();
	}
	
	////////////////////////////////////////////////////////////////////////
	//BackGround Thread
	////////////////////////////////////////////////////////////////////////
	private volatile MyHandlerThread myHandlerThread;
	private class MyHandlerThread extends Handler{
		
		private SensorManager sensorManager;
		private Sensor gSensor;

		public MyHandlerThread(Looper looper) {
			super(looper);			
			
			this.sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
			this.gSensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			this.sensorManager.registerListener(sensorEventListener, gSensor, SensorManager.SENSOR_DELAY_GAME);
			
			Log.i(TAG + "MyHandlerThread", "[SERVICE] super(looper)");
		}
		
		public void handleMessage(Message msg) {
			Log.i(TAG + "MyHandlerThread", "[SERVICE] handleMessage " + msg.obj);			
			if (msg.obj == "stop") this.sensorManager.unregisterListener(sensorEventListener);
		}		

		// calculates G Force basing on x-y-z values
		private void calculateGForce(double x, double y, double z){
			//Log.i(TAG + "MyHandlerThread", "[SERVICE] calculateGForce");
			g = Math.round((Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2))) / SensorManager.GRAVITY_EARTH);
			i++;
			if (isMainActivityRunning) dispatchData();
		}

		private final SensorEventListener sensorEventListener = new SensorEventListener() {
			public void onAccuracyChanged(Sensor sensor, int accuracy) {}
			public void onSensorChanged(SensorEvent event) {
				//Log.i(TAG + "MyHandlerThread", "[SERVICE] onSensorChanged");
				x = event.values[0];
				y = event.values[1];
				z = event.values[2];
				calculateGForce(x, y, z);
			}
		};
	}
	//End of BackGround Thread	
	
	////////////////////////////////////////////////////////////////////////
	//CallBacks
	////////////////////////////////////////////////////////////////////////
	public void dispatchData(){
		//Log.i(TAG, "[SERVICE] dispatchData()");
		//mainActivity.getData(String.valueOf(g) + String.valueOf(x) + String.valueOf(y) + String.valueOf(z));
		mainActivity.getData(String.valueOf(g) + " " + String.valueOf(i));
	}
	
	public void goSkyDiving(){
		Log.i(TAG, "[SERVICE] goSkyDiving()");
		
		if (! this.isSkyDiving) {
			this.isSkyDiving = true;
			//makeNotification();
			//Log.i(TAG, "[SERVICE] goSkyDiving() makeNotification()");
		}
		else stopSkyDiving();
	}
	
	public void stopSkyDiving(){
		Log.i(TAG, "[SERVICE] stopSkyDiving()");
		if (this.isSkyDiving){
			this.isSkyDiving = false;
			//this.notificationManager.cancel(0);			
		}
	}
	
	////////////////////////////////////////////////////////////////////////
	//Notification
	////////////////////////////////////////////////////////////////////////
	private NotificationManager notificationManager;
	private void makeNotification(){
		Log.i(TAG, "[SERVICE] makeNotification()");
		
		Intent intent = new Intent();
		intent.setComponent(new ComponentName(this, MainActivity.class));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pIntent = PendingIntent.getActivity(mainActivity, 0, intent, 0);
		
		@SuppressWarnings("deprecation")
		Notification notification = new Notification.Builder(mainActivity)
		.setTicker("SkyDive G-Force TICKER")
		.setContentTitle("SkyDive G-Force contentTitle")
		.setContentText("SkyDive G-Force content text")
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentIntent(pIntent)
		.getNotification();
		
		notification.flags = Notification.FLAG_NO_CLEAR;
		
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(0, notification);
	}
}
