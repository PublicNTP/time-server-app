package org.publicntp.gnssreader.repository.time;

import android.content.Context;

import org.publicntp.gnssreader.helper.DateFormatter;

import java.util.Date;

/**
 * Created by zac on 2/7/18.
 */

public class TimeStorage {
    private static Date satelliteDate;
    private static Date acquiredDate;

    public static void setMillis(Long millis) {
        long currentTime = System.currentTimeMillis();
        satelliteDate = new Date(millis);
        acquiredDate = new Date(currentTime);
    }

    public static void setDate(Date nmeaDate) {
        acquiredDate = new Date();
        TimeStorage.satelliteDate = nmeaDate;
    }

    public static long getDateDifference() throws Exception {
        if (satelliteDate == null) throw new Exception("--");
        return Math.abs(satelliteDate.getTime() - acquiredDate.getTime()); // This is the difference between system and satellite time
    }

    public static long getAdjustedMillis() {
        if (satelliteDate == null) return System.currentTimeMillis();

        return satelliteDate.getTime() + (new Date().getTime() - acquiredDate.getTime());
    }

    public static Date getAdjustedDate() throws Exception {
        if (satelliteDate == null) {
            throw new Exception("No satellite date received.");
        }

        return new Date(getAdjustedMillis());
    }

    public static String getDefaultDateString() {
        return "--:--:--.--";
    }

    public static String getAdjustedDateString(Context context) {
        try {
            return DateFormatter.preferredTimeString(context, getAdjustedDate());
        } catch (Exception e) {
            return getDefaultDateString();
        }
    }

    public static long getTime() {
        return getAdjustedMillis();
    }
}
