package com.example.alarmclock;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

import androidx.legacy.content.WakefulBroadcastReceiver;

public class RingingActivity extends AppCompatActivity {
    private Button fish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);

        fish = (Button)findViewById(R.id.ShutDown);

        fish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFish();
            }
        });
    }

    public void openFish() {
        Intent intent = new Intent(this, FishActivity.class);
        startActivity(intent);
    }
}
