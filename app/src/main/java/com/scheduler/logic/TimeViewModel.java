package com.scheduler.logic;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.scheduler.LessonTime;

import java.util.List;

public class TimeViewModel extends AndroidViewModel {

    private TimeRepository repository;
    private LiveData<List<LessonTime>> timeList;

    public TimeViewModel(@NonNull Application application) {
        super(application);
        repository = new TimeRepository(application);
        timeList = repository.getTimeList();
    }

    public void insert(LessonTime lessonTime) {
        repository.insert(lessonTime);
    }

    public void update(LessonTime lessonTime) {
        repository.update(lessonTime);
    }

    public void delete(LessonTime lessonTime) {
        repository.delete(lessonTime);
    }

    public LiveData<List<LessonTime>> getTimeList() {
        return timeList;
    }
}
