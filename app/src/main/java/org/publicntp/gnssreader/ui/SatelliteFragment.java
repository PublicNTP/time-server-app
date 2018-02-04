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
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class SatelliteFragment extends Fragment {

    private SatelliteViewModel mSatelliteViewModel;

    private SatellitePositionChart mPositionChart;
    private SatelliteSignalChart mSignalChart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_satellite, container, false);
        mPositionChart = view.findViewById(R.id.satellite_position_radial_chart);
        mSignalChart = view.findViewById(R.id.satellite_signal_bar_chart);

        mSatelliteViewModel = ViewModelProviders.of(this).get(SatelliteViewModel.class);

        setupSatellitePositionView();
        setupSatelliteSignalView();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setupSatellitePositionView() {
        mPositionChart.setBackgroundColor(Color.WHITE);

        mPositionChart.getDescription().setEnabled(false);

        mPositionChart.setWebLineWidth(1f);
        mPositionChart.setWebColor(Color.LTGRAY);
        mPositionChart.setWebLineWidthInner(1f);
        mPositionChart.setWebColorInner(Color.LTGRAY);
        mPositionChart.setWebAlpha(100);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
//        MarkerView mv = new SatelliteMarkerView(this.getContext(), R.layout.markerview_satellite);
//        mv.setChartView(mChart); // For bounds control
//        mChart.setMarker(mv); // Set the marker to the chart

        setPositionData();

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
        // mChart.setDrawYLabels(false);

        //IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mChart);

        XAxis xAxisSig = mSignalChart.getXAxis();
        xAxisSig.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisSig.setDrawGridLines(false);
        xAxisSig.setGranularity(1f); // only intervals of 1 day
        xAxisSig.setLabelCount(7);
        //xAxis.setValueFormatter(xAxisFormatter);

        //IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = mSignalChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        //leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = mSignalChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        //rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

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

        setSignalData(12, 50);
    }

    private void setPositionData() {

        float min = 1;
        int count = 10;

        ArrayList<SatelliteEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < count; i++) {
            int prnNumber = (int) (Math.random() * 99) + 1;
            float azimuth = (float) (Math.random() * 359) + min;
            float elevation = (float) (Math.random() * 90) + min;
            short signalQuality = (short) (Math.random() * 99);
            boolean usedInFix = (((int) (Math.random() * 10) % 2) == 0);

            SatelliteEntry entry = new SatelliteEntry(prnNumber,elevation,azimuth,signalQuality,usedInFix);
            entry.setIcon(getEntryShape(entry.getIsUsedInFix(), entry.getSignalQuality()));
            entries.add(entry);
        }

        SatelliteDataSet set = new SatelliteDataSet(entries, "");
        set.setColor(Color.rgb(103, 110, 129));
        set.setFillColor(Color.rgb(103, 110, 129));
        set.setFillAlpha(180);
        set.setLineWidth(0f);
        set.setDrawIcons(true);

        set.setDrawFilled(false);
        set.setDrawHighlightCircleEnabled(false);
        set.setDrawHighlightIndicators(false);
        set.setValueFormatter(new SatellitePointValueFormatter(0));

        ArrayList<ISatelliteDataSet> sets = new ArrayList<>();
        sets.add(set);

        SatelliteData data = new SatelliteData(sets);
        data.setValueTextSize(8f);
        data.setDrawValues(true);
        data.setValueTextColor(Color.BLACK);

        mPositionChart.setRotationEnabled(false);
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

    private void setSignalData(int count, float range) {

        float start = 1f;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = (int) start; i < start + count + 1; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult);

//            if (Math.random() * 100 < 25) {
//                yVals1.add(new BarEntry(i, val, getResources().getDrawable(R.drawable.star)));
//            } else {
                yVals1.add(new BarEntry(i, val));
//            }
        }

        BarDataSet set1;

        if (mSignalChart.getData() != null &&
                mSignalChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mSignalChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mSignalChart.getData().notifyDataChanged();
            mSignalChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "The year 2017");

            set1.setDrawIcons(false);

            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);

            mSignalChart.setData(data);
        }
    }

    public static SatelliteFragment newInstance() {
        return new SatelliteFragment();
    }
}
