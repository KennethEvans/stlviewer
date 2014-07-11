package net.kenevans.stlviewer.ui;

import java.awt.Color;
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
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/*
 * Created on Jul 29, 2012
 * By Kenneth Evans, Jr.
 */

/**
 * STLPlot handles plotting for the STLViewer2.
 * 
 * @author Kenneth Evans, Jr.
 */
public class STLPlot implements IConstants
{
    /** Default value for the range maximum. */
    private static final double YMAX = 160;
    /** Value for the domain maximum. */
    private static final double XMAX = 30;

    /** The TimeSeriesCollection used in the plot. It is filled out as needed. */
    private TimeSeriesCollection dataset = new TimeSeriesCollection();

    /** Used to retain the domain limits for resetting the plot. */
    private double defaultXMax;
    /** Used to retain the range limits for resetting the plot. */
    private double defaultYMax;
    /** Whether to show markers in the . */
    private boolean showMarkers = false;
    /** Whether to show RSA data in the plot. */
    private boolean showRSA = true;
    /** Whether to get the RSA values from the default or the current data mode */
    private boolean useDefaultAsRsaSource = true;

    /** The number of sub-plots to use. */
    private int nSubPlots = 1;

    /**
     * Determines the default value for determining the total range. Choosing
     * larger values makes the data look larger.
     */
    private static final double DATA_SCALE_DEFAULT = 1;
    /**
     * Determines the default scale factor for converting RSA values in seconds
     * to mm on the plot. The units are mm/sec.
     */
    private static final double RSA_SCALE_DEFAULT = 20;

    /** The dataScale to use */
    private double dataScale = DATA_SCALE_DEFAULT;
    /** The rsaScale to use */
    private double rsaScale = RSA_SCALE_DEFAULT;

    /** The color for the ECG strip. */
    private Paint stripColor = Color.RED;
    /** The color for the RSA curve. */
    private Paint rsaColor = new Color(0, 153, 255);
    /** The color for the RSA base line. */
    private Paint rsaBaseLineColor = new Color(0, 0, 255);

    /** The ChartPanel for the chart. */
    private ChartPanel chartPanel;

    /** The STLViewer2 that contains this plot. */
    private STLViewer2 viewer;
    
    /** The subtitle */
    TextTitle subTitle;

    public STLPlot(STLViewer2 viewer) {
        this.viewer = viewer;
    }

    /**
     * Creates the JFreeChart and ChartPanel. Sets the XYDataSet in it but does
     * nothing with it otherwise.
     * 
     * @return The chart created.
     */
    public JFreeChart createChart() {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(PLOT_TITLE_1, "Time",
            "HR", null, true, true, false);
        subTitle = new TextTitle("No file loaded");
        subTitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        chart.addSubtitle(subTitle);
        // chart.getPlot().setBackgroundPaint(Color.BLACK);
        chart.getPlot().setDrawingSupplier(
            new DefaultDrawingSupplier(zoneColors,
                DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));

        // Define the  before extending the popup menu
        chartPanel = new ChartPanel(chart);

        // Add to the popup menu
        extendPopupMenu();

        return chart;
    }

