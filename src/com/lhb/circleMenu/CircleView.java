package com.lhb.circleMenu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View;
import android.view.MotionEvent;

@SuppressLint("DrawAllocation")
public class CircleView extends View {

    private final int OUTTER_CIRCLE_RADIUS_DETAL = 60;
    private final int INNER_CIRCLE_RADIUS_DETAL = 60;

    private TextPaint mTextPaint = new TextPaint();
    private ViewItem[] mChildrenItems;
    private int mViewWidth;

    private Paint mPeakPaint = new Paint();

    private float mPointX = 400, mPointY = 200; 
    private int mRadius = 200;
    private int mDeltaX = 0;
    private int mDeltaY = INNER_CIRCLE_RADIUS_DETAL;

    private boolean mIsInCenter = false;
    private boolean mIsInRing = false;

    private GestureDetector mGestureDetector;

    private int speed = 100;
    private float deceleration = 1 + (25f / speed);
    private boolean allowRotating = true;
    private boolean mNeedCallListen = false;

    private final String[] mLetters = { "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z" };
    private float mDeltaAngle = 360 / mLetters.length;

    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mTextPaint.setTextSize(16);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mGestureDetector = new GestureDetector(getContext(),
                new CircleGestureListener());

        mPeakPaint.setAlpha(255);
        mPeakPaint.setAntiAlias(true);
        mPeakPaint.setColor(Color.LTGRAY);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = getMeasuredWidth();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mPointX = mViewWidth / 2;
        comuteRadius(mViewWidth);
        mPointY = mRadius + OUTTER_CIRCLE_RADIUS_DETAL;
        initViewResource();
    }

    public void initViewResource() {
        setupStones();
        computeCoordinates();
    }

    private void setupStones() {
        mChildrenItems = new ViewItem[mLetters.length];
        ViewItem stone;
        float angle = 270;
        float mDegreeDelta = 360.0f / (mLetters.length);

        for (int index = 0; index < mLetters.length; index++) {
            stone = new ViewItem();
            stone.angle = angle;
            stone.text = mLetters[index];
            stone.x = mPointX
                    + (float) (mRadius * Math.cos(Math.toRadians(stone.angle)));
            stone.y = mPointY
                    + (float) (mRadius * Math.sin(Math.toRadians(stone.angle)));
            mChildrenItems[index] = stone;
            angle += mDegreeDelta;
        }
    }

    private class ViewItem {
        float angle;
        float x;
        float y;
        String text;

        public int getLeft() {
            float t = Math.round(x) - mDeltaX - 2;
            return (int) (t < 0 ? 0 : t);
        }

        public int getRight() {
            return Math.round(x) + mDeltaX + 2;
        }

        public int getTop() {
            float t = Math.round(y) - mDeltaY - 5;
            return (int) (t < 0 ? 0 : t);
        }

        public int getBottom() {
            return Math.round(y) + mDeltaY + 5;
        }

        public boolean isInViewRect(float angle, float x, float y) {
            if ((getTop() < y && getBottom() > y)
                    && (this.angle + mDeltaAngle) > angle
                    && (this.angle - mDeltaAngle) < angle) {
                return true;
            }
            return false;
        }
    }

    private void computeCoordinates() {
        ViewItem stone;
        if (mChildrenItems == null || mChildrenItems.length < 0) {
            return;
        }
        for (int index = 0; index < mChildrenItems.length; index++) {
            stone = mChildrenItems[index];
            stone.x = mPointX
                    + (float) (mRadius * Math.cos(stone.angle * Math.PI / 180));
            stone.y = mPointY
                    + (float) (mRadius * Math.sin(stone.angle * Math.PI / 180));
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);

        canvas.drawCircle(mPointX, mPointY, mRadius
                + OUTTER_CIRCLE_RADIUS_DETAL, paint);

        if (mIsInCenter) {
            paint.setColor(Color.GRAY);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(mPointX, mPointY, mRadius
                    - INNER_CIRCLE_RADIUS_DETAL, paint);
        } else {
            paint.setColor(Color.BLUE);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(mPointX, mPointY, mRadius
                    - INNER_CIRCLE_RADIUS_DETAL, paint);
        }
        if (mChildrenItems != null) {
            for (int index = 0; index < mChildrenItems.length; index++) {
                ViewItem child = mChildrenItems[index];
                if(child != null && child.y < mPointY){
                    drawInCenter(canvas, mChildrenItems[index].x, mChildrenItems[index].y,
                            mChildrenItems[index].text);
                }
            }
        }
    }

    private void drawInCenter(Canvas canvas, float centerx, float centery,
            String text) {
        if (!text.isEmpty()) {
            Rect rect = new Rect();
            int width = 0;
            int height = 0;

            mTextPaint.getTextBounds(text, 0, text.length(), rect);
            width = rect.right - rect.left;
            height = rect.bottom - rect.top;
            int radius = width > height ? (width + 5) : (height + 5);
            canvas.drawCircle(centerx, centery, radius, mPeakPaint);
            canvas.drawText(text, centerx, centery + height / 2, mTextPaint);
        }
    }

    private class CircleGestureListener extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {

            if (e1.getX() > e2.getX()) {
                CircleView.this.post(new FlingRunnable(-1 * (velocityX)));
            } else {
                CircleView.this.post(new FlingRunnable(velocityX));
            }
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            ViewItem viewItem = pointToPosition(e.getX(), e.getY());
            if (viewItem != null) {
                rotateViewToCenter(viewItem);
                return true;
            } else {
                return super.onSingleTapUp(e);
            }
        }
    }

    private int mAngleStart = 0;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean result = false;
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            allowRotating = false;
            mNeedCallListen = true;
            mIsInCenter = computeIsInCenter(e);
            if (mIsInCenter) {
                invalidate();
                result = true;
            } else {
                mIsInRing = computeIsInCircleRing(e);
                if (mIsInRing) {
                    mAngleStart = computeCurrentAngleByOffset(e.getX(),
                            e.getY());
                    result = true;
                }
            }
            break;

        case MotionEvent.ACTION_MOVE:
            if (mIsInCenter) {
                result = true;
            } else {
                if (mIsInRing) {
                    int moveAngle = computeCurrentAngle(e.getX(), e.getY());
                    int deltaAngle = mAngleStart - moveAngle;
                    mAngleStart = moveAngle;
                    resetStonesAngle(deltaAngle);
                    computeCoordinates();
                    invalidate();
                    result = true;
                }
            }
            break;

        case MotionEvent.ACTION_UP:
            mAngleStart = 0;
            if (mIsInCenter) {
                Log.e("dispatchTouchEvent", "ACTION_UP true");
                mIsInCenter = false;
                mIsInRing = false;
                invalidate();
            } else {
                Log.e("dispatchTouchEvent", "ACTION_UP FALSE");

                mIsInCenter = false;
                mIsInRing = false;
            }
            allowRotating = true;
            break;
        }
        result |= mGestureDetector.onTouchEvent(e);
        return result;

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {

        return super.dispatchTouchEvent(e);
    }

    private void resetStonesAngle(int angle) {
        for (int index = 0; index < mLetters.length; index++) {
            mChildrenItems[index].angle += angle;
            if (mChildrenItems[index].angle > 360) {
                mChildrenItems[index].angle -= 360;
            } else {
                if (mChildrenItems[index].angle < 0) {
                    mChildrenItems[index].angle += 360;
                }
            }
        }
    }

    private int computeCurrentAngle(float x, float y) {
        // x += mRadius - getWidth()/2;

        float distance = (float) Math.hypot((x - mPointX), (y - mPointY));
        int degree = (int) (Math.acos((x - mPointX) / distance) * 180 / Math.PI);

        return degree;
    }

    private int computeQuadrantAngle(float x, float y) {
        // x += mRadius - getWidth()/2;

        float distance = (float) Math.hypot((x - mPointX), (y - mPointY));
        int degree = (int) (Math.acos((x - mPointX) / distance) * 180 / Math.PI);
        degree = 360 - degree;
        return degree;
    }

    private int computeCurrentAngleByOffset(float x, float y) {
        float distance = (float) Math.hypot((x - mPointX), (y - mPointY));
        int degree = (int) (Math.acos((x - mPointX) / distance) * 180 / Math.PI);
        return degree;
    }

    private boolean computeIsInCenter(MotionEvent e) {
        boolean result = false;
        float distance = (float) Math.hypot((e.getX() - mPointX),
                (e.getY() - mPointY));
        if (distance < (mRadius - INNER_CIRCLE_RADIUS_DETAL)) {
            result = true;
        }
        return result;
    }

    private boolean computeIsInCircleRing(MotionEvent e) {
        boolean result = false;
        float distance = (float) Math.hypot((e.getX() - mPointX),
                (e.getY() - mPointY));
        if (distance <= (mRadius + OUTTER_CIRCLE_RADIUS_DETAL)) {
            result = true;
        }
        return result;
    }

    private void comuteRadius(int width) {
        mRadius = (int) (((float) (Math.sqrt(3) / 3)) * width)
                - OUTTER_CIRCLE_RADIUS_DETAL;
        mDeltaX = mRadius / 12;
        mDeltaY = INNER_CIRCLE_RADIUS_DETAL;
    }

    private ViewItem camputeCenterRect(float x, float y) {
        for (int i = 0; i < mChildrenItems.length; i++) {
            ViewItem item = (ViewItem) mChildrenItems[i];
            if (item.y < mPointY) {
                int dis = (int) Math.hypot((y - item.y), (x - item.x));
                if (dis <= (getWidth() / 12 + 10)) {
                    return item;
                }
            }
        }
        return null;
    }

    private ViewItem pointToPosition(float x, float y) {
        Log.e("pointToPosition", " top x" + x + "y" + y);
        int angle = computeQuadrantAngle(x, y);
        for (int i = 0; i < mChildrenItems.length; i++) {
            ViewItem item = (ViewItem) mChildrenItems[i];
            if (item.isInViewRect(angle, x, y)) {
                Log.e("pointToPosition", " top x:" + item.x + "y:" + item.y
                        + "angle:" + item.angle);
                return item;
            }
        }
        return null;
    }

    private void rotateViewToCenter(ViewItem item) {
        if (item != null) {
            float angle = -(item.angle - 270);
            for (int index = 0; index < mChildrenItems.length; index++) {
                ViewItem stone = mChildrenItems[index];
                stone.angle += angle;
                if (stone.angle > 360) {
                    stone.angle -= 360;
                } else {
                    if (stone.angle < 0) {
                        stone.angle += 360;
                    }
                }
                stone.x = mPointX
                        + (float) (mRadius * Math.cos(stone.angle * Math.PI
                                / 180));
                stone.y = mPointY
                        + (float) (mRadius * Math.sin(stone.angle * Math.PI
                                / 180));
            }
            invalidate();
            if (mNeedCallListen && mOnItemSelected != null) {
                mOnItemSelected.onItemSelected(item.text);
            }
        }
    }

    private void rotateViewToCenter() {
        ViewItem item = camputeCenterRect(getWidth() / 2,
                INNER_CIRCLE_RADIUS_DETAL);
        if (item != null) {
            float angle = -(item.angle - 270);
            for (int index = 0; index < mChildrenItems.length; index++) {
                ViewItem stone = mChildrenItems[index];
                stone.angle += angle;
                if (stone.angle > 360) {
                    stone.angle -= 360;
                } else {
                    if (stone.angle < 0) {
                        stone.angle += 360;
                    }
                }
                stone.x = mPointX
                        + (float) (mRadius * Math.cos(stone.angle * Math.PI
                                / 180));
                stone.y = mPointY
                        + (float) (mRadius * Math.sin(stone.angle * Math.PI
                                / 180));
            }
            invalidate();
            if (mNeedCallListen && mOnItemSelected != null) {
                mOnItemSelected.onItemSelected(item.text);
            }
        }
    }

    public void rotateViewToCenter(String letter) {
        mNeedCallListen = false;
        char letterString = letter.charAt(0);
        ViewItem item = null;
        for (ViewItem viewItem : mChildrenItems) {
            if (viewItem.text.charAt(0) == letterString) {
                item = viewItem;
                break;
            }
        }
        rotateViewToCenter(item);
    }

    private class FlingRunnable implements Runnable {

        private float velocity;
        private boolean isFirstForwarding = true;

        public FlingRunnable(float velocity) {
            this(velocity, true);
        }

        public FlingRunnable(float velocity, boolean isFirst) {
            this.velocity = velocity;
            this.isFirstForwarding = isFirst;
        }

        public void run() {
            if (allowRotating) {
                if (Math.abs(velocity) > 1) {
                    resetStonesAngle((int) velocity / speed);
                    computeCoordinates();
                    invalidate();
                    velocity /= deceleration;
                    CircleView.this.post(this);
                } else {
                    if (isFirstForwarding) {
                        isFirstForwarding = false;
                        rotateViewToCenter();
                    }
                }
            }
        }
    }

    public void computeQuadrantAngle(String x) {

    }

    public abstract interface OnItemSelectedListern {
        public void onItemSelected(String x);
    }

    private OnItemSelectedListern mOnItemSelected;

    public void setOnItemSelectedListern(OnItemSelectedListern listern) {
        mOnItemSelected = listern;
    }
}
