package com.example.g_forcesensor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

public class FullScreenChartViewActivity extends MyActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shakelist);		
		this.ll = (LinearLayout)findViewById(R.id.chart);	
		
		this.isViewClickable = false;		
        createSrevicConnectionInstance();        
        bindService(new Intent(FullScreenChartViewActivity.this, MyService.class), myServiceConnection, Context.BIND_AUTO_CREATE);	
	}
	
	@Override
	public void getData(String great, String small, String last, String maxl) {
		if (view != null) view.repaint();
	}
	
	@Override
	protected void addGraphViewComponents() {}
}
