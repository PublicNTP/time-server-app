package org.publicntp.gnssreader.repository;

import android.location.Location;

/**
 * Created by zac on 2/7/18.
 */

public class LocationStorage {
    private static Location location;

    public static void setLocation(Location location) {
        LocationStorage.location = location;
    }

    public static double getLatitude() throws NullPointerException {
        return location.getLatitude();
    }

    public static double getLongitude() throws NullPointerException {
        return location.getLongitude();
    }

    public static float getError() throws NullPointerException {
        return location.getAccuracy();
    }
}
