package com.scheduler.firstSetting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.scheduler.R;
import com.scheduler.logic.ScheduleManager;

public class WeekActivity extends AppCompatActivity {

    private Button button1;
    private Button button2;
    private Button button3;
    private Button aheadButton;
    private int weekCount;
    private SharedPreferences sharedPref;
    private ScheduleManager scheduleManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week);

        sharedPref = this.getSharedPreferences(
                getString(R.string.common_preferences), Context.MODE_PRIVATE);
        scheduleManager = new ScheduleManager(getApplication());

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        aheadButton = findViewById(R.id.aheadButton);
        aheadButton.setEnabled(false);

        button1.setOnClickListener(view -> {
            button1.setBackground(getDrawable(R.drawable.blue_button_round_selector));
            button2.setBackground(getDrawable(R.drawable.white_background));
            button3.setBackground(getDrawable(R.drawable.white_background));
            weekCount = 1;
            aheadButton.setEnabled(true);
        });

        button2.setOnClickListener(view -> {
            button2.setBackground(getDrawable(R.drawable.blue_button_round_selector));
            button1.setBackground(getDrawable(R.drawable.white_background));
            button3.setBackground(getDrawable(R.drawable.white_background));
            weekCount = 2;
            aheadButton.setEnabled(true);
        });

        button3.setOnClickListener(view -> {
            button3.setBackground(getDrawable(R.drawable.blue_button_round_selector));
            button1.setBackground(getDrawable(R.drawable.white_background));
            button2.setBackground(getDrawable(R.drawable.white_background));
            weekCount = 3;
            aheadButton.setEnabled(true);
        });

        aheadButton.setOnClickListener(view -> {
            putToSharedPref();
            startActivity(new Intent(WeekActivity.this, ScheduleEditorStartActivity.class));
            finish();
        });
    }


    private void putToSharedPref() {
        scheduleManager.uploadWeekCount(weekCount);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.week_count), weekCount);
        editor.apply();
    }

}
