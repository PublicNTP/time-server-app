package org.publicntp.gnssreader.ui;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.listener.LocationListenerImpl;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private static final long LOCATION_RATE_GPS_MS = TimeUnit.SECONDS.toMillis(10);
    LocationManager mLocMgr;
    LocationListener mLocationListener;
    private Handler handler = new Handler();
    private Timer updateTimer;

//    @BindView(R.id.permissions_details) TextView mDisplayWindow;
//    @BindView(R.id.device_time) TextView mDeviceTime;
//    @BindView(R.id.gps_time) TextView mGpsTime;
//    @BindView(R.id.nmea_time) TextView mNMEATime;
//    @BindView(R.id.time_difference) TextView mTimeDifference;

    @Override
    @SuppressLint("MissingPermission")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initializeBottomNavigation();

        //mLocationListener = new LocationListenerImpl(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_FINE_LOCATION:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Timber.i("response for permissions check contains: granted");
                } else {
                    Timber.i("response for permissions check contains: denied");
                }

                break;

            default:
                Timber.w("unknown permissions type");
                break;
        }
    }

    @Override
    public void onPause() {
        //updateTimer.cancel();
        super.onPause();
    }

    @Override
    public void onResume() {
//        initializeTimeUpdates();
        super.onResume();
    }

    private void initializeBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener( item -> {
            Fragment selectedFragment;
            switch (item.getItemId()) {
                case R.id.action_info:
                    selectedFragment = InfoFragment.newInstance();
                    break;
                case R.id.action_serv:
                    selectedFragment = ServerFragment.newInstance();
                    break;
                case R.id.action_satl:
                    selectedFragment = SatelliteFragment.newInstance();
                    break;
                default:
                    selectedFragment = TimeFragment.newInstance();
                    break;
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, selectedFragment);
            transaction.commit();
            return true;
        });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, TimeFragment.newInstance());
        transaction.commit();

        //Used to select an item programmatically
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
    }












//    public void displayMsg(String msg) {
//        mDisplayWindow.setText(msg);
//    }

//    @SuppressLint("MissingPermission")
//    public void initializeTimeUpdates() {
//        Permission fine_location_permission = Permission.FINE_LOCATION;
//        boolean permission_granted_this_run = false;
//        while (!fine_location_permission.isGranted()) {
//            permission_granted_this_run = true;
//            fine_location_permission = PermissionsHelper.requestPermission(this, Permission.FINE_LOCATION);
//        }
//        if (permission_granted_this_run) {
//            Toast.makeText(this, "Permission Granted.", Toast.LENGTH_SHORT).show();
//        }
//
//        mLocMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (fine_location_permission.isGranted()) {
//            mLocMgr.registerGnssMeasurementsCallback(new GnssMeasurementsEvent.Callback() {
//                @Override
//                public void onGnssMeasurementsReceived(GnssMeasurementsEvent eventArgs) {
//                    final GnssClock gnssClock = eventArgs.getClock();
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            GpsTime gpsTime = new GpsTime(gnssClock);
//                            Date gpsDate = gpsTime.getDate();
//                            Date deviceDate = new Date();
//                            mDeviceTime.setText(GpsTime.formatMilliSeconds(deviceDate));
//                            mGpsTime.setText(GpsTime.formatMilliSeconds(gpsDate));
//                            mTimeDifference.setText(String.format("%d millis", gpsDate.getTime() - deviceDate.getTime()));
//                        }
//                    });
//                }
//            });
//        }
//
//        // Is GPS provider enabled?
//        if (mLocMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            if (Permission.FINE_LOCATION.isGranted()) {
//                updateTimer = new Timer();
//                updateTimer.scheduleAtFixedRate(new TimerTask() {
//                    @Override
//                    public void run() {
//                        mLocMgr.requestSingleUpdate(LocationManager.GPS_PROVIDER, mLocationListener, MainActivity.this.getMainLooper());
//                    }
//                }, 0, LOCATION_RATE_GPS_MS);
//
//                boolean added_nmea_success = mLocMgr.addNmeaListener(new OnNmeaMessageListener() {
//                    @Override
//                    public void onNmeaMessage(String s, long l) {
//                        mNMEATime.setText(GpsTime.formatMilliSeconds(new Date(l)));
//                    }
//                });
//
//                if (!added_nmea_success) {
//                    mNMEATime.setText("NMEA Unavailable");
//                }
//            }
//        } else {
//            mDisplayWindow.setText("GPS provider not enabled");
//        }
//    }

//    public static String gnssClockSummary(GnssClock gnssClock) {
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(timeForNanos("Full Bias Nanos", gnssClock.getFullBiasNanos()));
//        stringBuilder.append(timeForNanos("Time Nanos", gnssClock.getTimeNanos()));
//        stringBuilder.append(timeForNanos("Bias Nanos", (long) gnssClock.getBiasNanos()));
//        return stringBuilder.toString();
//    }

//    public static String timeForNanos(String label, long nanos) {
//        long years = TimeUnit.NANOSECONDS.toDays(nanos) / 365;
//        long days = TimeUnit.NANOSECONDS.toDays(nanos) - years * 365;
//        long hours = TimeUnit.NANOSECONDS.toHours(nanos) - days * 24;
//        long minutes = TimeUnit.NANOSECONDS.toMinutes(nanos) - hours * 60;
//        long seconds = TimeUnit.NANOSECONDS.toSeconds(nanos) - minutes * 60;
//
//        return String.format("%s: %d years, %d days, %d hours, %d minutes, %d seconds\n", label, years, days, hours, minutes, seconds);
//    }
}
