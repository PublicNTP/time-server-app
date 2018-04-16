package org.publicntp.gnssreader.repository;

import android.annotation.SuppressLint;

/**
 * Created by zac on 2/7/18.
 */

public class LocationStorageConsumer {
    public LocationStorageConsumer() {
    }

    public String getLatitudeDirection(double latitude) {
        return latitude > 0 ? "N" : "S";
    }
    public String getLatitudeDirection() {
        return getLatitudeDirection(LocationStorage.getLatitude());
    }

    public String getLongitudeDirection(double longitude) {
        return longitude > 0 ? "E" : "W";
    }
    public String getLongitudeDirection() {
        return getLongitudeDirection(LocationStorage.getLongitude());
    }

    @SuppressLint("DefaultLocale")
    public String getStringLatitude() {
        if (LocationStorage.isPopulated()) {
            double latitude = LocationStorage.getLatitude();
            return String.format("%.4f° %s", (float) latitude, getLatitudeDirection(latitude));
        } else {
            return "--°";
        }
    }

    @SuppressLint("DefaultLocale")
    public String getStringLongitude() {
        if(LocationStorage.isPopulated()) {
            double longitude = LocationStorage.getLongitude();
            return String.format("%.4f° %s", (float) longitude, getLongitudeDirection(longitude));
        } else {
            return "--°";
        }
    }

    @SuppressLint("DefaultLocale")
    public String getHumanReadableLocation() {
        return String.format("%.4f, %.4f", LocationStorage.getLatitude(), LocationStorage.getLongitude());
    }

    @SuppressLint("DefaultLocale")
    public String getSharableLocation() {
        return String.format("geo:%.4f,%.4f", LocationStorage.getLatitude(), LocationStorage.getLongitude());
    }

    @SuppressLint("DefaultLocale")
    public String getStringError() {
        if(LocationStorage.isPopulated()) {
            float error = LocationStorage.getAccuracy();
            return String.format("±%.2f", error);
        } else {
            return "±--";
        }
    }
}
