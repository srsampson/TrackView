package scope;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public final class DisplayConfigIntegerButton extends JPanel implements ActionListener {

    public static final String CMD_UP = "up";
    public static final String CMD_DOWN = "down";
    private final String key;
    private final String text;
    private int value;
    private final InvisibleButton upButton;
    private final InvisibleButton downButton;
    private final Config dc;

    class InvisibleButton extends JButton {

        public InvisibleButton(boolean up) {
            super();

            Dimension d = new Dimension(48, 15);
            setPreferredSize(d);
            setMinimumSize(d);
            setSize(d);

            setOpaque(false);
            setFocusable(false);

            setBorder(BorderFactory.createEmptyBorder());
            setForeground(Config.NORMAL_WHITE);
            setBackground(Config.LOW_WHITE);

            if (up) {
                setActionCommand("up");
            } else {
                setActionCommand("down");
            }
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            // not shown
        }
    }

    public DisplayConfigIntegerButton(String k, String t, Config c) {
        super();

        key = k;
        text = t;
        dc = c;
        setValue(dc.getIntegerSetting(key));

        Dimension d = new Dimension(50, 35);
        setPreferredSize(d);
        setMinimumSize(d);
        setSize(d);

        setLayout(null);
        setOpaque(false);
        setFocusable(false);

        setBorder(BorderFactory.createLineBorder(Config.NORMAL_WHITE));
        setForeground(Config.NORMAL_WHITE);
        setBackground(Config.LOW_WHITE);

        upButton = new InvisibleButton(true);
        add(upButton);
        upButton.setBounds(1, 1, upButton.getWidth(), upButton.getHeight());
        upButton.addActionListener(this);

        downButton = new InvisibleButton(false);
        add(downButton);
        downButton.setBounds(1, 20, downButton.getWidth(), downButton.getHeight());
        downButton.addActionListener(this);
    }

    public void setValue(int val) {
        value = val;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        graphics.setColor(Config.NORMAL_WHITE);
        graphics.setFont(new Font("Franklin Gothic Book", Font.BOLD, 10));

        graphics.drawString(text, 6, 11);
        graphics.drawString(Integer.toString(value), 20, 25);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == upButton && e.getActionCommand().equals(CMD_UP)) {
            setValue(dc.incIntegerSetting(key));
        }

        if (e.getSource() == downButton && e.getActionCommand().equals(CMD_DOWN)) {
            setValue(dc.decIntegerSetting(key));
        }
    }
}
