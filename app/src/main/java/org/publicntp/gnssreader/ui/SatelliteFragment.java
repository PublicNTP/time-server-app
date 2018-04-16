package org.publicntp.gnssreader.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.helper.TimeMillis;
import org.publicntp.gnssreader.model.SatelliteModel;
import org.publicntp.gnssreader.repository.LocationStorage;
import org.publicntp.gnssreader.ui.custom.SatelliteDetailFragment;
import org.publicntp.gnssreader.ui.custom.SatelliteRadialChart;
import org.publicntp.gnssreader.ui.custom.SignalGraphFragment;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SatelliteFragment extends Fragment implements SensorEventListener, SignalGraphFragment.OnSatelliteSelectedListener, SatelliteDetailFragment.OnDetailsClosedListener {
    @BindView(R.id.satellite_radial_chart) SatelliteRadialChart radialChart;
    @BindView(R.id.satellite_details_container) FrameLayout detailContainer;

    @BindView(R.id.satellites_in_view) TextView satellitesInView;
    @BindView(R.id.satellites_in_use) TextView satellitesInUse;
    @BindView(R.id.satellites_in_view_label) TextView satellitesInViewLabel;
    @BindView(R.id.satellites_in_use_label) TextView satellitesInUseLabel;

    private Timer refreshTimer = new Timer();
    private Handler handler = new Handler();
    private final int REFRESH_DELAY = (int) (TimeMillis.SECOND * 3);

    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor compass;

    private SignalGraphFragment signalGraphFragment;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_satellite, container, false);
        ButterKnife.bind(this, view);

        distributePositionData();
        registerSensors(view.getContext());

        signalGraphFragment = SignalGraphFragment.newInstance(LocationStorage.usedSatellites(), this);
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.satellite_details_container, signalGraphFragment).commit();

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();

        refreshTimer = new Timer();
        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    distributePositionData();

                    //prevent incorrect overlap
                    satellitesInUse.bringToFront();
                    satellitesInView.bringToFront();
                    satellitesInUseLabel.bringToFront();
                    satellitesInViewLabel.bringToFront();
                });
            }
        }, 0, REFRESH_DELAY);

        mSensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();

        refreshTimer.cancel();
        mSensorManager.unregisterListener(this);
    }

    private void registerSensors(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        compass = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    private void distributePositionData() {
        List<SatelliteModel> satellites = LocationStorage.sortedSatellites();
        satellites.sort((s1, s2) -> s1.svn > s2.svn ? 1 : -1);

        radialChart.setSatelliteModels(satellites);

        List<SatelliteModel> usedSatellites = LocationStorage.usedSatellites();
        if(signalGraphFragment == null) {
        } else {
            signalGraphFragment.setSatelliteModels(usedSatellites);
        }

        long in_view = satellites.size();
        satellitesInView.setText(in_view+"");
        long in_use = satellites.stream().filter(s -> s.usedInFix).count();
        satellitesInUse.setText(in_use+"");
    }


    public static SatelliteFragment newInstance() {
        return new SatelliteFragment();
    }

    float[] mGravity;
    float[] mGeomagnetic;
    Integer accuracy;

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                mGeomagnetic = event.values;
                break;
            case Sensor.TYPE_ACCELEROMETER:
                mGravity = event.values;
                break;
            default:
                return;
        }
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                float degrees = (float) Math.toDegrees(orientation[0]);
                //TODO CHECK BACK WITH THIS CONSTRAINT
                //if (accuracy != null && accuracy > SensorManager.SENSOR_STATUS_ACCURACY_LOW) {
                radialChart.setCompassReading(degrees);
                //}
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        switch (sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                this.accuracy = accuracy;
                break;
        }
    }

    @Override
    public void onSatelliteSelected(SatelliteModel satelliteModel) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.satellite_details_container, SatelliteDetailFragment.newInstance(satelliteModel, this)).commit();
    }

    @Override
    public void onDetailClose() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.satellite_details_container, signalGraphFragment).commit();
        signalGraphFragment.setSatelliteModels(LocationStorage.usedSatellites());
    }
}
