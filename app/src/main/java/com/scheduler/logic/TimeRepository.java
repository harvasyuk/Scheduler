package com.scheduler.logic;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.scheduler.LessonTime;
import com.scheduler.database.LessonDao;
import com.scheduler.database.LessonsDatabase;
import com.scheduler.database.TimeDao;

import java.util.List;

public class TimeRepository {

    private TimeDao timeDao;
    private LiveData<List<LessonTime>> timeList;
    private Application application;

    public TimeRepository(Application application) {
        this.application = application;
        LessonsDatabase lessonsDatabase = LessonsDatabase.getInstance(application);
        timeDao = lessonsDatabase.timeDao();
        timeList = timeDao.getAll();
    }

    LiveData<List<LessonTime>> getTimeList() { return timeList; }

    int getLessonsCount() {
        return timeDao.getLessonsCount();
    }

    void insert(LessonTime lessonTime) {
        new InsertTimeAsyncTask(timeDao).execute(lessonTime);
    }

    void update(LessonTime lessonTime) { new UpdateTimeAsyncTask(timeDao, application).execute(lessonTime); }

    void delete(LessonTime lessonTime) {
        new DeleteTimeAsyncTask(timeDao, application).execute(lessonTime);
    }

    void deleteAll() {
        new DeleteAllTimesAsyncTask(timeDao).execute();
    }


    private static class InsertTimeAsyncTask extends AsyncTask<LessonTime, Void, Void> {
        private TimeDao timeDao;

        private InsertTimeAsyncTask(TimeDao timeDao) {
            this.timeDao = timeDao;
        }

        @Override
        protected Void doInBackground(LessonTime... lessonTimes) {
            try {
                timeDao.insert(lessonTimes[0]);
            } catch (Exception e) {
                timeDao.deleteAll();
                timeDao.insert(lessonTimes[0]);
            }

            return null;
        }
    }


    private static class UpdateTimeAsyncTask extends AsyncTask<LessonTime, Void, Void> {
        private TimeDao timeDao;
        private TimeManager timeManager;

        private UpdateTimeAsyncTask(TimeDao timeDao, Application application) {
            this.timeDao = timeDao;
            timeManager = new TimeManager(application);
        }

        @Override
        protected Void doInBackground(LessonTime... lessonTimes) {
            timeDao.update(lessonTimes[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            timeManager.uploadData(timeDao.getAllStatic());
        }
    }

    private static class DeleteTimeAsyncTask extends AsyncTask<LessonTime, Void, Void> {
        private TimeDao timeDao;
        private TimeManager timeManager;

        private DeleteTimeAsyncTask(TimeDao timeDao, Application application) {
            this.timeDao = timeDao;
            timeManager = new TimeManager(application);
        }

        @Override
        protected Void doInBackground(LessonTime... lessonTimes) {
            timeDao.delete(lessonTimes[0]);
            timeManager.uploadData(timeDao.getAllStatic());
            return null;
        }
    }

    private static class DeleteAllTimesAsyncTask extends AsyncTask<Void, Void, Void> {
        private TimeDao timeDao;

        private DeleteAllTimesAsyncTask(TimeDao timeDao) {
            this.timeDao = timeDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            timeDao.deleteAllTimes();
            return null;
        }
    }

}
