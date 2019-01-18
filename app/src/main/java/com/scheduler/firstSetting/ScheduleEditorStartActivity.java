package com.scheduler.firstSetting;

import android.content.Intent;

import com.scheduler.MainActivity;
import com.scheduler.settings.ScheduleEditor;

public class ScheduleEditorStartActivity extends ScheduleEditor {

    public ScheduleEditorStartActivity() {
        upEnabled = false;
    }


    @Override
    public void fillSchedule() {
        super.fillSchedule();
        startActivity(new Intent(ScheduleEditorStartActivity.this, MainActivity.class));
        finish();
    }
}
