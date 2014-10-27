package net.kenevans.stlviewer.preferences;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.kenevans.core.utils.Utils;
import net.kenevans.stlviewer.model.IConstants;
import net.kenevans.stlviewer.ui.STLViewer;

/**
 * PreferencesDialog is a dialog to set the Preferences for STLViewer. It only
 * returns after Cancel. It can save the values to the preference store or set
 * them in the viewer. In either case it remains visible.
 * 
 * @author Kenneth Evans, Jr.
 */
public class PreferencesDialog extends JDialog implements IConstants
{
    private static final long serialVersionUID = 1L;
    private STLViewer viewer;
    /**
     * The return value. It is always true.
     */
    private boolean ok = true;

    JTextField defaultDirText;
    JCheckBox hrVisibileCheck;
    JCheckBox hrZonesVisibileCheck;
    JCheckBox speedVisibileCheck;
    JCheckBox eleVisibileCheck;
    JTextField hrRavCountText;
    JTextField speedRavCountText;
    JTextField eleRavCountText;

    /**
     * Constructor
     */
    public PreferencesDialog(Component parent, STLViewer viewer) {
        super();
        this.viewer = viewer;
        if(viewer == null) {
            Utils.errMsg("Viewer is null");
            return;
        }
        init();
        Settings settings = new Settings();
        settings.loadFromPreferences();
        setValues(settings);
        // Locate it on the screen
        this.setLocationRelativeTo(parent);
    }

    /**
     * This method initializes this dialog
     * 
     * @return void
     */
    private void init() {
        this.setTitle("Preferences");
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new GridBagLayout());

        GridBagConstraints gbcDefault = new GridBagConstraints();
        gbcDefault.insets = new Insets(2, 2, 2, 2);
        gbcDefault.anchor = GridBagConstraints.WEST;
        gbcDefault.fill = GridBagConstraints.NONE;
        GridBagConstraints gbc = null;
        int gridy = -1;

        // File Group //////////////////////////////////////////////////////
        JPanel fileGroup = new JPanel();
        fileGroup.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("File"),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        gridy++;
        fileGroup.setLayout(new GridBagLayout());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        contentPane.add(fileGroup, gbc);

        // Default directory
        JLabel label = new JLabel("Default Directory:");
        label.setToolTipText("The default directory.");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridy;
        fileGroup.add(label, gbc);

        // File JPanel holds the filename and browse button
        JPanel filePanel = new JPanel();
        filePanel.setLayout(new GridBagLayout());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        gbc.gridy = gridy;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        fileGroup.add(filePanel, gbc);

        defaultDirText = new JTextField(30);
        defaultDirText.setToolTipText(label.getText());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        filePanel.add(defaultDirText, gbc);

