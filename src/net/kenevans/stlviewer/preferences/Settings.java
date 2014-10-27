package net.kenevans.stlviewer.preferences;

import java.io.File;
import java.util.prefs.Preferences;

import net.kenevans.core.utils.Utils;
import net.kenevans.stlviewer.model.IConstants;
import net.kenevans.stlviewer.ui.STLViewer;

/*
 * Created on Aug 7, 2014
 * By Kenneth Evans, Jr.
 */

/**
 * Settings stores the settings for the STLViewer.
 * 
 * @author Kenneth Evans, Jr.
 */
public class Settings implements IConstants
{
    private String defaultDirectory = D_DEFAULT_DIR;

    private boolean hrVisible = D_HR_VISIBILITY;
    private boolean hrZonesVisible = D_HR_ZONES_VISIBILITY;
    private boolean speedVisible = D_SPEED_VISIBILITY;
    private boolean eleVisible = D_ELE_VISIBILITY;

    private int hrRollingAvgCount = D_HR_ROLLING_AVG_COUNT;
    private int speedRollingAvgCount = D_SPEED_ROLLING_AVG_COUNT;
    private int eleRollingAvgCount = D_ELE_ROLLING_AVG_COUNT;

    /**
     * Loads the settings from the preferences
     */
    public void loadFromPreferences() {
        Preferences prefs = STLViewer.getUserPreferences();
        defaultDirectory = prefs.get(P_DEFAULT_DIR, D_DEFAULT_DIR);

        hrVisible = prefs.getBoolean(P_HR_VISIBILITY, D_HR_VISIBILITY);
        hrZonesVisible = prefs.getBoolean(P_HR_ZONES_VISIBILITY,
            D_HR_ZONES_VISIBILITY);
        speedVisible = prefs.getBoolean(P_SPEED_VISIBILITY, D_SPEED_VISIBILITY);
        eleVisible = prefs.getBoolean(P_ELE_VISIBILITY, D_ELE_VISIBILITY);

        hrRollingAvgCount = prefs.getInt(P_HR_ROLLING_AVG_COUNT,
            D_HR_ROLLING_AVG_COUNT);
        speedRollingAvgCount = prefs.getInt(P_SPEED_ROLLING_AVG_COUNT,
            D_SPEED_ROLLING_AVG_COUNT);
        eleRollingAvgCount = prefs.getInt(P_ELE_ROLLING_AVG_COUNT,
            D_ELE_ROLLING_AVG_COUNT);
    }

    /**
     * Save the current values to the preferences.
     * 
     * @param showErrors Use Utils.errMsg() to show the errors.
     * @return
     */
    public boolean saveToPreferences(boolean showErrors) {
        boolean retVal = checkValues(showErrors);
        if(!retVal) {
            return retVal;
        }
        try {
            Preferences prefs = STLViewer.getUserPreferences();

            prefs.put(P_DEFAULT_DIR, defaultDirectory);

            prefs.putBoolean(P_HR_VISIBILITY, hrVisible);
            prefs.putBoolean(P_HR_ZONES_VISIBILITY, hrZonesVisible);
            prefs.putBoolean(P_SPEED_VISIBILITY, speedVisible);
            prefs.putBoolean(P_ELE_VISIBILITY, eleVisible);

            prefs.putInt(P_HR_ROLLING_AVG_COUNT, hrRollingAvgCount);
            prefs.putInt(P_SPEED_ROLLING_AVG_COUNT, speedRollingAvgCount);
            prefs.putInt(P_ELE_ROLLING_AVG_COUNT, eleRollingAvgCount);
        } catch(Exception ex) {
            retVal = false;
            if(showErrors) {
                Utils.excMsg("Error saving preferences", ex);
            }
        }
        return retVal;
    }

