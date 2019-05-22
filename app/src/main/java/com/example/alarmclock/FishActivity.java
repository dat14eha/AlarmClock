package com.example.alarmclock;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.app.Activity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.hardware.SensorEventListener;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.media.MediaPlayer;

import java.util.ArrayList;

public class FishActivity extends AppCompatActivity  implements SensorEventListener {
    private TextView testText;
    private Sensor mySensor;
    private SensorManager SM;
    private Button startThrowGameButton;
    private boolean buttonDown = false;

    private MediaPlayer mpSwoosh, mpSplash;
    private TextView gyroText;
    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];

    private ArrayList<float[]> accValues;
    private float xAcc;
    private float yAcc;
    private float zAcc;
    private float totAcc;
    private float angle;
    private double velocity;
    private double yVelocity;


    private Button throwButtonDone;



    private double distance;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish);
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        mpSplash = MediaPlayer.create(this, R.raw.rod_splah);
        mpSwoosh = MediaPlayer.create(this, R.raw.rod_swoosh);
        // Accelerometer Sensor
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Register sensor Listener
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
        testText = (TextView)findViewById(R.id.testText);

        startThrowGameButton = findViewById(R.id.startThrowGameButton);
        startThrowGameButton.setBackgroundColor(android.R.color.transparent);
        accValues = new ArrayList<float[]>();

        startThrowGameButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    accValues.clear();
                    xAcc = 0;
                    yAcc = 0;
                    zAcc = 0;
                    buttonDown = true;
                    vibrate();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    calcAngle(accValues);
                    buttonDown = false;

                    vibrate();
                    return true;
                }
                return false;
                }
            });
        /*
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        accValues.clear();
                        xAcc = 0;
                        yAcc = 0;
                        zAcc = 0;
                        buttonDown = true;
                        vibrate();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        buttonDown = false;
                        calcAngle(accValues);
                        vibrate();
                    }
                    return true;
            }
        });

        throwButtonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        throwButtonDone.setVisibility(View.INVISIBLE);
        */
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        // alpha is calculated as t / (t + dT)
        // with t, the low-pass filter's time-constant
        // and dT, the event delivery rate

        final float alpha = 0.8f;
        if(buttonDown) {
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            //With low pass filtering
            //linear_acceleration[0] = event.values[0] - gravity[0];
            //linear_acceleration[1] = event.values[1] - gravity[1];
            //linear_acceleration[2] = event.values[2] - gravity[2];

            //Without low pass filtering
            linear_acceleration[0] = event.values[0];
            linear_acceleration[1] = event.values[1];
            linear_acceleration[2] = event.values[2];
            accValues.add(linear_acceleration);


        }
    }



    public void vibrate(){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(200);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not in use
    }




    public void calcAngle(ArrayList<float[]> values){
        for(int i = 0; i < values.size(); i++){

                if (values.get(i)[0] > 0 ) {
                    xAcc += values.get(i)[0];
                }
                if (values.get(i)[1] > 0 ){
                    yAcc += values.get(i)[1];
                }

        }
        //Should be divided by values.size() to get the average acceleration, but values will be to low.
        xAcc = Math.abs(xAcc);
        yAcc = Math.abs(yAcc);
        if(xAcc > 2000){
            xAcc = 1500;
        }
        if(yAcc > 2000){
            yAcc = 1500;
        }
        totAcc = xAcc + yAcc;


        //calculate the angle as a percentage of the acceleration in x compared to the total (in x and y direction) acceleration.
        angle = 90 - Math.abs(((xAcc / totAcc) * 90));
        double throwTime = (values.size()*1.0)/100;
        Log.d("THROW", Double.toString(throwTime));
        if(throwTime >= 0.05) {
            velocity = (1.2 / throwTime )+ (xAcc / values.size() )* throwTime;
            yVelocity = velocity * Math.sin(Math.toRadians(angle));
            double maxHeightTime = yVelocity / 9.82;
            double throwHeight = 1.7;
            double maxHeight = yVelocity * maxHeightTime - (0.5 * 9.82 * Math.pow(maxHeightTime, 2));
            double totHeight = throwHeight + maxHeight;
            double totAirTime = Math.sqrt(totHeight * 2 / 9.82) + maxHeightTime;
            distance = velocity * Math.cos(Math.toRadians(angle)) * totAirTime;
            if(Double.isNaN(distance)){
                distance = 0.0;
            }
            if(distance < 10){
                testText.setTextSize(25);
                testText.setText("Your big fish must be somewhere, THROW HARDER");
            }else {
                testText.setTextSize(25);
                testText.setText("Distance thrown" + "\n" + String.format("%.2f", distance) + "meter");
                startThrowGameButton.setVisibility(View.INVISIBLE);
                mpSwoosh.start();
                mpSwoosh.setNextMediaPlayer(mpSplash);
            }
        }else{
            testText.setTextSize(25);
            testText.setText("Hold the button longer during the throw");
            startThrowGameButton.setVisibility(View.VISIBLE);
        }
    }

}

