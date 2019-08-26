package com.scheduler.logic;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.scheduler.Lesson;
import com.scheduler.database.LessonDao;
import com.scheduler.database.LessonsDatabase;

import java.util.Calendar;
import java.util.List;

class ScheduleRepository {

    private LessonDao lessonDao;
    //private LiveData<List<Lesson>> lessons;
    private ScheduleManager scheduleManager;
    private int weekNumber;


    //for other tasks
    ScheduleRepository(Application application) {
        LessonsDatabase lessonsDatabase = LessonsDatabase.getInstance(application);
        lessonDao = lessonsDatabase.lessonDao();
    }

    //for view model
    ScheduleRepository(Application application, int weekNumber) {
        this.weekNumber = weekNumber;
        LessonsDatabase lessonsDatabase = LessonsDatabase.getInstance(application);
        lessonDao = lessonsDatabase.lessonDao();
        scheduleManager = new ScheduleManager(application);
    }

    LiveData<List<Lesson>> getLessons(int day) {
        return lessonDao.loadAllByDay(day, weekNumber);
    }

    LiveData<List<Lesson>> getLessons(int day, int week) {
        return lessonDao.loadAllByDay(day, week);
    }

    int getLessonCount() {
        return lessonDao.getLessonCount();
    }

    int getLessonCountDay() {
        return lessonDao.getLessonCountDay(Calendar.MONDAY);
    }

    void insert(Lesson lesson) {
        new InsertLessonAsyncTask(lessonDao).execute(lesson);
    }

    void update(Lesson lesson) {
        new UpdateLessonAsyncTask(lessonDao, scheduleManager).execute(lesson);
    }

    void delete(Lesson lesson) {
        new DeleteLessonAsyncTask(lessonDao).execute(lesson);
    }

    void deleteAll() {
        new DeleteAllLessonsAsyncTask(lessonDao).execute();
    }


    private static class InsertLessonAsyncTask extends AsyncTask<Lesson, Void, Void> {
        private LessonDao lessonDao;

        private InsertLessonAsyncTask(LessonDao lessonDao) {
            this.lessonDao = lessonDao;
        }

        @Override
        protected Void doInBackground(Lesson... lessons) {
            try {
                lessonDao.insertLesson(lessons[0]);
            } catch (Exception e) {
                lessonDao.deleteAllLessons();
                lessonDao.insertLesson(lessons[0]);
            }

            return null;
        }
    }

    private static class UpdateLessonAsyncTask extends AsyncTask<Lesson, Void, Void> {
        private LessonDao lessonDao;
        private ScheduleManager scheduleManager;

        private UpdateLessonAsyncTask(LessonDao lessonDao, ScheduleManager scheduleManager) {
            this.lessonDao = lessonDao;
            this.scheduleManager = scheduleManager;
        }

        @Override
        protected Void doInBackground(Lesson... lessons) {
            lessonDao.update(lessons[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            scheduleManager.uploadData(lessonDao.getAllLessons());
        }
    }

    private static class DeleteLessonAsyncTask extends AsyncTask<Lesson, Void, Void> {
        private LessonDao lessonDao;

        private DeleteLessonAsyncTask(LessonDao lessonDao) {
            this.lessonDao = lessonDao;
        }

        @Override
        protected Void doInBackground(Lesson... lessons) {
            lessonDao.delete(lessons[0]);
            return null;
        }
    }

    private static class DeleteAllLessonsAsyncTask extends AsyncTask<Void, Void, Void> {
        private LessonDao lessonDao;

        private DeleteAllLessonsAsyncTask(LessonDao lessonDao) {
            this.lessonDao = lessonDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            lessonDao.deleteAllLessons();
            return null;
        }
    }

}
