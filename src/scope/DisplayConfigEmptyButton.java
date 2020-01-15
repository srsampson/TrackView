package scope;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public final class DisplayConfigEmptyButton extends JPanel {

    public DisplayConfigEmptyButton(boolean showBorder) {
        super();

        Dimension d = new Dimension(50, 35);
        this.setPreferredSize(d);
        this.setMinimumSize(d);
        this.setSize(d);

        this.setLayout(null);
        this.setOpaque(false);
        this.setFocusable(false);

        if (showBorder) {
            this.setBorder(BorderFactory.createLineBorder(Config.NORMAL_WHITE));
        } else {
            this.setBorder(BorderFactory.createEmptyBorder());
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        // not shown (only border)
    }
}
