package com.scheduler.utils;

public class DatabaseUtils {
    private static final DatabaseUtils ourInstance = new DatabaseUtils();

    public static DatabaseUtils getInstance() {
        return ourInstance;
    }

    private DatabaseUtils() {

    }
}
