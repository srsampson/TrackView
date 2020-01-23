package scope;

import gui.MapGeoData;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.sql.Connection;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

/*
 * Class to provide a graphical interface for the user
 */
public final class ScopeFrame extends JFrame implements KeyListener {

    public static final int LAYER_ECHO_PANEL = 1;
    public static final int LAYER_SCOPE_PANEL = 2;
    public static final int LAYER_CONFIG_PANEL = 3;
    //
    private ScopePanel scopePanel;
    private DisplayConfigPanel displayConfigPanel;
    private final ProcessTracks procTrack;
    private final Config props;
    private final JLayeredPane jLayeredPane1;
    private final JFrame parent;
    private final Connection db;

    public ScopeFrame(Config c, TrackDatabase tb, ProcessTracks pt) {
        procTrack = pt;
        db = tb.getDatabaseConnection();
        initComponents();

        parent = this;

        // we don't want any keyboard focus management
        setFocusTraversalKeysEnabled(false);

        props = c;

        jLayeredPane1 = new JLayeredPane();
        jLayeredPane1.setLayout(new ScopeLayoutManager(this));
        setContentPane(jLayeredPane1);

        initDisplayConfigPanel();
        initScopePanel(new LatLon(props.getHomeLat(), props.getHomeLon()));     // plot center
    }

    private void initScopePanel(LatLon center) {
        scopePanel = new ScopePanel(procTrack, db, center, props);
        jLayeredPane1.add(scopePanel, new Integer(LAYER_SCOPE_PANEL));
    }

    private void initDisplayConfigPanel() {
        displayConfigPanel = new DisplayConfigPanel(props);
        jLayeredPane1.add(displayConfigPanel, new Integer(LAYER_CONFIG_PANEL));
    }

    /**
     * Allow for reloading the configuration file
     * Also redraw the config panel on screen, only if it was there already.
     */
    private void reinitDisplayConfigPanel() {
        jLayeredPane1.remove(displayConfigPanel);
        displayConfigPanel = new DisplayConfigPanel(props);
        jLayeredPane1.add(displayConfigPanel, new Integer(LAYER_CONFIG_PANEL));
        jLayeredPane1.validate();

        /*
         * Keep display on or off, depending on its current state
         */

        displayConfigPanel.setVisible(ConfigCheckBox.isSelected());
    }

    public void setMapData(MapGeoData md) {
        scopePanel.setMapData(md);
    }

    public ScopePanel getScopePanel() {
        return scopePanel;
    }

