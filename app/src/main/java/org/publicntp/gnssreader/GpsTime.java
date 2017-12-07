package org.publicntp.gnssreader;

import android.location.GnssClock;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by zac on 11/14/17.
 */

public class GpsTime {
    public static final TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");
    public static final Calendar GPS_EPOCH = Calendar.getInstance();
    static {
        TimeZone.setDefault(UTC_TIMEZONE);

        GPS_EPOCH.set(Calendar.ZONE_OFFSET, 0);
        GPS_EPOCH.set(Calendar.YEAR, 1980);
        GPS_EPOCH.set(Calendar.MONTH, 0);
        GPS_EPOCH.set(Calendar.DAY_OF_MONTH, 6);
        GPS_EPOCH.set(Calendar.HOUR_OF_DAY, 0);
        GPS_EPOCH.set(Calendar.MINUTE, 0);
        GPS_EPOCH.set(Calendar.SECOND, 0);
        GPS_EPOCH.set(Calendar.MILLISECOND, 0);
    }
    static SimpleDateFormat milliDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS z");
    //public final long nanos;
    //public final long millis;
    //public final long years;
    //public final long days;
    //public final long hours;
    //public final long minutes;
    //public final long seconds;

    private final Date date;

    public GpsTime(GnssClock gnssClock) {
        if(gnssClock.hasBiasUncertaintyNanos()) {
            double biasUncertaintyNanos = Math.abs(gnssClock.getBiasUncertaintyNanos());
        }

        if(gnssClock.hasDriftNanosPerSecond()) {
            double driftNanosPerSecond = gnssClock.getDriftNanosPerSecond();
        }

        if(gnssClock.hasDriftUncertaintyNanosPerSecond()) {
            double driftUncertaintyNanosPerSecond = gnssClock.getDriftUncertaintyNanosPerSecond();
        }

        if(gnssClock.hasTimeUncertaintyNanos()) {
            double timeUncertaintyNanos = gnssClock.getTimeUncertaintyNanos();
        }

        double hardwareClockDiscontinuityCount = gnssClock.getHardwareClockDiscontinuityCount();

        if(!gnssClock.hasFullBiasNanos()) {
            //throw new RuntimeException("No Full Bias Nanos!");
            date = GPS_EPOCH.getTime();
            return;
        }

        if(!gnssClock.hasBiasNanos()) {
            //throw new RuntimeException("No Bias Nanos!");
            date = GPS_EPOCH.getTime();
            return;
        }

        double fullBiasNanos = Math.abs(gnssClock.getFullBiasNanos());
        double timeNanos = Math.abs(gnssClock.getTimeNanos());
        double biasNanos = Math.abs(gnssClock.getBiasNanos());

        //double utcTimeNanos = timeNanos - (fullBiasNanos + biasNanos);
        double leapSeconds = gnssClock.getLeapSecond();
        if (!gnssClock.hasLeapSecond()) {
            leapSeconds = -18000000;
        }

        double utcTimeNanos = fullBiasNanos + timeNanos + biasNanos + leapSeconds;
        date = getDateFromNanoOffset(utcTimeNanos);
        Date finish = new Date();
    }

    private Date getDateFromNanoOffset(double nanoOffset) {
        double nanos_remaining = Math.abs(nanoOffset);
        double hours = TimeUnit.NANOSECONDS.toHours((long) nanos_remaining);
        nanos_remaining -= TimeUnit.HOURS.toNanos((long) hours);
        double minutes = TimeUnit.NANOSECONDS.toMinutes((long) nanos_remaining);
        nanos_remaining -= TimeUnit.MINUTES.toNanos((long) minutes);
        double seconds = TimeUnit.NANOSECONDS.toSeconds((long) nanos_remaining);
        nanos_remaining -= TimeUnit.SECONDS.toNanos((long) seconds);
        double millis = TimeUnit.NANOSECONDS.toMillis((long) nanos_remaining);

        Calendar gps_time = (Calendar) GPS_EPOCH.clone();
        gps_time.add(Calendar.HOUR, (int) hours);
        gps_time.add(Calendar.MINUTE, (int) minutes);
        gps_time.add(Calendar.SECOND, (int) seconds);
        gps_time.add(Calendar.MILLISECOND, (int) millis);
        Date date = gps_time.getTime();
        return date;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return milliDateFormat.format(date);
    }
}
