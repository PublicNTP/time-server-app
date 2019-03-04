package org.app.timeserver.repository.location;

import android.location.GnssStatus;
import android.location.Location;

import org.app.timeserver.model.SatelliteModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zac on 2/7/18.
 */

public class LocationStorage {
    private static Location location;
    private static int selectedSVN;
    private static int selectedConstellation;
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

    public static List<SatelliteModel> sortedSatellites() {
        List<SatelliteModel> satellites = getSatelliteList();
        satellites.sort((s1, s2) -> s1.svn > s2.svn ? 1 : -1);
        return satellites;
    }

    public static List<SatelliteModel> usedSatellites() {
        List<SatelliteModel> satellites = sortedSatellites();
        return satellites.stream().filter(s -> s.usedInFix).collect(Collectors.toList());
    }

    public static boolean isPopulated() {
        return location != null;
    }

    public static void setSelectedSatellite(SatelliteModel satelliteModel) {
        LocationStorage.selectedSVN = satelliteModel.svn;
        LocationStorage.selectedConstellation = satelliteModel.constellationType;
    }

    public static boolean isSelected(SatelliteModel satelliteModel) {
        return satelliteModel.svn == selectedSVN && satelliteModel.constellationType == selectedConstellation;
    }

    public static void clearSelected() {
        selectedSVN = -9999;
        selectedConstellation = -9999;
    }
}
