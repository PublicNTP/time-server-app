package org.publicntp.gnssreader.repository;

import android.content.Context;

import org.publicntp.gnssreader.helper.DateFormatter;

import java.util.Date;

/**
 * Created by zac on 2/7/18.
 */

public class TimeStorage {
    private static Date nmeaDate;
    private static Date nmeaAcquiredDate;

    public static void setNmeaDate(Date nmeaDate) {
        nmeaAcquiredDate = new Date();
        TimeStorage.nmeaDate = nmeaDate;
    }

    public static long getDateDifference() throws Exception {
        if(nmeaDate == null) throw new Exception("No NMEA Date acquired.");
        return Math.abs(nmeaDate.getTime() - nmeaAcquiredDate.getTime()); // This is the difference between system and Nmea time
    }

    public static long getAdjustedMillis() {
        if(nmeaDate == null) return System.currentTimeMillis();

        return nmeaDate.getTime() + (new Date().getTime() - nmeaAcquiredDate.getTime());
    }

    public static Date getAdjustedDate() {
        if(nmeaDate == null) return new Date();

        return new Date(getAdjustedMillis());
    }

    public static String getAdjustedDateString(Context context) {
        return DateFormatter.utcTimeString(context, getAdjustedDate());
    }

    public static long getTime() {
        return getAdjustedMillis();
    }
}
