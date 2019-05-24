package com.example.alarmclock;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;

import android.content.Intent;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class RingingActivity extends AppCompatActivity {
    private ViewGroup mainLayout;
    private ImageView worm;
    private ImageView fish;

    private int xDelta;
    private int yDelta;

    private static RingingActivity inst;
    MediaPlayer mp;

    public static RingingActivity instance() {
        return inst;
    }

    //SlideToActView slideToActView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);
        mainLayout = (RelativeLayout) findViewById(R.id.alarm_ringing);
        worm = findViewById(R.id.worm);
        fish = findViewById(R.id.fish);

        play();
        mp.setLooping(true);

        worm.setOnTouchListener(onTouchListener());
    }

    private View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
                                view.getLayoutParams();

                        xDelta = x - lParams.leftMargin;
                        yDelta = y - lParams.topMargin;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                                .getLayoutParams();
                        layoutParams.leftMargin = x - xDelta;
                        layoutParams.topMargin = y - yDelta;
                        layoutParams.rightMargin = 0;
                        layoutParams.bottomMargin = 0;

                        if(worm.getTop()>fish.getTop()+200 && worm.getLeft()>fish.getLeft() -100) {
                            openFish();
                        }
                        view.setLayoutParams(layoutParams);
                        break;
                }
                mainLayout.invalidate();
                return true;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();  // Always call the superclass method first
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void openFish() {
        Intent intent = new Intent(this, FishActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    public void play() {
        if (mp == null) {
            mp = MediaPlayer.create(this, R.raw.alarm1);
        }
        mp.start();
    }

    public void stop() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }
}
