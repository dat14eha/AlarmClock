package com.example.alarmclock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;

public class FishDoneActivity extends AppCompatActivity {
    private MediaPlayer mpSucess;
    private CountDownTimer cTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish_done);
        mpSucess = MediaPlayer.create(this, R.raw.success);
        mpSucess.start();
        startTimer();
       // Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
       // v.cancel();
    }

    void startTimer() {
        cTimer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                Intent main = new Intent(FishDoneActivity.this, MainActivity.class);
                startActivity(main);
            }
        };
        cTimer.start();
    }
}
