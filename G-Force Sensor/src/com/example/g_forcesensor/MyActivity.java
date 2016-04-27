package com.example.g_forcesensor;

import org.achartengine.GraphicalView;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.LinearLayout;

abstract class MyActivity extends Activity{

	public abstract void getData(String great, String small, String last, String maxl);
	protected abstract void addGraphViewComponents();
	
	protected MyService myService = null;
	protected ServiceConnection myServiceConnection = null;
	protected static GraphicalView view;
	protected LinearLayout ll;
	protected boolean isViewClickable;
	
	protected void createSrevicConnectionInstance(){
     	if (myServiceConnection == null) {

    	    myServiceConnection = new ServiceConnection() {	
	    		@Override
	    		public void onServiceDisconnected(ComponentName name) {}
	    		
	    		@Override
	    		public void onServiceConnected(ComponentName name, IBinder service) {
	    			MyService.LocalBinder binder = (MyService.LocalBinder)service;
	    			myService = binder.getService(); 
	    			myService.registerActivity( MyActivity.this );	    			
	    			view = myService.getView(getApplicationContext());	    			
	    			if (isViewClickable) addGraphViewComponents();	    			
	    			if (view != null) ll.addView(view);	
	    		}
	    	}; 
    	}
    }

    @Override
    protected void onPause() {
    	if (myService!=null) {
        	myService.unregisterActivity();
        	unbindService(myServiceConnection);
        	myService = null;
        	myServiceConnection = null;
            ll.removeView(view);
        	view = null;
        }
        
        super.onPause();
    }
}
