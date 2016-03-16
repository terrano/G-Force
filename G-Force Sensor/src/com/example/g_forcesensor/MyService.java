package com.example.g_forcesensor;

import java.text.DecimalFormat;
import java.util.Calendar;

import org.achartengine.GraphicalView;

import com.example.db.InsertShakeTask;
import com.example.db.ShakeObject;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;

public class MyService extends Service {

	private static final String DISPATCH = "DISPATCH";
	
	private IntentFilter filter;
	private HandlerThread thread;
	private volatile MyHandlerThread myHandlerThread;
	private MySettings mySettings = null;
	private int sensitivity;

	private MyActivity mainActivity;

	private Handler serviceHandler;
	private Messenger messenger;

	private boolean isActivityRunning;
	public boolean hasGone2Forground;

	private double x = 0, y = 0, z = 0, g = 0;
	private int i = 0;
	private DecimalFormat df2;
	private DecimalFormat df6;
	private double lastShake = 0, globalMax = 0;
	private double threshold = 2;
	private double low_threshold = 1.5;
	public static final int chart_length = 150;
	public static final int chart_length_suplement = 50;
	private int start_offset = 5;

	private LineGraphDynamic lineDynamic = null;
	private MultipleSeriesDataset mDataset = null;
	private MultipleSeriesDataset mDataset_parallel = null;

	private PowerManager.WakeLock wakeLock = null;

