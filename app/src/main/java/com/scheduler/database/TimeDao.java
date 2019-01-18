package com.scheduler.database;

import com.scheduler.LessonTime;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TimeDao {

    @Insert
    void insert(LessonTime lessonTime);

    @Update
    void update(LessonTime lessonTime);

    @Delete
    void delete(LessonTime lessonTime);

    @Query("SELECT * FROM time_table")
    LiveData<List<LessonTime>> getAll();

    @Query("SELECT * FROM time_table")
    List<LessonTime> getAllStatic();

    @Query("DELETE FROM time_table")
    void deleteAll();
}
