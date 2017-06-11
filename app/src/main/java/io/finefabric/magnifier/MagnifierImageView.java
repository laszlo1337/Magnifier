package io.finefabric.magnifier;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;

/**
 * Created by laszlo on 2017-06-10.
 */

public class MagnifierImageView extends AppCompatImageView {

    private String TAG = "MAGNIFIER";

    private Matrix matrix = new Matrix();
    private RectF rectAreaBounds = new RectF();
    private Path circlePath = new Path();
    private Paint strokePaint;

    private int CIRCLE_RADIUS_DP = 60;
    private int INITIAL_MAGNIFY_FACTOR = 2;
    private int magnifyFactor = INITIAL_MAGNIFY_FACTOR;
    private float touchPosX = 0;
    private float touchPosY = 0;
    private int marginTopDp = 0;
    private int marginLeftDp = 0;
    private float circleCenterPosX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CIRCLE_RADIUS_DP + marginTopDp, getResources().getDisplayMetrics());
    private float circleCenterPosY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CIRCLE_RADIUS_DP + marginLeftDp, getResources().getDisplayMetrics());
    private float circleRadius;

    public MagnifierImageView(Context context) {
        this(context, null);
    }

    public MagnifierImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        circleRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CIRCLE_RADIUS_DP, getResources().getDisplayMetrics());
    }

    private void init() {
        strokePaint = new Paint();
        strokePaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()));
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.WHITE);
        strokePaint.setAlpha(180);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchPosX = event.getX() - circleCenterPosX / magnifyFactor;
        touchPosY = event.getY() - circleCenterPosY / magnifyFactor;

        Log.d(TAG, "rectAreaBounds : " + rectAreaBounds.toString());
        Log.d(TAG, "circle radius : " + circleRadius);
        Log.d(TAG, "circle x : " + circleCenterPosX);
        Log.d(TAG, "circle y : " + circleCenterPosY);
        Log.d(TAG, " touch pos x: " + touchPosX);
        Log.d(TAG, " touch pos y: " + touchPosY);

        getDrawable().invalidateSelf();

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMagnifier(canvas);
    }

    @Override
    public void invalidateDrawable(Drawable drawable) {
        Matrix mtx = getImageMatrix();
        rectAreaBounds.set(drawable.getBounds());
        mtx.mapRect(rectAreaBounds);

        super.invalidateDrawable(drawable);
    }

    private void drawMagnifier(Canvas canvas) {
        canvas.save();
        drawCircleMask(canvas);

        matrix.reset();
        matrix.preScale(magnifyFactor, magnifyFactor);
        matrix.postConcat(getImageMatrix());

        float px = touchPosX - rectAreaBounds.left / 2;
        float py = touchPosY - rectAreaBounds.top / 2;

        matrix.postTranslate(-px * magnifyFactor, -py * magnifyFactor);
        canvas.drawBitmap(((BitmapDrawable) getDrawable()).getBitmap(), matrix, null);
        canvas.drawCircle(circleCenterPosX, circleCenterPosY, circleRadius, strokePaint);
        canvas.restore();
    }

    private void drawCircleMask(Canvas canvas) {
        circlePath.reset();
        circlePath.addCircle(circleCenterPosX, circleCenterPosY, circleRadius, Path.Direction.CW);
        canvas.clipPath(circlePath);
    }

    public void setMargins(int marginLeftDp, int marginTopDp) {
        this.marginLeftDp = marginLeftDp;
        this.marginTopDp = marginTopDp;
    }

    public void setMagnifyFactor(int magnifyBy) {
        this.magnifyFactor = magnifyBy;
    }

}
