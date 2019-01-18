package com.scheduler.logic;

import com.scheduler.Lesson;

import java.util.Collection;
import java.util.List;

interface Schedule {

    void setupDatabase();

    void checkUpdates();

    void downloadData();

    void uploadData();
}
