package scope;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public final class DisplayConfigPanel extends JPanel
        implements ActionListener {

    private final Config dc;
    private final ConcurrentHashMap<String, DisplayConfigMenu> menus;

    public DisplayConfigPanel(Config dc) {
        this.dc = dc;
        Dimension d = new Dimension(215, 105);
        setPreferredSize(d);
        setMinimumSize(d);
        setSize(d);

        setLayout(null);
        setOpaque(false);
        setFocusable(false);
        setBorder(BorderFactory.createEmptyBorder());

        menus = new ConcurrentHashMap<>();

        initButtons();
        initMenus();
    }

    private void initButtons() {
        DisplayConfigMenuButton mb;

        mb = new DisplayConfigMenuButton("DISP", "INSTRM", this);
        add(mb);
        mb.setBounds(2, 30, mb.getWidth(), mb.getHeight());

        mb = new DisplayConfigMenuButton("COLORS", null, this);
        add(mb);
        mb.setBounds(82, 30, mb.getWidth(), mb.getHeight());

        mb = new DisplayConfigMenuButton("MAP", null, this);
        add(mb);
        mb.setBounds(162, 30, mb.getWidth(), mb.getHeight());
    }

    private void initMenus() {
        DisplayConfigMenu menu;

        menu = new DisplayConfigMenu("DISP_INSTRM");
        addToggleButton(menu, Config.DISP_INSTRM_TRACK, "TRACK", null);
        addIntegerButton(menu, Config.DISP_INSTRM_VECTOR, "VECTOR");
        addIntegerButton(menu, Config.DISP_INSTRM_LEADER, "LEADER");
        addIntegerButton(menu, Config.DISP_INSTRM_BLOCK, "BLOCK");

        addIntegerButton(menu, Config.DISP_INSTRM_ECHOES, "ECHOES");
        addToggleButton(menu, Config.DISP_INSTRM_GND_ECHO, "GDECHO", null);
        addIntegerButton(menu, Config.DISP_INSTRM_ESIZE, "ESIZE");

        addToggleButton(menu, Config.DISP_INSTRM_CA, "CA", null);
        addIntegerButton(menu, Config.DISP_INSTRM_CAFLOOR, "CAFLR");
        addIntegerButton(menu, Config.DISP_INSTRM_CAALT, "CAALT");
        addIntegerButton(menu, Config.DISP_INSTRM_CARNG, "CARNG");

        addIntegerButton(menu, Config.DISP_INSTRM_HIGH, "HIGH");
        addIntegerButton(menu, Config.DISP_INSTRM_LOW, "LOW");
        addIntegerButton(menu, Config.DISP_INSTRM_DIM, "DIM");
        addIntegerButton(menu, Config.DISP_INSTRM_DROP, "DROP");

        addIntegerButton(menu, Config.DISP_INSTRM_TRANS, "TRANS");
        this.addMenu(menu);

        menu = new DisplayConfigMenu("COLORS");
        addColorButton(menu, Config.COLORS_TRACK, "TRACK", "LEVEL");
        addColorButton(menu, Config.COLORS_TRACK_DESCEND, "TRACK", "DOWN");
        addColorButton(menu, Config.COLORS_TRACK_CLIMB, "TRACK", "UP");
        addColorButton(menu, Config.COLORS_TRACK_HIST, "HIST", "LEVEL");
        addColorButton(menu, Config.COLORS_TRACK_DESCEND_HIST, "HIST", "DOWN");
        addColorButton(menu, Config.COLORS_TRACK_CLIMB_HIST, "HIST", "UP");
        this.addMenu(menu);

        menu = new DisplayConfigMenu("MAP");
        addToggleButton(menu, Config.MAP_OBJECTS, "OBJ", null);
        addToggleButton(menu, Config.MAP_VECTORS, "VEC", null);
        addToggleButton(menu, Config.MAP_NAMES, "NAM", null);
        this.addMenu(menu);
    }

    private void addEmptyButton(DisplayConfigMenu menu) {
        menu.addButton(new DisplayConfigEmptyButton(true));
    }

    private void addToggleButton(DisplayConfigMenu menu, String key, String line1, String line2) {
        menu.addButton(new DisplayConfigToggleButton(key, line1, line2, dc));
    }

    private void addIntegerButton(DisplayConfigMenu menu, String key, String text) {
        menu.addButton(new DisplayConfigIntegerButton(key, text, dc));
    }

    private void addColorButton(DisplayConfigMenu menu, String key, String line1, String line2) {
        menu.addButton(new DisplayConfigColorButton(key, line1, line2, dc));
    }

    public void addMenu(DisplayConfigMenu menu) {
        menus.put(menu.getButtonName(), menu);
        add(menu);
        menu.setBounds(0, 0, menu.getWidth(), menu.getHeight());
        menu.setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source instanceof DisplayConfigMenuButton) {
            DisplayConfigMenuButton button = (DisplayConfigMenuButton) source;
            String cmd = e.getActionCommand();
            Object o = menus.get(cmd);

            if (o != null) {
                DisplayConfigMenu menu = (DisplayConfigMenu) o;
                menu.setVisible(button.isSelected());
                redisplayMenus();
            }
        }
    }

    private void redisplayMenus() {
        int y = 80;
        Collection<DisplayConfigMenu> c = menus.values();

        for (DisplayConfigMenu menu : c) {
            if (menu.isVisible()) {
                menu.setBounds(2, y, menu.getWidth(), menu.getHeight());
                y += menu.getHeight() + 9;
            }
        }

        Dimension d = new Dimension(215, y - 9);
        setPreferredSize(d);
        setMinimumSize(d);
        setSize(d);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        graphics.setColor(Config.NORMAL_WHITE);
        graphics.setFont(new Font("Franklin Gothic Book", Font.BOLD, 12));
        graphics.drawString("Display Configuration", 35, 12);
        graphics.drawRect(2, 0, getWidth() - 7, 17);
    }
}
