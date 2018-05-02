package org.publicntp.gnssreader.repository.location.converters;

import android.annotation.SuppressLint;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.coords.MGRSCoord;

public class MGRSConverter extends CoordinateConverter {
    @SuppressLint("DefaultLocale")
    @Override
    public String getString(double lat, double lon) {
        Angle latAngle = Angle.fromDegreesLatitude(lat);
        Angle lonAngle = Angle.fromDegreesLongitude(lon);

        MGRSCoord mgrsCoord = MGRSCoord.fromLatLon(latAngle, lonAngle);
        return mgrsCoord.toString().replace(' ', '\n');
    }
}