    public DisplayConfigPanel getDisplayConfigPanel() {
        return displayConfigPanel;
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        AboutDialog = new javax.swing.JDialog();
        AboutExitButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jFileChooserSave = new javax.swing.JFileChooser();
        jFileChooserOpen = new javax.swing.JFileChooser();
        jMenuBar = new javax.swing.JMenuBar();
        FileMenu1 = new javax.swing.JMenu();
        ExitMenuItem1 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        ConfigCheckBox = new javax.swing.JCheckBoxMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        HelpMenu1 = new javax.swing.JMenu();
        AboutMenuItem1 = new javax.swing.JMenuItem();

        AboutDialog.setTitle("About");
        AboutDialog.setAlwaysOnTop(true);
        AboutDialog.setBounds(new java.awt.Rectangle(400, 300, 0, 0));
        AboutDialog.setMinimumSize(new java.awt.Dimension(340, 226));
        AboutDialog.setName("AboutDialog"); // NOI18N
        AboutDialog.setResizable(false);

        AboutExitButton.setText("OK");
        AboutExitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AboutExitButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 3, 18)); // NOI18N
        jLabel1.setText("TrackView");

        jLabel2.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        jLabel2.setText("Version 1.91, January 2020");
        jLabel2.setToolTipText("");

        jLabel3.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel3.setText("ADS-B Graphics Display Program");

        javax.swing.GroupLayout AboutDialogLayout = new javax.swing.GroupLayout(AboutDialog.getContentPane());
        AboutDialog.getContentPane().setLayout(AboutDialogLayout);
        AboutDialogLayout.setHorizontalGroup(
            AboutDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AboutDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AboutDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AboutDialogLayout.createSequentialGroup()
                        .addGroup(AboutDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(AboutDialogLayout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AboutDialogLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(AboutExitButton)
                        .addGap(44, 44, 44))))
        );
        AboutDialogLayout.setVerticalGroup(
            AboutDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AboutDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(AboutExitButton)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));
        setBounds(new java.awt.Rectangle(260, 50, 0, 0));
        setFont(new java.awt.Font("Microsoft Sans Serif", 0, 10)); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jFileChooserSave.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        jFileChooserSave.setCurrentDirectory(new java.io.File("/home/ssampson/c:/users"));
        jFileChooserSave.setDialogTitle("Save Config File");

        jFileChooserOpen.setCurrentDirectory(new java.io.File("/home/ssampson/c:/users"));
        jFileChooserOpen.setDialogTitle("Open Config File");

        FileMenu1.setText("File");

        ExitMenuItem1.setText("Exit");
        ExitMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitMenuItemActionPerformed(evt);
            }
        });
        FileMenu1.add(ExitMenuItem1);

        jMenuBar.add(FileMenu1);

        jMenu1.setText("Config");

        ConfigCheckBox.setSelected(true);
        ConfigCheckBox.setText("On Screen");
        ConfigCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConfigCheckBoxActionPerformed(evt);
            }
        });
        jMenu1.add(ConfigCheckBox);

        jMenuItem3.setText("Load");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setText("Save");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuBar.add(jMenu1);

        HelpMenu1.setText("Help");

        AboutMenuItem1.setText("About");
        AboutMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AboutMenuItemActionPerformed(evt);
            }
        });
        HelpMenu1.add(AboutMenuItem1);

        jMenuBar.add(HelpMenu1);

        setJMenuBar(jMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jFileChooserSave, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jFileChooserOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jFileChooserSave, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFileChooserOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(212, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void ExitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitMenuItemActionPerformed
        procTrack.close();
        System.exit(0);
    }//GEN-LAST:event_ExitMenuItemActionPerformed

    private void AboutExitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AboutExitButtonActionPerformed
        AboutDialog.setVisible(false);
    }//GEN-LAST:event_AboutExitButtonActionPerformed

    private void AboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AboutMenuItemActionPerformed
        AboutDialog.setVisible(true);
    }//GEN-LAST:event_AboutMenuItemActionPerformed

    private void ConfigCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConfigCheckBoxActionPerformed
        displayConfigPanel.setVisible(ConfigCheckBox.isSelected());
    }//GEN-LAST:event_ConfigCheckBoxActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        jFileChooserOpen.showOpenDialog(parent);
        props.initProperties(jFileChooserOpen.getCurrentDirectory().getPath() + File.separator + jFileChooserOpen.getSelectedFile().getName());
        reinitDisplayConfigPanel();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        props.setHomeLat(scopePanel.getLat());
        props.setHomeLon(scopePanel.getLon());
        props.setMapScale(scopePanel.getScale());
        /*
         * I have no clue what I'm doing
         */
        jFileChooserSave.showSaveDialog(parent);
        props.saveProperties(jFileChooserSave.getCurrentDirectory().getPath() + File.separator + jFileChooserSave.getSelectedFile().getName());
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Runtime.getRuntime().exit(0);
    }//GEN-LAST:event_formWindowClosing
    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // for debugging
        // System.out.println(e.toString());
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog AboutDialog;
    private javax.swing.JButton AboutExitButton;
    private javax.swing.JMenuItem AboutMenuItem1;
    private javax.swing.JCheckBoxMenuItem ConfigCheckBox;
    private javax.swing.JMenuItem ExitMenuItem1;
    private javax.swing.JMenu FileMenu1;
    private javax.swing.JMenu HelpMenu1;
    private javax.swing.JFileChooser jFileChooserOpen;
    private javax.swing.JFileChooser jFileChooserSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    // End of variables declaration//GEN-END:variables
}
