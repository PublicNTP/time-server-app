package org.publicntp.gnssreader.ui.satellite;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import org.publicntp.gnssreader.R;
import org.publicntp.gnssreader.helper.GreyLevelHelper;
import org.publicntp.gnssreader.helper.Winebar;
import org.publicntp.gnssreader.model.SatelliteModel;
import org.publicntp.gnssreader.repository.location.LocationStorage;

import java.util.ArrayList;
import java.util.List;

public class SatelliteRadialChart extends View {
    private List<SatelliteModel> satelliteModels = new ArrayList<>();

    private Paint blackFill;
    private Paint textFill;
    private Paint cardinalTextFill;
    private Paint whiteFill;
    private Paint highlightFill;
    private Paint lightGrey;
    private Paint greyStroke;

    private Canvas canvas;

    boolean compassEnabled = false;

    public SatelliteRadialChart(Context context) {
        super(context);
        init();
    }

    public SatelliteRadialChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SatelliteRadialChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SatelliteRadialChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public float satelliteScale() {
        return .27f * getResources().getDisplayMetrics().density;
    }

    public void init() {
        cardinalTextFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        cardinalTextFill.setColor(ContextCompat.getColor(this.getContext(), R.color.black));
        cardinalTextFill.setTextAlign(Paint.Align.CENTER);
        cardinalTextFill.setTextSize(75f);

        textFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        textFill.setColor(ContextCompat.getColor(this.getContext(), R.color.black));
        textFill.setTextSize(50f * satelliteScale());

        whiteFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        whiteFill.setColor(ContextCompat.getColor(this.getContext(), R.color.white));

        blackFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        blackFill.setColor(ContextCompat.getColor(this.getContext(), R.color.black));

        highlightFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        highlightFill.setColor(ContextCompat.getColor(this.getContext(), R.color.blue));

        lightGrey = new Paint(Paint.ANTI_ALIAS_FLAG);
        lightGrey.setColor(ContextCompat.getColor(this.getContext(), R.color.greylight));

        greyStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        greyStroke.setColor(ContextCompat.getColor(this.getContext(), R.color.greylight));
        greyStroke.setStyle(Paint.Style.STROKE);
        greyStroke.setStrokeWidth(6f);

        this.setOnTouchListener((v, event) -> {
            compassEnabled = !compassEnabled;
            if (!compassEnabled) {
                Winebar.make(this, "Compass Disabled.", Snackbar.LENGTH_SHORT).show();
                resetRotation();
            } else {
                Winebar.make(this, "Compass Enabled.", Snackbar.LENGTH_SHORT).show();
            }
            return false;
        });
    }

    public void setSatelliteModels(List<SatelliteModel> satelliteModels) {
        this.satelliteModels.clear();
        this.satelliteModels.addAll(satelliteModels);
        this.invalidate();
    }

    public void redraw() {
        this.invalidate();
    }

    float azimuth = 0f;
    Long lastSet;

    public void resetRotation() {
        rotateToNDegrees(0f);
    }

