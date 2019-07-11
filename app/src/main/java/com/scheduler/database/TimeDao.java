package com.scheduler.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.scheduler.LessonTime;

import java.util.List;

@Dao
public interface TimeDao {

    @Insert
    void insert(LessonTime lessonTime);

    @Update
    void update(LessonTime lessonTime);

    @Delete
    void delete(LessonTime lessonTime);

    @Query("DELETE FROM time_table")
    void deleteAllTimes();

    @Query("SELECT * FROM time_table")
    LiveData<List<LessonTime>> getAll();

    @Query("SELECT * FROM time_table")
    List<LessonTime> getAllStatic();

    @Query("DELETE FROM time_table")
    void deleteAll();

    @Query("SELECT COUNT(id) FROM time_table")
    int getLessonsCount();
}
