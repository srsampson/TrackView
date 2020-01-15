package scope;

/*
 * This object contains a time and target echo position
 */
public final class Echo {

    private final String acid;
    private final int vertical_trend;
    private final long updateTime;
    private final LatLon position;

    public Echo(Track trk) {
        this.acid = trk.getAcid();
        this.vertical_trend = trk.getVerticalTrend();
        this.updateTime = trk.getUpdatedPosTime();
        this.position = trk.getPosition();
    }

    public int getEchoVertical() {
        return this.vertical_trend;
    }

    /**
     * Method to return the echo time in UTC milliseconds
     *
     * @return a long containing the UTC time at which the echo was stored.
     */
    public long getEchoTime() {
        return this.updateTime;
    }

    /**
     * Provides the longitude/latitude position of an echo.
     * Positive values are east/north.
     *
     * @return A 2D Point containing the current echo world position
     */
    public LatLon getEchoPosition() {
        return this.position;
    }

    /**
     * @return A Target object containing the current target track ID.
     */
    public String getEchoID() {
        return this.acid;
    }
}
