package org.publicntp.timeserver.repository.location.converters;

import android.annotation.SuppressLint;

public class LatLongConverter extends CoordinateConverter {
    public String getLatitudeDirection(double latitude) {
        return latitude > 0 ? "N" : "S";
    }

    public String getLongitudeDirection(double longitude) {
        return longitude > 0 ? "E" : "W";
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String getString(double lat, double lon) {
        return String.format("%.4f° %s\n%.4f° %s", (float) lat, getLatitudeDirection(lat), (float) lon, getLongitudeDirection(lon));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String clipboardString(double lat, double lon) {
        return String.format("%.4f, %.4f", lat, lon);
    }
}
