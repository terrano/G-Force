package com.example.g_forcesensor;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;

public abstract class LineGraph {
	
	protected XYMultipleSeriesDataset mDataset;
	
	protected XYSeriesRenderer rendererX;
	protected XYSeriesRenderer rendererY; 
	protected XYSeriesRenderer rendererZ; 
	protected XYSeriesRenderer rendererG;
	protected XYMultipleSeriesRenderer mRenderer;
	protected XYSeriesRenderer rendererAp;
	
	public LineGraph(String str) {
		
		rendererX = new XYSeriesRenderer();
		rendererY = new XYSeriesRenderer();
		rendererZ = new XYSeriesRenderer();
		rendererG = new XYSeriesRenderer();
		mRenderer = new XYMultipleSeriesRenderer();
		
		rendererX.setColor(Color.BLUE);
		rendererX.setPointStyle(PointStyle.POINT);
		rendererX.setLineWidth(1.5f);

		rendererY.setColor(Color.GREEN);
		rendererY.setPointStyle(PointStyle.POINT);
		rendererY.setLineWidth(1.5f);

		rendererZ.setColor(Color.YELLOW);
		rendererZ.setPointStyle(PointStyle.POINT);
		rendererZ.setLineWidth(1.5f);

		rendererG.setColor(Color.MAGENTA);
		rendererG.setPointStyle(PointStyle.POINT);
		rendererG.setLineWidth(3.5f);

		mRenderer.setMargins(new int [] { 0, 20, 0, 0 });
		
		mRenderer.setShowAxes(true);
		mRenderer.setShowLegend(true);

		mRenderer.setXLabelsColor(Color.MAGENTA);
		mRenderer.setXLabelsAlign(android.graphics.Paint.Align.LEFT);
		mRenderer.setShowCustomTextGridX(true);
		
		mRenderer.setYLabelsColor(0, Color.MAGENTA); 
		mRenderer.setYLabelsAlign(android.graphics.Paint.Align.RIGHT);
		mRenderer.setYTitle(str); 
		
		mRenderer.setShowGrid(true); 
		mRenderer.setGridColor(Color.GRAY);

		mRenderer.setBackgroundColor(Color.BLACK);
		mRenderer.setApplyBackgroundColor(true);
		
		mRenderer.addSeriesRenderer(rendererX);
		mRenderer.addSeriesRenderer(rendererY);
		mRenderer.addSeriesRenderer(rendererZ);
		mRenderer.addSeriesRenderer(rendererG);
	}
	
	public GraphicalView getView(Context context){
		if ((context == null) || (mDataset == null) ||(mRenderer == null)) return null;
		return ChartFactory.getLineChartView(context, mDataset, mRenderer);
	}
}
