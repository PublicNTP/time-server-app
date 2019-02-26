package org.publicntp.timeserver.model;

import android.location.GnssStatus;

/**
 * Created by zac on 2/14/18.
 */

public class SatelliteModel {
    public final int constellationType;
    public final int svn;
    public final float Cn0DbHz;
    public final float elevationDegrees;
    public final float azimuthDegrees;
    public final boolean ephemerisData;
    public final boolean almanacData;
    public final boolean usedInFix;

    public SatelliteModel(int i, GnssStatus status) {
        svn = status.getSvid(i);
        constellationType = status.getConstellationType(i);
        Cn0DbHz = status.getCn0DbHz(i);
        elevationDegrees = status.getElevationDegrees(i);
        azimuthDegrees = status.getAzimuthDegrees(i);
        ephemerisData = status.hasEphemerisData(i);
        almanacData = status.hasAlmanacData(i);
        usedInFix = status.usedInFix(i);
    }

    public String constellationName() {
        switch (constellationType) {
            case GnssStatus.CONSTELLATION_BEIDOU:
                return "BEIDOU";
            case GnssStatus.CONSTELLATION_GALILEO:
                return "GALILEO";
            case GnssStatus.CONSTELLATION_GLONASS:
                return "GLONASS";
            case GnssStatus.CONSTELLATION_GPS:
                return "GPS";
            case GnssStatus.CONSTELLATION_QZSS:
                return "QZSS";
            case GnssStatus.CONSTELLATION_SBAS:
                return "SBAS";
            case GnssStatus.CONSTELLATION_UNKNOWN:
            default:
                return "UNKNOWN";
        }
    }
}
