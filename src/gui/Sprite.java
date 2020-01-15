package gui;

import java.awt.Graphics2D;

public abstract class Sprite {

    protected boolean visible;
    protected boolean active;
    protected String acid;

    abstract void paint(Graphics2D graph);

    abstract void update();

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean val) {
        this.visible = val;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean val) {
        this.active = val;
    }

    public String getAcid() {
        return this.acid;
    }

    public void setAcid(String val) {
        this.acid = val;
    }

    public void suspend() {
        setVisible(false);
        setActive(false);
    }

    public void restore() {
        setVisible(true);
        setActive(true);
    }
}
