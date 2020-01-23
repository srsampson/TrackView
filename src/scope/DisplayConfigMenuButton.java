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

    public DisplayConfigMenuButton(String l1, String l2, ActionListener main) {
        super();

        line1 = l1;
        line2 = l2;

        Dimension d = new Dimension(50, 35);
        setPreferredSize(d);
        setMinimumSize(d);
        setSize(d);

        setOpaque(false);
        setFocusable(false);

        setBorder(BorderFactory.createLineBorder(Config.NORMAL_WHITE));
        setForeground(Config.NORMAL_WHITE);
        setBackground(Config.LOW_WHITE);

        String cmd = line1;
        if (line2 != null && line2.length() > 0) {
            cmd += "_" + line2;
        }
        setActionCommand(cmd);
        addActionListener(main);
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
}