        JButton button = new JButton();
        button.setText("Browse");
        button.setToolTipText("Choose the file.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if(defaultDirText == null) {
                    return;
                }
                String initialDirName = defaultDirText.getText();
                String dirName = browse(initialDirName);
                defaultDirText.setText(dirName);
            }
        });
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        filePanel.add(button);

        // HR Group /////////////////////////////////////////////////////////
        JPanel hrGroup = new JPanel();
        hrGroup.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Heart Rate"),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        gridy++;
        hrGroup.setLayout(new GridBagLayout());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        contentPane.add(hrGroup, gbc);

        // Visible
        hrVisibileCheck = new JCheckBox("Visible");
        hrVisibileCheck.setToolTipText("Whether HR data is visible.");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        hrGroup.add(hrVisibileCheck, gbc);

        // Zones visible
        hrZonesVisibileCheck = new JCheckBox("Zones Visible");
        hrZonesVisibileCheck.setToolTipText("Whether HR zones are visible.");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        hrGroup.add(hrZonesVisibileCheck, gbc);

        // Running average
        String toolTip = "Number of data points to average over.  "
            + "0->Don't average.  " + "Negative->Omit raw values.";
        label = new JLabel("Running Average Count:");
        label.setToolTipText(toolTip);
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        hrGroup.add(label, gbc);

        hrRavCountText = new JTextField(5);
        hrRavCountText.setToolTipText(label.getText());
        hrRavCountText.setToolTipText(toolTip);
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 2;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        hrGroup.add(hrRavCountText, gbc);

        // Speed Group //////////////////////////////////////////////////////
        JPanel speedGroup = new JPanel();
        speedGroup.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Speed"),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        gridy++;
        speedGroup.setLayout(new GridBagLayout());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        contentPane.add(speedGroup, gbc);

        // Visible
        speedVisibileCheck = new JCheckBox("Visible");
        speedVisibileCheck.setToolTipText("Whether HR data is visible.");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        speedGroup.add(speedVisibileCheck, gbc);

        // Running average
        toolTip = "Number of data points to average over.  "
            + "0->Don't average.  " + "Negative->Omit raw values.";
        label = new JLabel("Running Average Count:");
        label.setToolTipText(toolTip);
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        speedGroup.add(label, gbc);

        speedRavCountText = new JTextField(5);
        speedRavCountText.setToolTipText(label.getText());
        speedRavCountText.setToolTipText(toolTip);
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 2;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        speedGroup.add(speedRavCountText, gbc);

        // Elevation Group //////////////////////////////////////////////////
        JPanel eleGroup = new JPanel();
        eleGroup.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Elevation"),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        gridy++;
        eleGroup.setLayout(new GridBagLayout());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        contentPane.add(eleGroup, gbc);

        // Visible
        eleVisibileCheck = new JCheckBox("Visible");
        eleVisibileCheck.setToolTipText("Whether HR data is visible.");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        eleGroup.add(eleVisibileCheck, gbc);

        // Running average
        toolTip = "Number of data points to average over.  "
            + "0->Don't average.  " + "Negative->Omit raw values.";
        label = new JLabel("Running Average Count:");
        label.setToolTipText(toolTip);
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        eleGroup.add(label, gbc);

        eleRavCountText = new JTextField(5);
        eleRavCountText.setToolTipText(label.getText());
        eleRavCountText.setToolTipText(toolTip);
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 2;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        eleGroup.add(eleRavCountText, gbc);

        // // Dummy Group
        // JPanel dummyGroup = new JPanel();
        // dummyGroup.setBorder(BorderFactory.createCompoundBorder(
        // BorderFactory.createTitledBorder("Dummy"),
        // BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        // gridy++;
        // dummyGroup.setLayout(new GridBagLayout());
        // gbc = (GridBagConstraints)gbcDefault.clone();
        // gbc.gridx = 0;
        // gbc.gridy = gridy;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        // gbc.weightx = 100;
        // contentPane.add(dummyGroup, gbc);
        //
        // // Dummy
        // label = new JLabel("Dummy:");
        // label.setToolTipText("Dummy.");
        // gbc = (GridBagConstraints)gbcDefault.clone();
        // gbc.gridx = 0;
        // dummyGroup.add(label, gbc);
        //
        // JTextField dummyText = new JTextField(30);
        // dummyText.setToolTipText(label.getText());
        // gbc = (GridBagConstraints)gbcDefault.clone();
        // gbc.gridx = 1;
        // gbc.fill = GridBagConstraints.HORIZONTAL;
        // gbc.weightx = 100;
        // dummyGroup.add(dummyText, gbc);

        // Button panel /////////////////////////////////////////////////////
        gridy++;
        JPanel buttonPanel = new JPanel();
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridy = gridy;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPane.add(buttonPanel, gbc);

        button = new JButton();
        button.setText("Use Current");
        button.setToolTipText("Set to the current viewer values.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                Settings settings = viewer.getSettings();
                if(settings == null) {
                    Utils.errMsg("Settings in the viewer do not exist");
                    return;
                }
                setValues(settings);
            }
        });
        buttonPanel.add(button);

        button = new JButton();
        button.setText("Use Defaults");
        button.setToolTipText("Set to the STLViewer default values.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                Settings settings = new Settings();
                if(settings == null) {
                    Utils.errMsg("Default settings do not exist");
                    return;
                }
                setValues(settings);
            }
        });
        buttonPanel.add(button);

        button = new JButton();
        button.setText("Use Stored");
        button.setToolTipText("Reset to the current stored preferences.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                Settings settings = new Settings();
                settings.loadFromPreferences();
                if(settings == null) {
                    Utils.errMsg("Cannot load preferences");
                    return;
                }
                setValues(settings);
            }
        });
        buttonPanel.add(button);

        button = new JButton();
        button.setText("Save");
        button.setToolTipText("Save the changes as preferences.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                save();
            }
        });
        buttonPanel.add(button);

        button = new JButton();
        button.setText("Set Current");
        button.setToolTipText("Set the current values in the viewer.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                setToViewer();
            }
        });
        buttonPanel.add(button);

        button = new JButton();
        button.setText("Cancel");
        button.setToolTipText("Close the dialog and do nothing.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                PreferencesDialog.this.setVisible(false);
            }
        });
        buttonPanel.add(button);

        pack();
    }

    /**
     * Brings up a JFileChooser to choose a directory.
     */
    private String browse(String initialDirName) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(initialDirName != null) {
            File dir = new File(initialDirName);
            chooser.setCurrentDirectory(dir);
            chooser.setSelectedFile(dir);
        }
        int result = chooser.showOpenDialog(this);
        if(result == JFileChooser.APPROVE_OPTION) {
            // Process the directory
            String dirName = chooser.getSelectedFile().getPath();
            File dir = new File(dirName);
            if(!dir.exists()) {
                Utils.errMsg("Does not exist: " + dirName);
                return null;
            }
            if(!dir.isDirectory()) {
                Utils.errMsg("Not a diretory: " + dirName);
                return null;
            }
            return dirName;
        } else {
            return null;
        }
    }

    /**
     * Set the Controls from the given Settings. Can also be used to initialize
     * the dialog.
     * 
     * @param settings
     */
    public void setValues(Settings settings) {
        if(viewer == null) {
            return;
        }
        if(defaultDirText != null) {
            defaultDirText.setText(settings.getDefaultDirectory());
        }

        if(hrVisibileCheck != null) {
            hrVisibileCheck.setSelected(settings.getHrVisible());
        }
        if(hrZonesVisibileCheck != null) {
            hrZonesVisibileCheck.setSelected(settings.getHrZonesVisible());
        }
        if(speedVisibileCheck != null) {
            speedVisibileCheck.setSelected(settings.getSpeedVisible());
        }
        if(eleVisibileCheck != null) {
            eleVisibileCheck.setSelected(settings.getEleVisible());
        }

        if(hrRavCountText != null) {
            hrRavCountText.setText(Integer.toString(settings
                .getHrRollingAvgCount()));
        }
        if(speedRavCountText != null) {
            speedRavCountText.setText(Integer.toString(settings
                .getSpeedRollingAvgCount()));
        }
        if(eleRavCountText != null) {
            eleRavCountText.setText(Integer.toString(settings
                .getEleRollingAvgCount()));
        }
    }

    /**
     * Sets the current values in the given Settings then checks if they are
     * valid.
     * 
     * @param settings
     * @return True if they are valid, else false.
     */
    public boolean setSettingsFromValues(Settings settings) {
        if(settings == null) {
            Utils.errMsg("Input settings is null");
            return false;
        }
        try {
            settings.setDefaultDirectory(defaultDirText.getText());

            settings.setHrVisible(hrVisibileCheck.isSelected());
            settings.setHrZonesVisible(hrZonesVisibileCheck.isSelected());
            settings.setSpeedVisible(speedVisibileCheck.isSelected());
            settings.setEleVisible(eleVisibileCheck.isSelected());

            settings.setHrRollingAvgCount(Integer.parseInt((hrRavCountText
                .getText())));
            settings.setSpeedRollingAvgCount(Integer
                .parseInt((speedRavCountText.getText())));
            settings.setEleRollingAvgCount(Integer.parseInt((eleRavCountText
                .getText())));
        } catch(Exception ex) {
            Utils.excMsg("Error reading values", ex);
            return false;
        }

        // Check if the values are valid
        boolean res = settings.checkValues(true);
        if(!res) {
            Utils.errMsg("Some values are invalid");
        }
        return res;
    }

    /**
     * Saves the current values to the preference store if they are valid.
     */
    public void save() {
        Settings settings = new Settings();
        boolean res = setSettingsFromValues(settings);
        if(!res) {
            Utils.errMsg("Aborting");
            return;
        }
        // Save to the preference store
        try {
            res = settings.saveToPreferences(true);
        } catch(Exception ex) {
            Utils.excMsg("Error saving preferences", ex);
            return;
        }
        if(res) {
//            Utils.errMsg("Preferences saved successfully");
        } else {
            Utils.errMsg("Error saving preferences");
        }
    }

    /**
     * Sets the current values to the viewer if they are valid.
     */
    public void setToViewer() {
        Settings settings = new Settings();
        boolean res = setSettingsFromValues(settings);
        if(!res) {
            Utils.errMsg("Aborting");
            return;
        }
        // Copy to the viewer settings
        try {
            viewer.onPreferenceReset(settings);
        } catch(Exception ex) {
            Utils.excMsg("Error setting viewer settings", ex);
            return;
        }
        if(res) {
//            Utils.errMsg("Viewer settings set successfully");
        } else {
            Utils.errMsg("Error setting viewer settings");
        }
    }

    /**
     * Shows the dialog and returns whether it was successful or not. However
     * currently it is always successful and returns only on Cancel.
     * 
     * @return
     */
    public boolean showDialog() {
        setVisible(true);
        dispose();
        return ok;
    }

}
