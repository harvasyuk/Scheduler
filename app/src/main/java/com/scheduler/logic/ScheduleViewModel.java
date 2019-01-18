package com.scheduler.logic;

import android.app.Application;

import com.scheduler.Lesson;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ScheduleViewModel extends AndroidViewModel {

    private ScheduleRepository repository;
    private LiveData<List<Lesson>> lessons;
    private Application application;


    public ScheduleViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public void setDay(int day) {
        repository = new ScheduleRepository(application, day);
        lessons = repository.getLessons();
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
