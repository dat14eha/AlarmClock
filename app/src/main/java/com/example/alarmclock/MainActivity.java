package com.example.alarmclock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 500;
    private TimePickerDialog.OnTimeSetListener timeEndSetListener;
    private Calendar theTime;
    private ListView listView;
    public ArrayList<Alarm> alarmList;
    private ArrayList<String> saveAlarmList;
    private ArrayList<String> saveAlarmIdList;
    private ArrayList<String> saveAlarmState;
    public AlarmAdapter alarmAdapter;
    public int alarmId;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSettings;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // LOAD INITIAL PREFERENCE VALUES TO AVOID EVIL CRASHES.
        PreferenceManager.setDefaultValues(this, R.xml.pref_settings, false);

        // CLOSE THE ACTIVITY IF WE'RE IN AN ANOTHER APP.
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        alarmList = new ArrayList<>();
        alarmAdapter = new AlarmAdapter(this, R.layout.alarm_list, alarmList);

        // SETUP START PAGE VIEW.
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.testView);
        listView.setAdapter(alarmAdapter);
        alarmAdapter.setView(MainActivity.this.listView);

        // GET OUR SAVED ALARMS.
        mSettings = getSharedPreferences("alarms", Context.MODE_PRIVATE);
        GsonBuilder gsonb = new GsonBuilder();
        Gson mGson = gsonb.create();
        String loadValue = mSettings.getString("alarmList", "");
        saveAlarmList = mGson.fromJson(loadValue, ArrayList.class);
        loadValue = mSettings.getString("alarmIdList", "");
        saveAlarmIdList = mGson.fromJson(loadValue, ArrayList.class);
        loadValue = mSettings.getString("alarmState", "");
        saveAlarmState = mGson.fromJson(loadValue, ArrayList.class);

        // CHECK IF WE HAVE ANY SAVED ALARMS.
        if (saveAlarmList != null) {
            Calendar newAlarm;
            String id;
            boolean state;
            Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent pendingIntent;

            for (int i = 0; i < saveAlarmList.size(); i++) {
                id = saveAlarmIdList.get(i);

                // CHECK IF ALARM IS TO BE SET OR NOT
                if (saveAlarmState.get(i).equals("on")) {
                    state = true;
                } else {
                    state = false;
                }

                // CREATE OUR ALARM PARAMETERS.
                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, Integer.parseInt(id), intent, 0);
                newAlarm = Calendar.getInstance();

                // PARSE DATE FROM SAVED ALARMS' ARRAYLIST.
                try {
                    SimpleDateFormat spd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    Date time = spd.parse(saveAlarmList.get(i));
                    newAlarm.setTime(time);
                } catch (ParseException pe) {
                }

                // CREATE ALARM AND ADD TO VIEW.
                Alarm alarm = new Alarm(newAlarm, alarmManager, pendingIntent, Integer.parseInt(id), state);
                alarmAdapter.loadAlarm(alarm);
            }
        } else {
            // FIRST RUN.
            saveAlarmList = new ArrayList<>();
            saveAlarmIdList = new ArrayList<>();
            saveAlarmState = new ArrayList<>();
        }


        // FANCY LISTENER FOR OUR TIME PICKER DIALOG.
        timeEndSetListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar newAlarm = Calendar.getInstance();
                theTime = Calendar.getInstance();

                if (alarmList.size() > 0) {
                    alarmId = Integer.parseInt(alarmList.get(alarmList.size() - 1).getalarmId()) + 1;
                } else {
                    // FIRST RUN
                    alarmId = 1;
                }

                // CREATE OUR ALARM PARAMETERS.
                Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, alarmId, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                // SET ALARM TIME.
                newAlarm.set(Calendar.HOUR_OF_DAY, hourOfDay);
                newAlarm.set(Calendar.MINUTE, minute);
                newAlarm.set(Calendar.SECOND, 0);
                newAlarm.set(Calendar.MILLISECOND, 0);

                // TODAY'S SET TIME HAS PASSED, COUNT TO TOMORROW INSTEAD.
                if (newAlarm.compareTo(theTime) <= 0) {
                    newAlarm.add(Calendar.DATE, 1);
                }

                // CREATE ALARM AND ADD TO VIEW.
                Alarm alarm = new Alarm(newAlarm, alarmManager, pendingIntent, alarmId, true);
                alarmAdapter.addAlarm(alarm);
                save();
            }
        };

        // FLOATING ACTION BUTTON ONCLICK() METHOD POINTS TO openTimePicker().
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePicker(view);
            }
        });

    }

    public void deleteAlarm(View v) {
        Button deleteButton = (Button) v.findViewById(R.id.deleteAlarm);
        final Alarm deleteAlarm = (Alarm) deleteButton.getTag();
        deleteAlarm.setAlarm(false);
        alarmList.remove(deleteAlarm);
        alarmAdapter.notifyDataSetChanged();
        Snackbar.make(v, "You deleted the " + deleteAlarm.getTime() + " alarm", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alarmAdapter.addAlarm(deleteAlarm);
                        deleteAlarm.setAlarm(true);
                        alarmAdapter.notifyDataSetChanged();
                    }
                }).show();
        save();
    }

    // SAVE OUR ALARMS HERE.
    public void save() {
        mSettings = getSharedPreferences("alarms", Context.MODE_PRIVATE);
        mEditor = mSettings.edit();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        saveAlarmList = new ArrayList<>();
        saveAlarmIdList = new ArrayList<>();
        saveAlarmState = new ArrayList<>();

        for (int i = 0; i < alarmList.size(); i++) {
            saveAlarmList.add(sdf.format(alarmList.get(i).getAlarm().getTime()));
            saveAlarmIdList.add(alarmList.get(i).getalarmId());
            if (alarmList.get(i).state()) {
                saveAlarmState.add("on");
            } else {
                saveAlarmState.add("off");
            }
        }

        GsonBuilder gsonb = new GsonBuilder();
        Gson mGson = gsonb.create();
        String writeValue = mGson.toJson(saveAlarmList);
        mEditor.putString("alarmList", writeValue);
        writeValue = mGson.toJson(saveAlarmIdList);
        mEditor.putString("alarmIdList", writeValue);
        writeValue = mGson.toJson(saveAlarmState);
        mEditor.putString("alarmState", writeValue);
        mEditor.commit();
    }

    public void onResume() {
        // UPDATE VIEW BASED ON CHANGES.
        alarmAdapter.notifyDataSetChanged();
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("testAlarm", true)) {
            FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
            fab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openAlarm(view);
                }
            });
            fab2.show();
        } else {
            FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
            fab2.hide();
        }
        super.onResume();
    }

    // LET'S NOT FORGET TO SAVE.
    public void onPause() {
        save();
        // CHECK IF MAIN ACTIVITY CREATED.
        mEditor.putString("mainActivityFlag", "1");
        mEditor.commit();
        super.onPause();
    }

    public void onStop() {
        // CHECK IF MAIN ACTIVITY DESTROYED.
        mEditor.putString("mainActivityFlag", "0");
        mEditor.commit();
        super.onStop();
    }

    // OPENS THE TEST ALARMACTIVITY.
    public void openAlarm(View v) {
        Intent intent = new Intent(this, RingingActivity.class);
        startActivity(intent);
    }

    // OPENS THE TIME PICKER.
    public void openTimePicker(View v) {
        theTime = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                timeEndSetListener, theTime.get(Calendar.HOUR_OF_DAY),
                theTime.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // TODO:
        // CODE TO CHANGE ACTIVITY HERE.
        if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
