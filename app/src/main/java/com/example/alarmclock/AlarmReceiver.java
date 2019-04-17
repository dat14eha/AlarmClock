package com.example.alarmclock;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.View;

import androidx.legacy.content.WakefulBroadcastReceiver;

import static androidx.core.content.ContextCompat.startActivity;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    private MediaPlayer mp;

    @Override
    public void onReceive(final Context context, Intent intent) {
        //this will update the UI with message
        AlarmActivity inst = AlarmActivity.instance();

        //this will sound the alarm tone
        //this will sound the alarm once, if you wish to
        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();

        //this will send a notification message
        ComponentName comp = new ComponentName(context.getPackageName(),
                AlarmService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);

        //inst.changeToRingingScreen();
    }
}