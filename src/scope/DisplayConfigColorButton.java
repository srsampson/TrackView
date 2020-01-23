package scope;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;

public final class DisplayConfigColorButton extends JButton implements ActionListener {

    public static final String CMD_CLICK = "click";
    private final String key;
    private final String line1;
    private final String line2;
    private final Config dc;

    public DisplayConfigColorButton(String k, String l1, String l2, Config c) {
        super();

        key = k;

        line1 = l1;
        line2 = l2;

        dc = c;

        Dimension d = new Dimension(50, 35);
        setPreferredSize(d);
        setMinimumSize(d);
        setSize(d);

        setOpaque(false);
        setFocusable(false);

        setBorder(BorderFactory.createLineBorder(Config.NORMAL_WHITE));
        setForeground(Config.NORMAL_WHITE);
        setBackground(Config.LOW_WHITE);

        setActionCommand(CMD_CLICK);
        addActionListener(this);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        graphics.setColor(Config.NORMAL_WHITE);
        graphics.setFont(new Font("Franklin Gothic Book", Font.BOLD, 10));

        graphics.drawString(line1, 6, 11);

        if (line2 != null && line2.length() > 0) {
            graphics.drawString(line2, 6, 25);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this && e.getActionCommand().equals(CMD_CLICK)) {
            Color oldColor = dc.getColorSetting(key);
            Color newColor = JColorChooser.showDialog(getRootPane(), "Color", oldColor);

            dc.changeColorSetting(key, newColor);
        }
    }
}