	@Override
	public void onCreate() {
		super.onCreate();

		getMySettings();

		this.mDataset = new MultipleSeriesDataset();
		this.mDataset_parallel = new MultipleSeriesDataset();

		this.lineDynamic = new LineGraphDynamic(this.mDataset.getmDataset(), chart_length, this.getString(R.string.gforce));

		this.hasGone2Forground = false;
		this.mySettings = null;

		this.thread = new HandlerThread("[ServiceThread]");
		this.thread.start();

		this.myHandlerThread = new MyHandlerThread(thread.getLooper());

		this.filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		this.filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(mReceiver, filter);

		this.df2 = new DecimalFormat("#.##");
		this.df6 = new DecimalFormat("#.######");

		this.serviceHandler = new Handler() {
			public void handleMessage(Message paramAnonymousMessage) {
				super.handleMessage(paramAnonymousMessage);
				if (paramAnonymousMessage.obj == DISPATCH) MyService.this.dispatchData();
			}
		};
		
		this.messenger = new Messenger(serviceHandler);
		Message localMessage = this.myHandlerThread.obtainMessage();
		localMessage.obj = this.messenger;
		this.myHandlerThread.sendMessage(localMessage);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	public class LocalBinder extends Binder {
		MyService getService() {
			return MyService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (mySettings == null) getMySettings();
		return new LocalBinder();
	}

	@Override
	public void onRebind(Intent intent) {
		this.restartSensor();
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return true;
	}

	private void getMySettings() {
		this.mySettings = MySettings.getInstance(this);
		this.mySettings.setServiceRunning(true);
		this.globalMax = this.mySettings.getMax();
		this.sensitivity = this.mySettings.getSensitivity();
		this.threshold = this.mySettings.getThreshold() / 10;
		this.low_threshold = this.threshold / 2;

		switch (this.sensitivity) {
			case SensorManager.SENSOR_DELAY_FASTEST: {
	 			this.start_offset = 35;
				break;
			}
			case SensorManager.SENSOR_DELAY_GAME: {
				this.start_offset = 30;
				break;
			}
			case SensorManager.SENSOR_DELAY_UI: {
				this.start_offset = 10;
				break;
			}
			case SensorManager.SENSOR_DELAY_NORMAL: {
				this.start_offset = 5;
				break;
			}
			default: {
				this.start_offset = 15;
			}
		}
	}

	public void registerActivity(MyActivity mainActivity) {
		this.mainActivity = mainActivity;
		this.isActivityRunning = true;
	}

	public void unregisterActivity() {
		this.mainActivity = null;
		this.isActivityRunning = false;
		if (mySettings.shouldTheServiceBeTerminated()) stopMyself();
	}
	
	private void stopMyself() {
		Message msg = this.myHandlerThread.obtainMessage();
		msg.obj = "stop";
		this.myHandlerThread.sendMessage(msg);

		MySettings.resetSettings();
		this.mySettings = null;

		this.mDataset.removeAllPoints();
		this.mDataset_parallel.removeAllPoints();
		this.lineDynamic = null;
		this.mDataset = null;
		this.mDataset_parallel = null;
		
		this.stopSelf();
	}

	@Override
	public void onDestroy() {
		this.thread.interrupt();
		this.thread.quit();

		this.myHandlerThread = null;

		unregisterReceiver(mReceiver);

		super.onDestroy();
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		super.onTaskRemoved(rootIntent);
		stopMyself();
	}

	public void becomeForground() {
		if (!this.hasGone2Forground) {
			Intent intent1 = new Intent();
			intent1.setComponent(new ComponentName(this, MainActivity.class));
			intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent pIntent = PendingIntent.getActivity(mainActivity, 0, intent1, 0);

			@SuppressWarnings("deprecation")
			Notification notification = new Notification.Builder(mainActivity)
					.setTicker(getResources().getString(R.string.app_name))
					.setContentTitle(getResources().getString(R.string.app_name))
					.setContentText(getResources().getString(R.string.content_text))
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentIntent(pIntent).getNotification();
			notification.flags = Notification.FLAG_NO_CLEAR;

			startForeground(100, notification);
			this.hasGone2Forground = true;
			this.mySettings.setTheServiceToBeTerminated(false);
		}
	}

	public void ceaseForground() {
		if (this.hasGone2Forground) {
			stopForeground(true);
			this.hasGone2Forground = false;
			this.mySettings.setTheServiceToBeTerminated(true);
		}
	}

	public void restartSensor() {
		this.sensitivity = this.mySettings.getSensitivity();
		Message msg = this.myHandlerThread.obtainMessage();
		msg.obj = "restartSensor";
		this.myHandlerThread.sendMessage(msg);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				acquireWakeLock();

				Message msg = myHandlerThread.obtainMessage();
				msg.obj = "restartSensor";
				myHandlerThread.sendMessageDelayed(msg, sensitivity);

				restartReceiver();
			}

			if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) 
				if (wakeLock != null) if (wakeLock.isHeld()) wakeLock.release();
		}
	};

	private void acquireWakeLock() {
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		int wakeFlags = PowerManager.PARTIAL_WAKE_LOCK;
		wakeLock = pm.newWakeLock(wakeFlags, "");
		wakeLock.acquire();
	}

	private void restartReceiver() {
		unregisterReceiver(mReceiver);
		registerReceiver(mReceiver, filter);
	}

	private class MyHandlerThread extends Handler {

		private Messenger messenger;
		
		private SensorManager sensorManager;
		private Sensor gSensor;

		private double currentMax = 0;
		private ShakeObject jo = null;
		private boolean isShakeDetected = false, recordEnable = false, finish_detected = false, isEndOfShake = false;
		private int end_point = -1, tail_offset = -1;
		private double finalSum = 0;

		private InsertShakeTask insertJump = null;

		private long time_start, time_stop = 0;

		public MyHandlerThread(Looper looper) {
			super(looper);
			this.registerSensor();
			time_start = System.currentTimeMillis();
		}

		public void handleMessage(Message msg) {

			if (msg.obj instanceof Messenger) this.messenger = (Messenger) msg.obj;
			
			if (msg.obj == "restartSensor") restartSensor();

			if (msg.obj == "stop") this.sensorManager.unregisterListener(sensorEventListener);
		}

		private void registerSensor() {
			this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
			this.gSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			this.sensorManager.registerListener(sensorEventListener, gSensor, sensitivity);
		}

		private void restartSensor() {
			sensorManager.unregisterListener(sensorEventListener);
			registerSensor();
		}

		private final SensorEventListener sensorEventListener = new SensorEventListener() {
			public void onAccuracyChanged(Sensor sensor, int accuracy) {}

			public void onSensorChanged(SensorEvent event) {
				x = event.values[0];
				y = event.values[1];
				z = event.values[2];
				g = (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2))) / SensorManager.GRAVITY_EARTH;
				i++;

				if (g > globalMax) {
					globalMax = g;
					mySettings.setMax((float) globalMax);
				}

				if ((g > threshold) && (!isShakeDetected)) {
					isShakeDetected = true;
					int offset = 0;
					if (mDataset_parallel.datasetX.getItemCount() > start_offset) 
						offset = mDataset_parallel.datasetX.getItemCount() - start_offset;

					while (offset > 0) {
						mDataset_parallel.removeOldPoints();
						offset--;
					}

					lastShake = g;
					time_start = System.currentTimeMillis();
				}

				if ((g > currentMax) && (isShakeDetected))
					currentMax = g;
				
				if ((g < low_threshold) && (isShakeDetected) && (!isEndOfShake)) {
					end_point = 3;
					isEndOfShake = true;
				}

				if (end_point > 0) {
					finalSum = finalSum + g;
					end_point--;
				}

				if ((end_point == 0) && (isEndOfShake)) {
					if ((finalSum / 3) < low_threshold) {
						finish_detected = true;
						tail_offset = start_offset;
						end_point--;
					} else {
						finalSum = 0;
						end_point = 3;
					}
				}

				if (finish_detected) tail_offset--;
				
				if (tail_offset == 0) {
					tail_offset--;
					finish_detected = false;
					time_stop = System.currentTimeMillis();
					recordEnable = true;
				}
				
				if (recordEnable) { 
					Calendar localCalendar = Calendar.getInstance();
		            
					String date = Calendar.getInstance().getTime().toString();
		            String shake_name = "jump_" + (localCalendar.get(11) + ":" + localCalendar.get(12) + ":" + localCalendar.get(13)).replace(":", "_");
		            String duration = String.valueOf(time_stop - time_start);
					jo = new ShakeObject(shake_name, date, duration, currentMax, mDataset_parallel);
					
					time_stop = time_start = 0;

					insertJump = new InsertShakeTask(getApplicationContext(), jo);
					insertJump.execute();
					recordEnable = false;
				}

				if (insertJump != null)
					if (insertJump.getStatus() == AsyncTask.Status.FINISHED) {
						isShakeDetected = false;
						isEndOfShake = false;
						insertJump = null;
					}
				
				if ((mDataset_parallel != null) && (mDataset != null)) {
					mDataset.addPoint(i, x, y, z, g);
					mDataset_parallel.addPoint(i, x, y, z, g);

					if (isActivityRunning) {
						lineDynamic.scrollGraphFroward(i);
						
						Message msg = Message.obtain();
						msg.obj = DISPATCH;
						try {
							messenger.send(msg); 
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}

					if (!isShakeDetected) {
						if (mDataset.datasetX.getItemCount() > chart_length) mDataset.removeOldPoints();
						if (mDataset_parallel.datasetX.getItemCount() > chart_length_suplement) mDataset_parallel.removeOldPoints();
					}
				}
			}
		};
	}

	public void dispatchData() {
		if (this.mainActivity != null)
			this.mainActivity.getData(String.valueOf(this.df2.format(g)),
					String.valueOf(this.df6.format(g)),
					String.valueOf(this.df2.format(lastShake)),
					String.valueOf(this.df2.format(globalMax)));
	}

	public GraphicalView getView(Context context) {
		if (context == null) return null;
		if (this.lineDynamic == null) return null;
		return this.lineDynamic.getView(context);
	}
}
