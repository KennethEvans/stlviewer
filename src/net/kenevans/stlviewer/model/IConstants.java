package net.kenevans.stlviewer.model;

import java.awt.Color;
import java.awt.Paint;

/*
 * Created on Jul 9, 2012
 * By Kenneth Evans, Jr.
 */

/**
 * Provides constants for classes related to STL files.
 * 
 * @author Kenneth Evans, Jr.
 */
public interface IConstants
{
    public static final String LS = System.getProperty("line.separator");

    public static final String DEFAULT_DIR = "C:/Users/evans/Documents/GPSLink/STL";
    // public static final String FILE_NAME =
    // "track2014-06-30-Workout-Rehab-1475016.gpx";
    // public static final String FILE_NAME = "../CM2013.gpx";
    public static final String FILE_NAME = "track2014-06-30-Workout-Rehab-1475018-Combined.gpx";

    /** The title for viewer 1. */
    public static final String TITLE_1 = "STL Viewer";
    /** The plot title for viewer 1. */
    public static final String PLOT_TITLE_1 = "STL Data";
    /** The frame width for viewer 1. */
    public static final int PLOT_WIDTH_1 = 1200;
    /** The frame height for viewer 2. */
    public static final int PLOT_HEIGHT_1 = 400;

    /** The title for viewer 2. */
    public static final String TITLE_2 = "STL Viewer 2";
    /** The title for viewer 2. */
    public static final String PLOT_TITLE_2 = "STL Data";
    /** The frame width for viewer 2. */
    public static final int WIDTH_2 = 1200;
    /** The frame height for viewer 2. */
    public static final int HEIGHT_2 = 600;
    /** The divider location for the main split pane. */
    public static final int MAIN_PANE_DIVIDER_LOCATION = 5 * HEIGHT_2 / 8;
    /** The divider location for the lower split pane. */
    public static final int LOWER_PANE_DIVIDER_LOCATION = WIDTH_2 / 2;

    /** Zone boundaries */
    public static final double[] hrZones = {157, 141, 125, 109, 94, 78};
    /** Series colors (data, boundaries). */
    public static Paint[] zoneColors = {Color.BLACK, Color.RED,
        Color.decode("0xFFAA00"), Color.ORANGE, Color.YELLOW, Color.GREEN,
        Color.BLUE};

    public static final String FILE_PATH = DEFAULT_DIR + "/" + FILE_NAME;

}
