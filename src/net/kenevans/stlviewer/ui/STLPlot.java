package net.kenevans.stlviewer.ui;

import java.awt.Font;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import net.kenevans.core.utils.Utils;
import net.kenevans.stlviewer.model.IConstants;
import net.kenevans.stlviewer.model.STLFileModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/*
 * Created on Jul 29, 2012
 * By Kenneth Evans, Jr.
 */

/**
 * STLPlot handles plotting for the STLViewer.
 * 
 * @author Kenneth Evans, Jr.
 */
public class STLPlot implements IConstants
{
    // /** Default value for the range maximum. */
    // private static final double YMAX = 160;
    // /** Value for the domain maximum. */
    // private static final double XMAX = 30;

    /** The TimeSeriesCollection used in the plot. It is filled out as needed. */
    private TimeSeriesCollection dataset = new TimeSeriesCollection();

    // /** Used to retain the domain limits for resetting the plot. */
    // private double defaultXMax;
    // /** Used to retain the range limits for resetting the plot. */
    // private double defaultYMax;

    /** Whether to show markers in the plot. */
    private boolean showMarkers = false;

    /** The ChartPanel for the chart. */
    private ChartPanel chartPanel;

    /** The STLViewer that contains this plot. */
    private STLViewer viewer;

    /** The subtitle */
    TextTitle subTitle;

    public STLPlot(STLViewer viewer) {
        this.viewer = viewer;
    }

    /**
     * Creates the JFreeChart and ChartPanel. Sets the XYDataSet in it but does
     * nothing with it otherwise.
     * 
     * @return The chart created.
     */
    public JFreeChart createChart() {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(PLOT_TITLE,
            "Time", "HR", null, true, true, false);
        subTitle = new TextTitle("No file loaded");
        subTitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        chart.addSubtitle(subTitle);
        // chart.getPlot().setBackgroundPaint(Color.BLACK);

        // chart.getPlot().setDrawingSupplier(
        // new DefaultDrawingSupplier(zoneColors,
        // DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
        // DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
        // DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
        // DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));

        // Define the before extending the popup menu
        chartPanel = new ChartPanel(chart);

        // Add to the popup menu
        extendPopupMenu();

        return chart;
    }

    private TimeSeriesCollection createDataset() {
        if(viewer.getModel() == null) {
            return null;
        }
        clearPlot();

        double[] hrVals = viewer.getModel().getHrVals();
        long[] timeVals = viewer.getModel().getTimeVals();
        plot("HR", hrColor, timeVals, hrVals);

        // Do the zones
        int nPoints = timeVals.length;
        if(nPoints > 2 && timeVals[0] != timeVals[nPoints - 1]) {
            int nZones = hrZones.length;
            long[] zoneTimeVals = {timeVals[0], timeVals[nPoints - 1]};
            // Only need an array of one since the value is constant
            double[] zoneVals = new double[1];
            for(int i = 0; i < nZones; i++) {
                zoneVals[0] = hrZones[i];
                plot(String.format(BOUNDARY_SERIES_NAME_PREFIX + "%.0f",
                    hrZones[i]), zoneColors[i], zoneTimeVals, zoneVals);
            }
        }

        setSeriesMarkers(dataset, showMarkers);
        return dataset;
    }

    /**
     * General routine to add one or more series to a plot.
     * 
     * @param seriesName The name of the series.
     * @param paint A Paint representing the color of the series.
     * @param timeVals The array of time values.
     * @param yVals The array of y values. If the length of this array is 1, it
     *            is used as a constant value for all x values. Otherwise it
     *            should be nSeries times as long as the length of the x values.
     *            If it is shorter, then null will be used for the remaining
     *            plot values.
     */
    private void plot(String seriesName, Paint paint, long[] timeVals,
        double[] yVals) {
        int nPoints = timeVals.length;
        int nDataPoints = yVals.length;
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)chartPanel
            .getChart().getXYPlot().getRenderer();
        TimeSeries series = new TimeSeries(seriesName);

