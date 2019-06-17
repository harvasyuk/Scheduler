package com.scheduler.settings;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scheduler.LessonTime;
import com.scheduler.R;
import com.scheduler.SimpleDividerItemDecoration;
import com.scheduler.logic.ScheduleManager;
import com.scheduler.logic.TimeManager;
import com.scheduler.logic.TimeViewModel;
import com.scheduler.logic.Utils;

import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class ScheduleEditor extends AppCompatActivity {

    private ScheduleAdapter scheduleAdapter;
    private Button addButton;
    private Button saveButton;
    private RecyclerView recyclerView;
    private TimeViewModel timeViewModel;
    private LessonTime lessonTime;
    private ScheduleManager scheduleManager;
    private boolean insert = false;
    public boolean upEnabled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_editor_activity);

        upEnabled = true;
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(upEnabled);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        scheduleManager = new ScheduleManager(this.getApplication());

        addButton = findViewById(R.id.add_item);
        saveButton = findViewById(R.id.save_items);
        recyclerView = findViewById(R.id.timeTableRecycler);
        recyclerView.setHasFixedSize(true);

        saveButton.setText(R.string.save_timetable);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        scheduleAdapter = new ScheduleAdapter(this);

        updateLiveData();

        recyclerView.setAdapter(scheduleAdapter);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
    }


    private void updateLiveData() {

        timeViewModel = ViewModelProviders.of(this).get(TimeViewModel.class);
        timeViewModel.getTimeList().observe(this, new androidx.lifecycle.Observer<List<LessonTime>>() {
            @Override
            public void onChanged(List<LessonTime> timeList) {
                scheduleAdapter.updateTimeList(timeList);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleAdapter.addItem(true);
                recyclerView.smoothScrollToPosition(scheduleAdapter.getItemCount() - 1);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scheduleAdapter.getLessons().size() > 0) {
                    TimeManager timeManager = new TimeManager(getApplication());
                    timeManager.uploadData(scheduleAdapter.getLessons());
                }
                fillSchedule();
            }
        });

        scheduleAdapter.setOnItemClick(new ScheduleAdapter.OnItemClick() {
            @Override
            public void getPosition(int position, char state, String time) {

                setTimeData(position, state);
            }
        });
    }



    private void setTimeData(final int position, final char state) {
        int listSize = scheduleAdapter.getLessons().size();
        String time;
        lessonTime = null;
        String oldTimeStart = null;
        String oldTimeEnd = null;

        try {
            lessonTime = scheduleAdapter.getLessonTime(position);
            oldTimeStart = lessonTime.getLessonStart();
            oldTimeEnd = lessonTime.getLessonEnd();
        } catch (Exception ignored) { }

        if (listSize == 0 && lessonTime == null) {
            insert = true;
            lessonTime = new LessonTime(position);
            time = "08:00";
            showPicker(time, state);
        } else {
            if (state == 's' && oldTimeStart == null) {
                insert = true;
                lessonTime = scheduleAdapter.getLessonTime(position - 1);
                time = lessonTime.getLessonEnd();
                lessonTime = new LessonTime(position);
                showPicker(time, state);
            }
            else if (state == 'e' && oldTimeEnd == null) {
                try {
                    lessonTime = scheduleAdapter.getLessonTime(position);
                    time = lessonTime.getLessonStart();
                    showPicker(time, state);
                } catch (Exception e) {
                    Utils.showToast(this, R.string.start_field_empty);
                }
            } else if (state == 's') {
                showPicker(oldTimeStart, state);
            } else if (state == 'e') {
                showPicker(oldTimeEnd, state);
            }
        }
    }


    public void showPicker(String time, final char state) {
        String[] timeArray = time.split(":");
        int hour = Integer.parseInt(timeArray[0]);
        int minute = Integer.parseInt(timeArray[1]);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
                        String newTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);

                        if (state == 's' && lessonTime.getLessonEnd() != null) {
                            lessonTime.setLessonStart(newTime);
                        } else if (state == 's') {
                            lessonTime.setLessonStart(newTime);
                            lessonTime.setLessonEnd(newTime);
                        } else if (state == 'e') {
                            lessonTime.setLessonEnd(newTime);
                        }

                        if (insert) {
                            timeViewModel.insert(lessonTime);
                            scheduleManager.insertEmptyLesson();
                            insert = false;
                            scheduleAdapter.addItem(false);
                        } else {
                            timeViewModel.update(lessonTime);
                        }
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }


    public void fillSchedule() { }

}
