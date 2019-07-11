package com.scheduler.logic;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.scheduler.LessonTime;
import com.scheduler.UserAccount;
import com.scheduler.firstSetting.MatrixActivity;
import com.scheduler.firstSetting.SharedViewModel;

import org.joda.time.LocalTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class TimeManager {

    private final static String USERS = "users";
    private final static String DATA = "data";
    private final static String TIMETABLE = "timetable";
    private final static String UPDATED = "updated";
    private final static String UNIVERSITIES = "universities";


    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private byte[] item = {8, 0};
    private LocalTime nextStart;

    private Map<String, Object> timeTable;
    private List<String> timetable;

    private int time;
    private int overallTime;
    private UserAccount account;
    private TimeRepository repository;


    public TimeManager(Application application) {
        account = new UserAccount(application);
        prefs = PreferenceManager.getDefaultSharedPreferences(application);
        repository = new TimeRepository(application);
    }

    public void deleteTimes() {
        repository.deleteAll();
    }


    public int getTime() {
        return time;
    }


    public int getOverallTime() {
        return overallTime;
    }


    public void checkUpdates(MatrixActivity activity) {
        DocumentReference docRef = firestore.collection(USERS).document(account.getPersonEmail()).
                collection(DATA).document(UPDATED);

        SharedViewModel model = ViewModelProviders.of(activity).get(SharedViewModel.class);

        docRef.addSnapshotListener((document, e) -> {
            if (document != null && document.exists()) {
                Log.d(TAG, "Current data: " + document.getData());
                editor = prefs.edit();
                editor.putString(UPDATED, Objects.requireNonNull(document.getData()).toString());
                editor.apply();

                model.setDate(timestampToString(document.getDate("date")));

            } else {
                Log.d(TAG, "Current data: null");
            }
        });
    }

    private String timestampToString(Date timestamp) {
        DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        return dateFormat.format(timestamp);
    }


    public void downloadData() {

        getFromFirestore(firestore.collection(USERS).document(account.getPersonEmail())
                .collection(DATA).document(TIMETABLE));
    }

    public void downloadData(String university) {

        getFromFirestore(firestore.collection(UNIVERSITIES).document(university));
    }


    private void getFromFirestore(DocumentReference docRef) {
        docRef.get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            if (document != null) {
                timetable = (List<String>) document.get("timetable");
                setLocalDatabase(timetable);
            } else {
                Log.d("Firestore error", "cannot get snapshot");
            }
        });
    }


    public void uploadData(List<LessonTime> timeList) {
        parseTime(timeList);

        firestore.collection(USERS).document(account.getPersonEmail()).collection(DATA)
                .document(TIMETABLE).set(timeTable).addOnSuccessListener(aVoid ->
                        Log.d("Firestore: ", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e ->
                        Log.w("Firestore: ", "Error writing document", e));
    }


    private void parseTime(List<LessonTime> timeList) {
        timeTable = new HashMap<>();

        ArrayList<String> stringTimeList = new ArrayList<>();

        for (LessonTime time : timeList) {
            stringTimeList.add(time.getLessonStart());
            stringTimeList.add(time.getLessonEnd());
        }

        timeTable.put("timetable", stringTimeList);
    }


    private void setLocalDatabase(List<String> timetable) {
        int j = -1;

        for (int i = 0; i < timetable.size(); i+=2) {
            if(i % 2 == 0) j++;
            LessonTime lessonTime = new LessonTime(j, timetable.get(i), timetable.get(i + 1));
            repository.insert(lessonTime);
        }
    }


    public byte[] getCurrentItem(List<LessonTime> timeList) {
        LocalTime timeNow = LocalTime.now();
        byte listSize = (byte) timeList.size();

        for (byte i = 0; i < listSize; i++) {

            LocalTime start = LocalTime.parse(timeList.get(i).getLessonStart());
            LocalTime end = LocalTime.parse(timeList.get(i).getLessonEnd());

            try {
                nextStart = LocalTime.parse(timeList.get(i + 1).getLessonStart());
            } catch (Exception ignored) { }

            try {
                if (timeNow.isAfter(start) && timeNow.isBefore(end)) {
                    item[0] = i;
                    item[1] = 0; // 0 is for lesson

                    time = (end.getHourOfDay() - timeNow.getHourOfDay()) * 3600 * 1000 +
                            (end.getMinuteOfHour() - timeNow.getMinuteOfHour()) * 60 * 1000 +
                            (end.getSecondOfMinute() - timeNow.getSecondOfMinute()) * 1000 +
                            (end.getMillisOfSecond() - timeNow.getMillisOfSecond());

                    overallTime = (end.getHourOfDay() - start.getHourOfDay()) * 60 +
                            (end.getMinuteOfHour() - start.getMinuteOfHour());


                } else if (timeNow.isAfter(end) && timeNow.isBefore(nextStart)) {
                    item[0] = (byte) (i + 1);
                    item[1] = 1; // 1 is for break

                    time = (nextStart.getHourOfDay() - timeNow.getHourOfDay()) * 3600 * 1000 +
                            (nextStart.getMinuteOfHour() - timeNow.getMinuteOfHour()) * 60 * 1000 +
                            (nextStart.getSecondOfMinute() - timeNow.getSecondOfMinute()) * 1000 +
                            (nextStart.getMillisOfSecond() - timeNow.getMillisOfSecond());


                    overallTime = (nextStart.getHourOfDay() - end.getHourOfDay()) * 60 +
                            (nextStart.getMinuteOfHour() - end.getMinuteOfHour());
                }
            } catch (Exception ignored) {}

        }
        return item;
    }
}
