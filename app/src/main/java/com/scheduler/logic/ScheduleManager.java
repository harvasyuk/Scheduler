package com.scheduler.logic;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.scheduler.Lesson;
import com.scheduler.UserAccount;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;


public class ScheduleManager {

    private static final String USERS = "users";
    private static final String DATA = "data";
    private static final String SCHEDULE = "schedule";
    private static final String UPDATED = "updated";

    private static final String UNIVERSITIES = "universities";
    private static final String DEPARTMENTS = "departments";
    private static final String GROUPS = "groups";

    private static String UNIVERSITY;
    private static String DEPARTMENT;
    private static String GROUP;

    private static final String LOCAL_DATABASE = "local";
    private static final String REMOTE_DATABASE = "remote";

    private String databaseType;
    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private UserAccount account;
    private Map<String, Object> schedule;
    private ScheduleRepository scheduleRepository;


    public ScheduleManager(Application application) {
        account = new UserAccount(application);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
        databaseType = sharedPreferences.getString("databaseType", LOCAL_DATABASE);
        UNIVERSITY = sharedPreferences.getString("universityName", "chnu");
        DEPARTMENT = sharedPreferences.getString("departmentName", "ComputerScience");
        GROUP = sharedPreferences.getString("groupName", "542");
        scheduleRepository = new ScheduleRepository(application);
        setupDatabase();
    }


    private void setupDatabase() {
        firestore = FirebaseFirestore.getInstance();

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


    public void checkUpdates() {
        docRef = firestore.collection(USERS).document(account.getPersonEmail()).
                collection(DATA).document(UPDATED);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot document,
                                @Nullable FirebaseFirestoreException e) {
                if (document != null && document.exists()) {
                    Log.d(TAG, "Current data: " + document.getData());
                    editor = sharedPreferences.edit();
                    editor.putString(UPDATED, Objects.requireNonNull(document.getData()).toString());
                    editor.apply();
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }


    public void insertEmptyLesson() {
        String lessonNumber = String.valueOf(scheduleRepository.getLessonCountDay() + 1);
        int count = scheduleRepository.getLessonCount();

        for (int i = Calendar.MONDAY; i <= Calendar.FRIDAY; i++) {
            Lesson lesson = new Lesson(count++, i, lessonNumber, "", "", "");
            scheduleRepository.insert(lesson);
        }
    }


    public void downloadData() {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    schedule = task.getResult().getData();
                    setLocalDatabase();
                } else {
                    Log.d("Firestore error", "cannot get snapshot");
                }
            }
        });
    }


    void uploadData(List<Lesson> lessons) {
        parseLessons(lessons);

        docRef.set(schedule)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore: ", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore: ", "Error writing document", e);
                    }
                });
    }


    private void parseLessons(List<Lesson> lessons) {
        schedule = new HashMap<>();
        Map<String, String> lessonMap = new HashMap<>();

        StringBuilder lessonString;

        for (int i = 0; i < lessons.size(); i++) {
            Lesson lesson = lessons.get(i);
            lessonString = new StringBuilder().
                    append(Integer.toString(lesson.getId())).append("/").
                    append(Integer.toString(lesson.getDayOfWeek())).append("/").
                    append(lesson.getLessonNumber()).append("/").
                    append(lesson.getSubjectName()).append("/").
                    append(lesson.getTeacherName()).append("/").
                    append(lesson.getRoom());
            lessonMap.put(Integer.toString(i), lessonString.toString());
        }
        schedule.put("schedule", lessonMap);
    }


    private void setLocalDatabase() {
        scheduleRepository.deleteAll();

        @SuppressWarnings("unchecked")
        Map<String, String> lessonMap = (Map<String, String>) schedule.get("schedule");

        assert lessonMap != null;
        for (int i = 0; i < lessonMap.size(); i++) {
            String[] props = {" ", " ", " ", " ", " ", " "};
            String[] remoteProps = Objects.requireNonNull(lessonMap.get(Integer.toString(i))).split("/");
            System.arraycopy(remoteProps, 0, props, 0, remoteProps.length);

            Lesson lesson = new Lesson(Integer.parseInt(props[0]), Integer.parseInt(props[1]),
                    props[2], props[3], props[4], props[5]);

            scheduleRepository.insert(lesson);
        }
    }
}