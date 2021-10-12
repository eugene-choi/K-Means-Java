import java.awt.Color;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.JFrame;  
import org.jfree.chart.ChartFactory;  
import org.jfree.chart.ChartPanel;  
import org.jfree.chart.JFreeChart;  
import org.jfree.chart.plot.XYPlot;  
import org.jfree.data.xy.XYDataset;  
import org.jfree.data.xy.XYSeries;  
import org.jfree.data.xy.XYSeriesCollection;  
import org.jfree.chart.ChartUtils;

public class Visualisation extends JFrame{
	
	public Visualisation(String fileName, String title,String[] clusterLabels, int[][] clusterGroups, double[][] points) {  
		super(title);
		System.out.println("- Creating " + title + ":");
		XYDataset dataset = createDataset(clusterLabels, clusterGroups, points);
		JFreeChart chart = ChartFactory.createScatterPlot("Visualisation of " + title, "X-Axis (Principal Component 1)", "Y-Axis (Principal Component 2)", dataset);  
		XYPlot plot = (XYPlot)chart.getPlot();  
		plot.setBackgroundPaint(Color.WHITE);
		ChartPanel panel = new ChartPanel(chart);  
		panel.setSize(700, 500);
		try {
			ChartUtils.writeChartAsPNG(new FileOutputStream(fileName), chart, panel.getWidth(), panel.getHeight());
		} catch (Exception ex) {
			System.err.format("Exception occured: %s%n", ex);
		}
	}  
  
	private XYDataset createDataset(String[] clusterLabels, int[][] clusterGroups, double[][] points) {  
		XYSeriesCollection dataset = new XYSeriesCollection();
		for(int i = 0; i < clusterGroups.length; i++) {
			XYSeries series = new XYSeries(clusterLabels[i]);
			for(int j = 0; j < clusterGroups[0].length; j++) if(clusterGroups[i][j] != 0) series.add(points[j][0], points[j][1]);
			dataset.addSeries(series);
		}
		return dataset;  
	}  
}