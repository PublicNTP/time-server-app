package org.publicntp.gnssreader.ui.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class SatellitePositionRenderer extends DataRenderer {

    private SatellitePositionChart mChart;

    private Paint mRadialGridPaint;
    private Paint mHighlightCirclePaint;

    private Path mDrawDataSetSurfacePathBuffer = new Path();
    private Path mDrawHighlightCirclePathBuffer = new Path();


    SatellitePositionRenderer(SatellitePositionChart chart,
                              ChartAnimator animator,
                              ViewPortHandler viewPortHandler) {

        super(animator, viewPortHandler);

        mChart = chart;
        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.STROKE);
        mHighlightPaint.setStrokeWidth(2f);
        mHighlightPaint.setColor(Color.rgb(255, 187, 115));

        mRadialGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRadialGridPaint.setStyle(Paint.Style.STROKE);

        mHighlightCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }


    @Override
    public void initBuffers() {
        // No Implementation
    }


    @Override
    public void drawData(Canvas c) {

        SatelliteData satelliteData = mChart.getData();

        int mostEntries = satelliteData.getMaxEntryCountSet().getEntryCount();

//        for (ISatelliteDataSet set : satelliteData.getDataSets()) {
//
//            if (set.isVisible()) {
//                drawDataSet(c, set, mostEntries);
//            }
//        }
    }


    @Override
    public void drawValues(Canvas c) {

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to pixels
        float factor = mChart.getFactor();

        MPPointF center = mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0,0);
        MPPointF pIcon = MPPointF.getInstance(0,0);

        float yoffset = Utils.convertDpToPixel(15f);

        for (int i = 0; i < mChart.getData().getDataSetCount(); i++) {

            ISatelliteDataSet dataSet = mChart.getData().getDataSetByIndex(i);

//            if (!shouldDrawValues(dataSet))
//                continue;

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            MPPointF iconsOffset = MPPointF.getInstance(dataSet.getIconsOffset());
            iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x);
            iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y);

            for (int j = 0; j < dataSet.getEntryCount(); j++) {

                SatelliteEntry entry = dataSet.getEntryForIndex(j);

                Utils.getPosition(
                        center,
                        (entry.getY() - mChart.getYChartMin()) * factor * phaseY,
                        sliceangle * j * phaseX + mChart.getRotationAngle(),
                        pOut);

//                if (dataSet.isDrawValuesEnabled()) {
                    drawValue(c,
                            dataSet.getValueFormatter(),
                            entry.getPRNNumber(),
                            entry,
                            i,
                            pOut.x,
                            pOut.y + yoffset,
                            dataSet.getValueTextColor
                                    (j));
//                }

                if (entry.getIcon() != null) { //&& dataSet.isDrawIconsEnabled()) {

                    Drawable icon = entry.getIcon();


                    Utils.getPosition(
                            center,
                            (entry.getY()) * factor * phaseY + iconsOffset.y,
                            sliceangle * j * phaseX + mChart.getRotationAngle(),
                            pIcon);

                    //noinspection SuspiciousNameCombination
                    pIcon.y += iconsOffset.x;

                    Utils.drawImage(
                            c,
                            icon,
                            (int)pIcon.x,
                            (int)pIcon.y,
                            icon.getIntrinsicWidth(),
                            icon.getIntrinsicHeight());
                }
            }

            MPPointF.recycleInstance(iconsOffset);
        }

        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(pOut);
        MPPointF.recycleInstance(pIcon);
    }


    @Override
    public void drawExtras(Canvas c) {
        drawRadialGridBackground(c);
    }


    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        MPPointF center = mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0,0);

        SatelliteData radarData = mChart.getData();

        for (Highlight high : indices) {

            ISatelliteDataSet set = radarData.getDataSetByIndex(high.getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            SatelliteEntry e = set.getEntryForIndex((int) high.getX());

//            if (!isInBoundsX(e, set))
//                continue;

            float y = (e.getY() - mChart.getYChartMin());

            Utils.getPosition(center,
                    y * factor * mAnimator.getPhaseY(),
                    sliceangle * high.getX() * mAnimator.getPhaseX() + mChart.getRotationAngle(),
                    pOut);

            high.setDraw(pOut.x, pOut.y);

//            // draw the lines
//            drawHighlightLines(c, pOut.x, pOut.y, set);
//
//            if (set.isDrawHighlightCircleEnabled()) {
//
//                if (!Float.isNaN(pOut.x) && !Float.isNaN(pOut.y)) {
//
//                    int strokeColor = set.getHighlightCircleStrokeColor();
//                    if (strokeColor == ColorTemplate.COLOR_NONE) {
//                        strokeColor = set.getColor(0);
//                    }
//
//                    if (set.getHighlightCircleStrokeAlpha() < 255) {
//                        strokeColor = ColorTemplate.colorWithAlpha(strokeColor, set.getHighlightCircleStrokeAlpha());
//                    }
//
//                    drawHighlightCircle(c,
//                            pOut,
//                            set.getHighlightCircleInnerRadius(),
//                            set.getHighlightCircleOuterRadius(),
//                            set.getHighlightCircleFillColor(),
//                            strokeColor,
//                            set.getHighlightCircleStrokeWidth());
//                }
//            }
        }

        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(pOut);
    }


    private void drawDataSet(Canvas c, ISatelliteDataSet dataSet, int mostEntries) {

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to pixels
        float factor = mChart.getFactor();

        MPPointF center = mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0,0);
        Path surface = mDrawDataSetSurfacePathBuffer;
        surface.reset();

        boolean hasMovedToPoint = false;

        for (int j = 0; j < dataSet.getEntryCount(); j++) {

            mRenderPaint.setColor(dataSet.getColor(j));

            SatelliteEntry e = dataSet.getEntryForIndex(j);

            Utils.getPosition(
                    center,
                    (e.getY() - mChart.getYChartMin()) * factor * phaseY,
                    sliceangle * j * phaseX + mChart.getRotationAngle(), pOut);

            if (Float.isNaN(pOut.x))
                continue;

            if (!hasMovedToPoint) {
                surface.moveTo(pOut.x, pOut.y);
                hasMovedToPoint = true;
            } else
                surface.lineTo(pOut.x, pOut.y);
        }

        if (dataSet.getEntryCount() > mostEntries) {
            // if this is not the largest set, draw a line to the center before closing
            surface.lineTo(center.x, center.y);
        }

        surface.close();



        mRenderPaint.setStyle(Paint.Style.STROKE);
