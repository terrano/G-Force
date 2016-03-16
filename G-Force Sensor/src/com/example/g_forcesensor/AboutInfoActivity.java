package com.example.g_forcesensor;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class AboutInfoActivity extends Activity {

	private TextView vendor;
	private TextView model;
	private TextView version;
	private TextView power;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_info_activity);

		vendor = (TextView) findViewById(R.id.vendor_name);
		model = (TextView) findViewById(R.id.model_name);
		version = (TextView) findViewById(R.id.version_value);
		power = (TextView) findViewById(R.id.power_value);

		SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Sensor accel = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);

		vendor.setText(accel.getVendor());
		model.setText(accel.getName());
		power.setText(String.valueOf(accel.getPower()));
		version.setText(String.valueOf(accel.getVersion()));
	}
}