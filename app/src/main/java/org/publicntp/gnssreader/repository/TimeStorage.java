package org.publicntp.gnssreader.repository;

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
        if(satelliteDate == null) throw new Exception("One moment...");
        return Math.abs(satelliteDate.getTime() - acquiredDate.getTime()); // This is the difference between system and satellite time
    }

    public static long getAdjustedMillis() {
        if(satelliteDate == null) return System.currentTimeMillis();

        return satelliteDate.getTime() + (new Date().getTime() - acquiredDate.getTime());
    }

    public static Date getAdjustedDate() {
        if(satelliteDate == null) return new Date();

        return new Date(getAdjustedMillis());
    }

    public static String getAdjustedDateString(Context context) {
        return DateFormatter.preferredTimeString(context, getAdjustedDate());
    }

    public static long getTime() {
        return getAdjustedMillis();
    }
}
