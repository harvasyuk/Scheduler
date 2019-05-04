package com.scheduler.firstSetting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.scheduler.MainActivity;
import com.scheduler.R;
import com.scheduler.logic.TimeManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class FirstSettingActivity extends AppCompatActivity implements UniversityFragment.SendUniversityName  {

    private ViewPager mViewPager;
    private TextView headerText;
    private Button backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_setting);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_welkome_action_bar);

        headerText = findViewById(R.id.first_start_header_text);
        backButton = findViewById(R.id.go_back);
        Button forwardButton = findViewById(R.id.go_ahead);

        backButton.setVisibility(View.GONE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPager.getCurrentItem() == 0) {
                    mViewPager.setCurrentItem(1);
                } else {
                    Intent intent = new Intent(FirstSettingActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    TimeManager time = new TimeManager(getApplication());
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    time.downloadData(prefs.getString("universityName", "chnu"));

                    startActivity(intent);
                    finish();
                }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    headerText.setText(R.string.choose_university);
                    backButton.setVisibility(View.GONE);

                } else {
                    headerText.setText(R.string.choose_group);
                    backButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }


    @Override
    public void sendData(String universityName) {
        String tag = "android:switcher:" + R.id.container + ":" + 1;
        GroupFragment groupFragment = (GroupFragment) getSupportFragmentManager().findFragmentByTag(tag);
        assert groupFragment != null;
        groupFragment.setGroupKey(universityName);
        groupFragment.updateReference();
    }


    class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new UniversityFragment();
            } else {
                return new GroupFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
