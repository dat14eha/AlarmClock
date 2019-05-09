package com.example.alarmclock;


import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmAdapter extends ArrayAdapter<Alarm> {
    private Context mContext;
    private int id;
    public ArrayList<Alarm> alarms;
    private Calendar theTime;
    private View mainActivity;

    public AlarmAdapter(Context context, int textViewResourceId, ArrayList<Alarm> list) {
        super(context, textViewResourceId, list);
        mContext = context;
        id = textViewResourceId;
        alarms = list;
    }
    @Override
    public View getView(int pos, View v, ViewGroup parent) {
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(id, null);
        }

        final int position = pos;
        final TextView timeView = (TextView) v.findViewById(R.id.timeView);
        final Switch onOffSwitch = (Switch) v.findViewById(R.id.onOffSwitch);
        final Button deleteButton = (Button) v.findViewById(R.id.deleteAlarm);
        deleteButton.setTag(alarms.get(position));

        // INITIAL SETTINGS.
        if (alarms.get(position) != null) {
            timeView.setTextColor(Color.WHITE);
            timeView.setText(alarms.get(position).getTime());
            onOffSwitch.setChecked(alarms.get(position).state());
        }

        // TURN SWITCH OFF IF ALARM HAS GONE OFF.
        if (alarms.get(position).getAlarm().compareTo(Calendar.getInstance()) <= 0) {
            alarms.get(position).setAlarm(false);
            onOffSwitch.setChecked(false);
        }

        // LISTEN FOR THE ON/OFF SWITCH AND TURN ON/OFF CORRESPONDING ALARM.
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int pos;
                if(alarms.size() > 1) {
                    pos = position;
                } else {
                    pos = 0;
                }
                if(alarms.get(pos).state() != isChecked){
                    alarms.get(pos).setAlarm(isChecked);
                    if(isChecked){
                        displayAlarm(alarms.get(pos).getAlarm());
                    }
                }
            }
        });

        // FANCY LISTENER FOR OUR TIME PICKER DIALOG.
        final TimePickerDialog.OnTimeSetListener timePicker = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar newAlarm = Calendar.getInstance();
                theTime = Calendar.getInstance();

                // SET ALARM TIME.
                newAlarm.set(Calendar.HOUR_OF_DAY, hourOfDay);
                newAlarm.set(Calendar.MINUTE, minute);
                newAlarm.set(Calendar.SECOND, 0);
                newAlarm.set(Calendar.MILLISECOND, 0);

                // TODAY'S SET TIME HAS PASSED, COUNT TO TOMORROW INSTEAD.
                if (newAlarm.compareTo(theTime) <= 0) {
                    newAlarm.add(Calendar.DATE, 1);
                }
                alarms.get(position).setTime(newAlarm);
                alarms.get(position).setAlarm(true);
                timeView.setText(alarms.get(position).getTime());
                onOffSwitch.setChecked(true);
                displayAlarm(alarms.get(position).getAlarm());
            }
        };

        // LISTEN FOR A CLICK ON EDIT TIME.
        timeView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                theTime = alarms.get(position).getAlarm();
                TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(), timePicker, theTime.get(Calendar.HOUR_OF_DAY), theTime.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });

        return v;
    }

    // SHOW THE USER HOW FAR INTO THE FUTURE THE ALARM IS.
    public void displayAlarm(Calendar alarm) {
        int displayHour = 0;
        int displayMinutes = 0;
        Calendar newAlarm = alarm;
        theTime = Calendar.getInstance();
        String hourAmount;
        String minuteAmount;

        // TODAY'S SET TIME HAS PASSED, COUNT TO TOMORROW INSTEAD.
        if (newAlarm.get(Calendar.DATE) > theTime.get(Calendar.DATE)) {
            displayHour = 24;
        }

        displayHour = Math.abs(displayHour - Math.abs(newAlarm.get(Calendar.HOUR_OF_DAY) - theTime.get(Calendar.HOUR_OF_DAY)));
        if (displayHour == 1) {
            hourAmount = " hour";
        } else {
            hourAmount = " hours";
        }
        if (newAlarm.get(Calendar.MINUTE) - theTime.get(Calendar.MINUTE) != 0) {
            displayMinutes = newAlarm.get(Calendar.MINUTE) - theTime.get(Calendar.MINUTE);
            if (displayMinutes < 0) {
                displayHour--;
                displayMinutes = 60 - Math.abs(displayMinutes);
            }
            if (displayMinutes == 1) {
                minuteAmount = " minute";
            } else {
                minuteAmount = " minutes";
            }
            if (displayHour == 0) {
                Snackbar.make(mainActivity, "Alarm set for " + displayMinutes + minuteAmount + " from now", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(mainActivity, "Alarm set for " + displayHour + hourAmount + " and " + displayMinutes + minuteAmount + " from now", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(mainActivity, "Alarm set for " + displayHour + hourAmount + " from now", Snackbar.LENGTH_LONG).show();
        }
    }

    public void setView(View view) {
        mainActivity = view;
    }

    // ADDS ALARM TO THE ALARMLIST SHARED ACROSS THE APP.
    public void addAlarm(Alarm alarm) {
        alarms.add(alarm);
        displayAlarm(alarm.getAlarm());
        this.notifyDataSetChanged();
    }

    // SPECIFIC VERSION OF THE addAlarm()-METHOD FOR STARTUP.
    public void loadAlarm(Alarm alarm) {
        alarms.add(alarm);
        this.notifyDataSetChanged();
    }
}
