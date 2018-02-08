package org.publicntp.gnssreader.ui.chart;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.listener.PieRadarChartTouchListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import org.publicntp.gnssreader.ui.renderer.SatelliteRadialPlotXAxisRenderer;
import org.publicntp.gnssreader.ui.renderer.SatelliteRadialPlotYAxisRenderer;


public class SatellitePositionChart extends Chart<SatelliteData> {

    /**
     * width of the main web lines
     */
    private float mWebLineWidth = 2.5f;

    /**
     * width of the inner web lines
     */
    private float mInnerWebLineWidth = 1.5f;

    /**
     * color for the main web lines
     */
    private int mWebColor = Color.rgb(122, 122, 122);

    /**
     * color for the inner web
     */
    private int mWebColorInner = Color.rgb(122, 122, 122);

    /**
     * transparency the grid is drawn with (0-255)
     */
    private int mWebAlpha = 150;

    /**
     * flag indicating if the web lines should be drawn or not
     */
    private boolean mDrawWeb = true;

    /**
     * modulus that determines how many labels and web-lines are skipped before the next is drawn
     */
    private int mSkipWebLineCount = 0;

    /**
     * the object reprsenting the y-axis labels
     */
    private YAxis mYAxis;

    protected SatelliteRadialPlotYAxisRenderer mYAxisRenderer;
    protected SatelliteRadialPlotXAxisRenderer mXAxisRenderer;

    /**
     * holds the normalized version of the current rotation angle of the chart
     */
    private float mRotationAngle = 270f;

    /**
     * holds the raw version of the current rotation angle of the chart
     */
    private float mRawRotationAngle = 270f;



    /**
     * Sets the minimum offset (padding) around the chart, defaults to 0.f
     */
    protected float mMinOffset = 0.f;

    public SatellitePositionChart(Context context) {
        super(context);
    }

    public SatellitePositionChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SatellitePositionChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        //mHighlighter = new SatelliteHighlighter(this);
        //mChartTouchListener = new PieRadarChartTouchListener(this);

        mYAxis = new YAxis(YAxis.AxisDependency.LEFT);

        mWebLineWidth = Utils.convertDpToPixel(1.5f);
        mInnerWebLineWidth = Utils.convertDpToPixel(0.75f);

        mRenderer = new SatellitePositionRenderer(this, mAnimator, mViewPortHandler);
        mYAxisRenderer = new SatelliteRadialPlotYAxisRenderer(mViewPortHandler, mYAxis, this);
        mXAxisRenderer = new SatelliteRadialPlotXAxisRenderer(mViewPortHandler, mXAxis, this);
    }

    @Override
    protected void calcMinMax() {
        mYAxis.calculate(mData.getYMin(YAxis.AxisDependency.LEFT), mData.getYMax(YAxis.AxisDependency.LEFT));
        mXAxis.calculate(0, mData.getMaxEntryCountSet().getEntryCount());
    }

    @Override
    public void notifyDataSetChanged() {
        if (mData == null)
            return;

        calcMinMax();

        mYAxisRenderer.computeAxis(mYAxis.mAxisMinimum, mYAxis.mAxisMaximum, mYAxis.isInverted());
        mXAxisRenderer.computeAxis(mXAxis.mAxisMinimum, mXAxis.mAxisMaximum, false);

        if (mLegend != null && !mLegend.isLegendCustom())
            mLegendRenderer.computeLegend(mData);

        calculateOffsets();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mData == null)
            return;

//        if (mYAxis.isEnabled())
//            mYAxisRenderer.computeAxis(mYAxis.mAxisMinimum, mYAxis.mAxisMaximum, mYAxis.isInverted());

        if (mXAxis.isEnabled())
            mXAxisRenderer.computeAxis(mXAxis.mAxisMinimum, mXAxis.mAxisMaximum, false);

        mXAxisRenderer.renderAxisLabels(canvas);

        if (mDrawWeb)
            mRenderer.drawExtras(canvas);

        if (mYAxis.isEnabled() && mYAxis.isDrawLimitLinesBehindDataEnabled())
            mYAxisRenderer.renderLimitLines(canvas);

        mRenderer.drawData(canvas);

        if (valuesToHighlight())
            mRenderer.drawHighlighted(canvas, mIndicesToHighlight);

        if (mYAxis.isEnabled() && !mYAxis.isDrawLimitLinesBehindDataEnabled())
            mYAxisRenderer.renderLimitLines(canvas);

        mYAxisRenderer.renderAxisLabels(canvas);

        mRenderer.drawValues(canvas);

        mLegendRenderer.renderLegend(canvas);

        drawDescription(canvas);

        drawMarkers(canvas);
    }

    /**
     * Returns the factor that is needed to transform values into pixels.
     *
     * @return
     */
    public float getFactor() {
        RectF content = mViewPortHandler.getContentRect();
        return Math.min(content.width() / 2f, content.height() / 2f) / mYAxis.mAxisRange;
    }

    /**
     * Returns the angle that each slice in the radar chart occupies.
     *
     * @return
     */
    public float getSliceAngle() {
        return 360f / (float) mData.getMaxEntryCountSet().getEntryCount();
    }