    private TimeSeriesCollection createDataset() {
        if(viewer.getModel() == null) {
            return null;
        }
        TimeSeries series = new TimeSeries("HR");

        double[] hrVals = viewer.getModel().getHrVals();
        long[] timeVals = viewer.getModel().getTimeVals();
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
     * Adds to the plot pop-up menu.
     */
    private void extendPopupMenu() {
        JPopupMenu menu = chartPanel.getPopupMenu();
        if(menu == null) return;

        JSeparator separator = new JSeparator();
        menu.add(separator);

        JMenuItem item = new JMenuItem();
        item.setText("Reset");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // JFreeChart chart = chartPanel.getChart();
                // chart.getXYPlot().getRangeAxis()
                // .setRange(-.5 * defaultYMax, .5 * defaultYMax);
                // chart.getXYPlot().getDomainAxis().setRange(0, defaultXMax);
                // if(showMarkers) {
                // showMarkers = false;
                // // Get the renderer
                // XYLineAndShapeRenderer renderer =
                // (XYLineAndShapeRenderer)chartPanel
                // .getChart().getXYPlot().getRenderer();
                // // Change for the first 3 series
                // for(int i = 0; i < 3; i++) {
                // renderer.setSeriesShapesVisible(i, showMarkers);
                // }
                // }
            }
        });
        menu.add(item);

        item = new JMenuItem();
        item.setText("Reset Axes");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // JFreeChart chart = chartPanel.getChart();
                // chart.getXYPlot().getRangeAxis()
                // .setRange(-.5 * defaultYMax, .5 * defaultYMax);
                // chart.getXYPlot().getDomainAxis().setRange(0, defaultXMax);
            }
        });
        menu.add(item);

        item = new JMenuItem();
        item.setText("Toggle Markers");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                showMarkers = !showMarkers;
                // // Get the renderer
                // XYLineAndShapeRenderer renderer =
                // (XYLineAndShapeRenderer)chartPanel
                // .getChart().getXYPlot().getRenderer();
                // // Change for the first 3 series
                // for(int i = 0; i < 3; i++) {
                // renderer.setSeriesShapesVisible(i, showMarkers);
                // }
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
     * Fills in the chart with the data from the given strip.
     * 
     * @param strip
     */
    // TODO
    public void addModelToChart(STLFileModel model) {
        try {
            dataset = createDataset();
            if(dataset == null) {
                dataset = new TimeSeriesCollection();
            }
            JFreeChart chart = chartPanel.getChart();
            chart.getXYPlot().setDataset(dataset);
            chart.removeSubtitle(subTitle);
            subTitle.setText(model.getFileName());
            chart.addSubtitle(subTitle);

            // Set the axis limits in the plot
            // JFreeChart chart = chartPanel.getChart();
            //            chart.getXYPlot().getRangeAxis().setRange(0, totalHeight);
//            chart.getXYPlot().getRangeAxis()
//                .setRange(-.5 * totalHeight, .5 * totalHeight);
//            chart.getXYPlot().getDomainAxis().setRange(0, xMax);

            // Plot the data
//            plot("Segment", stripColor, nSubPlots, totalHeight, .5, xVals, data);
        } catch(Exception ex) {
            Utils.excMsg("Error adding profile to plot", ex);
            ex.printStackTrace();
        }
    }

    /**
     * General routine to add one or more series to a plot.
     * 
     * @param seriesName The series name will be this value with the sub-plot
     *            number appended.
     * @param paint A Paint representing the color of the series.
     * @param nSubPlots The number of sub-plots to use.
     * @param totalHeight The total height of the chart. Each sub-plot height
     *            will be this value divided by nSubPlots.
     * @param originFraction What fraction of the sub-plot area to use as the
     *            origin. Measured from the bottom. .5 is the middle, and .2 is
     *            below the middle. The useful range is 0 to 1.
     * @param xVals The array of x values.
     * @param yVals The array of y values. If the length of this array is 1, it
     *            is used as a constant value for all x values. Otherwise it
     *            should be nSeries times as long as the length of the x values.
     *            If it is shorter, then null will be used for the remaining
     *            plot values.
     */
    private void plot(String seriesName, Paint paint, int nSubPlots,
        double totalHeight, double originFraction, double[] xVals,
        double[] yVals) {
        // int index;
        // int nPoints = xVals.length;
        // int nDataPoints = yVals.length;
        // XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)chartPanel
        // .getChart().getXYPlot().getRenderer();
        // double offset;
        // for(int i = 0; i < nSubPlots; i++) {
        // XYSeries series = new XYSeries(seriesName + " " + (i + 1));
        // offset = .5 * totalHeight
        // - ((i + 1 - originFraction) * totalHeight) / nSubPlots;
        // for(int n = 0; n < nPoints; n++) {
        // if(nDataPoints == 1) {
        // series.add(xVals[n], yVals[0] + offset);
        // } else {
        // index = i * nPoints + n;
        // // In case yVals does not fill the segment
        // if(index > nDataPoints - 1) {
        // series.add(xVals[n], null);
        // } else {
        // series.add(xVals[n], yVals[index] + offset);
        // }
        // }
        // }
        // dataset.addSeries(series);
        // renderer.setSeriesPaint(dataset.indexOf(series), paint);
        // }
    }

    /**
     * @return The value of chartPanel.
     */
    public ChartPanel getChartPanel() {
        return chartPanel;
    }

}