//        mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
//
//        // draw the line (only if filled is disabled or alpha is below 255)
//        if (!dataSet.isDrawFilledEnabled() || dataSet.getFillAlpha() < 255)
//            c.drawPath(surface, mRenderPaint);

        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(pOut);
    }


    public Paint getWebPaint() {
        return mRadialGridPaint;
    }


    private void drawHighlightCircle(Canvas c,
                                    MPPointF point,
                                    float innerRadius,
                                    float outerRadius,
                                    int fillColor,
                                    int strokeColor,
                                    float strokeWidth) {
        c.save();

        outerRadius = Utils.convertDpToPixel(outerRadius);
        innerRadius = Utils.convertDpToPixel(innerRadius);

        if (fillColor != ColorTemplate.COLOR_NONE) {
            Path p = mDrawHighlightCirclePathBuffer;
            p.reset();
            p.addCircle(point.x, point.y, outerRadius, Path.Direction.CW);
            if (innerRadius > 0.f) {
                p.addCircle(point.x, point.y, innerRadius, Path.Direction.CCW);
            }
            mHighlightCirclePaint.setColor(fillColor);
            mHighlightCirclePaint.setStyle(Paint.Style.FILL);
            c.drawPath(p, mHighlightCirclePaint);
        }

        if (strokeColor != ColorTemplate.COLOR_NONE) {
            mHighlightCirclePaint.setColor(strokeColor);
            mHighlightCirclePaint.setStyle(Paint.Style.STROKE);
            mHighlightCirclePaint.setStrokeWidth(Utils.convertDpToPixel(strokeWidth));
            c.drawCircle(point.x, point.y, outerRadius, mHighlightCirclePaint);
        }

        c.restore();
    }


    private void drawRadialGridBackground(Canvas c) {

        // These should be settings on the chart class;

        final int DIVISION_COUNT = 8;
        final int CONCENTRIC_CIRCLE_COUNT = 3;


// draw the grid lines that come from the center

        mRadialGridPaint.setStrokeWidth(mChart.getWebLineWidth());
        mRadialGridPaint.setColor(mChart.getWebColor());
        mRadialGridPaint.setAlpha(mChart.getWebAlpha());

        // calculate the factor that is needed for transforming the value to pixels
        float factor = mChart.getFactor();
        float rotationangle = mChart.getRotationAngle();
        float radius = mChart.getYRange() * factor;


        int maxEntryCount = DIVISION_COUNT; //mChart.getData().getMaxEntryCountSet().getEntryCount();
        float sliceangle = 360f / (float) maxEntryCount; //mChart.getSliceAngle();

        MPPointF center = mChart.getCenterOffsets();
        MPPointF p = MPPointF.getInstance(0,0);
        for (int i = 0; i < maxEntryCount; i++) {
            float angle = sliceangle * i + rotationangle;
            Utils.getPosition(center, radius, angle, p);
            c.drawLine(center.x, center.y, p.x, p.y, mRadialGridPaint);
        }

        MPPointF.recycleInstance(p);


// draw the inner-web

        mRadialGridPaint.setStrokeWidth(mChart.getWebLineWidthInner());
        mRadialGridPaint.setColor(mChart.getWebColorInner());
        mRadialGridPaint.setAlpha(mChart.getWebAlpha());

        float cursor = 0f;
        float between = radius / (float) CONCENTRIC_CIRCLE_COUNT;
        for (int i = 0; i < CONCENTRIC_CIRCLE_COUNT; i++) {
            c.drawCircle(center.x, center.y, (cursor + between), mRadialGridPaint);
            cursor += between;
        }
    }
}
