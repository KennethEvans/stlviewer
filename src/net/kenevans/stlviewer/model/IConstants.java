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
/**
 * IConstants
 * 
 * @author Kenneth Evans, Jr.
 */
public interface IConstants
{
    public static final String LS = System.getProperty("line.separator");

    // public static final String FILE_NAME =
    // "track2014-06-30-Workout-Rehab-1475016.gpx";
    // public static final String FILE_NAME = "../CM2013.gpx";
    public static final String FILE_NAME = "track2014-06-30-Workout-Rehab-1475018-Combined.gpx";

    // Preferences
    /***
     * The name of the preference node for accessing preferences for this
     * application. On Windows these are found in the registry under
     * HKCU/JavaSoft/Prefs.
     */
    public static final String P_PREFERENCE_NODE = "net/kenevans/stlviewer/preferences";

    /*** The default directory name for finding GPX files */
    public static final String P_DEFAULT_DIR = "defaultDir";
    /*** The default directory default for finding GPX files */
    public static final String D_DEFAULT_DIR = "C:/Users/evans/Documents/GPSLink/STL";

    /** The title for viewer 2. */
    public static final String TITLE = "STL Viewer";
    /** The version */
    public static final String VERSION = "1.0.0.0";
    /** The title for viewer 2. */
    public static final String PLOT_TITLE = "STL Data";
    /** The frame width for viewer 2. */
    public static final int CHART_WIDTH = 1200;
    /** The frame height for viewer 2. */
    public static final int CHART_HEIGHT = 600;
    /** The divider location for the main split pane. */
    public static final int MAIN_PANE_DIVIDER_LOCATION = 5 * CHART_HEIGHT / 8;
    /** The divider location for the lower split pane. */
    public static final int LOWER_PANE_DIVIDER_LOCATION = CHART_WIDTH / 2;

    /** Zone boundaries */
    public static final double[] hrZones = {157, 141, 125, 109, 94, 78};
    /** Series colors (data, boundaries). */
    public static Paint[] zoneColors = {Color.RED, Color.decode("0xFFAA00"),
        Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE};
    /** The prefix used to represent a boundary in the series name. */
    public static final String BOUNDARY_SERIES_NAME_PREFIX = "HR=";

    /** Color for HR */
    public static Paint hrColor = Color.BLACK;

    public static final String FILE_PATH = D_DEFAULT_DIR + "/" + FILE_NAME;

}