        for(int n = 0; n < nPoints; n++) {
            if(nDataPoints == 1) {
                series.addOrUpdate(new Minute(new Date(timeVals[n])), yVals[0]);
            } else {
                series.addOrUpdate(new Minute(new Date(timeVals[n])), yVals[n]);
            }
        }
        dataset.addSeries(series);
        int seriesIndex = dataset.indexOf(series);
        renderer.setSeriesPaint(seriesIndex, paint);
    }

    /**
     * Adds to the plot pop-up menu.
     */
    private void extendPopupMenu() {
        JPopupMenu menu = chartPanel.getPopupMenu();
        if(menu == null) return;

        JSeparator separator = new JSeparator();
        menu.add(separator);

        JMenuItem item;

        // item = new JMenuItem();
        // item.setText("Reset");
        // item.addActionListener(new ActionListener() {
        // public void actionPerformed(ActionEvent ae) {
        // JFreeChart chart = chartPanel.getChart();
        // chart.getXYPlot().getRangeAxis()
        // .setRange(-.5 * defaultYMax, .5 * defaultYMax);
        // chart.getXYPlot().getDomainAxis().setRange(0, defaultXMax);
        // if(showMarkers) {
        // showMarkers = false;
        // // Get the renderer
        // XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)chartPanel
        // .getChart().getXYPlot().getRenderer();
        // // Change for the first 3 series
        // for(int i = 0; i < 3; i++) {
        // renderer.setSeriesShapesVisible(i, showMarkers);
        // }
        // }
        // }
        // });
        // menu.add(item);

        // item = new JMenuItem();
        // item.setText("Reset Axes");
        // item.addActionListener(new ActionListener() {
        // public void actionPerformed(ActionEvent ae) {
        // JFreeChart chart = chartPanel.getChart();
        // chart.getXYPlot().getRangeAxis()
        // .setRange(-.5 * defaultYMax, .5 * defaultYMax);
        // chart.getXYPlot().getDomainAxis().setRange(0, defaultXMax);
        // }
        // });
        // menu.add(item);

        item = new JMenuItem();
        item.setText("Toggle Markers");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                toggleMarkers();
            }
        });
        menu.add(item);
    }

    /**
     * Removes all series from the plot.
     */
    public void clearPlot() {
        try {
            dataset.removeAllSeries();
        } catch(Exception ex) {
            Utils.excMsg("Error clearing plot", ex);
        }
    }

    /**
     * Toggles the markers on series that are not HR boundaries.
     */
    public void toggleMarkers() {
        showMarkers = !showMarkers;
        setSeriesMarkers(dataset, showMarkers);
        ;
    }

    /**
     * Sets the markers for all series depending on showMarkers.
     */
    public void setSeriesMarkers(TimeSeriesCollection dataset,
        boolean showMarkers) {
        int nSeries = dataset.getSeriesCount();
        if(nSeries == 0) {
            return;
        }
        // Get the renderer
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)chartPanel
            .getChart().getXYPlot().getRenderer();
        // Change for the series that are not boundaries
        String seriesName;
        for(int i = 0; i < nSeries; i++) {
            seriesName = (String)dataset.getSeries(i).getKey();
            if(!seriesName.startsWith(BOUNDARY_SERIES_NAME_PREFIX)) {
                renderer.setSeriesShapesVisible(i, showMarkers);
            }
        }
    }

    /**
     * Fills in the chart with the data from the given strip.
     * 
     * @param strip
     */
    // TODO
    public void addModelToChart(STLFileModel model) {
        try {
            createDataset();
            JFreeChart chart = chartPanel.getChart();
            chart.getXYPlot().setDataset(dataset);
            chart.removeSubtitle(subTitle);
            subTitle.setText(model.getFileName());
            chart.addSubtitle(subTitle);

            // Set the axis limits in the plot
            // JFreeChart chart = chartPanel.getChart();
            // chart.getXYPlot().getRangeAxis().setRange(0, totalHeight);
            // chart.getXYPlot().getRangeAxis()
            // .setRange(-.5 * totalHeight, .5 * totalHeight);
            // chart.getXYPlot().getDomainAxis().setRange(0, xMax);

            // Plot the data
            // plot("Segment", stripColor, nSubPlots, totalHeight, .5, xVals,
            // data);
        } catch(Exception ex) {
            Utils.excMsg("Error adding profile to plot", ex);
            ex.printStackTrace();
        }
    }

    /**
     * @return The value of chartPanel.
     */
    public ChartPanel getChartPanel() {
        return chartPanel;
    }

}
