package org.publicntp.gnssreader.ui.chart;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.mikephil.charting.data.Entry;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.ui.renderer.CircleShapeRenderer;
import org.publicntp.gnssreader.ui.renderer.IShapeRenderer;
import org.publicntp.gnssreader.ui.renderer.SquareShapeRenderer;

/**
 * Represents a single GPS Satellite position relative to the device.
 *
 * When plotting the satellite position, the azimuth is represented by the radial
 * grid rings and elevation is represented by the straight lines intersecting at
 * the center point.
 *
 * A single Satellite Entry must be capable or tracking the following data points:
 *   - azimuth                                  000-359 degrees from true north
 *   - elevation                                0-90 degrees, 0 = horizon, 90 = zenith
 *   - pseudorandom noise (PRN) code or number  01+, can be used as unique ID for satellite
 *   - signal to noise ratio (SNR)              00-99 dB, higher is better, null when not tracking
 *   - used in fix                              boolean, indicates sat used in location fix
 *
 * All data can be fetched from the $GPGSV sentence except for used in fix which
 * must come from the list presented by the $GPGSA sentence.
 *
 * Each satellite represented on the grid will be drawn as a circle or square depending
 * on whether that satellite was used in the location fix or not.
 *
 * The PRN number must also be drawn beneath the satellite's representation on the grid.
 *
 * Each shape will be drawn with a varying shade of gray representing the signal quality
 * where darker shading means better signal quality.
 */
@SuppressLint("ParcelCreator")
public class SatelliteEntry extends Entry {

    /**
     * used as a unique identifier for the satellite who provided the data.
     * 1023 bit number can be used as unique satellite ID, currently all PRN numbers
     * range between 01 and 32. An int should work as PRN numbers are recycled and
     * are not likely to increase to a 1023 bit number in the near future.
     */
    private int mPRNNumber;

    /**
     * determines signal quality in dB between the device and the satellite.
     * expressed as a range between 00-99 dB, higher is better, 00 when not tracking
     */
    private short mSignalQuality = 0;

    /**
     * indicates whether this satellite data was used to determine location.
     */
    private boolean mUsedInFix = false;

    /**
     * selection of shapes to use as icons on the radial chart.
     */
    private enum SatelliteShape {

        SQUARE("SQUARE"),
        CIRCLE("CIRCLE");

        private final String shapeId;

        SatelliteShape(final String shapeId) {
            this.shapeId = shapeId;
        }

        @Override
        public String toString() {
            return shapeId;
        }

        public static SatelliteShape[] getAllDefaultShapes() {
            return new SatelliteShape[] { SQUARE, CIRCLE };
        }
    }

    /**
     * constructor intended for data from satellite in view but not used in fix.
     * @param prnNumber PRN number or code from the $GPGSV sentence
     * @param elevation degrees above horizon from the $GPGSV sentence
     * @param azimuth degrees from true north from the $GPGSV sentence
     */
    public SatelliteEntry(int prnNumber, float elevation, float azimuth) {
        super(azimuth, elevation);
        this.mPRNNumber = prnNumber;

        init();
    }

    /**
     * constructor intended for data from satellite in view but not used in fix.
     * @param prnNumber PRN number or code from the $GPGSV sentence
     * @param elevation degrees above horizon from the $GPGSV sentence
     * @param azimuth degrees from true north from the $GPGSV sentence
     * @param signalQuality signal to noise ratio on dB from $GPGSV sentence
     * @param usedInFix boolean indicating satellite was used in location fix
     */
    public SatelliteEntry(int prnNumber, float elevation, float azimuth, short signalQuality, boolean usedInFix) {
        super(azimuth, elevation);
        this.mPRNNumber = prnNumber;
        this.mUsedInFix = usedInFix;
        this.mSignalQuality = signalQuality;

        init();
    }

    public int getPRNNumber() {
        return mPRNNumber;
    }

    /**
     * `Y` represents the distance from the center on the grid.
     * @return float elevation above horizon
     */
    public float getElevation() {
        return getY();
    }

    /**
     * `X` represents the clockwise distance from true north.
     * @return float distance in degrees from true north
     */
    public float getAzimuth() {
        return getX();
    }

    public short getSignalQuality() {
        return mSignalQuality;
    }

    public boolean getIsUsedInFix() {
        return mUsedInFix;
    }

    public SatelliteEntry copy() {

        int prnNumber = getPRNNumber();
        float elevation = getElevation();
        float azimuth = getAzimuth();
        short signalQuality = getSignalQuality();
        boolean usedInFix = getIsUsedInFix();

        return new SatelliteEntry(
                prnNumber,
                elevation,
                azimuth,
                signalQuality,
                usedInFix);
    }

    /**
     * initialization of satellite entry
     * set display shape and alpha value
     */
    private void init() {

//        if (mUsedInFix) {
//            setIcon();
//        }
//        else {
//
//        }


    }

    private static IShapeRenderer getRendererForShape(SatelliteShape shape) {

        switch (shape) {
            case SQUARE: return new SquareShapeRenderer();
            case CIRCLE: return new CircleShapeRenderer();
            default: return null;
        }
    }
}
