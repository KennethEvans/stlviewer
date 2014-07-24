package net.kenevans.stlviewer.ui;

import java.awt.Paint;

import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeriesCollection;

/*
 * Created on Jul 17, 2014
 * By Kenneth Evans, Jr.
 */

/**
 * HrDataType manages a heart rate data type. It differs from DataType by
 * implementing zones in the plot.
 * 
 * @author Kenneth Evans, Jr.
 */
public class HrDataType extends DataType
{
    /**
     * HrDataType constructor that sets axisIndex the same as datasetIndex. Just
     * calls super().
     * 
     * @param plot
     * @param name
     * @param datasetIndex
     * @param paint
     * @param visible
     * @see DataType#DataType(XYPlot, String, int, Paint, boolean)
     */
    public HrDataType(XYPlot plot, String name, int datasetIndex, Paint paint,
        boolean visible) {
        super(plot, name, datasetIndex, paint, visible);
    }

    /**
     * HrDataType constructor. Just calls super().
     * 
     * @param plot
     * @param name
     * @param datasetIndex
     * @param axisIndex
     * @param paint
     * @param visible
     * @see DataType#DataType(XYPlot, String, int, int, Paint, boolean)
     */
    public HrDataType(XYPlot plot, String name, int datasetIndex,
        int axisIndex, Paint paint, boolean visible) {
        super(plot, name, datasetIndex, axisIndex, paint, visible);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.kenevans.stlviewer.ui.DataType#createDataset(long[], double[])
     */
    @Override
    public TimeSeriesCollection createDataset(long[] timeVals, double[] yVals) {
        dataset = super.createDataset(timeVals, yVals);

        // Add the zones
        int nPoints = timeVals.length;
        if(nPoints > 2 && timeVals[0] != timeVals[nPoints - 1]) {
            int nZones = hrZones.length;
            long[] zoneTimeVals = {timeVals[0], timeVals[nPoints - 1]};
            // Only need an array of one since the value is constant
            double[] zoneVals = new double[1];
            for(int i = 0; i < nZones; i++) {
                zoneVals[0] = hrZones[i];
                addSeries(dataset, String.format(BOUNDARY_SERIES_NAME_PREFIX
                    + "%.0f", hrZones[i]), zoneColors[i], zoneTimeVals,
                    zoneVals);
            }
        }

        return dataset;
    }
}
