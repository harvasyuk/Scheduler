package com.scheduler.logic;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.scheduler.Lesson;
import com.scheduler.R;
import com.scheduler.UserAccount;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ScheduleManager {

    private static final String USERS = "users";
    private static final String DATA = "data";
    private static final String SCHEDULE = "schedule";
    //private static final String UPDATED = "updated";
    private static final String UNIVERSITIES = "universities";
    private static final String DEPARTMENTS = "departments";
    private static final String GROUPS = "groups";

    private static String UNIVERSITY;
    private static String DEPARTMENT;
    private static String GROUP;

    private static final String LOCAL_DATABASE = "local";
    private static final String REMOTE_DATABASE = "remote";

    private String databaseType;
    private DocumentReference docRef;
    private Application application;
    //private SharedPreferences.Editor editor;

    private UserAccount account;
    private Map<String, Object> schedule;
    private ScheduleRepository scheduleRepository;

    private int weekCount;

    private SharedPreferences sharedPref;

    private DownloadListener downloadListener;



    public ScheduleManager(Application application) {
        this.application = application;
        account = new UserAccount(application);
        sharedPref = application.getSharedPreferences(application.getString(R.string.common_preferences), Context.MODE_PRIVATE);
        databaseType = sharedPref.getString(application.getString(R.string.database_type), LOCAL_DATABASE);
        UNIVERSITY = sharedPref.getString(application.getString(R.string.university_name), "chnu");
        DEPARTMENT = sharedPref.getString(application.getString(R.string.department_name), "ComputerScience");
        GROUP = sharedPref.getString(application.getString(R.string.group_name), "142");
        weekCount = sharedPref.getInt(application.getString(R.string.week_count), 0);

        scheduleRepository = new ScheduleRepository(application);
        setupDatabase();
    }


    private void setupDatabase() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        switch (databaseType) {

            case LOCAL_DATABASE:
                docRef = firestore.collection(USERS).document(account.getPersonEmail()).
                        collection(DATA).document(SCHEDULE);
                break;
            case REMOTE_DATABASE:
                docRef = firestore.collection(UNIVERSITIES).document(UNIVERSITY).
                        collection(DEPARTMENTS).document(DEPARTMENT).collection(GROUPS).document(GROUP);
                break;
        }
    }

    public void deleteAllLessons() { scheduleRepository.deleteAll(); }


    public void insertEmptyLesson(int timeCount) {
        int lessonCount = scheduleRepository.getLessonCountDay() + 1;

        if (lessonCount > timeCount) return;

        String lessonNumber = String.valueOf(lessonCount);
        int count = scheduleRepository.getLessonCount();

        for (int weekNuber = 0; weekNuber < weekCount; weekNuber++) {
            for (int i = Calendar.MONDAY; i <= Calendar.FRIDAY; i++) {
                Lesson lesson = new Lesson(count++, weekNuber, i, lessonNumber, "", "", "");
                scheduleRepository.insert(lesson);
            }
        }
    }


    void uploadData(List<Lesson> lessons) {
        parseLessons(lessons);

        docRef.update(schedule)
                .addOnSuccessListener(aVoid -> Log.d("Firestore: ", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("Firestore: ", "Error writing document", e));
    }


    private void parseLessons(List<Lesson> lessons) {
        schedule = new HashMap<>();
        Map<String, String> lessonMap = new HashMap<>();

        StringBuilder lessonString;

        for (int i = 0; i < lessons.size(); i++) {
            Lesson lesson = lessons.get(i);
            lessonString = new StringBuilder().
                    append(lesson.getId()).append("/").
                    append(lesson.getWeekNumber()).append("/").
                    append(lesson.getDayOfWeek()).append("/").
                    append(lesson.getLessonNumber()).append("/").
                    append(lesson.getSubjectName()).append("/").
                    append(lesson.getTeacherName()).append("/").
                    append(lesson.getRoom());
            lessonMap.put(Integer.toString(i), lessonString.toString());
        }
        schedule.put("schedule", lessonMap);
    }


     public void downloadData() {
        docRef.get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            if (document != null) {
                schedule = task.getResult().getData();
                setLocalDatabase();
            } else {
                Log.d("Firestore error", "cannot get snapshot");
            }
        });
    }


    private void setLocalDatabase() {
        scheduleRepository.deleteAll();

        Map<String, String> lessonMap = (Map<String, String>) schedule.get("schedule");
        setWeekCount((String) schedule.get("weekCount"));

        assert lessonMap != null;
        for (int i = 0; i < lessonMap.size(); i++) {
            String[] props = {" ", " ", " ", " ", " ", " ", " "};
            String[] remoteProps = Objects.requireNonNull(lessonMap.get(Integer.toString(i))).split("/");
            System.arraycopy(remoteProps, 0, props, 0, remoteProps.length);

            Lesson lesson = new Lesson(Integer.parseInt(props[0]), Integer.parseInt(props[1]),
                    Integer.parseInt(props[2]), props[3], props[4], props[5], props[6]);

            scheduleRepository.insert(lesson);
        }
    }

    private void setWeekCount(String weekCount) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(application.getString(R.string.week_count), Integer.parseInt(weekCount));
        editor.apply();

        if (downloadListener != null) {
            downloadListener.onDownloadCompleted();
        }
    }


    public void uploadWeekCount(int weekCount) {
        schedule = new HashMap<>();
        schedule.put("weekCount", String.valueOf(weekCount));
        docRef.update(schedule)
                .addOnSuccessListener(aVoid -> Log.d("Firestore: ", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("Firestore: ", "Error writing document", e));
    }

    public void setOnDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    public interface DownloadListener {
        void onDownloadCompleted();
    }
}