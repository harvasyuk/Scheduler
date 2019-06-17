package com.scheduler.logic;

interface Schedule {

    void setupDatabase();

    void checkUpdates();

    void downloadData();

    void uploadData();
}