//    public int getIndexForAngle(float angle) {
//
//        // take the current angle of the chart into consideration
//        float a = Utils.getNormalizedAngle(angle - getRotationAngle());
//
//        float sliceangle = getSliceAngle();
//
//        int max = mData.getMaxEntryCountSet().getEntryCount();
//
//        int index = 0;
//
//        for (int i = 0; i < max; i++) {
//
//            float referenceAngle = sliceangle * (i + 1) - sliceangle / 2f;
//
//            if (referenceAngle > a) {
//                index = i;
//                break;
//            }
//        }
//
//        return index;
//    }

    /**
     * Returns the object that represents all y-labels of the RadarChart.
     *
     * @return
     */
    public YAxis getYAxis() {
        return mYAxis;
    }

    /**
     * Sets the width of the web lines that come from the center.
     *
     * @param width
     */
    public void setWebLineWidth(float width) {
        mWebLineWidth = Utils.convertDpToPixel(width);
    }

    public float getWebLineWidth() {
        return mWebLineWidth;
    }

    /**
     * Sets the width of the web lines that are in between the lines coming from
     * the center.
     *
     * @param width
     */
    public void setWebLineWidthInner(float width) {
        mInnerWebLineWidth = Utils.convertDpToPixel(width);
    }

    public float getWebLineWidthInner() {
        return mInnerWebLineWidth;
    }

    /**
     * Sets the transparency (alpha) value for all web lines, default: 150, 255
     * = 100% opaque, 0 = 100% transparent
     *
     * @param alpha
     */
    public void setWebAlpha(int alpha) {
        mWebAlpha = alpha;
    }

    /**
     * Returns the alpha value for all web lines.
     *
     * @return
     */
    public int getWebAlpha() {
        return mWebAlpha;
    }

    /**
     * Sets the color for the web lines that come from the center. Don't forget
     * to use getResources().getColor(...) when loading a color from the
     * resources. Default: Color.rgb(122, 122, 122)
     *
     * @param color
     */
    public void setWebColor(int color) {
        mWebColor = color;
    }

    public int getWebColor() {
        return mWebColor;
    }

    /**
     * Sets the color for the web lines in between the lines that come from the
     * center. Don't forget to use getResources().getColor(...) when loading a
     * color from the resources. Default: Color.rgb(122, 122, 122)
     *
     * @param color
     */
    public void setWebColorInner(int color) {
        mWebColorInner = color;
    }

    public int getWebColorInner() {
        return mWebColorInner;
    }

    /**
     * If set to true, drawing the web is enabled, if set to false, drawing the
     * whole web is disabled. Default: true
     *
     * @param enabled
     */
    public void setDrawWeb(boolean enabled) {
        mDrawWeb = enabled;
    }

    /**
     * Sets the number of web-lines that should be skipped on chart web before the
     * next one is drawn. This targets the lines that come from the center of the RadarChart.
     *
     * @param count if count = 1 -> 1 line is skipped in between
     */
    public void setSkipWebLineCount(int count) {

        mSkipWebLineCount = Math.max(0, count);
    }

    /**
     * Returns the modulus that is used for skipping web-lines.
     *
     * @return
     */
    public int getSkipWebLineCount() {
        return mSkipWebLineCount;
    }

    protected float getRequiredLegendOffset() {
        return mLegendRenderer.getLabelPaint().getTextSize() * 4.f;
    }

    protected float getRequiredBaseOffset() {
        return mXAxis.isEnabled() && mXAxis.isDrawLabelsEnabled() ?
                mXAxis.mLabelRotatedWidth :
                Utils.convertDpToPixel(10f);
    }

    public float getRadius() {
        RectF content = mViewPortHandler.getContentRect();
        return Math.min(content.width() / 2f, content.height() / 2f);
    }

    /**
     * Returns the maximum value this chart can display on it's y-axis.
     */
    public float getYChartMax() {
        return mYAxis.mAxisMaximum;
    }

    /**
     * Returns the minimum value this chart can display on it's y-axis.
     */
    public float getYChartMin() {
        return mYAxis.mAxisMinimum;
    }

    /**
     * Returns the range of y-values this chart can display.
     *
     * @return
     */
    public float getYRange() {
        return mYAxis.mAxisRange;
    }




    @Override
    public int getMaxVisibleCount() {
        return mData.getEntryCount();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // use the pie- and radarchart listener own listener
        if (mTouchEnabled && mChartTouchListener != null)
            return mChartTouchListener.onTouch(this, event);
        else
            return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {

        if (mChartTouchListener instanceof PieRadarChartTouchListener)
            ((PieRadarChartTouchListener) mChartTouchListener).computeScroll();
    }

    @Override
    public void calculateOffsets() {

        float legendLeft = 0f, legendRight = 0f, legendBottom = 0f, legendTop = 0f;

        if (mLegend != null && mLegend.isEnabled() && !mLegend.isDrawInsideEnabled()) {

            float fullLegendWidth = Math.min(mLegend.mNeededWidth,
                    mViewPortHandler.getChartWidth() * mLegend.getMaxSizePercent());

            switch (mLegend.getOrientation()) {
                case VERTICAL: {
                    float xLegendOffset = 0.f;

                    if (mLegend.getHorizontalAlignment() == Legend.LegendHorizontalAlignment.LEFT
                            || mLegend.getHorizontalAlignment() == Legend.LegendHorizontalAlignment.RIGHT) {
                        if (mLegend.getVerticalAlignment() == Legend.LegendVerticalAlignment.CENTER) {
                            // this is the space between the legend and the chart
                            final float spacing = Utils.convertDpToPixel(13f);

                            xLegendOffset = fullLegendWidth + spacing;

                        } else {
                            // this is the space between the legend and the chart
                            float spacing = Utils.convertDpToPixel(8f);

                            float legendWidth = fullLegendWidth + spacing;
                            float legendHeight = mLegend.mNeededHeight + mLegend.mTextHeightMax;

                            MPPointF center = getCenter();

                            float bottomX = mLegend.getHorizontalAlignment() ==
                                    Legend.LegendHorizontalAlignment.RIGHT
                                    ? getWidth() - legendWidth + 15.f
                                    : legendWidth - 15.f;
                            float bottomY = legendHeight + 15.f;
                            float distLegend = distanceToCenter(bottomX, bottomY);

                            MPPointF reference = getPosition(center, getRadius(),
                                    getAngleForPoint(bottomX, bottomY));

                            float distReference = distanceToCenter(reference.x, reference.y);
                            float minOffset = Utils.convertDpToPixel(5f);

                            if (bottomY >= center.y && getHeight() - legendWidth > getWidth()) {
                                xLegendOffset = legendWidth;
                            } else if (distLegend < distReference) {

                                float diff = distReference - distLegend;
                                xLegendOffset = minOffset + diff;
                            }

                            MPPointF.recycleInstance(center);
                            MPPointF.recycleInstance(reference);
                        }
                    }

                    switch (mLegend.getHorizontalAlignment()) {
                        case LEFT:
                            legendLeft = xLegendOffset;
                            break;

                        case RIGHT:
                            legendRight = xLegendOffset;
                            break;

                        case CENTER:
                            switch (mLegend.getVerticalAlignment()) {
                                case TOP:
                                    legendTop = Math.min(mLegend.mNeededHeight,
                                            mViewPortHandler.getChartHeight() * mLegend.getMaxSizePercent());
                                    break;
                                case BOTTOM:
                                    legendBottom = Math.min(mLegend.mNeededHeight,
                                            mViewPortHandler.getChartHeight() * mLegend.getMaxSizePercent());
                                    break;
                            }
                            break;
                    }
                }
                break;

                case HORIZONTAL:
                    float yLegendOffset = 0.f;

                    if (mLegend.getVerticalAlignment() == Legend.LegendVerticalAlignment.TOP ||
                            mLegend.getVerticalAlignment() == Legend.LegendVerticalAlignment.BOTTOM) {

                        // It's possible that we do not need this offset anymore as it
                        //   is available through the extraOffsets, but changing it can mean
                        //   changing default visibility for existing apps.
                        float yOffset = getRequiredLegendOffset();

                        yLegendOffset = Math.min(mLegend.mNeededHeight + yOffset,
                                mViewPortHandler.getChartHeight() * mLegend.getMaxSizePercent());

                        switch (mLegend.getVerticalAlignment()) {
                            case TOP:
                                legendTop = yLegendOffset;
                                break;
                            case BOTTOM:
                                legendBottom = yLegendOffset;
                                break;
                        }
                    }
                    break;
            }

            legendLeft += getRequiredBaseOffset();
            legendRight += getRequiredBaseOffset();
            legendTop += getRequiredBaseOffset();
            legendBottom += getRequiredBaseOffset();
        }

        float minOffset = Utils.convertDpToPixel(mMinOffset);

//        if (this instanceof RadarChart) {
//            XAxis x = this.getXAxis();
//
//            if (x.isEnabled() && x.isDrawLabelsEnabled()) {
//                minOffset = Math.max(minOffset, x.mLabelRotatedWidth);
//            }
//        }

        legendTop += getExtraTopOffset();
        legendRight += getExtraRightOffset();
        legendBottom += getExtraBottomOffset();
        legendLeft += getExtraLeftOffset();

        float offsetLeft = Math.max(minOffset, legendLeft);
        float offsetTop = Math.max(minOffset, legendTop);
        float offsetRight = Math.max(minOffset, legendRight);
        float offsetBottom = Math.max(minOffset, Math.max(getRequiredBaseOffset(), legendBottom));

        mViewPortHandler.restrainViewPort(offsetLeft, offsetTop, offsetRight, offsetBottom);

        if (mLogEnabled)
            Log.i(LOG_TAG, "offsetLeft: " + offsetLeft + ", offsetTop: " + offsetTop
                    + ", offsetRight: " + offsetRight + ", offsetBottom: " + offsetBottom);
    }

    /**
     * returns the angle relative to the chart center for the given point on the
     * chart in degrees. The angle is always between 0 and 360°, 0° is NORTH,
     * 90° is EAST, ...
     *
     * @param x
     * @param y
     * @return
     */
    public float getAngleForPoint(float x, float y) {

        MPPointF c = getCenterOffsets();

        double tx = x - c.x, ty = y - c.y;
        double length = Math.sqrt(tx * tx + ty * ty);
        double r = Math.acos(ty / length);

        float angle = (float) Math.toDegrees(r);

        if (x > c.x)
            angle = 360f - angle;

        // add 90° because chart starts EAST
        angle = angle + 90f;

        // neutralize overflow
        if (angle > 360f)
            angle = angle - 360f;

        MPPointF.recycleInstance(c);

        return angle;
    }

    /**
     * Returns a recyclable MPPointF instance.
     * Calculates the position around a center point, depending on the distance
     * from the center, and the angle of the position around the center.
     *
     * @param center
     * @param dist
     * @param angle  in degrees, converted to radians internally
     * @return
     */
    public MPPointF getPosition(MPPointF center, float dist, float angle) {

        MPPointF p = MPPointF.getInstance(0, 0);
        getPosition(center, dist, angle, p);
        return p;
    }

    public void getPosition(MPPointF center, float dist, float angle, MPPointF outputPoint) {
        outputPoint.x = (float) (center.x + dist * Math.cos(Math.toRadians(angle)));
        outputPoint.y = (float) (center.y + dist * Math.sin(Math.toRadians(angle)));
    }

    /**
     * Returns the distance of a certain point on the chart to the center of the
     * chart.
     *
     * @param x
     * @param y
     * @return
     */
    public float distanceToCenter(float x, float y) {

        MPPointF c = getCenterOffsets();

        float dist = 0f;

        float xDist = 0f;
        float yDist = 0f;

        if (x > c.x) {
            xDist = x - c.x;
        } else {
            xDist = c.x - x;
        }

        if (y > c.y) {
            yDist = y - c.y;
        } else {
            yDist = c.y - y;
        }

        // pythagoras
        dist = (float) Math.sqrt(Math.pow(xDist, 2.0) + Math.pow(yDist, 2.0));

        MPPointF.recycleInstance(c);

        return dist;
    }

    /**
     * Set an offset for the rotation of the RadarChart in degrees. Default 270f
     * --> top (NORTH)
     *
     * @param angle
     */
    public void setRotationAngle(float angle) {
        mRawRotationAngle = angle;
        mRotationAngle = Utils.getNormalizedAngle(mRawRotationAngle);
    }

    /**
     * gets the raw version of the current rotation angle of the pie chart the
     * returned value could be any value, negative or positive, outside of the
     * 360 degrees. this is used when working with rotation direction, mainly by
     * gestures and animations.
     *
     * @return
     */
    public float getRawRotationAngle() {
        return mRawRotationAngle;
    }

    /**
     * gets a normalized version of the current rotation angle of the pie chart,
     * which will always be between 0.0 < 360.0
     *
     * @return
     */
    public float getRotationAngle() {
        return mRotationAngle;
    }

    /**
     * Gets the minimum offset (padding) around the chart, defaults to 0.f
     */
    public float getMinOffset() {
        return mMinOffset;
    }

    /**
     * Sets the minimum offset (padding) around the chart, defaults to 0.f
     */
    public void setMinOffset(float minOffset) {
        mMinOffset = minOffset;
    }

    /**
     * returns the diameter of the pie- or radar-chart
     *
     * @return
     */
    public float getDiameter() {
        RectF content = mViewPortHandler.getContentRect();
        content.left += getExtraLeftOffset();
        content.top += getExtraTopOffset();
        content.right -= getExtraRightOffset();
        content.bottom -= getExtraBottomOffset();
        return Math.min(content.width(), content.height());
    }
}

