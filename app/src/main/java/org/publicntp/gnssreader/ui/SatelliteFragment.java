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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.model.SatelliteModel;
import org.publicntp.gnssreader.repository.LocationStorage;
import org.publicntp.gnssreader.ui.chart.radialchart.SatelliteRadialChart;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SatelliteFragment extends Fragment implements SensorEventListener {

    private SatelliteViewModel mSatelliteViewModel;

    //@BindView(R.id.satellite_position_radial_chart) SatellitePositionChart mPositionChart;
    @BindView(R.id.satellite_radial_chart)
    SatelliteRadialChart radialChart;
    @BindView(R.id.satellite_signal_bar_chart)
    BarChart mSignalChart;

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

        mSatelliteViewModel = ViewModelProviders.of(this).get(SatelliteViewModel.class);

        setupSatelliteSignalView();
        distributePositionData();

        mSensorManager = (SensorManager) view.getContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        compass = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);

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

    private void distributePositionData() {
        List<SatelliteModel> satellites = LocationStorage.getSatelliteList();
        satellites.sort((s1, s2) -> s1.prn > s2.prn ? 1 : -1);
        radialChart.setSatelliteModels(satellites);
        setSignalData(satellites);
    }

    private void setupSatelliteSignalView() {

        mSignalChart.setDrawBarShadow(false);
        mSignalChart.setDrawValueAboveBar(true);
        mSignalChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mSignalChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mSignalChart.setPinchZoom(false);

        mSignalChart.setDrawGridBackground(false);
        mSignalChart.setDrawValueAboveBar(false);
        //mSignalChart.setDrawYLabels(false);


        XAxis xAxisSig = mSignalChart.getXAxis();
        xAxisSig.setEnabled(true);
        xAxisSig.setPosition(XAxis.XAxisPosition.TOP);
        xAxisSig.setDrawGridLines(false);
        xAxisSig.setGranularity(1f);
        xAxisSig.setLabelRotationAngle(30f);
//        xAxisSig.setLabelCount(7);

        YAxis leftAxis = mSignalChart.getAxisLeft();
        leftAxis.setEnabled(false);
//        leftAxis.setLabelCount(8, false);
//        leftAxis.setValueFormatter(custom);
//        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
//        leftAxis.setSpaceTop(15f);
//        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = mSignalChart.getAxisRight();
        rightAxis.setEnabled(false);
//        rightAxis.setDrawGridLines(false);
//        rightAxis.setLabelCount(8, false);
//        rightAxis.setValueFormatter(custom);
//        rightAxis.setSpaceTop(15f);
//        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend legend = mSignalChart.getLegend();
        legend.setEnabled(false);
//        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
//        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        legend.setDrawInside(false);
//        legend.setForm(Legend.LegendForm.SQUARE);
//        legend.setFormSize(9f);
//        legend.setTextSize(11f);
//        legend.setXEntrySpace(4f);
        // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });

//        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
//        mv.setChartView(mSignalChart); // For bounds control
//        mSignalChart.setMarker(mv); // Set the marker to the chart
    }


    private void setSignalData(List<SatelliteModel> satellites) {
        int i = 0;
        List<BarEntry> satelliteBars = new ArrayList<>();
        for (SatelliteModel satellite : satellites) {
            satelliteBars.add(new BarEntry(i++, satellite.Cn0DbHz));
        }
        BarDataSet barDataSet = new BarDataSet(satelliteBars, "BarDataSet");
        String[] labelArray = satellites.stream().map(s -> "" + s.prn).collect(Collectors.toList()).toArray(new String[satellites.size()]);
        barDataSet.setStackLabels(labelArray);

        BarData signalData = new BarData(barDataSet);
        signalData.setBarWidth(.8f);
        signalData.setDrawValues(false);

        mSignalChart.setData(signalData);
        mSignalChart.setFitBars(true);
        mSignalChart.getXAxis().setLabelCount(satelliteBars.size(), true);
        mSignalChart.getXAxis().setValueFormatter((value, axis) -> {
            try {
                String label = "" + satellites.get((int) value).prn;
                int j = 0;
                j++;
                return label;
            } catch (Exception e) {
                return "";
            }
        });
        mSignalChart.invalidate();
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
                if(accuracy != null && accuracy > SensorManager.SENSOR_STATUS_ACCURACY_LOW) {
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
