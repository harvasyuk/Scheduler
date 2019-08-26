package com.scheduler.logic;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.scheduler.Lesson;
import com.scheduler.R;

import java.util.List;

public class ScheduleViewModel extends AndroidViewModel {

    private ScheduleRepository repository;
    private LiveData<List<Lesson>> lessons;
    private MutableLiveData<Integer> week = new MutableLiveData<>();
    private SharedPreferences sharedPref;


    public ScheduleViewModel(@NonNull Application application) {
        super(application);
        sharedPref = application.getSharedPreferences(
                application.getString(R.string.common_preferences), Context.MODE_PRIVATE);
        repository = new ScheduleRepository(application, sharedPref.getInt(application.getString(R.string.current_week), 0));

    }

    public void setWeek(int week) {
        this.week.setValue(week);
    }

    public LiveData<Integer> getWeek() {
        return week;
    }


    public void setLessons(int day) {
        lessons = repository.getLessons(day);
    }

    public void setLessonsWeek(int day, int week) {
        lessons = repository.getLessons(day, week);
    }

    public void insert(Lesson lesson) {
        repository.insert(lesson);
    }

    public void update(Lesson lesson) {
        repository.update(lesson);
    }

    public void delete(Lesson lesson) {
        repository.delete(lesson);
    }

    public LiveData<List<Lesson>> getLessons() {
        return lessons;
    }
}
