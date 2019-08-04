package com.scheduler;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "time_table")
public class LessonTime {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "lesson_start")
    private String lessonStart;

    @ColumnInfo(name = "lesson_end")
    private String lessonEnd;


    public LessonTime(int id, String lessonStart, String lessonEnd) {
        this.id = id;
        this.lessonStart = lessonStart;
        this.lessonEnd = lessonEnd;
    }

    @Ignore
    public LessonTime(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLessonStart() {
        return lessonStart;
    }

    public void setLessonStart(String lessonStart) {
        this.lessonStart = lessonStart;
    }

    public String getLessonEnd() {
        return lessonEnd;
    }

    public void setLessonEnd(String lessonEnd) {
        this.lessonEnd = lessonEnd;
    }
}