package app.timeserver.repository.location.converters;

import android.annotation.SuppressLint;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.coords.UTMCoord;

public class UTMConverter extends CoordinateConverter {
    public String getShortHemisphere(String longHemisphere) {
        return AVKey.NORTH.equals(longHemisphere) ? "N" : "S"; //Taken from UTMCoord.toString
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String getString(double lat, double lon) {
        Angle latAngle = Angle.fromDegreesLatitude(lat);
        Angle lonAngle = Angle.fromDegreesLongitude(lon);

        UTMCoord utmCoord = UTMCoord.fromLatLon(latAngle, lonAngle);
        return String.format("%s%s\n%.2fE\n%.2fN", utmCoord.getZone(), getShortHemisphere(utmCoord.getHemisphere()), utmCoord.getNorthing(), utmCoord.getEasting());
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String clipboardString(double lat, double lon) {
        return getString(lat, lon).replace('\n', ' ');
    }
}
