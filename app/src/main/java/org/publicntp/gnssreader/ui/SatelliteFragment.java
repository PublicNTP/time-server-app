package org.publicntp.gnssreader.ui;

import android.arch.lifecycle.ViewModelProviders;
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
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.model.SatelliteModel;
import org.publicntp.gnssreader.repository.LocationStorage;
import org.publicntp.gnssreader.ui.chart.radialchart.SatelliteRadialChart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.formatter.AxisValueFormatter;
import lecho.lib.hellocharts.formatter.ColumnChartValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

public class SatelliteFragment extends Fragment implements SensorEventListener {
    @BindView(R.id.satellite_radial_chart) SatelliteRadialChart radialChart;
    @BindView(R.id.satellite_bar_chart) ColumnChartView barChart;

    private Timer refreshTimer = new Timer();
    private Handler handler = new Handler();
    private final int REFRESH_DELAY = 1000;

    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor compass;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_satellite, container, false);
        ButterKnife.bind(this, view);

        setupBarChart();
        distributePositionData();
        registerSensors(view.getContext());

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
                handler.post(() -> distributePositionData());
            }
        }, REFRESH_DELAY, REFRESH_DELAY);

        mSensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();

        refreshTimer.cancel();
        mSensorManager.unregisterListener(this);
    }

    private void setupBarChart() {
        barChart.setInteractive(true);
    }

    private void registerSensors(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        compass = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    private void distributePositionData() {
        List<SatelliteModel> satellites = LocationStorage.getSatelliteList();
        satellites.sort((s1, s2) -> s1.prn > s2.prn ? 1 : -1);
        radialChart.setSatelliteModels(satellites);
        setBarChartData(satellites);
    }

    private void setBarChartData(List<SatelliteModel> satellites) {
        List<Column> satelliteSignalValues = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();
        satellites.forEach(s -> {
            SubcolumnValue subcolumnValue = new SubcolumnValue(s.Cn0DbHz).setColor(GreyLevelHelper.asColor(getContext(), s.Cn0DbHz));
            List<SubcolumnValue> subcolumnValues = new ArrayList<>();
            subcolumnValues.add(subcolumnValue);

            Column column = new Column().setValues(subcolumnValues);
            AxisValue axisValue = new AxisValue(s.prn).setLabel(s.prn + "");

            satelliteSignalValues.add(column);
            axisValues.add(axisValue);
        });

        ColumnChartData columnChartData = new ColumnChartData(satelliteSignalValues);

        Axis xAxis = new Axis();
        xAxis.setValues(axisValues);
        xAxis.setFormatter(new AxisValueFormatter() {
            @Override
            public int formatValueForManualAxis(char[] formattedValue, AxisValue axisValue) {
                int i = formattedValue.length - 1;
                char[] label = axisValue.getLabelAsChars();
                for(char c: label) {
                    if(i < 0) break;
                    formattedValue[i] = c;
                    i--;
                }
                return label.length;
            }

            @Override
            public int formatValueForAutoGeneratedAxis(char[] formattedValue, float value, int autoDecimalDigits) {
                return 0;
            }
        });
        columnChartData.setAxisXBottom(xAxis);
        barChart.setColumnChartData(columnChartData);
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
                if (accuracy != null && accuracy > SensorManager.SENSOR_STATUS_ACCURACY_LOW) {
                    radialChart.setCompassReading(degrees);
                }
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
}
