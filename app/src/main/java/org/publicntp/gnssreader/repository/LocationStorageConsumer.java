package org.publicntp.gnssreader.repository;

import android.location.Location;

/**
 * Created by zac on 2/7/18.
 */

public class LocationStorageConsumer {
    public LocationStorageConsumer() {}

    public String getLatitudeDirection(double latitude) {
        return latitude > 0 ? "N" : "S";
    }

    public String getLongitudeDirection(double longitude) {
        return longitude > 0 ? "E" : "W";
    }

    public String getStringLatitude() {
        try{
            double latitude = LocationStorage.getLatitude();
            return String.format("%.4f° %s", (float) latitude, getLatitudeDirection(latitude));
        } catch(NullPointerException e) {
            return "";
        }
    }

    public String getStringLongitude() {
        try{
            double longitude = LocationStorage.getLongitude();
            return String.format("%.4f° %s", (float) longitude, getLongitudeDirection(longitude));
        } catch(NullPointerException e) {
            return "One moment...";
        }
    }

    public String getStringError() {
        try{
            float error = LocationStorage.getError();
            return String.format("±%.2f", error);
        } catch(NullPointerException e) {
            return "±0";
        }
    }
}
