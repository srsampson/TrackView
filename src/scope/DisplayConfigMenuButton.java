package scope;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JToggleButton;

public final class DisplayConfigMenuButton extends JToggleButton {

    private final String line1;
    private final String line2;

    public DisplayConfigMenuButton(String line1, String line2, ActionListener main) {
        super();

        this.line1 = line1;
        this.line2 = line2;

        Dimension d = new Dimension(50, 35);
        this.setPreferredSize(d);
        this.setMinimumSize(d);
        this.setSize(d);

        this.setOpaque(false);
        this.setFocusable(false);

        this.setBorder(BorderFactory.createLineBorder(Config.NORMAL_WHITE));
        this.setForeground(Config.NORMAL_WHITE);
        this.setBackground(Config.LOW_WHITE);

        String cmd = line1;
        if (line2 != null && line2.length() > 0) {
            cmd += "_" + line2;
        }
        this.setActionCommand(cmd);
        this.addActionListener(main);
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
}
