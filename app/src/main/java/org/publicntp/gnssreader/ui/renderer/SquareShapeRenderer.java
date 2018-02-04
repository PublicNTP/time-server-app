package org.publicntp.gnssreader.ui.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.publicntp.gnssreader.ui.chart.ISatelliteDataSet;

/**
 * Created by richard on 2/3/18.
 */

public class SquareShapeRenderer implements IShapeRenderer {

    @Override
    public void renderShape(Canvas c,
                            ISatelliteDataSet dataSet,
                            ViewPortHandler viewPortHandler,
                            float posX, float posY, Paint renderPaint) {

//        final float shapeSize = dataSet.getScatterShapeSize();
//        final float shapeHalf = shapeSize / 2f;
//        final float shapeHoleSizeHalf = Utils.convertDpToPixel(dataSet.getScatterShapeHoleRadius());
//        final float shapeHoleSize = shapeHoleSizeHalf * 2.f;
//        final float shapeStrokeSize = (shapeSize - shapeHoleSize) / 2.f;
//        final float shapeStrokeSizeHalf = shapeStrokeSize / 2.f;
//
//        final int shapeHoleColor = dataSet.getScatterShapeHoleColor();
//
//        if (shapeSize > 0.0) {
//            renderPaint.setStyle(Paint.Style.STROKE);
//            renderPaint.setStrokeWidth(shapeStrokeSize);
//
//            c.drawRect(posX - shapeHoleSizeHalf - shapeStrokeSizeHalf,
//                    posY - shapeHoleSizeHalf - shapeStrokeSizeHalf,
//                    posX + shapeHoleSizeHalf + shapeStrokeSizeHalf,
//                    posY + shapeHoleSizeHalf + shapeStrokeSizeHalf,
//                    renderPaint);
//
//            if (shapeHoleColor != ColorTemplate.COLOR_NONE) {
//                renderPaint.setStyle(Paint.Style.FILL);
//
//                renderPaint.setColor(shapeHoleColor);
//                c.drawRect(posX - shapeHoleSizeHalf,
//                        posY - shapeHoleSizeHalf,
//                        posX + shapeHoleSizeHalf,
//                        posY + shapeHoleSizeHalf,
//                        renderPaint);
//            }
//
//        } else {
//            renderPaint.setStyle(Paint.Style.FILL);
//
//            c.drawRect(posX - shapeHalf,
//                    posY - shapeHalf,
//                    posX + shapeHalf,
//                    posY + shapeHalf,
//                    renderPaint);
//        }
    }
}
