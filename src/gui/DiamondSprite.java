package gui;

import java.awt.Color;
import java.awt.Graphics2D;

public final class DiamondSprite extends Sprite2D {

    protected int width;
    protected int height;

    public DiamondSprite(int x, int y, int w, int h, Color c, String id) {
        acid = id;
        locx = x;
        locy = y;
        width = w;
        height = h;
        color = c;
        fill = false;
        restore();
    }

    public DiamondSprite(int x, int y, Color c, String id) {
        acid = id;
        locx = x;
        locy = y;
        width = 4;
        height = 4;
        color = c;
        fill = false;
        restore();
    }

    public DiamondSprite() {
        acid = "";
        locx = 0;
        locy = 0;
        width = 0;
        height = 0;
        color = null;
        fill = false;
        restore();
    }

    @Override
    public void update() {
    }

    @Override
    public void paint(Graphics2D g) {
        if (visible) {
            if (fill) {
                g.setColor(color.brighter());
                g.drawLine(locx - width, locy, locx, locy - height);
                g.drawLine(locx + width, locy, locx, locy - height);
                g.drawLine(locx - width, locy, locx, locy + height);
                g.drawLine(locx + width, locy, locx, locy + height);

                g.drawLine(locx - width, locy, locx + width, locy);
                g.drawLine(locx, locy - height, locx, locy + height);
                g.setColor(color);
            } else {
                g.setColor(color);
                g.drawLine(locx - width, locy, locx, locy - height);
                g.drawLine(locx + width, locy, locx, locy - height);
                g.drawLine(locx - width, locy, locx, locy + height);
                g.drawLine(locx + width, locy, locx, locy + height);
            }
        }
    }
}
