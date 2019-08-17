package com.scheduler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.scheduler.authentication.LoginActivity;
import com.scheduler.days.Friday;
import com.scheduler.days.LessonDialog;
import com.scheduler.days.Monday;
import com.scheduler.days.Thursday;
import com.scheduler.days.Tuesday;
import com.scheduler.days.Wednesday;
import com.scheduler.firstSetting.MatrixActivity;
import com.scheduler.firstSetting.SetupActivity;
import com.scheduler.logic.ScheduleManager;
import com.scheduler.logic.ScheduleViewModel;
import com.scheduler.settings.SettingsActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements LessonDialog.LessonDialogListener {

    private SwipeRefreshLayout refreshLayout;
    private ScheduleManager schedule;
    private SharedPreferences sharedPref;
    private int day;

    private MultiToggleView toggleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkUserAuth();
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        refreshLayout = findViewById(R.id.refresh);
        TextView groupName = findViewById(R.id.group_name);
        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager mViewPager = findViewById(R.id.container);
        toggleView = findViewById(R.id.weekToggle);
        toggleView.setToggle(0);

        refreshLayout.setEnabled(false);
        mViewPager.setAdapter(sectionsPagerAdapter);
        mViewPager.setCurrentItem(currentDay());
        mViewPager.setOffscreenPageLimit(5);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        groupName.setText(sharedPref.getString(getString(R.string.group_name), "Group 1"));

        tabLayout.setupWithViewPager(mViewPager);

        toggleView.setOnStateChangeListener(position -> schedule.setWeekNumber(position));
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refreshLayout.setRefreshing(true);
                updateSchedule();
                return true;
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                this.startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void updateSchedule() {
        schedule.downloadData();
        refreshLayout.postDelayed(() -> refreshLayout.setRefreshing(false), 2000);
    }


    @Override
    public void saveData(String subject, String teacher, String room, Lesson lesson) {
        if (!subject.equals(lesson.getSubjectName()) || !teacher.equals(lesson.getTeacherName()) || !room.equals(lesson.getRoom())) {

            lesson.setSubjectName(subject);
            lesson.setTeacherName(teacher);
            lesson.setRoom(room);

            ScheduleViewModel scheduleViewModel = ViewModelProviders.of(this).get(ScheduleViewModel.class);
            scheduleViewModel.setDay(day);

            scheduleViewModel.update(lesson);
        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new Monday();
                case 1:
                    return new Tuesday();
                case 2:
                    return new Wednesday();
                case 3:
                    return new Thursday();
                case 4:
                    return new Friday();
            }
            return new Monday();
        }


        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.monday);
                case 1:
                    return getString(R.string.tuesday);
                case 2:
                    return getString(R.string.wednesday);
                case 3:
                    return getString(R.string.thursday);
                case 4:
                    return getString(R.string.friday);
            }
            return null;
        }
    }


    //go to LoginActivity if user is not signed in
    private void checkUserAuth() {
        sharedPref = this.getSharedPreferences(
                getString(R.string.common_preferences), Context.MODE_PRIVATE);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            checkSetupStage();
            schedule = new ScheduleManager(getApplication());
        }
    }

    private void checkSetupStage() {
        int stage = sharedPref.getInt(getString(R.string.setup_stage), 0);
        switch (stage) {
            case 0:
                startActivity(new Intent(MainActivity.this, MatrixActivity.class));
                finish();
                break;
            case 1:
                startActivity(new Intent(MainActivity.this, SetupActivity.class));
                finish();
                break;
        }
    }


    private int currentDay() {

        switch (day) {
            case Calendar.MONDAY:
                return 0;
            case Calendar.TUESDAY:
                return 1;
            case Calendar.WEDNESDAY:
                return 2;
            case Calendar.THURSDAY:
                return 3;
            case Calendar.FRIDAY:
                return 4;
            default:
                return 0;
        }
    }
}
