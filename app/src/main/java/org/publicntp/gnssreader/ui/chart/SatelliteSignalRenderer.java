package org.publicntp.gnssreader.ui.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.Range;
import com.github.mikephil.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;


public class SatelliteSignalRenderer extends DataRenderer {

    protected SatelliteSignalChart mChart;

    /**
     * the rect object that is used for drawing the bars
     */
    protected RectF mBarRect = new RectF();

    protected BarBuffer[] mBarBuffers;

    protected Paint mShadowPaint;
    protected Paint mBarBorderPaint;

    /**
     * buffer for storing the current minimum and maximum visible x
     */
    protected SatelliteSignalRenderer.XBounds mXBounds = new SatelliteSignalRenderer.XBounds();

    /**
     * Returns true if the DataSet values should be drawn, false if not.
     *
     * @param set
     * @return
     */
    protected boolean shouldDrawValues(IDataSet set) {
        return set.isVisible() && (set.isDrawValuesEnabled() || set.isDrawIconsEnabled());
    }

    /**
     * Checks if the provided entry object is in bounds for drawing considering the current animation phase.
     *
     * @param e
     * @param set
     * @return
     */
    protected boolean isInBoundsX(Entry e, IBarLineScatterCandleBubbleDataSet set) {

        if (e == null)
            return false;

        float entryIndex = set.getEntryIndex(e);

        if (e == null || entryIndex >= set.getEntryCount() * mAnimator.getPhaseX()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Class representing the bounds of the current viewport in terms of indices in the values array of a DataSet.
     */
    protected class XBounds {

        /**
         * minimum visible entry index
         */
        public int min;

        /**
         * maximum visible entry index
         */
        public int max;

        /**
         * range of visible entry indices
         */
        public int range;

        /**
         * Calculates the minimum and maximum x values as well as the range between them.
         *
         * @param chart
         * @param dataSet
         */
        public void set(BarLineScatterCandleBubbleDataProvider chart, IBarLineScatterCandleBubbleDataSet dataSet) {
            float phaseX = Math.max(0.f, Math.min(1.f, mAnimator.getPhaseX()));

            float low = chart.getLowestVisibleX();
            float high = chart.getHighestVisibleX();

            Entry entryFrom = dataSet.getEntryForXValue(low, Float.NaN, DataSet.Rounding.DOWN);
            Entry entryTo = dataSet.getEntryForXValue(high, Float.NaN, DataSet.Rounding.UP);

            min = entryFrom == null ? 0 : dataSet.getEntryIndex(entryFrom);
            max = entryTo == null ? 0 : dataSet.getEntryIndex(entryTo);
            range = (int) ((max - min) * phaseX);
        }
    }

    public SatelliteSignalRenderer(SatelliteSignalChart chart,
                                   ChartAnimator animator,
                                   ViewPortHandler viewPortHandler) {

        super(animator, viewPortHandler);
        this.mChart = chart;

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.FILL);
        mHighlightPaint.setColor(Color.rgb(0, 0, 0));
        // set alpha after color
        mHighlightPaint.setAlpha(120);

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setStyle(Paint.Style.FILL);

        mBarBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarBorderPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void initBuffers() {

        SatelliteData data = mChart.getData();
        mBarBuffers = new BarBuffer[data.getDataSetCount()];

        for (int i = 0; i < mBarBuffers.length; i++) {
            ISatelliteDataSet set = data.getDataSetByIndex(i);
            mBarBuffers[i] = new BarBuffer(set.getEntryCount() * 4,
                    data.getDataSetCount(), false);
        }
    }

    @Override
    public void drawData(Canvas c) {

        SatelliteData data = mChart.getData();
        for (int i = 0; i < data.getDataSetCount(); i++) {

            ISatelliteDataSet set = data.getDataSetByIndex(i);

            if (set.isVisible()) {
                drawDataSet(c, set, i);
            }
        }
    }

    private RectF mBarShadowRectBuffer = new RectF();

    protected void drawDataSet(Canvas c, ISatelliteDataSet dataSet, int index) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

//        mBarBorderPaint.setColor(Color.LTGRAY); //dataSet.getBarBorderColor());
//        mBarBorderPaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getBarBorderWidth()));

        final boolean drawBorder = false; //dataSet.getBarBorderWidth() > 0.f;

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        // draw the bar shadow before the values
        if (mChart.isDrawBarShadowEnabled()) {
//            mShadowPaint.setColor(dataSet.getBarShadowColor());

            SatelliteData barData = mChart.getData();

            final float barWidth = 4.0f; //barData.getBarWidth();
            final float barWidthHalf = barWidth / 2.0f;
            float x;

            for (int i = 0, count = Math.min((int)(Math.ceil((float)(dataSet.getEntryCount()) * phaseX)), dataSet.getEntryCount());
                 i < count;
                 i++) {

                SatelliteEntry entry = dataSet.getEntryForIndex(i);

                x = entry.getSignalQuality(); //entry.getX();

                mBarShadowRectBuffer.left = x - barWidthHalf;
                mBarShadowRectBuffer.right = x + barWidthHalf;

                trans.rectValueToPixel(mBarShadowRectBuffer);

                if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right))
                    continue;

                if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left))
                    break;

                mBarShadowRectBuffer.top = mViewPortHandler.contentTop();
                mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom();

                c.drawRect(mBarShadowRectBuffer, mShadowPaint);
            }
        }

        // initialize the buffer
        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));
//        buffer.setBarWidth(mChart.getBarData().getBarWidth());

