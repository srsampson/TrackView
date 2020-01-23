package scope;

import java.awt.Graphics;
import math.Navigator;
import math.Projection;

public abstract class ScopeRenderer {

    protected Projection projection;
    protected Navigator navigator;
    protected double scale;
    protected Config dc;

    public ScopeRenderer(Projection p, Navigator n, double s, Config c) {
        projection = p;
        navigator = n;
        scale = s;
        dc = c;
    }

    /**
     * Returns the projection used for converting longitude/latitude
     * coordinates to meters (measured from the center of the scope).
     * @return projection The projection that is used by this renderer.
     */
    public Projection getProjection() {
        return projection;
    }

    /**
     * Sets the projection to be used for converting longitude/latitude
     * coordinates to meters (measured from the center of the scope).
     * @param val The projection to used.
     */
    public void setProjection(Projection val) {
        projection = val;
    }

    /**
     * Returns the navigator used for calculating things like distances.
     * @return navigator the navigator used.
     */
    public Navigator getNavigator() {
        return navigator;
    }

    /**
     * Sets the navigator used for calculating things like distances.
     * @param val The navigator to use.
     */
    public void setNavigator(Navigator val) {
        navigator = val;
    }

    /**
     * Returns the scale (in pixels per meter) that is applied
     * before drawing.
     * @return scale The scale used when drawing (in pixels per meter).
     */
    public double getScale() {
        return scale;
    }

    /**
     * Specifies the scale (in pixels per meter) that should be applied
     * for drawing.
     * @param val The scale to be used when drawing (in pixels per meter).
     */
    public void setScale(double val) {
        scale = val;
    }

    /* 
     * NOTE: Doesn't perform clipping yet.  If you roll the Earth over you
     * will still see the other countries on the other side.
     */
    public abstract void renderer(Graphics graphics, int width, int height, boolean displayStep);
}
