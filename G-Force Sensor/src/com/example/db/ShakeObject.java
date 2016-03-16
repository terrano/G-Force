package com.example.db;

import com.example.g_forcesensor.MultipleSeriesDataset;

public final class ShakeObject {
	private String shake_name = null;
	private String date = null;
	private String duration = null;
	private MultipleSeriesDataset mDataset = null;
	private String maxG;
	
	public ShakeObject(String name, String date, String duration, String maxG){
		this.shake_name = name;
		this.date = date;
		this.duration = duration;
		this.maxG = maxG;
	}
	
	public ShakeObject(String name, String date, String duration, double maxG, MultipleSeriesDataset paramMultipleSeriesDataset) {
		this.shake_name = name;
		this.date = date;
		this.duration = duration;
		this.maxG = String.valueOf(Math.floor(100.0D * maxG) / 100.0D);
		this.mDataset = paramMultipleSeriesDataset;
	}

	public String getDuration() {
		return duration;
	}

	public String getCurrentMax() {
		return this.maxG;
	}

	public String getDate() {
		return this.date;
	}

	public String getShakeName() {
		return this.shake_name;
	}

	public MultipleSeriesDataset getmDataset() {
		return this.mDataset;
	}

	public void onDestroy() {
		this.shake_name = null;
		this.date = null;
		this.duration = null;
		if (this.mDataset != null) this.mDataset.removeAllPoints();
	}

	public void setCurrentMax(double maxG) {
		this.maxG = String.valueOf(maxG);
	}

	public void setmDataset(MultipleSeriesDataset paramMultipleSeriesDataset) {
		this.mDataset = paramMultipleSeriesDataset;
	}
}
