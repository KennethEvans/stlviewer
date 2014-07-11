/* ---------------------
 * STLViewer.java
 * ---------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited.
 *
 */

package net.kenevans.stlviewer.ui;

import java.awt.Font;
import java.util.Date;

import javax.swing.JPanel;

import net.kenevans.stlviewer.model.IConstants;
import net.kenevans.stlviewer.model.STLFileModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A demo showing a time series with per minute data.
 */
public class STLViewer extends ApplicationFrame implements IConstants
{
    private static final long serialVersionUID = 1L;

    private STLFileModel model;

    /**
     * STLViewer constructor. Viewer for Stl data.
     * 
     * @param title
     */
    public STLViewer(String title) {
        super(title);

        model = new STLFileModel(FILE_PATH);

        XYDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(PLOT_WIDTH_1,
            PLOT_HEIGHT_1));
        setContentPane(chartPanel);
    }

    private JFreeChart createChart(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(PLOT_TITLE_1,
            "Time", "HR", dataset, true, true, false);
        TextTitle subTitle = new TextTitle(model.getFileName());
        subTitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        chart.addSubtitle(subTitle);
        // chart.getPlot().setBackgroundPaint(Color.BLACK);
        chart.getPlot().setDrawingSupplier(
            new DefaultDrawingSupplier(zoneColors,
                DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
        return chart;
    }

    private XYDataset createDataset() {
        TimeSeries series = new TimeSeries("HR");

        double[] hrVals = model.getHrVals();
        long[] timeVals = model.getTimeVals();
        int nPoints = hrVals.length;
        for(int i = 0; i < nPoints; i++) {
            // Use addOrUpdate to avoid duplicates
            series.addOrUpdate(new Minute(new Date(timeVals[i])), hrVals[i]);
            // System.out.println(hrVals[i] + " " + timeVals[i]);
        }
        TimeSeriesCollection dataset = new TimeSeriesCollection(series);

        // Do the zones
        if(nPoints > 2 && timeVals[0] != timeVals[nPoints - 1]) {
            int nZones = hrZones.length;
            for(int i = 0; i < nZones; i++) {
                series = new TimeSeries(String.format("%.0f", hrZones[i]));
                series.add(new Minute(new Date(timeVals[0])), hrZones[i]);
                series.add(new Minute(new Date(timeVals[nPoints - 1])),
                    hrZones[i]);
                dataset.addSeries(series);
            }
        }

        return dataset;
    }

    /**
     * Creates a panel for the demo (used by SuperDemo.java).
     * 
     * @return A panel.
     */
    public JPanel createDemoPanel() {
        JFreeChart chart = createChart(createDataset());
        return new ChartPanel(chart);
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args ignored.
     */
    public static void main(String[] args) {
        STLViewer demo = new STLViewer(TITLE_1);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
