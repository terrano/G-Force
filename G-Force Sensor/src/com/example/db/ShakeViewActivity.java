package com.example.db;

import org.achartengine.GraphicalView;

import com.example.g_forcesensor.LineGraphStatic;
import com.example.g_forcesensor.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class ShakeViewActivity extends Activity{
	
	private ShakeDBHandler db;
	private ShakeObject jump;
	
	private TextView textDate;
	private TextView textMaxG;
	private TextView textTime;
	private LinearLayout ll;
	
	private long rowId;
	private LineGraphStatic lineStatic;
	private static GraphicalView view;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.shake_view);
		
		this.ll = (LinearLayout)findViewById(R.id.chart_static);
	    this.textMaxG = ((TextView)findViewById(R.id.chart_max));
	    this.textTime = ((TextView)findViewById(R.id.chart_duration));
	    this.textDate = ((TextView)findViewById(R.id.chart_date));
		
		this.rowId = -1;
		Bundle extras = getIntent().getExtras();
		if (extras != null) this.rowId = extras.getLong(ShakeDBHandler.COLUMN_ID);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		this.db = new ShakeDBHandler(this);
		
		this.jump = this.db.loadJump(rowId);
		
		this.lineStatic = new LineGraphStatic(this.jump.getmDataset().getmDataset(), this.getString(R.string.gforce));
		view = this.lineStatic.getView(this);
		if (view != null) ll.addView(view);
		
	    this.textMaxG.setText(String.valueOf(this.jump.getCurrentMax()));
	    this.textTime.setText(String.valueOf(this.jump.getDuration()));
	    this.textDate.setText(String.valueOf(this.jump.getDate()));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		getLoaderManager().destroyLoader(1);
		if (db != null) {
			db.closeDB();
			db = null;
		}
		
		if (jump != null) {
			jump.onDestroy();
			jump = null;
		}
	}
}
