package net.kenevans.stlviewer.ui;

import java.awt.Paint;
import java.util.Date;

import net.kenevans.stlviewer.model.IConstants;

import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/*
 * Created on Jul 17, 2014
 * By Kenneth Evans, Jr.
 */

/**
 * DataType manages a particular type of data to be plotted, such as HR, Speed,
 * etc.
 * 
 * @author Kenneth Evans, Jr.
 */
public class DataType implements IConstants
{
    protected String name;
    protected int index;
    protected Paint paint;
    protected boolean visible;

    protected XYPlot plot;
    protected TimeSeriesCollection dataset;
    protected XYLineAndShapeRenderer renderer;

    /**
     * DataType constructor.
     * 
     * @param name
     * @param index
     * @param paint
     * @param visible
     */
    public DataType(XYPlot plot, String name, int index, Paint paint,
        boolean visible) {
        this.plot = plot;
        this.name = name;
        this.index = index;
        this.paint = paint;
        this.visible = visible;
    }

    /**
     * Creates a dataset for the given model for this data type.
     * 
     * @param model
     * @return
     */
    public TimeSeriesCollection createDataset(long[] timeVals, double[] yVals) {
        dataset = new TimeSeriesCollection();
        renderer = new XYLineAndShapeRenderer();

        addSeries(dataset, name, paint, timeVals, yVals);
        return dataset;
    }

    protected void addSeries(TimeSeriesCollection dataset, String seriesName,
        Paint paint, long[] timeVals, double[] yVals) {
        int nPoints = timeVals.length;
        int nDataPoints = yVals.length;
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
     * @return The value of name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The new value for name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The value of index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index The new value for index.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return The value of paint.
     */
    public Paint getPaint() {
        return paint;
    }

    /**
     * @param paint The new value for paint.
     */
    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    /**
     * @return The value of visible.
     */
    public boolean getVisible() {
        return visible;
    }

    /**
     * @param visible The new value for visible.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @return The value of plot.
     */
    public XYPlot getPlot() {
        return plot;
    }

    /**
     * @return The value of dataset.
     */
    public TimeSeriesCollection getDataset() {
        return dataset;
    }

    /**
     * @return The value of renderer.
     */
    public XYLineAndShapeRenderer getRenderer() {
        return renderer;
    }

}
