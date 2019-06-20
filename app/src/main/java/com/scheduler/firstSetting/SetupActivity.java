package com.scheduler.firstSetting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.scheduler.MainActivity;
import com.scheduler.R;
import com.scheduler.logic.ScheduleManager;
import com.scheduler.logic.TimeManager;

import org.jetbrains.annotations.NotNull;

public class SetupActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TextView headerText;
    private Button backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_welkome_action_bar);

        headerText = findViewById(R.id.first_start_header_text);
        backButton = findViewById(R.id.go_back);
        Button forwardButton = findViewById(R.id.go_ahead);

        backButton.setVisibility(View.GONE);

        backButton.setOnClickListener(v -> mViewPager.setCurrentItem(0));

        forwardButton.setOnClickListener(v -> {
            if (mViewPager.getCurrentItem() == 0) {
                mViewPager.setCurrentItem(1);

            } else if (mViewPager.getCurrentItem() == 1) {
                mViewPager.setCurrentItem(2);
            } else {
                ScheduleManager schedule = new ScheduleManager(getApplication());
                schedule.downloadData();

                Intent intent = new Intent(SetupActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                TimeManager time = new TimeManager(getApplication());
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                time.downloadData(prefs.getString("universityName", "chnu"));

                startActivity(intent);
                finish();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        headerText.setText(R.string.choose_university);
                        backButton.setVisibility(View.GONE);
                        break;
                    case 1:
                        headerText.setText(R.string.choose_department);
                        backButton.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        headerText.setText(R.string.choose_group);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }


    class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new UniversityFragment();
                case 1:
                    return new DepartmentFragment();
                case 2:
                    return new GroupFragment();
                default:
                    return new UniversityFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
