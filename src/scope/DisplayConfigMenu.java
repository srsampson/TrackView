package scope;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

public final class DisplayConfigMenu extends JPanel {

    private final String name;
    private final List<JComponent> buttons;

    public DisplayConfigMenu(String name) {
        this.name = name;
        Dimension d = new Dimension(209, 0);
        setPreferredSize(d);
        setMinimumSize(d);
        setSize(d);

        setLayout(null);
        setOpaque(false);
        setFocusable(false);
        setBorder(BorderFactory.createEmptyBorder());

        buttons = new ArrayList<>();
    }

    public void addButton(JComponent button) {
        buttons.add(button);
        recalculateSize();
        add(button);

        int i = buttons.indexOf(button);
        int x = (i % 4) * 53;
        int y = (i / 4) * 38;

        button.setBounds(x, y, button.getWidth(), button.getHeight());
    }

    public String getButtonName() {
        return name;
    }

    protected void recalculateSize() {
        int h = (buttons.size() - 1) / 4 + 1;

        Dimension d = new Dimension(209, h * 35);

        if (h > 0) {
            d.height += (h - 1) * 3;
        }

        setPreferredSize(d);
        setMinimumSize(d);
        setSize(d);
    }

    @Override
    protected void paintComponent(Graphics g) {
    }
}
