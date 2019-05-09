package com.example.alarmclock;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.media.MediaPlayer;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;

import com.ncorti.slidetoact.SlideToActView;

import org.jetbrains.annotations.NotNull;

public class RingingActivity extends AppCompatActivity {
    private Button fish;
    private static RingingActivity inst;
    MediaPlayer mp;

    public static RingingActivity instance() {
        return inst;
    }
    SlideToActView slideToActView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);

        slideToActView = (SlideToActView)findViewById(R.id.slide_to_unlock);
        slideToActView.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(@NotNull SlideToActView slideToActView) {
                openFish();
            }
        });
        play();
        mp.setLooping(true);

        //RotateAnimation rotate= (RotateAnimation) AnimationUtils.loadAnimation(this,R.anim.rotateAnimation);
        //slideToActView.setAnimation(rotate);
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
        startActivity(intent);
    }

    public void play(){
        if(mp == null) {
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
