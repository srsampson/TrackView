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

    public DisplayConfigToggleButton(String key, String line1, String line2, Config c) {
        super();

        this.key = key;
        this.line1 = line1;
        this.line2 = line2;

        this.dc = c;

        Dimension d = new Dimension(50, 35);
        this.setPreferredSize(d);
        this.setMinimumSize(d);
        this.setSize(d);

        this.setOpaque(false);
        this.setFocusable(false);

        this.setBorder(BorderFactory.createLineBorder(Config.NORMAL_WHITE));
        this.setForeground(Config.NORMAL_WHITE);
        this.setBackground(Config.LOW_WHITE);

        this.addActionListener(this);

        this.setSelected(dc.getBooleanSetting(key));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        if (this.isSelected()) {
            graphics.setColor(Config.LOW_WHITE);
            graphics.fillRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);

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
            this.setSelected(dc.getBooleanSetting(key));
        }
    }
}
