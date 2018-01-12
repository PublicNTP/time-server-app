package org.publicntp.gnssreader.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.publicntp.gnssreader.listener.LocationListener;
import org.publicntp.gnssreader.listener.NmeaMsgListener;

import java.util.concurrent.TimeUnit;

import org.publicntp.gnssreader.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    protected static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private static final long LOCATION_RATE_GPS_MS = TimeUnit.SECONDS.toMillis(10L);
    protected TextView mDisplayWindow;
    protected LocationManager mLocMgr;
    protected LocationListener mLocationListener;
    NmeaMsgListener mListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mLocMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mDisplayWindow = findViewById(R.id.screenOutputId);
        mLocationListener = new LocationListener(this);
        mListener = new NmeaMsgListener(this);


        // Can we get ACCESS_FINE_LOCATION permissions from system?
        if (ContextCompat.checkSelfPermission(this,
                ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Do we need an explanation for why we need it?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    ACCESS_FINE_LOCATION)) {
                mDisplayWindow.setText("Don't have permission, need to ask nicely");
            } else {
                mDisplayWindow.setText("Don't have permission, but don't need to ask");

                ActivityCompat.requestPermissions(this,
                        new String[]{ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_FINE_LOCATION);
            }

        } else {
            // Is GPS provider enabled?
            if (mLocMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // Need to make a location request to get things flowing
                mLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        LOCATION_RATE_GPS_MS,
                        0.0f /* minDistance */,
                        mLocationListener);

                if (mLocMgr.addNmeaListener(mListener)) {
                    mDisplayWindow.setText("Had permissions, provider enabled, added listener");
                } else {
                    mDisplayWindow.setText("Could not add nmea listener");
                }
            } else {
                mDisplayWindow.setText("GPS provider not enabled");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_FINE_LOCATION: {
                mDisplayWindow.setText("Got response back for permissions check");

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Now add listener
                    LocationManager locMgr = (LocationManager)
                            getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

                    locMgr.addNmeaListener(mListener);
                    mDisplayWindow.setText("Added listener successfully!");
                } else {
                    mDisplayWindow.setText("Permission denied!");
                }

                break;
            }
            default: {
                mDisplayWindow.setText("Unknown permissions type");
                break;
            }
        }
    }

    public void displayMsg(String msg) {
        mDisplayWindow.setText(msg);
    }
}
