package org.publicntp.gnssreader.listener;

import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.os.Bundle;

import org.publicntp.gnssreader.repository.LocationStorage;
import org.publicntp.gnssreader.ui.MainActivity;


public class LocationListenerImpl extends GnssStatus.Callback implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {
        LocationStorage.setLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onSatelliteStatusChanged(GnssStatus status) {
        LocationStorage.setSatelliteList(status);
    }
}
