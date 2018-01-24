package org.publicntp.gnssreader.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GnssClock;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.publicntp.gnssreader.model.TimeAndLocation;

import java.util.Collection;

import timber.log.Timber;

public class GNSSMessageService extends Service {

    private TimeAndLocation timeAndLocation;
    private LocationManager locationManager;
    private Handler handler;

    private static final String TAG = "GNSSMessageService";
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    private class LocationListenerImpl implements LocationListener {

        Location location;

        public LocationListenerImpl(String provider) {
            //Log.e(TAG, "LocationListener " + provider);
            location = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
//            Log.e(TAG, "onLocationChanged: " + location);
            location.set(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
//            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
//            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
//            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] locationListeners = new LocationListener[]{
            new LocationListenerImpl(LocationManager.GPS_PROVIDER),
            new LocationListenerImpl(LocationManager.NETWORK_PROVIDER)
    };

    @Nullable
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
//        Log.e(TAG, "onStartCommand");

        return START_STICKY;
    }

    @Override
    public void onCreate() {

//        Log.e(TAG, "onCreate");
//        initializeLocationManager();
//
//        try {
//            locationManager.requestLocationUpdates(
//                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//                    locationListeners[1]);
//
//        } catch (java.lang.SecurityException ex) {
//            Log.i(TAG, "fail to request location update, ignore", ex);
//
//        } catch (IllegalArgumentException ex) {
//            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
//        }
//
//        try {
//            locationManager.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//                    locationListeners[0]);
//
//        } catch (java.lang.SecurityException ex) {
//            Log.i(TAG, "fail to request location update, ignore", ex);
//
//        } catch (IllegalArgumentException ex) {
//            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        Log.e(TAG, "onDestroy");

        if (locationManager != null) {
            for (int i = 0; i < locationListeners.length; i++) {
                try {
                    locationManager.removeUpdates(locationListeners[i]);

                } catch (Exception ex) {
//                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    public TimeAndLocation getTimeAndLocation() {
        return timeAndLocation;
    }

    private void initializeLocationManager() {
        Timber.e("initializeLocationManager");
//        if (locationManager == null) {
//            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                locationManager.registerGnssMeasurementsCallback(new GnssMeasurementsEvent.Callback() {
//                    @Override
//                    public void onGnssMeasurementsReceived(GnssMeasurementsEvent eventArgs) {
//                        final Collection<GnssMeasurement> measurements = eventArgs.getMeasurements();
//
//                        handler.post(() -> {
//                            timeAndLocation = TimeAndLocation.newInstance(measurements);
//                            // TODO: Add logging here
//
//                            GpsTime gpsTime = new GpsTime(gnssClock);
//                            Date gpsDate = gpsTime.getDate();
//                            Date deviceDate = new Date();
//                            mDeviceTime.setText(GpsTime.formatMilliSeconds(deviceDate));
//                            mGpsTime.setText(GpsTime.formatMilliSeconds(gpsDate));
//                            mTimeDifference.setText(String.format("%d millis", gpsDate.getTime() - deviceDate.getTime()));
//                        });
//                    }
//                });
//            }
//        }
    }
}
