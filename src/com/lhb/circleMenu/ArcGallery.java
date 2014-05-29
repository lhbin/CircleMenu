package com.lhb.circleMenu;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Gallery;

public class ArcGallery extends Gallery {

    private int mRadius = 500;
    private static final int DEGREE = 50;

    private int coveflowCenterX, coveflowCenterY;// 半径值

    public ArcGallery(Context context) {
        super(context);
        init();

    }

    public ArcGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ArcGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setSpacing(20);
        setStaticTransformationsEnabled(true);
    }

    private int getCenterOfCoverflow() {
        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2
                + getPaddingLeft();
    }

    private static int getCenterOfView(View view) {
        return view.getLeft() + view.getWidth() / 2;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        int childCenter = getCenterOfView(child);
        float rotationAngle = (int) ((coveflowCenterX - childCenter) * 180f
                / Math.PI / mRadius);

        int top = child.getTop();
        int left = child.getLeft();
        int childWidth = child.getMeasuredWidth();

        double radians = Math.toRadians(0 - rotationAngle);
        int dx = (int) (mRadius * Math.sin(radians) + coveflowCenterX - left - childWidth * 0.5);
        int dy = (int) (coveflowCenterY - mRadius * Math.cos(radians)) - top;

        canvas.save();

        canvas.translate(dx, dy);
        canvas.rotate(-rotationAngle, childCenter,
                (child.getTop() + child.getBottom()) / 2);
        boolean result = super.drawChild(canvas, child, drawingTime);
        canvas.restore();
        return result;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mRadius = (int) (w / 2 / Math.sin(Math.toRadians(DEGREE)));
        Log.e("tag", "radius:" + mRadius);
        coveflowCenterX = getCenterOfCoverflow();
        coveflowCenterY = mRadius;
        super.onSizeChanged(w, h, oldw, oldh);
    }

}
