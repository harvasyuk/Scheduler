package com.scheduler.firstSetting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.scheduler.MainActivity;
import com.scheduler.R;
import com.scheduler.UserAccount;
import com.scheduler.logic.ScheduleManager;
import com.scheduler.logic.TimeManager;

public class MatrixActivity extends AppCompatActivity {

    private static final String LOCAL_DATABASE = "local";
    private static final String REMOTE_DATABASE = "remote";

    private Button searchSchedule;
    private Button createSchedule;
    private Button loadButton;
    private TextView greetingTextView;
    private TextView searchTextView;
    private ProgressBar progressBar;
    private ConstraintLayout scheduleLayout;
    private String greetingText;



    private UserAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matrix);

        searchSchedule = findViewById(R.id.search_schedule_button);
        createSchedule = findViewById(R.id.create_schedule_button);
        greetingTextView = findViewById(R.id.greeting_text);
        progressBar = findViewById(R.id.progressBar);
        searchTextView = findViewById(R.id.searchTextView);
        scheduleLayout = findViewById(R.id.scheduleLayout);
        loadButton = findViewById(R.id.loadButton);

        greetingText = greetingTextView.getText().toString();

        account = new UserAccount(this);
        getAccountInfo();
    }


    private void getAccountInfo() {

        if (account != null) {
            greetingText += account.getName();
            greetingTextView.setText(greetingText);
        }
    }


    private void showLayout() {
        progressBar.setVisibility(View.GONE);
        searchTextView.setVisibility(View.GONE);
        scheduleLayout.setVisibility(View.VISIBLE);

        loadButton.setOnClickListener(v -> {
            TimeManager timeManager = new TimeManager(getApplication());
            timeManager.downloadData();
            ScheduleManager schedule = new ScheduleManager(getApplication());
            schedule.downloadData();

            putToSharedPref(LOCAL_DATABASE);
            startActivity(new Intent(MatrixActivity.this, MainActivity.class));
            finish();
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        UserAccount account = new UserAccount(this);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        try {
            DocumentReference docRef = db.collection("users").document(account.getPersonEmail()).
                    collection("data").document("timetable");

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        showLayout();
                    }
                }
            });
        } catch (Exception ignored) { }

        searchSchedule.setOnClickListener(v -> {
            putToSharedPref(REMOTE_DATABASE);
            startActivity(new Intent(MatrixActivity.this, SetupActivity.class));
            finish();
        });

        createSchedule.setOnClickListener(v -> {
            putToSharedPref(LOCAL_DATABASE);
            startActivity(new Intent(MatrixActivity.this, ScheduleEditorStartActivity.class));
            finish();
        });
    }


    private void putToSharedPref(String type) {
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.common_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.database_type), type);
        editor.apply();
    }
}
