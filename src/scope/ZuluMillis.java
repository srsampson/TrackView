package scope;

import java.util.Calendar;
import java.util.GregorianCalendar;

public final class ZuluMillis {

    private static final Calendar CAL = new GregorianCalendar();

    /**
     * Method to return the current UTC time
     *
     * @return a long Representing the UTC time.
     */
    public long getUTCTime() {
        CAL.setTimeInMillis(System.currentTimeMillis());

        return CAL.getTimeInMillis()
                - CAL.get(Calendar.ZONE_OFFSET)
                - CAL.get(Calendar.DST_OFFSET);
    }
}
