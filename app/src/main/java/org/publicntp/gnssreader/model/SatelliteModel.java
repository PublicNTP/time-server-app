package org.publicntp.gnssreader.model;

import android.location.GnssStatus;

/**
 * Created by zac on 2/14/18.
 */

public class SatelliteModel {
    public final int constellationType;
    public final int prn;
    public final float Cn0DbHz;
    public final float elevationDegrees;
    public final float azimuthDegrees;
    public final boolean ephemerisData;
    public final boolean almanacData;
    public final boolean usedInFix;

    public SatelliteModel(int i, GnssStatus status) {
        prn = status.getSvid(i);
        constellationType = status.getConstellationType(i);
        Cn0DbHz = status.getCn0DbHz(i);
        elevationDegrees = status.getElevationDegrees(i);
        azimuthDegrees = status.getAzimuthDegrees(i);
        ephemerisData = status.hasEphemerisData(i);
        almanacData = status.hasAlmanacData(i);
        usedInFix = status.usedInFix(i);
    }
}
