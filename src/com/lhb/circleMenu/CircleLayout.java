package com.lhb.circleMenu;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemSelectedListener;



public class CircleLayout extends ViewGroup {
	// Event listeners
	private OnItemSelectedListener mOnItemSelectedListener = null;
	private OnCenterClickListener mOnCenterClickListener = null;


    
    private boolean mIsInCenter = false;
    private boolean mIsInRing = false;

	// Background image
	private Matrix matrix;

	private int mTappedViewsPostition = -1;
	private View mTappedView = null;
	private int selected = 0;

	// Child sizes
	private int childWidth = 0;
	private int childHeight = 0;

	// Sizes of the ViewGroup
	private int circleWidth, circleHeight;
	private int radius = 0;

	// Touch detection
	private GestureDetector mGestureDetector;
	// needed for detecting the inversed rotations
	private boolean[] quadrantTouched;

	// Settings of the ViewGroup
	private boolean allowRotating = true;
	private boolean allowRotateCenter = false;
	private float angle = 270;
	private float firstChildPos = 270;
	private boolean rotateToCenter = true;
	private boolean isRotating = true;
	private int speed = 50;
	private float deceleration = 1 + (5f / speed);
	private Paint mPaint = new Paint();
	
	private boolean mNeedCallListen = false;

	// The runnable of the current rotation

	/**
	 * @param context
	 */
	public CircleLayout(Context context) {
		this(context, null);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public CircleLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}


