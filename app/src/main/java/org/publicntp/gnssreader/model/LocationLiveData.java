package org.publicntp.gnssreader.model;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import javax.inject.Inject;

import timber.log.Timber;

public class LocationLiveData extends LiveData<Location> implements LocationListener {

    private LocationManager locationManager;

    @Inject
    public LocationLiveData(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    @SuppressLint("MissingPermission")
    public void onInactive() {
        Timber.i("live location: onInactive");
        locationManager.removeUpdates(this);
    }

    @SuppressLint("MissingPermission")
    public void refreshLocation() {
        Timber.i("live location refreshLocation");
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
    }

    @Override
    public void onLocationChanged(Location location) {
        Timber.i("live location onLocationChanged : %s", location.toString());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Timber.i("live location onStatusChanged : %s", provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Timber.i("live location onProviderEnabled : %s", provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Timber.i("live location onProviderDisabled : %s", provider);
    }
}
