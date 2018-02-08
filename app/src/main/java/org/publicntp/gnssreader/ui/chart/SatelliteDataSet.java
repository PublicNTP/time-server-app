package org.publicntp.gnssreader.ui.chart;

import com.github.mikephil.charting.data.DataSet;

import java.util.ArrayList;
import java.util.List;

public class SatelliteDataSet extends DataSet<SatelliteEntry> implements ISatelliteDataSet {

    public SatelliteDataSet(List<SatelliteEntry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public DataSet<SatelliteEntry> copy() {

        List<SatelliteEntry> yVals = new ArrayList<>();

        for (int i = 0; i < mValues.size(); i++) {
            yVals.add(mValues.get(i).copy());
        }

        SatelliteDataSet copied = new SatelliteDataSet(yVals, getLabel());
        copied.mColors = mColors;

        return copied;
    }
}
