package com.example.alarmclock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class FishUpActivity extends AppCompatActivity implements SensorEventListener {
    private Sensor mySensor;
    private SensorManager SM;
    private float[] gravity = new float[3];
    private int correct = 0;
    private ImageView panel_IMG_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish_up);
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Accelerometer Sensor
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Register sensor Listener
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
        panel_IMG_back = (ImageView) findViewById(R.id.backgroudviewcatch);
        Glide
                .with(this)
                .load(R.drawable.catchfish)
                .into(panel_IMG_back);

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        gravity[0] = event.values[0];

        if (gravity[0] < -10) {
            correct++;
            vibrate();
        }
        if (correct >= 3) {
            correct = 0;
            Intent intent = new Intent(this, FishDoneActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(400);
    }
}
