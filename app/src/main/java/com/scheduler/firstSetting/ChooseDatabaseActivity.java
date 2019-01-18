package com.scheduler.firstSetting;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static com.scheduler.MainActivity.LOCAL_DATABASE;
import static com.scheduler.MainActivity.REMOTE_DATABASE;

public class ChooseDatabaseActivity extends AppCompatActivity {

    private Button searchSchedule;
    private Button createSchedule;
    private TextView greetingTextView;
    private String greetingText;

    private UserAccount account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_setting);

        searchSchedule = findViewById(R.id.search_schedule_button);
        createSchedule = findViewById(R.id.create_schedule_button);
        greetingTextView = findViewById(R.id.greeting_text);

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


    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to load existing schedule?");

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                TimeManager timeManager = new TimeManager(getApplication());
                timeManager.downloadData();
                ScheduleManager schedule = new ScheduleManager(getApplication());
                schedule.downloadData();

                putToSharedPref(LOCAL_DATABASE);
                startActivity(new Intent(ChooseDatabaseActivity.this, MainActivity.class));
                finish();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    protected void onStart() {
        super.onStart();

        UserAccount account = new UserAccount(this);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(account.getPersonEmail()).
                collection("data").document("timetable");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    showAlert();
                }
            }
        });

        searchSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putToSharedPref(REMOTE_DATABASE);
                startActivity(new Intent(ChooseDatabaseActivity.this, FirstSettingActivity.class));
                finish();
            }
        });

        createSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putToSharedPref(LOCAL_DATABASE);
                startActivity(new Intent(ChooseDatabaseActivity.this, ScheduleEditorStartActivity.class));
                finish();
            }
        });

    }


    private void putToSharedPref(String type) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ChooseDatabaseActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("databaseType", type);
        editor.apply();
    }
}
