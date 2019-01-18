package com.scheduler;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.scheduler.authentication.LoginActivity;
import com.scheduler.days.Friday;
import com.scheduler.days.LessonDialog;
import com.scheduler.days.Monday;
import com.scheduler.days.Thursday;
import com.scheduler.days.Tuesday;
import com.scheduler.days.Wednesday;
import com.scheduler.logic.ScheduleManager;
import com.scheduler.logic.ScheduleViewModel;
import com.scheduler.settings.SettingsActivity;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity implements LessonDialog.LessonDialogListener {

    public static final String LOCAL_DATABASE = "local";
    public static final String REMOTE_DATABASE = "remote";

    private SwipeRefreshLayout refreshLayout;
    private String databaseType;
    private ScheduleManager schedule;
    private int day;

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
        TextView weekNumber = findViewById(R.id.week_number);
        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager mViewPager = findViewById(R.id.container);

        refreshLayout.setEnabled(false);
        mViewPager.setAdapter(sectionsPagerAdapter);
        mViewPager.setCurrentItem(currentDay());
        mViewPager.setOffscreenPageLimit(6);

        //custom toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        schedule = new ScheduleManager(getApplication());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        databaseType = sharedPreferences.getString("databaseType", LOCAL_DATABASE);
        groupName.setText(sharedPreferences.getString("groupListPreference", ""));
        weekNumber.setText(R.string.week);

        tabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem refresh = menu.findItem(R.id.menu_refresh);
        if (databaseType.equals(REMOTE_DATABASE)) {
            refresh.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refreshLayout.setRefreshing(true);
                updateSchedule();
                return true;
        }
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            this.startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void updateSchedule() {
        schedule.downloadData();
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        }, 2000);
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


    class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

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
            return null;
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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
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
