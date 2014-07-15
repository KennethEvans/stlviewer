package net.kenevans.stlviewer.preferences;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.kenevans.core.utils.Utils;
import net.kenevans.stlviewer.model.IConstants;
import net.kenevans.stlviewer.ui.STLViewer;

/**
 * PreferencesDialog is a dialog to set the Preferences for STLViewer.
 * 
 * @author Kenneth Evans, Jr.
 */
public class PreferencesDialog extends JDialog implements IConstants
{
    private static final long serialVersionUID = 1L;
    private STLViewer viewer;
    private boolean ok = false;

    JTextField defaultDirText;

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
        setValuesFromPreferences();
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

        // Default directory
        gridy++;
        JLabel label = new JLabel("Default Directory:");
        label.setToolTipText("The default directory.");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridy;
        contentPane.add(label, gbc);

        JPanel filePanel = new JPanel();
        filePanel.setLayout(new GridBagLayout());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        gbc.gridy = gridy;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        contentPane.add(filePanel, gbc);

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

        // Dummy
        gridy++;
        label = new JLabel("Dummy:");
        label.setToolTipText("Dummy.");
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 0;
        gbc.gridy = gridy;
        contentPane.add(label, gbc);

        JTextField dummyText = new JTextField(30);
        dummyText.setToolTipText(label.getText());
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridx = 1;
        gbc.gridy = gridy;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 100;
        contentPane.add(dummyText, gbc);

        // Button panel
        gridy++;
        JPanel buttonPanel = new JPanel();
        gbc = (GridBagConstraints)gbcDefault.clone();
        gbc.gridy = 0;
        gbc.gridy = gridy;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPane.add(buttonPanel, gbc);

        button = new JButton();
        button.setText("Save");
        button.setToolTipText("Save the changes as preferences.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                boolean result = setPreferencesFromValues();
                if(result) {
                    ok = result;
                    PreferencesDialog.this.setVisible(false);
                }
            }
        });
        buttonPanel.add(button);

        button = new JButton();
        button.setText("Use Current");
        button.setToolTipText("Set to the current viewer values.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                setValuesFromViewer();
            }
        });
        buttonPanel.add(button);

        button = new JButton();
        button.setText("Reset");
        button.setToolTipText("Reset to the current preferences.");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                setValuesFromPreferences();
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
     * Resets the internal state from the input viewer.
     */
    public void setValuesFromViewer() {
        if(viewer == null) {
            return;
        }
        if(defaultDirText != null) {
            defaultDirText.setText(viewer.getDefaultDirectory());
        }
    }

    /**
     * Set the Controls from the preference store. Can also be used to
     * initialize the dialog.
     */
    public void setValuesFromPreferences() {
        if(viewer == null) {
            return;
        }
        Preferences prefs = STLViewer.getUserPreferences();
        if(defaultDirText != null) {
            defaultDirText.setText(prefs.get(P_DEFAULT_DIR, D_DEFAULT_DIR));
        }
    }

    /**
     * Collects the values of the components, and if they are valid, then calls
     * the saveFile method in the viewer.
     * 
     * @return True on success to close the dialog or false otherwise to leave
     *         the dialog up.
     */
    public boolean setPreferencesFromValues() {
        Preferences prefs = STLViewer.getUserPreferences();

        // Default directory
        String defaultDir = defaultDirText.getText();
        if(defaultDir == null) {
            Utils.errMsg("Value for the default directory is null");
            return false;
        }
        File file = new File(defaultDir);
        if(file == null) {
            Utils.errMsg("The default directory is invalid");
            return false;
        }
        if(!file.exists()) {
            Utils.errMsg("The default directory does not exist");
            return false;
        }
        if(!file.isDirectory()) {
            Utils.errMsg("The default directory is not a directory");
            return false;
        }

        // Set the values
        prefs.put(P_DEFAULT_DIR, defaultDir);

        return true;
    }

    /**
     * Shows the dialog an returns whether it was successful or not.
     * 
     * @return
     */
    public boolean showDialog() {
        setVisible(true);
        dispose();
        return ok;
    }

}
