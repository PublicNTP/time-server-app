package org.publicntp.gnssreader.repository;

import android.location.GnssStatus;
import android.location.Location;

import org.publicntp.gnssreader.model.SatelliteModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zac on 2/7/18.
 */

public class LocationStorage {
    private static Location location;

    private static List<SatelliteModel> satelliteList = new ArrayList<>();

    public static void setLocation(Location location) {
        LocationStorage.location = location;
    }

    public static double getLatitude() {
        return location.getLatitude();
    }

    public static double getLongitude() {
        return location.getLongitude();
    }

    public static float getAccuracy() {
        return location.getAccuracy();
    }

    public static void setSatelliteList(GnssStatus status) {
        satelliteList.clear();
        for(int i=0; i<status.getSatelliteCount(); i++) {
            satelliteList.add(new SatelliteModel(i, status));
        }
    }

    public static List<SatelliteModel> getSatelliteList() {
        return new ArrayList<>(satelliteList);
    }

    public static boolean isPopulated() {
        return location != null;
    }
}
