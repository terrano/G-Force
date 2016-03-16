package com.example.g_forcesensor;

import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;

public class MultipleSeriesDataset {
	
	protected TimeSeries datasetX;
	protected TimeSeries datasetY;
	protected TimeSeries datasetZ;
	protected TimeSeries datasetG;
	protected XYMultipleSeriesDataset mDataset;
	
	public MultipleSeriesDataset(){
		this.datasetX = new TimeSeries("X Acceleration");
		this.datasetY = new TimeSeries("Y Acceleration");
		this.datasetZ = new TimeSeries("Z Acceleration");
		this.datasetG = new TimeSeries("G Force");
		
		this.mDataset = new XYMultipleSeriesDataset();
		
		this.mDataset.addSeries(datasetX);
		this.mDataset.addSeries(datasetY);
		this.mDataset.addSeries(datasetZ);
		this.mDataset.addSeries(datasetG);	
	}
	
	public XYMultipleSeriesDataset getmDataset() {
		return mDataset;
	}

	public void addPoint(int i, double x, double y, double z, double g){
		this.datasetX.add(i, x);
		this.datasetY.add(i, y);
		this.datasetZ.add(i, z);
		this.datasetG.add(i, g);
	}
	
	public void removeOldPoints(){
		datasetX.remove(0);
		datasetY.remove(0);
		datasetZ.remove(0);
		datasetG.remove(0);
	}
	
	public void removeAllPoints(){
		datasetX.clear();
		datasetY.clear();
		datasetZ.clear();
		datasetG.clear();
	}

	public TimeSeries getDatasetX() {
		return datasetX;
	}

	public TimeSeries getDatasetY() {
		return datasetY;
	}

	public TimeSeries getDatasetZ() {
		return datasetZ;
	}

	public TimeSeries getDatasetG() {
		return datasetG;
	}
}
