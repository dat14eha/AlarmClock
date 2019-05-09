package com.example.alarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;

import java.util.Calendar;

public class Alarm {
    private Calendar alarm;
    private boolean[] days;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private int alarmId;
    private boolean state;

    public Alarm(Calendar cal, AlarmManager alarmManager, PendingIntent pendingIntent, int alarmId, boolean state) {
        this.alarm = cal;
        this.alarmManager = alarmManager;
        this.pendingIntent = pendingIntent;
        this.alarmId = alarmId;
        this.state = state;
        days = new boolean[7];

        // CHECK IF THE ALARM IS SUPPOSED TO BE OFF - OR IF THE ALARM IS STILL SCHEDULED TO RING.
        if (this.state && this.alarm.compareTo(Calendar.getInstance()) > 0) {
            this.alarmManager.setExact(AlarmManager.RTC_WAKEUP, this.alarm.getTimeInMillis(), this.pendingIntent);
        }
    }

    public String getTime() {
        String hour;
        String minute;
        if ((alarm.get(Calendar.HOUR_OF_DAY) < 10) || (alarm.get(Calendar.MINUTE) < 10)) {
            hour = String.format("%2s", String.valueOf(alarm.get(Calendar.HOUR_OF_DAY))).replace(' ', '0');
            minute = String.format("%2s", String.valueOf(alarm.get(Calendar.MINUTE))).replace(' ', '0');
            return hour + ":" + minute;
        } else {
            return String.valueOf(alarm.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(alarm.get(Calendar.MINUTE));
        }
    }

    public void setAlarm(Boolean newState) {
        state = newState;
        if (state) {
            Calendar theTime = Calendar.getInstance();
            // TODAY'S SET TIME HAS PASSED, COUNT TO TOMORROW INSTEAD.
            if (alarm.compareTo(theTime) <= 0) {
                alarm.set(Calendar.DATE, theTime.get(Calendar.DATE) + 1);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis(), this.pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis(), this.pendingIntent);
            }
        } else {
            alarmManager.cancel(pendingIntent);
        }
    }

    public void setTime(Calendar cal) {
        alarm = cal;
    }

    public void setDay(Boolean state, int day) {
        days[day] = state;
    }

    public String getalarmId() {
        return Integer.toString(alarmId);
    }

    public boolean state() {
        return state;
    }

    public Calendar getAlarm() {
        return alarm;
    }

}

