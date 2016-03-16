package com.example.g_forcesensor;

import org.achartengine.model.XYMultipleSeriesDataset;

public class LineGraphDynamic extends LineGraph {

	private int chartLength;
	private double lmax = 0.0D;
	private double lmin = 0.0D;

	public LineGraphDynamic(XYMultipleSeriesDataset mDataset, int chartLength, String str) {
		super(str);
		this.mDataset = mDataset;
		this.mRenderer.setClickEnabled(true);
		this.chartLength = chartLength;
	}

	public void scrollGraphFroward(int i) {
		this.lmin = 0.0D;
		this.lmax = 0.0D;

		if (this.lmax < this.mDataset.getSeriesAt(0).getMaxY()) this.lmax = this.mDataset.getSeriesAt(0).getMaxY();
		if (this.lmin > this.mDataset.getSeriesAt(0).getMinY()) this.lmin = this.mDataset.getSeriesAt(0).getMinY();
		if (this.lmax < this.mDataset.getSeriesAt(1).getMaxY()) this.lmax = this.mDataset.getSeriesAt(1).getMaxY();
		if (this.lmin > this.mDataset.getSeriesAt(1).getMinY()) this.lmin = this.mDataset.getSeriesAt(1).getMinY();
		if (this.lmax < this.mDataset.getSeriesAt(2).getMaxY()) this.lmax = this.mDataset.getSeriesAt(2).getMaxY();
		if (this.lmin > this.mDataset.getSeriesAt(2).getMinY()) this.lmin = this.mDataset.getSeriesAt(2).getMinY();
		if (this.lmax < this.mDataset.getSeriesAt(3).getMaxY()) this.lmax = this.mDataset.getSeriesAt(3).getMaxY();
		if (this.lmin > this.mDataset.getSeriesAt(3).getMinY()) this.lmin = this.mDataset.getSeriesAt(3).getMinY();

		if (this.mRenderer != null) {
			this.mRenderer.setXAxisMin(i - this.chartLength);
			this.mRenderer.setXAxisMax(i);
			this.mRenderer.setYAxisMin(this.lmin - 1.0D);
			this.mRenderer.setYAxisMax(1.0D + this.lmax);
		}
	}
}
