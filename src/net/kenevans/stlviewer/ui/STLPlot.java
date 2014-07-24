package net.kenevans.stlviewer.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import net.kenevans.core.utils.Utils;
import net.kenevans.stlviewer.model.IConstants;
import net.kenevans.stlviewer.model.STLFileModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
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
    private TextTitle subTitle;

    /** The array of types */
    private DataType[] dataTypes;

    public STLPlot(STLViewer viewer) {
        // this.viewer = viewer;
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

        // Define the panel before extending the popup menu
        chartPanel = new ChartPanel(chart);

        // Add to the popup menu
        extendPopupMenu();

        XYPlot plot = chart.getXYPlot();
        dataTypes = new DataType[] {
            // Don't use Boolean.getBoolean. It gets a system value with that
            // name
            new HrDataType(plot, D_HR_NAME, HR_INDEX, Color.decode(D_HR_COLOR),
                Boolean.parseBoolean(D_HR_VISIBILITY)),
            new DataType(plot, D_SPEED_NAME, SPEED_INDEX,
                Color.decode(D_SPEED_COLOR),
                Boolean.parseBoolean(D_SPEED_VISIBILITY)),
            new DataType(plot, D_ELE_NAME, ELE_INDEX,
                Color.decode(D_ELE_COLOR),
                Boolean.parseBoolean(D_ELE_VISIBILITY)),
        // Comment to keep brace on a separate line
        };

        return chart;
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

        separator = new JSeparator();
        menu.add(separator);

        final JCheckBoxMenuItem hrVisibleItem = new JCheckBoxMenuItem("HR",
            Boolean.parseBoolean(D_HR_VISIBILITY));
        hrVisibleItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                boolean selected = ((JCheckBoxMenuItem)ae.getSource())
                    .isSelected();
                int index = HR_INDEX;
                DataType type = dataTypes[index];
                type.setVisible(selected);
                XYPlot plot = type.getPlot();
                plot.getRangeAxis(index).setVisible(selected);
                plot.setDataset(index, selected ? type.getDataset() : null);
            }
        });
        menu.add(hrVisibleItem);

        final JCheckBoxMenuItem speedVisibleItem = new JCheckBoxMenuItem(
            "Speed", Boolean.parseBoolean(D_SPEED_VISIBILITY));
        speedVisibleItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                boolean selected = ((JCheckBoxMenuItem)ae.getSource())
                    .isSelected();
                int index = SPEED_INDEX;
                DataType type = dataTypes[index];
                type.setVisible(selected);
                XYPlot plot = type.getPlot();
                plot.getRangeAxis(index).setVisible(selected);
                plot.setDataset(index, selected ? type.getDataset() : null);
            }
        });
        menu.add(speedVisibleItem);

        final JCheckBoxMenuItem eleVisibleItem = new JCheckBoxMenuItem(
            "Elevation", Boolean.parseBoolean(D_ELE_VISIBILITY));
        eleVisibleItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                boolean selected = ((JCheckBoxMenuItem)ae.getSource())
                    .isSelected();
                int index = ELE_INDEX;
                DataType type = dataTypes[index];
                type.setVisible(selected);
                XYPlot plot = type.getPlot();
                plot.getRangeAxis(index).setVisible(selected);
                plot.setDataset(index, selected ? type.getDataset() : null);
            }
        });
        menu.add(eleVisibleItem);
    }

    // public String info() {
    // String info = "Plot info" + LS;
    // XYPlot plot = chartPanel.getChart().getXYPlot();
    // int nDatasets = plot.getDatasetCount();
    // info += "DatasetCount=" + plot.getDatasetCount() + LS;
    // TimeSeriesCollection dataset;
    // Series series;
    // int nSeries = 0;
    // for(int i = 0; i < nDatasets; i++) {
    // dataset = (TimeSeriesCollection)plot.getDataset(i);
    // nSeries = dataset.getSeriesCount();
    // for(int j = 0; j < nSeries; j++) {
    // series = dataset.getSeries(j);
    // }
    // }
    //
    // return info;
    // }

    /**
     * Removes all series from the plot.
     */
    public void clearPlot() {
        if(dataTypes == null) {
            return;
        }
        TimeSeriesCollection dataset;
        for(DataType type : dataTypes) {
            dataset = type.getDataset();
            if(dataset != null) {
                try {
                    ((TimeSeriesCollection)dataset).removeAllSeries();
                } catch(Exception ex) {
                    Utils.excMsg("Error clearing plot", ex);
                }
            }
        }
    }

    /**
     * Toggles the markers.
     */
    public void toggleMarkers() {
        showMarkers = !showMarkers;
        setAllMarkers();
    }

    /**
     * Sets all the markers to the current marker visibility.
     */
    public void setAllMarkers() {
        if(dataTypes == null) {
            return;
        }
        TimeSeriesCollection dataset;
        XYLineAndShapeRenderer renderer;
        for(DataType type : dataTypes) {
            dataset = type.getDataset();
            renderer = type.getRenderer();
            if(dataset != null) {
                try {
                    setSeriesMarkers((TimeSeriesCollection)dataset, renderer,
                        showMarkers);
                } catch(Exception ex) {
                    Utils.excMsg("Error clearing plot", ex);
                }
            }
        }
    }

    /**
     * Sets the markers for all series that are not HR boundaries.
     * 
     * @param dataset
     * @param showMarkers
     */
    public void setSeriesMarkers(TimeSeriesCollection dataset,
        XYLineAndShapeRenderer renderer, boolean showMarkers) {
        int nSeries = dataset.getSeriesCount();
        if(nSeries == 0) {
            return;
        }
        // Change for the series that are not boundaries
        // String seriesName;
        for(int i = 0; i < nSeries; i++) {
            // seriesName = (String)dataset.getSeries(i).getKey();
            // if(!seriesName.startsWith(BOUNDARY_SERIES_NAME_PREFIX)) {
            renderer.setSeriesShapesVisible(i, showMarkers);
            // }
        }
    }

    /**
     * Fills in the chart with the data from the given model.
     * 
     * @param model
     */
    public void addModelToChart(STLFileModel model) {
        if(dataTypes == null) {
            Utils.errMsg("No data types defined");
            return;
        }
        try {
            JFreeChart chart = chartPanel.getChart();
            chart.removeSubtitle(subTitle);
            subTitle.setText(model.getFileName());
            chart.addSubtitle(subTitle);

            int index;
            long[] timeVals;
            double[] yVals;
            XYPlot plot;
            for(DataType type : dataTypes) {
                if(!type.getVisible()) {
                    continue;
                }
                index = type.getIndex();
                // Axis
                NumberAxis axis = new NumberAxis(type.getName());
                axis.setFixedDimension(10.0);
                axis.setAutoRangeIncludesZero(false);
                axis.setAutoRange(true);
                axis.setLabelPaint(type.getPaint());
                axis.setTickLabelPaint(type.getPaint());
                // Make the label font be the same as for the primary axis
                // axis.setLabelFont(font);
                plot = type.getPlot();
                plot.setRangeAxis(index, axis);
                // type.getPlot().setRangeAxisLocation(index,
                // AxisLocation.BOTTOM_OR_LEFT);
                switch(index) {
                case HR_INDEX:
                    timeVals = model.getHrTimeVals();
                    yVals = model.getHrVals();
                    type.createDataset(timeVals, yVals);
                    plot.setDataset(index, type.getDataset());
                    plot.mapDatasetToRangeAxis(index, index);
                    plot.setRenderer(index, type.getRenderer());
                    break;
                case SPEED_INDEX:
                    timeVals = model.getSpeedTimeVals();
                    yVals = model.getSpeedVals();
                    // yVals = MathUtils.medianFilter(model.getEleVals(), 10);
                    type.createDataset(timeVals, yVals);
                    plot.setDataset(index, type.getDataset());
                    plot.mapDatasetToRangeAxis(index, index);
                    plot.setRenderer(index, type.getRenderer());
                    break;
                case ELE_INDEX:
                    timeVals = model.getEleTimeVals();
                    yVals = model.getEleVals();
                    type.createDataset(timeVals, yVals);
                    plot.setDataset(index, type.getDataset());
                    plot.mapDatasetToRangeAxis(index, index);
                    plot.setRenderer(index, type.getRenderer());
                    break;
                default:
                    Utils.errMsg("Invalid data set index:" + index);
                    break;
                }
            }
            setAllMarkers();
        } catch(Exception ex) {
            Utils.excMsg("Error adding data to plot", ex);
            ex.printStackTrace();
        }
    }

    /**
     * @return The value of chartPanel.
     */
    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    /**
     * @return The value of dataTypes.
     */
    public DataType[] getDataTypes() {
        return dataTypes;
    }

}
