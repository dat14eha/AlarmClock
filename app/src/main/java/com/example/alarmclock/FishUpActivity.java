package com.example.alarmclock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;

public class FishUpActivity extends AppCompatActivity implements SensorEventListener {
    private Sensor mySensor;
    private SensorManager SM;
    private float[] gravity = new float[3];
    private int correct = 0;
    private Handler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish_up);

        SM = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Accelerometer Sensor
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Register sensor Listener
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

        myHandler = new Handler();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.8f;
        gravity[0] = event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        while(correct < 5) {
            vibrate();
            if (gravity[0] < -15){
                correct++;
            }
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                }
                //gotoState1();
            }, 250);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(200);
    }
}
