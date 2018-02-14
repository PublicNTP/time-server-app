package org.publicntp.gnssreader.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.model.SatelliteModel;
import org.publicntp.gnssreader.repository.LocationStorage;
import org.publicntp.gnssreader.ui.chart.ISatelliteDataSet;
import org.publicntp.gnssreader.ui.chart.SatelliteData;
import org.publicntp.gnssreader.ui.chart.SatelliteDataSet;
import org.publicntp.gnssreader.ui.chart.SatelliteEntry;
import org.publicntp.gnssreader.ui.chart.SatellitePointValueFormatter;
import org.publicntp.gnssreader.ui.chart.SatellitePositionChart;
import org.publicntp.gnssreader.ui.chart.SatelliteSignalChart;


import android.graphics.Color;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class SatelliteFragment extends Fragment {

    private SatelliteViewModel mSatelliteViewModel;

    private SatellitePositionChart mPositionChart;
    private SatelliteSignalChart mSignalChart;

    private Timer refreshTimer = new Timer();
    private final int REFRESH_DELAY = 500;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_satellite, container, false);
        mPositionChart = view.findViewById(R.id.satellite_position_radial_chart);
        mSignalChart = view.findViewById(R.id.satellite_signal_bar_chart);

        mSatelliteViewModel = ViewModelProviders.of(this).get(SatelliteViewModel.class);

        setupSatellitePositionView();
        setupSatelliteSignalView();
        setPositionData();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    /*
    * The timer here vvv is only until we figure out how to data bind this.
    * */

    @Override
    public void onResume() {
        super.onResume();

        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                setPositionData();
            }
        }, REFRESH_DELAY, REFRESH_DELAY);
    }

    @Override
    public void onPause() {
        super.onPause();

        refreshTimer.cancel();
    }

    private void setupSatellitePositionView() {

        mPositionChart.setBackgroundColor(Color.WHITE);
        mPositionChart.getDescription().setEnabled(false);
        mPositionChart.setWebColorInner(Color.LTGRAY);
        mPositionChart.setWebColor(Color.LTGRAY);
        mPositionChart.setWebLineWidthInner(1f);
        mPositionChart.setWebLineWidth(1f);
        mPositionChart.setWebAlpha(100);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
//        MarkerView mv = new SatelliteMarkerView(this.getContext(), R.layout.markerview_satellite);
//        mv.setChartView(mChart); // For bounds control
//        mChart.setMarker(mv); // Set the marker to the chart



//        mChart.animateXY(
//                1400, 1400,
//                Easing.EasingOption.EaseInOutQuad,
//                Easing.EasingOption.EaseInOutQuad);

        XAxis xAxis = mPositionChart.getXAxis();
        xAxis.setDrawLabels(false);
//        xAxis.setValueFormatter(new IAxisValueFormatter() {
//
//            private String[] mActivities = new String[] {
//                    "0", "45", "90", "135", "180", "225", "270", "315"};
//
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                return mActivities[(int) value % mActivities.length];
//            }
//        });

        YAxis yAxis = mPositionChart.getYAxis();
        yAxis.setDrawLabels(false);

        Legend l = mPositionChart.getLegend();
        l.setEnabled(false);
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        l.setDrawInside(true);
//        l.setXEntrySpace(7f);
//        l.setYEntrySpace(5f);
//        l.setTextColor(Color.BLACK);
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

        //IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mChart);

        XAxis xAxisSig = mSignalChart.getXAxis();
        xAxisSig.setEnabled(false);
//        xAxisSig.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxisSig.setDrawGridLines(false);
//        xAxisSig.setGranularity(1f); // only intervals of 1 day
//        xAxisSig.setLabelCount(7);
//        xAxis.setValueFormatter(xAxisFormatter);

        //IAxisValueFormatter custom = new MyAxisValueFormatter();

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

    private void setPositionData() {
        // TODO maybe combine SatelliteModel and SatelliteEntry?
        List<SatelliteModel> satellites = LocationStorage.getSatelliteList();
        List<SatelliteEntry> entries = satellites.parallelStream().map(s -> new SatelliteEntry(s.prn, s.elevationDegrees, s.azimuthDegrees)).collect(Collectors.toList());

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for(SatelliteEntry entry : entries) {
            entry.setIcon(getEntryShape(entry.getIsUsedInFix(), entry.getSignalQuality()));
        }

        SatelliteDataSet set = new SatelliteDataSet(entries, "");
        set.setValueFormatter(new SatellitePointValueFormatter(0));
        set.setColor(Color.rgb(103, 110, 129));
        set.setDrawIcons(true);

        ArrayList<ISatelliteDataSet> sets = new ArrayList<>();
        sets.add(set);

        SatelliteData data = new SatelliteData(sets);
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(8f);
        data.setDrawValues(true);

        mSignalChart.setData(data);
        mPositionChart.setData(data);
        mPositionChart.invalidate();
    }

    private Drawable getEntryShape(boolean usedInFix, short signalQuality) {

        final int UNTRACKED_ALPHA = 50;

        Drawable shape;

        if (usedInFix) {
            shape = getResources().getDrawable(R.drawable.ic_fixed_black_24dp, null);
            shape.setAlpha((signalQuality * 2) + UNTRACKED_ALPHA);
        }
        else {
            shape = getResources().getDrawable(R.drawable.ic_unfixed_black_24dp, null);
            shape.setAlpha(UNTRACKED_ALPHA);
        }

        return shape;
    }

    public static SatelliteFragment newInstance() {
        return new SatelliteFragment();
    }
}
