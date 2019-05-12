package com.example.alarmclock;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.PreferenceScreen);
        super.onCreate(savedInstanceState);

        SharedPreferences mSettings = getSharedPreferences("alarms", Context.MODE_PRIVATE);
        mEditor = mSettings.edit();

        // DISPLAY THE FRAGMENT AS THE MAIN CONTENT.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public void onPause() {
        // CHECK IF SETTINGS ACTIVITY CREATED.
        mEditor.putString("settingsActivityFlag", "1");
        mEditor.commit();
        super.onPause();
    }

    public void onStop() {
        // CHECK IF SETTINGS ACTIVITY DESTROYED.
        mEditor.putString("settingsActivityFlag", "0");
        mEditor.commit();
        super.onStop();
    }


    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);
        }

        // FIX COLORS. REMOVE THIS IF IT CAUSES ISSUES.
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // REMOVE DIVIDERS.
            View rootView = getView();
            ListView list = (ListView) rootView.findViewById(android.R.id.list);
            list.setDivider(new ColorDrawable(Color.parseColor("#FF313F46")));
            list.setDividerHeight(5);
        }
    }
}
