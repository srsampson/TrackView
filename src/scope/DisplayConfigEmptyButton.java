package scope;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public final class DisplayConfigEmptyButton extends JPanel {

    public DisplayConfigEmptyButton(boolean showBorder) {
        super();

        Dimension d = new Dimension(50, 35);
        setPreferredSize(d);
        setMinimumSize(d);
        setSize(d);

        setLayout(null);
        setOpaque(false);
        setFocusable(false);

        if (showBorder) {
            setBorder(BorderFactory.createLineBorder(Config.NORMAL_WHITE));
        } else {
            setBorder(BorderFactory.createEmptyBorder());
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        // not shown (only border)
    }
}
