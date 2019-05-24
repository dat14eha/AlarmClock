package com.example.alarmclock;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class SoundService extends Service {
    MediaPlayer mp;

    public SoundService() {
    }

    public void onCreate()
    {
        mp = MediaPlayer.create(this, R.raw.alarm1);
        mp.setLooping(false);
    }
    public void onDestroy()
    {
        mp.stop();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onStart(Intent intent,int startid){
        mp.start();
    }
}