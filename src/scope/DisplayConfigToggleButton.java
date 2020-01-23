package scope;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JToggleButton;

public final class DisplayConfigToggleButton extends JToggleButton implements ActionListener {

    private final String key;
    private final String line1;
    private final String line2;
    private final Config dc;

    public DisplayConfigToggleButton(String k, String l1, String l2, Config c) {
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

        addActionListener(this);

        setSelected(dc.getBooleanSetting(key));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        if (isSelected()) {
            graphics.setColor(Config.LOW_WHITE);
            graphics.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

            graphics.setColor(Config.HIGH_WHITE);
        } else {
            graphics.setColor(Config.NORMAL_WHITE);
        }

        graphics.setFont(new Font("Franklin Gothic Book", Font.BOLD, 10));

        graphics.drawString(line1, 6, 11);
        if (line2 != null && line2.length() > 0) {
            graphics.drawString(line2, 6, 25);
        }
    }

    @Override
    public void setSelected(boolean value) {
        super.setSelected(value);
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this) {
            dc.toggleBooleanSetting(key);
            setSelected(dc.getBooleanSetting(key));
        }
    }
}