	public CircleLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}


	protected void init(AttributeSet attrs) {
		mGestureDetector = new GestureDetector(getContext(),
				new MyGestureListener());
		quadrantTouched = new boolean[] { false, false, false, false, false };

		if (attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs,
					R.styleable.Circle);

			// The angle where the first menu item will be drawn
			angle = a.getInt(R.styleable.Circle_firstChildPosition, 90);
			firstChildPos = angle = 270;

			rotateToCenter = a.getBoolean(R.styleable.Circle_rotateToCenter,
					true);
			isRotating = a.getBoolean(R.styleable.Circle_isRotating, true);
			speed = 75;
			deceleration = 1 + (5f / speed);

			// If the menu is not rotating then it does not have to be centered
			// since it cannot be even moved
			if (!isRotating) {
				rotateToCenter = false;
			}

			a.recycle();

			// initialize the matrix only once
			if (matrix == null) {
				matrix = new Matrix();
			} else {
				// not needed, you can also post the matrix immediately to
				// restore the old state
				matrix.reset();
			}

			// Needed for the ViewGroup to be drawn
			setWillNotDraw(false);
		}
	}

	/**
	 * Returns the currently selected menu
	 * 
	 * @return the view which is currently the closest to the start position
	 */
	public View getSelectedItem() {
		return (selected >= 0) ? getChildAt(selected) : null;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// the sizes of the ViewGroup
		circleHeight = getHeight();
		circleWidth = getWidth();
		
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(getWidth()/2, radius + radius/5 , radius + radius/7, mPaint);
        
        mPaint.setColor(Color.BLUE);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getWidth()/2, radius + radius/5, radius - radius/8, mPaint);
	}


	private void comuteRadius(int width) {
	    int outRadius = (int) (((float) (Math.sqrt(3) / 3)) * width);
	    this.radius = outRadius - outRadius/10;
    }
	
	private boolean computeIsInCenter(MotionEvent e) {
        boolean result = false;
        float distance = (float) Math.hypot((e.getX() - getWidth()/2),
                (e.getY() - radius - radius/5));
        if (distance < (radius - radius/10)) {
            result = true;
        }
        return result;
    }

    private boolean computeIsInCircleRing(MotionEvent e) {
        boolean result = false;
        float distance = (float) Math.hypot((e.getX() - getWidth()/2),
                (e.getY() - radius - radius/5));
        if (distance <= (radius + radius/10)) {
            result = true;
        }
        return result;
    }
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int layoutWidth = r - l;
		
		// Laying out the child views
		final int childCount = getChildCount();
		int left, top;

		comuteRadius(layoutWidth);
		childHeight = childWidth = (int) (radius / 5);

		float angleDelay = 360f / childCount;

		for (int i = 0; i < childCount; i++) {
			final ChildView child = (ChildView) getChildAt(i);
			if (child.getVisibility() == GONE) {
				continue;
			}

			if (angle > 360) {
				angle -= 360;
			} else if (angle < 0) {
				angle += 360;
			}

			child.setAngle(angle);
			child.setPosition(i);

			left = (int) (((getWidth() / 2) - childWidth / 2) + radius
                    * Math.cos(Math.toRadians(angle)));
			top = (int) (radius + radius* Math.sin(Math.toRadians(angle)) + childHeight / 2);

			child.layout(left, top, left + childWidth, top + childHeight);

			angle += angleDelay;
		}
		
	}


	private void rotateButtons(float degrees) {  
		int left, top, childCount = getChildCount();
		float angleDelay = 360f / childCount;
		angle += degrees;

		if (angle > 360) {
			angle -= 360;
		} else if (angle < 0) {
		    angle += 360;
		}

		for (int i = 0; i < childCount; i++) {
			if (angle > 360) {
				angle -= 360;
			} else if (angle < 0) {
				angle += 360;
			}

			final ChildView child = (ChildView) getChildAt(i);
			if (child.getVisibility() == GONE) {
				continue;
			}
			left = (int) (((getWidth() / 2) - childWidth / 2) + radius
							* Math.cos(Math.toRadians(angle)));
			top = (int) (radius + radius
                    * Math.sin(Math.toRadians(angle) ) + childHeight / 2);
			child.setAngle(angle);
			child.layout(left, top, left + childWidth, top + childHeight);
			angle += angleDelay;
		}
		
	}

	/**
	 * @return The angle of the unit circle with the image view's center
	 */
	private double getAngle(double xTouch, double yTouch) {
		double x = xTouch - (circleWidth / 2d);
		double y = radius - yTouch ;

		switch (getQuadrant(x, y)) {
			case 1:
				return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;

			case 2:
			case 3:
				return 180 - (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);

			case 4:
				return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;

			default:
				// ignore, does not happen
				return 0;
		}
	}

	/**
	 * @return The selected quadrant.
	 */
	private static int getQuadrant(double x, double y) {
		if (x >= 0) {
			return y >= 0 ? 1 : 4;
		} else {
			return y >= 0 ? 2 : 3;
		}
	}

	private double startAngle;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    
		if (isEnabled()) {
		    boolean result = false;
			if (isRotating) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						// reset the touched quadrants
						for (int i = 0; i < quadrantTouched.length; i++) {
							quadrantTouched[i] = false;
						}
						mIsInCenter = computeIsInCenter(event);
						mIsInRing = false;
			            if (!mIsInCenter) {
			                mIsInRing = computeIsInCircleRing(event);
			            } 
			            if(mIsInRing || mIsInCenter){
    			            allowRotating = false;
                            allowRotateCenter = true;
                            startAngle = getAngle(event.getX(), event.getY());
                            result = true;
                        }
						break;
					case MotionEvent.ACTION_MOVE:
					    if(mIsInCenter){
					        result = true;
					    }else if(mIsInRing){
    						double currentAngle = getAngle(event.getX(),
    								event.getY());
    						rotateButtons((float) (startAngle - currentAngle));
    						startAngle = currentAngle;
    						result = true;
						}
						break;
					case MotionEvent.ACTION_UP:
					    mNeedCallListen = true;
						allowRotating = true;
						if (mIsInCenter || mIsInRing) {
			                if(mIsInRing){
			                    CircleLayout.this.postDelayed(new RotateCenterRunnable(), 100);
			                }
			                result = true;
			            } 
						break;
				}
			}

			// set the touched quadrant to true
			quadrantTouched[getQuadrant(event.getX() - (circleWidth / 2),
					circleHeight - event.getY() - (circleHeight / 2))] = true;
			result |= mGestureDetector.onTouchEvent(event);
	        return result;
			
		}
		return false;
	}

	private class MyGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (!isRotating) {
				return false;
			}
			if(!mIsInRing){
			    return false;
			}
			allowRotateCenter = false;
			// get the quadrant of the start and the end of the fling
			int q1 = getQuadrant(e1.getX() - (circleWidth / 2), circleHeight
					- e1.getY() - (circleHeight / 2));
			int q2 = getQuadrant(e2.getX() - (circleWidth / 2), circleHeight
					- e2.getY() - (circleHeight / 2));

			// the inversed rotations
			if ((q1 == 2 && q2 == 2 && Math.abs(velocityX) < Math
					.abs(velocityY))
					|| (q1 == 3 && q2 == 3)
					|| (q1 == 1 && q2 == 3)
					|| (q1 == 4 && q2 == 4 && Math.abs(velocityX) > Math
							.abs(velocityY))
					|| ((q1 == 2 && q2 == 3) || (q1 == 3 && q2 == 2))
					|| ((q1 == 3 && q2 == 4) || (q1 == 4 && q2 == 3))
					|| (q1 == 2 && q2 == 4 && quadrantTouched[3])
					|| (q1 == 4 && q2 == 2 && quadrantTouched[3])) {

				CircleLayout.this.post(new FlingRunnable(-1
						* (velocityX + velocityY)));
			} else {
				// the normal rotation
				CircleLayout.this
						.post(new FlingRunnable(velocityX + velocityY));
			}

			return true;

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
		    if(!mIsInRing){
                return false;
            }
		    mTappedView = null;
		    allowRotateCenter = false;
			mTappedViewsPostition = pointToPosition(e.getX(), e.getY());
			if (mTappedViewsPostition >= 0) {
				mTappedView = getChildAt(mTappedViewsPostition);
				mTappedView.setPressed(true);
			} else {
				float centerX = circleWidth / 2;
				float centerY = circleHeight / 2;

				if (e.getX() < centerX + (childWidth / 2)
						&& e.getX() > centerX - childWidth / 2
						&& e.getY() < centerY + (childHeight / 2)
						&& e.getY() > centerY - (childHeight / 2)) {
					if (mOnCenterClickListener != null) {
						mOnCenterClickListener.onCenterClick();
						return true;
					}
				}
			}

			if (mTappedView != null) {
				ChildView view = (ChildView) (mTappedView);
				if (selected != mTappedViewsPostition) {
				    
					rotateViewToCenter(view, false);
					if (!rotateToCenter) {
						if (mOnItemSelectedListener != null && mNeedCallListen) {
							mOnItemSelectedListener.onItemSelected(mTappedView,
									view.getName());
						}

						
					}
				} 
				
				return true;
			}
			return super.onSingleTapUp(e);
		}
	}

	/**
	 * Rotates the given view to the center of the menu.
	 * 
	 * @param view
	 *            the view to be rotated to the center
	 * @param fromRunnable
	 *            if the method is called from the runnable which animates the
	 *            rotation then it should be true, otherwise false
	 */
	private void rotateViewToCenter(ChildView view, boolean fromRunnable) {
		if (rotateToCenter) {
			float velocityTemp = 1;
			float destAngle = (float) (firstChildPos - view.getAngle());
			float startAngle = 0;
			int reverser = 1;
			if(destAngle == 0){
			    return ;
			}
			if (destAngle < 0) {
				destAngle += 360;
			}

			if (destAngle > 180) {
				reverser = -1;
				destAngle = 360 - destAngle;
			}

			while (startAngle < destAngle) {
				velocityTemp *= deceleration;
				startAngle += velocityTemp / speed;
			}

			CircleLayout.this.post(new FlingRunnable(reverser * velocityTemp,
					!fromRunnable));
		}
	}
	
	
	private void rotateComputeViewToCenter() {
        if (rotateToCenter) {
            int childCount = getChildCount();
            float childDelayAngle = 360f / childCount;
            float degree = 0f;
            ChildView selectView = null;
            for (int i = 0; i < childCount; i++) {
                ChildView child = (ChildView) getChildAt(i);
                float childAngle = child.getAngle();
                if(Math.abs(childAngle - firstChildPos) < childDelayAngle/2f){
                    degree = firstChildPos - childAngle;
                    selectView = child;
                    break;
                }
            }
            rotateButtons(degree);
            if(mOnItemSelectedListener != null && mNeedCallListen){
                mOnItemSelectedListener.onItemSelected(selectView, selectView.getName());
                selected = selectView.getPosition();
            }
        }
    }

	/**
	 * A {@link Runnable} for animating the menu rotation.
	 */
	private class FlingRunnable implements Runnable {

		private float velocity;

		public FlingRunnable(float velocity) {
			this(velocity, true);
		}

		public FlingRunnable(float velocity, boolean isFirst) {
			this.velocity = velocity;
		}

		public void run() {
			if (allowRotating) {
				if (rotateToCenter) {
					if (Math.abs(velocity) > 1) {
						rotateButtons(velocity / speed);
						velocity /= deceleration;
						CircleLayout.this.post(this);
					} else {
					    rotateComputeViewToCenter();
					}
				}
			}
		}
	}
	
	private class RotateCenterRunnable implements Runnable {

        public void run() {
            if (allowRotating && allowRotateCenter) {
                allowRotateCenter = false;
                rotateComputeViewToCenter();
            }
        }
    }

	private int pointToPosition(float x, float y) {
	    int result = -1;
		for (int i = 0; i < getChildCount(); i++) {
			View item = (View) getChildAt(i);
			if (item.getLeft() < x && item.getRight() > x & item.getTop() < y
					&& item.getBottom() > y) {
			    result = i;
			    break;
			}
		}
		return result;
	}
	
	public void rotateViewToCenter(String letter) {
        mNeedCallListen = false;
        char letterString = letter.charAt(0);
        ChildView item = null;
        int childCount = getChildCount();
        for (int i = 0;i < childCount;i++) {
            ChildView child = (ChildView) getChildAt(i);
            String name = child.getName();
            if(letterString == name.charAt(0)){
                item = child;
                break;
            }
            
        }
        rotateViewToCenter(item,false);
    }


	public abstract interface OnItemSelectedListener {
		void onItemSelected(View view, String name);
	}

	public void setOnItemSelectedListener(
			OnItemSelectedListener onItemSelectedListener) {
		this.mOnItemSelectedListener = onItemSelectedListener;
	}

	public interface OnCenterClickListener {
		void onCenterClick();
	}

	public void setOnCenterClickListener(
			OnCenterClickListener onCenterClickListener) {
		this.mOnCenterClickListener = onCenterClickListener;
	}

	public interface OnRotationFinishedListener {
		void onRotationFinished(View view, String name);
	}

}