package com.lhb.circleMenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;

import com.lhb.circleMenu.CircleView.OnItemSelectedListern;

public class CircleMenuActivity extends Activity {

    private ArcGallery myHorizontalListView;
    private CarouselAdapter myAdapter;
    private FrameLayout view;

    private CircleView mCircleView;

    /** Called when the activity is first created. */
    @SuppressLint("ShowToast")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        view = (FrameLayout) findViewById(R.id.linear_layout);
        myHorizontalListView = (ArcGallery) findViewById(R.id.horizontallistview);
        mCircleView = (CircleView) findViewById(R.id.circleView);
        mCircleView.initViewResource();
        mCircleView.setOnItemSelectedListern(new OnItemSelectedListern() {

            @Override
            public void onItemSelected(String x) {
                if (x != null && !x.isEmpty()) {
                    int position = myAdapter.getLetterPosition(x.charAt(0));
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