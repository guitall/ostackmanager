package com.intel.internship.openstackmanagement.util;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.graphics.Color;

public class ChartTools {
	
	public static GraphicalView createNewPairChart(Context ctx, double [] values) {
		GraphicalView chart;
		DefaultRenderer renderer = new DefaultRenderer();
		CategorySeries series = new CategorySeries("");
		int[] colors = new int[] { Color.BLUE, Color.GRAY};
		
		renderer.setApplyBackgroundColor(false);
//		renderer.setBackgroundColor(Color.WHITE);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setMargins(new int[] {10,10,10,0});
		renderer.setStartAngle(90);
		renderer.setZoomEnabled(false);
		renderer.setZoomButtonsVisible(false);
		renderer.setDisplayValues(false);
		renderer.setShowLabels(false);
		renderer.setPanEnabled(false);
		renderer.setShowLegend(false);
		
		for(int i = 0 ; i < values.length; i ++ ){
			series.add(values[i]);
			SimpleSeriesRenderer siRenderer = new SimpleSeriesRenderer();
			siRenderer.setColor(colors[(series.getItemCount() - 1)
			                           % colors.length]);
			renderer.addSeriesRenderer(siRenderer);
		}
		
		chart = ChartFactory.getPieChartView(ctx, series, renderer);
		return chart;
	}
}
