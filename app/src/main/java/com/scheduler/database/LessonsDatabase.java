package com.scheduler.database;

import android.content.Context;

import com.scheduler.Lesson;
import com.scheduler.LessonTime;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Lesson.class, LessonTime.class}, version = 1, exportSchema = false)
public abstract class LessonsDatabase extends RoomDatabase {

    private static LessonsDatabase instance;

    public abstract LessonDao lessonDao();
    public abstract TimeDao timeDao();

    public static synchronized LessonsDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    LessonsDatabase.class, "lesson_database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}
