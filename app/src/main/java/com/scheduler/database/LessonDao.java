package com.scheduler.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.scheduler.Lesson;

import java.util.List;

@Dao
public interface LessonDao {

    @Insert
    void insertLesson(Lesson lesson);

    @Update
    void update(Lesson lesson);

    @Delete
    void delete(Lesson lesson);

    @Query("DELETE FROM lesson_table")
    void deleteAllLessons();

    @Query("SELECT * FROM lesson_table WHERE day_of_week IN (:dayOfWeek) AND week_number IN (:weekNumber)")
    LiveData<List<Lesson>> loadAllByDay(int dayOfWeek, int weekNumber);

    @Query("SELECT * FROM lesson_table")
    List<Lesson> getAllLessons();

    //returns lesson count (for one day)
    @Query("SELECT COUNT(id) FROM lesson_table WHERE day_of_week IN (:dayOfWeek) AND week_number IN (1)")
    int getLessonCountDay(int dayOfWeek);

    //also returns lesson count (for whole week)
    @Query("SELECT COUNT(id) FROM lesson_table")
    int getLessonCount();
}
