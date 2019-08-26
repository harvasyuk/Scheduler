package com.scheduler.firstSetting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.scheduler.MainActivity;
import com.scheduler.R;
import com.scheduler.UserAccount;
import com.scheduler.logic.ScheduleManager;
import com.scheduler.logic.TimeManager;

public class MatrixActivity extends AppCompatActivity implements ScheduleManager.DownloadListener {

    private static final String LOCAL_DATABASE = "local";
    private static final String REMOTE_DATABASE = "remote";

    private Button searchSchedule;
    private Button createSchedule;
    private Button loadButton;
    private TextView greetingTextView;
    private TextView searchTextView;
    private TextView dateTextView;
    private ProgressBar progressBar;
    private ConstraintLayout scheduleLayout;
    private String greetingText;
    private SharedPreferences sharedPref;
    private UserAccount account;
    TimeManager timeManager;
    ScheduleManager schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matrix);

        sharedPref = this.getSharedPreferences(getString(R.string.common_preferences), Context.MODE_PRIVATE);

        searchSchedule = findViewById(R.id.search_schedule_button);
        createSchedule = findViewById(R.id.create_schedule_button);
        greetingTextView = findViewById(R.id.greeting_text);
        progressBar = findViewById(R.id.progressBar);
        searchTextView = findViewById(R.id.searchTextView);
        dateTextView = findViewById(R.id.dateTextView);
        scheduleLayout = findViewById(R.id.scheduleLayout);
        loadButton = findViewById(R.id.loadButton);

        greetingText = greetingTextView.getText().toString();

        account = new UserAccount(this);
        getAccountInfo();

        SharedViewModel model = ViewModelProviders.of(this).get(SharedViewModel.class);
        model.getDate().observe(this, this::setDate);
    }


    private void getAccountInfo() {
        if (account != null) {
            greetingText += account.getName();
            greetingTextView.setText(greetingText);
        }
    }


    private void showLayout() {
        putToSharedPref(LOCAL_DATABASE);

        timeManager = new TimeManager(getApplication());
        schedule = new ScheduleManager(getApplication());

        progressBar.setVisibility(View.GONE);
        searchTextView.setVisibility(View.GONE);
        scheduleLayout.setVisibility(View.VISIBLE);

        timeManager.checkUpdates(this);

        loadButton.setOnClickListener(v -> {
            timeManager.downloadData();
            schedule.downloadData();
            schedule.setOnDownloadListener(this);
        });
    }


    private void setDate(String date) {
        dateTextView.setText(date);
    }


    @Override
    protected void onStart() {
        super.onStart();

        UserAccount account = new UserAccount(this);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        try {
            DocumentReference docRef = db.collection("users").document(account.getPersonEmail()).
                    collection("data").document("timetable");

            docRef.get().addOnCompleteListener(task -> {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    showLayout();
                } else {
                    searchTextView.setText(R.string.no_schedule_text);
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception ignored) { }

        searchSchedule.setOnClickListener(v -> {
            putToSharedPref(REMOTE_DATABASE);
            activityDone(1);
            startActivity(new Intent(MatrixActivity.this, SetupActivity.class));
            finish();
        });

        createSchedule.setOnClickListener(v -> {
            putToSharedPref(LOCAL_DATABASE);

            timeManager = new TimeManager(getApplication());
            schedule = new ScheduleManager(getApplication());

            activityDone(2);
            timeManager.deleteTimes();
            schedule.deleteAllLessons();
            startActivity(new Intent(MatrixActivity.this, WeekActivity.class));
            finish();
        });
    }


    private void putToSharedPref(String type) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.database_type), type);
        editor.apply();
    }


    private void activityDone(int stage) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.setup_stage), stage);
        editor.apply();
    }

    @Override
    public void onDownloadCompleted() {
        activityDone(2);
        startActivity(new Intent(MatrixActivity.this, MainActivity.class));
        finish();
    }
}