    /**
     * Returns if the parameters are valid
     * 
     * @param showErrors Use Utils.errMsg() to show the errors.
     * @return
     */
    public boolean checkValues(boolean showErrors) {
        boolean retVal = true;

        // Default directory
        if(defaultDirectory == null) {
            if(showErrors) {
                Utils.errMsg("Value for the default directory is null");
            }
            retVal = false;
        } else {
            File file = new File(defaultDirectory);
            if(file == null) {
                if(showErrors) {
                    Utils.errMsg("The default directory is invalid");
                }
                retVal = false;
            } else {
                if(!file.exists()) {
                    if(showErrors) {
                        Utils.errMsg("The default directory does not exist");
                    }
                    retVal = false;
                } else {
                    if(!file.isDirectory()) {
                        if(showErrors) {
                            Utils
                                .errMsg("The default directory is not a directory");
                        }
                        retVal = false;

                    }
                }
            }
        }

        return retVal;
    }

    /**
     * Copies the values in the given settings to this settings.
     * 
     * @param settings
     */
    public void copyFrom(Settings settings) {
        this.defaultDirectory = settings.defaultDirectory;

        this.hrVisible = settings.hrVisible;
        this.hrZonesVisible = settings.hrZonesVisible;
        this.speedVisible = settings.speedVisible;
        this.eleVisible = settings.eleVisible;

        this.hrRollingAvgCount = settings.hrRollingAvgCount;
        this.speedRollingAvgCount = settings.speedRollingAvgCount;
        this.eleRollingAvgCount = settings.eleRollingAvgCount;
    }

    /**
     * @return The value of defaultDirectory.
     */
    public String getDefaultDirectory() {
        return defaultDirectory;
    }

    /**
     * @param defaultDirectory The new value for defaultDirectory.
     */
    public void setDefaultDirectory(String defaultDirectory) {
        this.defaultDirectory = defaultDirectory;
    }

    /**
     * @return The value of hrVisible.
     */
    public boolean getHrVisible() {
        return hrVisible;
    }

    /**
     * @param hrVisible The new value for hrVisible.
     */
    public void setHrVisible(boolean hrVisible) {
        this.hrVisible = hrVisible;
    }

    /**
     * @return The value of hrZonesVisible.
     */
    public boolean getHrZonesVisible() {
        return hrZonesVisible;
    }

    /**
     * @param hrZonesVisible The new value for hrZonesVisible.
     */
    public void setHrZonesVisible(boolean hrZonesVisible) {
        this.hrZonesVisible = hrZonesVisible;
    }

    /**
     * @return The value of speedVisible.
     */
    public boolean getSpeedVisible() {
        return speedVisible;
    }

    /**
     * @param speedVisible The new value for speedVisible.
     */
    public void setSpeedVisible(boolean speedVisible) {
        this.speedVisible = speedVisible;
    }

    /**
     * @return The value of eleVisible.
     */
    public boolean getEleVisible() {
        return eleVisible;
    }

    /**
     * @param eleVisible The new value for eleVisible.
     */
    public void setEleVisible(boolean eleVisible) {
        this.eleVisible = eleVisible;
    }

    /**
     * @return The value of hrRollingAvgCount.
     */
    public int getHrRollingAvgCount() {
        return hrRollingAvgCount;
    }

    /**
     * @param hrRollingAvgCount The new value for hrRollingAvgCount.
     */
    public void setHrRollingAvgCount(int hrRollingAvgCount) {
        this.hrRollingAvgCount = hrRollingAvgCount;
    }

    /**
     * @return The value of speedRollingAvgCount.
     */
    public int getSpeedRollingAvgCount() {
        return speedRollingAvgCount;
    }

    /**
     * @param speedRollingAvgCount The new value for speedRollingAvgCount.
     */
    public void setSpeedRollingAvgCount(int speedRollingAvgCount) {
        this.speedRollingAvgCount = speedRollingAvgCount;
    }

    /**
     * @return The value of eleRollingAvgCount.
     */
    public int getEleRollingAvgCount() {
        return eleRollingAvgCount;
    }

    /**
     * @param eleRollingAvgCount The new value for eleRollingAvgCount.
     */
    public void setEleRollingAvgCount(int eleRollingAvgCount) {
        this.eleRollingAvgCount = eleRollingAvgCount;
    }

}
