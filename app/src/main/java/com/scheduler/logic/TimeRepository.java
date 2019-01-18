package com.scheduler.logic;

import android.app.Application;
import android.os.AsyncTask;

import com.scheduler.LessonTime;
import com.scheduler.database.LessonsDatabase;
import com.scheduler.database.TimeDao;

import java.util.List;

import androidx.lifecycle.LiveData;

class TimeRepository {

    private TimeDao timeDao;
    private LiveData<List<LessonTime>> timeList;
    private TimeManager timeManager;

    TimeRepository(Application application) {
        LessonsDatabase lessonsDatabase = LessonsDatabase.getInstance(application);
        timeDao = lessonsDatabase.timeDao();
        timeList = timeDao.getAll();
        timeManager = new TimeManager(application);
    }

    LiveData<List<LessonTime>> getTimeList() { return timeList; }

    void insert(LessonTime lessonTime) {
        new InsertTimeAsyncTask(timeDao).execute(lessonTime);
    }

    void update(LessonTime lessonTime) {
        new UpdateTimeAsyncTask(timeDao, timeManager).execute(lessonTime);
    }

    void delete(LessonTime lessonTime) {
        new DeleteTimeAsyncTask(timeDao).execute(lessonTime);
    }

//    void deleteAll() {
//        new ScheduleRepository.DeleteAllLessonsAsyncTask(lessonDao).execute();
//    }

    private static class InsertTimeAsyncTask extends AsyncTask<LessonTime, Void, Void> {
        private TimeDao timeDao;

        private InsertTimeAsyncTask(TimeDao timeDao) {
            this.timeDao = timeDao;
        }

        @Override
        protected Void doInBackground(LessonTime... lessonTimes) {
            timeDao.insert(lessonTimes[0]);
            return null;
        }
    }

    private static class UpdateTimeAsyncTask extends AsyncTask<LessonTime, Void, Void> {
        private TimeDao timeDao;
        private TimeManager timeManager;

        private UpdateTimeAsyncTask(TimeDao timeDao, TimeManager timeManager) {
            this.timeDao = timeDao;
            this.timeManager = timeManager;
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

        private DeleteTimeAsyncTask(TimeDao timeDao) {
            this.timeDao = timeDao;
        }

        @Override
        protected Void doInBackground(LessonTime... lessonTimes) {
            timeDao.delete(lessonTimes[0]);
            return null;
        }
    }
}
