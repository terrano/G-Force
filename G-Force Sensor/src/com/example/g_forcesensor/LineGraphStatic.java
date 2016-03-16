package com.example.g_forcesensor;

import org.achartengine.model.XYMultipleSeriesDataset;

public class LineGraphStatic extends LineGraph{

	public LineGraphStatic(XYMultipleSeriesDataset mDataset, String str) {
		super(str);
		this.mDataset = mDataset;
		
		this.mRenderer.setZoomButtonsVisible(true);
		this.mRenderer.setZoomEnabled(true);
	}	
}
