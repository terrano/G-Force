package com.example.interthreadcommunication_ex1;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity implements dataRetrieving{
	
	private static final String TAG = "MainActivity";
	private TextView text;
	
	////////////////////////////////////////////////////////////////////////
	//Start and Finish Activity
	////////////////////////////////////////////////////////////////////////
	private MyService myService;
	private MySettings mySettings;
    private boolean isServiceRunning;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.i(TAG, "[ACTIVITY] onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        
        
        text = (TextView)findViewById(R.id.textView1);
      	
    }
    
    @Override
	protected void onResume() {
    	Log.i(TAG, "[ACTIVITY] onResume");
		super.onResume();
		
		// connecting settings
        mySettings = MySettings.getInstance(getApplicationContext());
        
        // Read from preferences if the service was running on the last onPause
        isServiceRunning = mySettings.isServiceRunning();
        
        // Start the service if this is considered to be an application start 
        if (!isServiceRunning) {
        	Log.i(TAG, "[ACTIVITY] onResume Serive IS NOT running");
            startMyService();            
            mySettings.setServiceRunning(true);
			bindMyService();
        }
        else {
        	Log.i(TAG, "[ACTIVITY] onResume Serive IS running");
            bindMyService();
        }
            
	}

    ServiceConnection myServiceConnection = new ServiceConnection() {		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "[ACTIVITY] onServiceDisconnected");
			isServiceRunning = false;
			myService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "[ACTIVITY] onServiceConnected");
			MyService.LocalBinder binder = (MyService.LocalBinder)service;
			myService = binder.getService();
			myService.registerActivity(MainActivity.this);
		}
	};
	
	// Start and Bind service
	private void startMyService() {
        if (! isServiceRunning) {
            Log.i(TAG, "[ACTIVITY] startMyService");
            startService(new Intent(MainActivity.this, MyService.class));
            isServiceRunning = true;
        }
    }
    
    private void bindMyService() {
        Log.i(TAG, "[ACTIVITY] bindMyService");
        bindService(new Intent(MainActivity.this, MyService.class), myServiceConnection, Context.BIND_AUTO_CREATE);
    }

    // Stop Service
    @Override
    protected void onPause() {
        Log.i(TAG, "[ACTIVITY] onPause");
        if (isServiceRunning) unbindMyService();
        super.onPause();
    }

    ////////////////////////////////////////////////////////////////////////
    public void setRetainInstance(boolean retain){
    	Log.i(TAG, "[ACTIVITY] setRetainInstance");
    }
    
    @Override
	protected void onDestroy() {
    	Log.i(TAG, "[ACTIVITY] onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, "[ACTIVITY] onRestart");
		super.onRestart();
	}

	@Override
	protected void onStart() {
		Log.i(TAG, "[ACTIVITY] onStart");
		super.onStart();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "[ACTIVITY] onStop");
		super.onStop();
	}
	///////////////////////////////////////////////////////////////////////
	
	private void unbindMyService() {
        Log.i(TAG, "[ACTIVITY] unbindMyService");
        if (myService!=null){
        	myService.unregisterActivity();
        	unbindService(myServiceConnection);
        }        
    }
         
    ////////////////////////////////////////////////////////////////////////
	//CallBack
	////////////////////////////////////////////////////////////////////////
	@Override
	public void getData(String str){
		//Log.i(TAG, "[ACTIVITY] getData");
		text.setText(str);
	}
	
	////////////////////////////////////////////////////////////////////////
    //Buttons
    ////////////////////////////////////////////////////////////////////////
	public void onRun(View v){
    	Log.i(TAG, "[ACTIVITY] onRun");        
    	myService.dispatchData();
    }
	
	public void onGo(View v){
		Log.i(TAG, "[ACTIVITY] onGo");		
		myService.goSkyDiving();
	}

    public void onBye(View v){
    	Log.i(TAG, "[ACTIVITY] onBye");    
    	mySettings.setServiceRunning(false);
    	if (this.myService != null) myService.stopSkyDiving();
    	this.finish();
    }
    
    public void onJL(View v){
    	Log.i(TAG, "[ACTIVITY] onJL");  
    	Intent intent = new Intent(MainActivity.this, JumpList.class);
    	startActivity(intent);
    }
    ////////////////////////////////////////////////////////////////////////
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
