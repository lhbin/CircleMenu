package com.lhb.circleMenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.lhb.circleMenu.CircleLayout;



public class CircleMenuActivity extends Activity {

    private ArcGallery myHorizontalListView;
    private CarouselAdapter myAdapter;
    private FrameLayout view;

    private CircleLayout mCircleView;
    private final String[] mLetters = { "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z" };
    private String mSelectedLetter;

    /** Called when the activity is first created. */
    @SuppressLint("ShowToast")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        view = (FrameLayout) findViewById(R.id.linear_layout);
        myHorizontalListView = (ArcGallery) findViewById(R.id.horizontallistview);
        mCircleView = (CircleLayout) findViewById(R.id.circleView);
        for (int i = 0; i < 26; i++) {
            ChildView imageView = new ChildView(this);
            imageView.setText(mLetters[i]);
            imageView.setTextColor(Color.BLACK);
            imageView.setGravity(Gravity.CENTER);
            imageView.setName("" + mLetters[i]);
            mCircleView.addView(imageView);
            
        }
        
        mCircleView.setOnItemSelectedListener(new CircleLayout.OnItemSelectedListener() {
            
            @Override
            public void onItemSelected(View view, String name) {
                if (name != null && !name.isEmpty()) {
                    int position = myAdapter.getLetterPosition(name.charAt(0));
                    if (position != -1) {
                        myHorizontalListView.setSelection(position);
                    }
                }
            }
        });
        myAdapter = new CarouselAdapter(this);
        myHorizontalListView.setAdapter(myAdapter);
        myHorizontalListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1,
                    int position, long id) {
                String letter = (String) myAdapter.getItem(position);
                
                if (letter != null) {
                    mCircleView.rotateViewToCenter(letter);
                    Toast.makeText(CircleMenuActivity.this, letter, Toast.LENGTH_SHORT).show();
                }
            }
        });

        myHorizontalListView
                .setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                            View view, int position, long id) {
                        String letter = (String) myAdapter.getItem(position);
                        Log.e("onItemSelected", "letter :" + letter);
                        
                        if (letter != null) {
                            mCircleView.rotateViewToCenter(letter);
                            Toast.makeText(CircleMenuActivity.this, letter, Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
        
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}