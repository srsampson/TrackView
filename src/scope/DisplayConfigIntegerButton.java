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
            this.setPreferredSize(d);
            this.setMinimumSize(d);
            this.setSize(d);

            this.setOpaque(false);
            this.setFocusable(false);

            this.setBorder(BorderFactory.createEmptyBorder());
            this.setForeground(Config.NORMAL_WHITE);
            this.setBackground(Config.LOW_WHITE);

            if (up) {
                this.setActionCommand("up");
            } else {
                this.setActionCommand("down");
            }
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            // not shown
        }
    }

    public DisplayConfigIntegerButton(String key, String text, Config c) {
        super();

        this.key = key;
        this.text = text;
        this.dc = c;
        this.setValue(dc.getIntegerSetting(key));

        Dimension d = new Dimension(50, 35);
        this.setPreferredSize(d);
        this.setMinimumSize(d);
        this.setSize(d);

        this.setLayout(null);
        this.setOpaque(false);
        this.setFocusable(false);

        this.setBorder(BorderFactory.createLineBorder(Config.NORMAL_WHITE));
        this.setForeground(Config.NORMAL_WHITE);
        this.setBackground(Config.LOW_WHITE);

        upButton = new InvisibleButton(true);
        this.add(upButton);
        upButton.setBounds(1, 1, upButton.getWidth(), upButton.getHeight());
        upButton.addActionListener(this);

        downButton = new InvisibleButton(false);
        this.add(downButton);
        downButton.setBounds(1, 20, downButton.getWidth(), downButton.getHeight());
        downButton.addActionListener(this);
    }

    public void setValue(int val) {
        this.value = val;
        this.repaint();
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
            this.setValue(dc.incIntegerSetting(key));
        }

        if (e.getSource() == downButton && e.getActionCommand().equals(CMD_DOWN)) {
            this.setValue(dc.decIntegerSetting(key));
        }
    }
}