    public void rotateToNDegrees(float degrees) {
        RotateAnimation rotateAnimation;
        if (Math.abs(degrees - azimuth) < 180) { //Always rotate less than halfway around
            rotateAnimation = new RotateAnimation(-azimuth, -degrees, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
        } else {
            rotateAnimation = new RotateAnimation(-azimuth, degrees - 360f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
        }
        azimuth = degrees;
        lastSet = System.currentTimeMillis();
        rotateAnimation.setDuration(200);
        rotateAnimation.setRepeatCount(0);
        rotateAnimation.setFillAfter(true);
        this.startAnimation(rotateAnimation);
    }

    public void setCompassReading(float degrees) {
        if (!compassEnabled) {
            return;
        }

        if (degrees < 0) {
            degrees = 360f + degrees;
        }

        if (degrees > 360) {
            degrees = degrees % 360;
        }

        float rotatedDegrees = degrees - 360f;
        // sometimes, we go from 359 degrees to 1 degree, and the difference rotated should not be 358 degrees, it should be 2 degrees
        float rotationalDifference = Math.min(Math.abs(azimuth - degrees), Math.abs(azimuth - rotatedDegrees));
        if (lastSet == null || System.currentTimeMillis() - lastSet > 300 || rotationalDifference > 25f) {
            rotateToNDegrees(degrees);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(getChartBounds(canvas), whiteFill);
        drawRadarGuides(canvas);
        satelliteModels.forEach((sat) -> drawSatellite(sat, canvas));
    }

    private float getRadius(Canvas canvas) {
        Rect canvasBoundary = canvas.getClipBounds();
        float radius = Math.min(canvasBoundary.height(), canvasBoundary.width()) / 2;
        radius = radius * .97f; // To make room for stroke width
        return radius;
    }

    // Our chart must operate within a square, so this is that square
    private Rect getChartBounds(Canvas canvas) {
        Rect canvasBoundary = canvas.getClipBounds();

        float radius = getRadius(canvas);

        float trueLeft = canvasBoundary.centerX() - radius;
        float trueRight = canvasBoundary.centerX() + radius;
        float trueTop = canvasBoundary.centerY() - radius;
        float trueBottom = canvasBoundary.centerY() + radius;

        return new Rect((int) trueLeft, (int) trueTop, (int) trueRight, (int) trueBottom);
    }

    private void drawRadarGuides(Canvas canvas) {
        Rect bounds = getChartBounds(canvas);

        int circleRadius = bounds.height() / 2;
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), circleRadius, greyStroke);
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), circleRadius * 2 / 3, greyStroke);
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), circleRadius / 3, greyStroke);

        canvas.drawLine(bounds.left, bounds.centerY(), bounds.right, bounds.centerY(), greyStroke); //horizontal
        canvas.drawLine(bounds.centerX(), bounds.top, bounds.centerX(), bounds.bottom, greyStroke); //vertical

        float legLength = (float) Math.sqrt(Math.pow(getRadius(canvas), 2) / 2);
        canvas.drawLine(bounds.centerX() + legLength, bounds.centerY() - legLength, bounds.centerX() - legLength, bounds.centerY() + legLength, greyStroke); //top-right to bottom-left
        canvas.drawLine(bounds.centerX() - legLength, bounds.centerY() - legLength, bounds.centerX() + legLength, bounds.centerY() + legLength, greyStroke); //top-left to bottom-right

        canvas.drawText("N", bounds.centerX(), bounds.top + bounds.height() * .05f, cardinalTextFill);
    }

    private void drawTriangle(Canvas canvas, float x, float y, float radius, Paint paint) {
        radius *= 1.4;
        Path triangle = new Path();
        triangle.setFillType(Path.FillType.EVEN_ODD);
        triangle.moveTo(x, y - radius);
        float yOffset = radius / 2;
        float xOffset = (float) (yOffset * Math.sqrt(3));
        triangle.lineTo(x - xOffset, y + yOffset);
        triangle.lineTo(x + xOffset, y + yOffset);
        triangle.close();
        canvas.drawPath(triangle, paint);
    }

    private void drawSatellite(SatelliteModel satelliteModel, Canvas canvas) {
        drawSatellite(satelliteModel, canvas, false);
    }

    private void drawSatellite(SatelliteModel satelliteModel, Canvas canvas, boolean forceHighlight) {
        Rect bounds = canvas.getClipBounds();
        float radius = getRadius(canvas) * .85f; // don't let satellites go out of bounds

        // Degrees in this instance are counted clockwise from North (0)
        float azimuthDegreesRelativeToNorth = satelliteModel.azimuthDegrees - 90f;

        int magnitude = (int) (radius * Math.cos(Math.toRadians(satelliteModel.elevationDegrees)));
        int x = (int) (magnitude * Math.cos(Math.toRadians(azimuthDegreesRelativeToNorth)));
        int y = (int) (magnitude * Math.sin(Math.toRadians(azimuthDegreesRelativeToNorth)));
        x = bounds.centerX() + x;
        y = bounds.centerY() + y;

        Paint paint;
        if (LocationStorage.isSelected(satelliteModel)) {
            paint = highlightFill;
        } else {
            paint = GreyLevelHelper.asPaint(getContext(), satelliteModel.Cn0DbHz);
        }

        float shapeRadius = 30f * satelliteScale();
        if (satelliteModel.usedInFix) {
            drawTriangle(canvas, x, y, shapeRadius, paint);
        } else {
            canvas.drawRect(new Rect(
                            (int) (x - shapeRadius),
                            (int) (y - shapeRadius),
                            (int) (x + shapeRadius),
                            (int) (y + shapeRadius)),
                    paint);
        }

        canvas.drawText(satelliteModel.svn + "", x + shapeRadius * 1.3f, y + shapeRadius / 1.6f, textFill);
    }
}
