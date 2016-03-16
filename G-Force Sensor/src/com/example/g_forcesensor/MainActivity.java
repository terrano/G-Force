package com.example.g_forcesensor;

import java.text.DecimalFormat;

import com.example.db.DBListActivity;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

public class MainActivity extends MyActivity{
	
	private TextView greatG;
	private TextView smallG;
	private TextView lastG;
	private TextView maxG;
	private TextView threshold;
	private DecimalFormat df2;
	
	private MySettings mySettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        
        
        this.greatG = (TextView)findViewById(R.id.greatG); 
        this.smallG = (TextView)findViewById(R.id.smallG);
        
        this.lastG = (TextView)findViewById(R.id.last_num); 
        this.maxG = (TextView)findViewById(R.id.maximum_num);
        this.threshold = (TextView) findViewById(R.id.threshold_value);
       
        this.df2 = new DecimalFormat("##.##");
		
        this.ll = (LinearLayout) findViewById(R.id.chart);
        
        this.isViewClickable = true;
    }
    
    @Override
	protected void onResume() {
    	super.onResume();
		
		mySettings = MySettings.getInstance(this);         
		createSrevicConnectionInstance();
        
        if (!mySettings.isServiceRunning()) {
        	startService(new Intent(MainActivity.this, MyService.class));
        	bindMyService();
        }
        else bindMyService();
         
        if (!mySettings.shouldTheServiceBeTerminated()) ((Button)findViewById(R.id.GoBackground)).setText(R.string.StopBackGround);
	}

    private void bindMyService() {
    	bindService(new Intent(MainActivity.this, MyService.class), myServiceConnection, Context.BIND_AUTO_CREATE);
    }
    
	@Override
	protected void addGraphViewComponents() {
		view.setClickable(true);
		view.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, FullScreenChartViewActivity.class);
		    	startActivity(intent); 
			}
		});
		
		threshold.setText(String.valueOf(df2.format(mySettings.getThreshold() / 10D)));
	}
	

    public void getData(String great, String small, String last, String max){
		this.greatG.setText(great);
		this.smallG.setText(small);
		this.lastG.setText(last);
		this.maxG.setText(max);
		if (view != null) view.repaint();
	}
	
	public void onGoBackGround(View v){
		Button btn = (Button) findViewById(R.id.GoBackground);
		
    	if (myService != null) 
    		if (!myService.hasGone2Forground) {
    			myService.becomeForground();
    			btn.setText(R.string.StopBackGround);
    		}
    		else {
    			myService.ceaseForground();
    			btn.setText(R.string.GoBackground);
    		}
    }
	
    public void onShakeList(View v){
    	Intent intent = new Intent(MainActivity.this, DBListActivity.class);
    	startActivity(intent);
    }
    
    public void showPopupMenu(View v) {
    	PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
		popupMenu.inflate(R.menu.popupmenu); 

		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Intent intent;
						switch (item.getItemId()) {
						case R.id.menu1:
							mySettings.setTheServiceToBeTerminated(true);
					    	MainActivity.this.finish();
							return true;
						case R.id.menu2:
							intent = new Intent(MainActivity.this, SettingsActivity.class);
							startActivity(intent);
							return true;
						case R.id.menu3:
							intent = new Intent(MainActivity.this, AboutInfoActivity.class);
							startActivity(intent);
							return true;
						default:
							return false;
						}
					}
				});

		popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
			@Override
			public void onDismiss(PopupMenu menu) {}
		});
		popupMenu.show();
	}
}
