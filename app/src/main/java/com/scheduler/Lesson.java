package com.scheduler;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lesson_table")
public class Lesson {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "lesson_number")
    private String lessonNumber;

    @ColumnInfo(name = "week_number")
    private int weekNumber;

    @ColumnInfo(name = "day_of_week")
    private int dayOfWeek;

    @ColumnInfo(name = "subject_name")
    private String subjectName;

    @ColumnInfo(name = "teacher_name")
    private String teacherName;

    @ColumnInfo(name = "room")
    private String room;


    public Lesson(int id, int weekNumber, int dayOfWeek, String lessonNumber, String subjectName, String teacherName, String room) {
        this.id = id;
        this.weekNumber = weekNumber;
        this.dayOfWeek = dayOfWeek;
        this.lessonNumber = lessonNumber;
        this.subjectName = subjectName;
        this.teacherName = teacherName;
        this.room = room;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLessonNumber() {
        return lessonNumber;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
