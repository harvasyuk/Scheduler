package com.scheduler.database;

import com.scheduler.Lesson;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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

    @Query("SELECT * FROM lesson_table WHERE day_of_week IN (:dayOfWeek)")
    LiveData<List<Lesson>> loadAllByDay(int dayOfWeek);

    @Query("SELECT * FROM lesson_table")
    List<Lesson> getAllLessons();

//    @Query("SELECT * FROM lesson_table WHERE subject_name LIKE :name")
//    Lesson findByName(String name);
//
    //returns lesson count (for one day)
    @Query("SELECT COUNT(id) FROM lesson_table WHERE day_of_week IN (:dayOfWeek)")
    int getLessonCountDay(int dayOfWeek);
//
    //also returns lesson count (for whole week)
    @Query("SELECT COUNT(id) FROM lesson_table")
    int getLessonCount();
//
//    @Query("SELECT id FROM lesson_table WHERE day_of_week IN (:dayOfWeek) AND lesson_number IN (:lessonNumber)")
//    int getLessonID(int dayOfWeek, String lessonNumber);


}
