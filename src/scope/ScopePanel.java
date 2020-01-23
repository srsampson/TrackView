package scope;

import gui.DiamondSprite;
import gui.MapGeoData;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.sql.Connection;
import java.util.Collection;
import javax.swing.JPanel;
import javax.swing.Timer;
import math.OrthographicNavigator;
import math.OrthographicProjection;

public final class ScopePanel extends JPanel
        implements MouseListener, MouseMotionListener, MouseWheelListener,
        ActionListener {

    /*
     * Measures the display-scale of the map and tracks in pixels per meter.
     */
    private double scale;
    private LatLon center;
    private LatLon mouseLatLon;
    private double minScale;
    private double maxScale;
    private boolean displayStep;
    private final OrthographicProjection projection;
    private final OrthographicNavigator navigator;
    private final Renderer renderer;
    private final ProcessTracks process;
    private final Config dc;
    private final Connection db;
    
    //
    //private static int middleX = 0;
    //private static int middleY = 0;

    public ScopePanel(ProcessTracks p, Connection con, LatLon gcenter, Config c) {
        process = p;
        center = gcenter;
        dc = c;
        db = con;
        scale = c.getMapScale();
        minScale = 0.00004;
        maxScale = 1000.0;
        displayStep = false;
        setBackground(dc.getColorSetting(Config.COLORS_BACK_GND));

        navigator = new OrthographicNavigator(center);
        projection = navigator.getProjection();
        
        new Conflict(process, dc);
        renderer = new Renderer(process, db, projection, navigator, scale, dc);

        // required for regularly changing or blinking symbols
        Timer timer = new Timer(500, this);
        timer.setCoalesce(false);
        timer.start();

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);

        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        // we don't want any keyboard focus management
        setFocusTraversalKeysEnabled(false);
    }

    public void setMapData(MapGeoData mapData) {
        renderer.setMapData(mapData);
    }

    public LatLon getCenter() {
        return center;
    }

    public double getLon() {
        return center.lon;
    }

    public double getLat() {
        return center.lat;
    }

    public double getScale() {
        return scale;
    }

    public double getMinScale() {
        return minScale;
    }

    public void setMinScale(double val) {
        minScale = val;
    }

    public double getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(double val) {
        maxScale = val;
    }

    /*
     * This is called whenever the user does something that requires a redraw,
     * or the displayStep timer fires.
     *
     * It first calls the jpanel paintComponent
     */
    @Override
    public void paintComponent(Graphics graphics) {
        if (graphics == null) {
            return;
        }

        super.paintComponent(graphics);

        int x = getWidth() / 2;
        int y = getHeight() / 2;

        graphics.translate(x, y);

        renderer.renderer(graphics, getWidth(), getHeight(), displayStep);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        if (e.getButton() == MouseEvent.BUTTON3) {
            setMouseLatLon(x, y);
            projection.setCenter(mouseLatLon);
            center = projection.getCenter();
            repaint();
        } else if (e.getButton() == MouseEvent.BUTTON1) {
            Collection<DiamondSprite> sprites = renderer.getSprites();

            setMouseLatLon(x, y);
            LatLon p1 = projection.convertToMeters(mouseLatLon);

            int x2 = (int) (p1.lon * scale);
            int y2 = (int) (p1.lat * -scale);

            try {
                if (!sprites.isEmpty()) {
                    for (DiamondSprite ds : sprites) {
                        int x3 = Math.abs(x2 - ds.getX());
                        int y3 = Math.abs(y2 - ds.getY());

                        if ((x3 < 6) && (y3 < 6)) {
                            Track t = process.getTrack(ds.getAcid());

                            Integer block = (Integer) t.getTrackOption(Track.TRACKBLOCK_POSITION);
                            block += 1;

                            if (block >= 9) {
                                block = 0;
                            }

                            t.setTrackIntegerOption(Track.TRACKBLOCK_POSITION, block);
                            repaint();
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println("ScopePanel::mouseClicked event exception " + ex.toString());
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Collection<DiamondSprite> sprites = renderer.getSprites();

        if (!sprites.isEmpty()) {
            int x = e.getX();
            int y = e.getY();

            setMouseLatLon(x, y);
            LatLon p1 = projection.convertToMeters(mouseLatLon);

            int x2 = (int) (p1.lon * scale);
            int y2 = (int) (p1.lat * -scale);

            try {
                if (!sprites.isEmpty()) {
                    for (DiamondSprite ds : sprites) {
                        Track track = process.getTrack(ds.getAcid());

                        if (track != (Track) null) {
                            int x3 = Math.abs(x2 - ds.getX());
                            int y3 = Math.abs(y2 - ds.getY());

                            if ((x3 < 6) && (y3 < 6)) {
                                track.setMouseOver(true);
                            } else {
                                track.setMouseOver(false);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println("ScopePanel::mouseMoved event exception " + ex.toString());
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int amount = e.getWheelRotation();

        if (amount > 0) {
            // zoom out (decrease meters per pixel)
            rescale(scale / (1.2f * amount));
        }
        
        if (amount < 0) {
            // zoom in (increase meters per pixel)
            rescale(scale * (1.2f * -amount));
        }

        setMouseLatLon(e.getX(), e.getY());
        repaint();
    }

    public void rescale(double newScale) {
        if (newScale <= maxScale && newScale >= minScale) {
            scale = newScale;
            renderer.setScale(scale);
        }
    }

    private void setMouseLatLon(int x, int y) {
        double lon = (x - (getWidth() / 2)) / scale;
        double lat = -(y - (getHeight() / 2)) / scale;

        mouseLatLon = projection.convertToCoords(lat, lon);
    }

    public LatLon getMouseLatLon() {
        return mouseLatLon;
    }

    public void aircraftAdded(Track target) {
        repaint();
    }

    public void aircraftUpdated(Track target) {
        repaint();
    }

    public void aircraftRemoved(Track target) {
        repaint();
    }

    /*
     * This allows the display to blink stuff by timer.
     */
    public void flipDisplayStep() {
        displayStep = displayStep == false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o != null && o instanceof Timer) {
            flipDisplayStep();
            repaint();
        }
    }
}
