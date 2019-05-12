package com.example.alarmclock;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.content.Intent;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ncorti.slidetoact.SlideToActView;

public class RingingActivity extends AppCompatActivity {
    private ViewGroup mainLayout;
    private ImageView image;

    private int xDelta;
    private int yDelta;

    private Button fish;
    private static RingingActivity inst;
    MediaPlayer mp;
    // <p>

    public static RingingActivity instance() {
        return inst;
    }

    SlideToActView slideToActView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);
        mainLayout = (RelativeLayout) findViewById(R.id.alarm_ringing);
        image = (ImageView) findViewById(R.id.worm);

        play();
        mp.setLooping(true);

        image.setOnTouchListener(onTouchListener());
    }
    /**
     <p>
     slideToActView = (SlideToActView)findViewById(R.id.slide_to_unlock);
     slideToActView.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {

    @Override public void onSlideComplete(@NotNull SlideToActView slideToActView) {
    openFish();
    }
    });
     play();
     mp.setLooping(true);
     <p>
     //RotateAnimation rotate= (RotateAnimation) AnimationUtils.loadAnimation(this,R.anim.rotateAnimation);
     //slideToActView.setAnimation(rotate);
     }
     */

    /**
     * // Try to move worm
     *
     * @Override protected void onCreate(Bundle savedInstanceState) {
     * super.onCreate(savedInstanceState);
     * setContentView(R.layout.activity_alarm_ringing);
     * <p>
     * mainLayout = (RelativeLayout) findViewById(R.id.alarm_ringing);
     * image = (ImageView) findViewById(R.id.worm);
     * <p>
     * image.setOnTouchListener(onTouchListener());
     * }
     */

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

                    case MotionEvent.ACTION_UP:
                        Toast.makeText(RingingActivity.this,
                                "I'm here!", Toast.LENGTH_SHORT)
                                .show();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                                .getLayoutParams();
                        layoutParams.leftMargin = x - xDelta;
                        layoutParams.topMargin = y - yDelta;
                        layoutParams.rightMargin = 0;
                        layoutParams.bottomMargin = 0;
                        view.setLayoutParams(layoutParams);
                        break;
                }
                mainLayout.invalidate();
                return true;
            }
        };
    }
//End try move worm

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
        startActivity(intent);
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
