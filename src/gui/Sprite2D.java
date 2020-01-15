package gui;

import java.awt.Color;

public abstract class Sprite2D extends Sprite {

    protected int locx;
    protected int locy;
    protected Color color;
    protected boolean fill;

    public int getX() {
        return this.locx;
    }

    public int getY() {
        return this.locy;
    }

    public boolean getFill() {
        return this.fill;
    }

    public void setFill(boolean val) {
        this.fill = val;
    }

    public void setColor(Color val) {
        this.color = val;
    }

    public Color getColor() {
        return this.color;
    }
}
