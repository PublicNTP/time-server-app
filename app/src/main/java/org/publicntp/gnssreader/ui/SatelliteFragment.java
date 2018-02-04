package org.publicntp.gnssreader.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.ui.chart.ISatelliteDataSet;
import org.publicntp.gnssreader.ui.chart.SatelliteData;
import org.publicntp.gnssreader.ui.chart.SatelliteDataSet;
import org.publicntp.gnssreader.ui.chart.SatelliteEntry;
import org.publicntp.gnssreader.ui.chart.SatelliteMarkerView;
import org.publicntp.gnssreader.ui.chart.SatellitePointValueFormatter;
import org.publicntp.gnssreader.ui.chart.SatelliteRadialPlotChart;


import android.graphics.Color;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;

public class SatelliteFragment extends Fragment {

    private SatelliteRadialPlotChart mChart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_satellite, container, false);

        mChart = view.findViewById(R.id.satellite_radial_plot);
        mChart.setBackgroundColor(Color.WHITE);

        mChart.getDescription().setEnabled(false);

        mChart.setWebLineWidth(1f);
        mChart.setWebColor(Color.LTGRAY);
        mChart.setWebLineWidthInner(1f);
        mChart.setWebColorInner(Color.LTGRAY);
        mChart.setWebAlpha(100);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
//        MarkerView mv = new SatelliteMarkerView(this.getContext(), R.layout.markerview_satellite);
//        mv.setChartView(mChart); // For bounds control
//        mChart.setMarker(mv); // Set the marker to the chart

        setData();

//        mChart.animateXY(
//                1400, 1400,
//                Easing.EasingOption.EaseInOutQuad,
//                Easing.EasingOption.EaseInOutQuad);

        XAxis xAxis = mChart.getXAxis();
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

        YAxis yAxis = mChart.getYAxis();
        yAxis.setDrawLabels(false);

        Legend l = mChart.getLegend();
        l.setEnabled(false);
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        l.setDrawInside(true);
//        l.setXEntrySpace(7f);
//        l.setYEntrySpace(5f);
//        l.setTextColor(Color.BLACK);

        return view;
    }

    public void setData() {

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
        //set.setValueFormatter(new SatellitePointValueFormatter(0));

        ArrayList<ISatelliteDataSet> sets = new ArrayList<>();
        sets.add(set);

        SatelliteData data = new SatelliteData(sets);
        data.setValueTextSize(8f);
        data.setDrawValues(true);
        data.setValueTextColor(Color.BLACK);

        mChart.setRotationEnabled(false);
        mChart.setData(data);
        mChart.invalidate();
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
