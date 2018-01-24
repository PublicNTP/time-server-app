package org.publicntp.gnssreader.ui.chart;

import android.annotation.SuppressLint;

import com.github.mikephil.charting.data.Entry;

@SuppressLint("ParcelCreator")
public class SatelliteEntry extends Entry {

    private short signalStrength;

    public SatelliteEntry(float value) {
        super(0f, value);
    }

    public SatelliteEntry(float value, Object data) {
        super(0f, value, data);
    }

    /**
     * This is the same as getY(). Returns the value of the SatelliteEntry.
     *
     * @return
     */
    public float getValue() {
        return getY();
    }

    public SatelliteEntry copy() {
        SatelliteEntry e = new SatelliteEntry(getY(), getData());
        return e;
    }

    @Deprecated
    @Override
    public void setX(float x) {
        super.setX(x);
    }

    @Deprecated
    @Override
    public float getX() {
        return super.getX();
    }
}
