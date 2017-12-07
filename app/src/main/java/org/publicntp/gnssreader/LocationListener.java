package org.publicntp.gnssreader;

import android.location.Location;
import android.os.Bundle;

/**
 * Created by TerryPC on 11/12/2017.
 */

public class LocationListener implements android.location.LocationListener {
    MainActivity mActivity;

    public LocationListener(MainActivity activity) {
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


}