//        buffer.feed(dataSet);

        trans.pointValuesToPixel(buffer.buffer);

        final boolean isSingleColor = dataSet.getColors().size() == 1;

        if (isSingleColor) {
            mRenderPaint.setColor(dataSet.getColor());
        }

        for (int j = 0; j < buffer.size(); j += 4) {

            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                continue;

            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                break;

            if (!isSingleColor) {
                // Set the color for the currently drawn value. If the index
                // is out of bounds, reuse colors.
                mRenderPaint.setColor(dataSet.getColor(j / 4));
            }

            c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], mRenderPaint);

            if (drawBorder) {
                c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mBarBorderPaint);
            }
        }
    }

    protected void prepareBarHighlight(float x, float y1, float y2, float barWidthHalf, Transformer trans) {

        float left = x - barWidthHalf;
        float right = x + barWidthHalf;
        float top = y1;
        float bottom = y2;

        mBarRect.set(left, top, right, bottom);

        trans.rectToPixelPhase(mBarRect, mAnimator.getPhaseY());
    }

    @Override
    public void drawValues(Canvas c) {

        // if values are drawn
        if (!(isDrawingValuesAllowed(mChart))) {
            return;
        }

        final float valueOffsetPlus = Utils.convertDpToPixel(4.5f);
        float posOffset = 0f;
        float negOffset = 0f;
        boolean drawValueAboveBar = mChart.isDrawValueAboveBarEnabled();

        for (int i = 0; i < mChart.getData().getDataSetCount(); i++) {

            ISatelliteDataSet dataSet = mChart.getData().getDataSetByIndex(i);

            if (!shouldDrawValues(dataSet))
                continue;

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            boolean isInverted = mChart.isInverted(dataSet.getAxisDependency());

            // calculate the correct offset depending on the draw position of
            // the value
            float valueTextHeight = Utils.calcTextHeight(mValuePaint, "8");
            posOffset = (drawValueAboveBar ? -valueOffsetPlus : valueTextHeight + valueOffsetPlus);
            negOffset = (drawValueAboveBar ? valueTextHeight + valueOffsetPlus : -valueOffsetPlus);

            if (isInverted) {
                posOffset = -posOffset - valueTextHeight;
                negOffset = -negOffset - valueTextHeight;
            }

            // get the buffer
            BarBuffer buffer = mBarBuffers[i];

            final float phaseY = mAnimator.getPhaseY();

            MPPointF iconsOffset = MPPointF.getInstance(dataSet.getIconsOffset());
            iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x);
            iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y);

            for (int j = 0; j < buffer.buffer.length * mAnimator.getPhaseX(); j += 4) {

                float x = (buffer.buffer[j] + buffer.buffer[j + 2]) / 2f;

                if (!mViewPortHandler.isInBoundsRight(x))
                    break;

                if (!mViewPortHandler.isInBoundsY(buffer.buffer[j + 1])
                        || !mViewPortHandler.isInBoundsLeft(x))
                    continue;

                SatelliteEntry entry = dataSet.getEntryForIndex(j / 4);
                float val = entry.getY();

                if (dataSet.isDrawValuesEnabled()) {
                    drawValue(c, dataSet.getValueFormatter(), val, entry, i, x,
                            val >= 0 ?
                                    (buffer.buffer[j + 1] + posOffset) :
                                    (buffer.buffer[j + 3] + negOffset),
                            dataSet.getValueTextColor(j / 4));
                }

                if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {

                    Drawable icon = entry.getIcon();

                    float px = x;
                    float py = val >= 0 ?
                            (buffer.buffer[j + 1] + posOffset) :
                            (buffer.buffer[j + 3] + negOffset);

                    px += iconsOffset.x;
                    py += iconsOffset.y;

                    Utils.drawImage(
                            c,
                            icon,
                            (int)px,
                            (int)py,
                            icon.getIntrinsicWidth(),
                            icon.getIntrinsicHeight());
                }
            }

            MPPointF.recycleInstance(iconsOffset);
        }
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

//        BarData barData = mChart.getBarData();
//
//        for (Highlight high : indices) {
//
//            IBarDataSet set = barData.getDataSetByIndex(high.getDataSetIndex());
//
//            if (set == null || !set.isHighlightEnabled())
//                continue;
//
//            BarEntry e = set.getEntryForXValue(high.getX(), high.getY());
//
//            if (!isInBoundsX(e, set))
//                continue;
//
//            Transformer trans = mChart.getTransformer(set.getAxisDependency());
//
//            mHighlightPaint.setColor(set.getHighLightColor());
//            mHighlightPaint.setAlpha(set.getHighLightAlpha());
//
//            boolean isStack = (high.getStackIndex() >= 0  && e.isStacked()) ? true : false;
//
//            final float y1;
//            final float y2;
//
//            if (isStack) {
//
//                if(mChart.isHighlightFullBarEnabled()) {
//
//                    y1 = e.getPositiveSum();
//                    y2 = -e.getNegativeSum();
//
//                } else {
//
//                    Range range = e.getRanges()[high.getStackIndex()];
//
//                    y1 = range.from;
//                    y2 = range.to;
//                }
//
//            } else {
//                y1 = e.getY();
//                y2 = 0.f;
//            }
//
//            prepareBarHighlight(e.getX(), y1, y2, barData.getBarWidth() / 2f, trans);
//
//            setHighlightDrawPos(high, mBarRect);
//
//            c.drawRect(mBarRect, mHighlightPaint);
//        }
    }

    /**
     * Sets the drawing position of the highlight object based on the riven bar-rect.
     * @param high
     */
    protected void setHighlightDrawPos(Highlight high, RectF bar) {
        high.setDraw(bar.centerX(), bar.top);
    }

    @Override
    public void drawExtras(Canvas c) {
    }
}
