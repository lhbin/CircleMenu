package com.lhb.circleMenu;



import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.TextView;



public class ChildView extends TextView {

	private float angle = 0;
	private int position = 0;
	private String name;

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}

	/**
	 * @param context
	 */
	public ChildView(Context context) {
		this(context, null);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ChildView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ChildView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs,
					R.styleable.ChildView);
			
			name = a.getString(R.styleable.ChildView_name);
		}
	}

}
