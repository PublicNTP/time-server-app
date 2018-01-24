package org.publicntp.gnssreader.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.ui.chart.ISatalliteDataSet;
import org.publicntp.gnssreader.ui.chart.SatelliteData;
import org.publicntp.gnssreader.ui.chart.SatelliteDataSet;
import org.publicntp.gnssreader.ui.chart.SatelliteEntry;
import org.publicntp.gnssreader.ui.chart.SatelliteMarkerView;
import org.publicntp.gnssreader.ui.chart.SatelliteRadialPlotChart;

//public class SatelliteFragment extends Fragment {
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_satellite, container, false);
//    }
//
//    public static SatelliteFragment newInstance() {
//        return new SatelliteFragment();
//    }
//}





import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

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
        MarkerView mv = new SatelliteMarkerView(this.getContext(), R.layout.markerview_satellite);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

        setData();

//        mChart.animateXY(
//                1400, 1400,
//                Easing.EasingOption.EaseInOutQuad,
//                Easing.EasingOption.EaseInOutQuad);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private String[] mActivities = new String[]{"0", "45", "90", "135", "180", "225", "270", "315"};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int) value % mActivities.length];
            }
        });
        xAxis.setTextColor(Color.WHITE);

        YAxis yAxis = mChart.getYAxis();
        yAxis.setLabelCount(8, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(80f);
        yAxis.setDrawLabels(false);


        mChart.getLegend().setEnabled(false);

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

        float mult = 80;
        float min = 20;
        int cnt = 5;

        ArrayList<SatelliteEntry> entries1 = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < cnt; i++) {
            float val1 = (float) (Math.random() * mult) + min;
            entries1.add(new SatelliteEntry(val1));
        }

        SatelliteDataSet set1 = new SatelliteDataSet(entries1, "Last Week");
        set1.setColor(Color.rgb(103, 110, 129));
        set1.setFillColor(Color.rgb(103, 110, 129));
        set1.setDrawFilled(false);
        set1.setFillAlpha(180);
        set1.setLineWidth(0f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(true);

        ArrayList<ISatalliteDataSet> sets = new ArrayList<>();
        sets.add(set1);

        SatelliteData data = new SatelliteData(sets);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);
        mChart.invalidate();
    }

    public static SatelliteFragment newInstance() {
        return new SatelliteFragment();
    }
}
