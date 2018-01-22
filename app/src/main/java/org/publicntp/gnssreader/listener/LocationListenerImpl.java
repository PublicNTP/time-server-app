package org.publicntp.gnssreader.listener;

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.OnNmeaMessageListener;
import android.os.Bundle;

import org.publicntp.gnssreader.ui.MainActivity;


public class LocationListenerImpl implements LocationListener, GpsStatus.Listener, GpsStatus.NmeaListener {

    MainActivity mActivity;

    public LocationListenerImpl(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onProviderEnabled(String s) {
        //mActivity.displayMsg( "Provider enabled: " + s);
    }

    @Override
    public void onProviderDisabled(String s) {
        //mActivity.displayMsg( "Provider disabled: " + s );
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        //mActivity.displayMsg( "Status changed: " + s);
    }

    @Override
    public void onLocationChanged(Location loc) {
        //mActivity.displayMsg("Location changed");
    }

    @Override
    public void onGpsStatusChanged(int event) {

    }

    @Override
    public void onNmeaReceived(long timestamp, String nmeaSentence) {



    }
}
