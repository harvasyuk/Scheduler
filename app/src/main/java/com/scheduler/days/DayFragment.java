package com.scheduler.days;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scheduler.Lesson;
import com.scheduler.R;
import com.scheduler.RecyclerViewClickListener;
import com.scheduler.RecyclerViewTouchListener;
import com.scheduler.logic.ScheduleViewModel;
import com.scheduler.logic.TimeViewModel;
import com.scheduler.logic.Utils;

import java.util.Calendar;
import java.util.List;


public class DayFragment extends Fragment {

    //logic
    protected int day;
    private Context context;

    //user interface
    private RecyclerView mRecyclerView;
    private DayAdapter dayAdapter;

    private ScheduleViewModel scheduleViewModel;
    private SharedPreferences sharedPref;
    private boolean weekSet = false;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_layout, container, false);
        context = getContext();

        scheduleViewModel = ViewModelProviders.of(getActivity()).get(ScheduleViewModel.class);
        sharedPref = this.getContext().getSharedPreferences(
                this.getString(R.string.common_preferences), Context.MODE_PRIVATE);

        mRecyclerView = rootView.findViewById(R.id.mRecycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        assert getActivity() != null;
        Application application = getActivity().getApplication();

        dayAdapter = new DayAdapter(day, application);

        updateLiveData();

        mRecyclerView.setAdapter(dayAdapter);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                       @NonNull RecyclerView parent,
                                       @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int totalWidth = parent.getWidth();
                int sidePadding = (totalWidth - (totalWidth - Utils.dpToPx(8, context))) / 2;
                sidePadding = Math.max(0, sidePadding);
                outRect.set(sidePadding, 0, sidePadding, 0);
                }
            });

        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(context,
                mRecyclerView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) { }

            @Override
            public void onLongClick(View view, int position) {
                mRecyclerView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                showDialog(position);
            }
        }));
    }


    private void updateLiveData() {
        if (day == Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
        {
            TimeViewModel timeViewModel = ViewModelProviders.of(this).get(TimeViewModel.class);
            timeViewModel.getTimeList().observe(this, timeList -> dayAdapter.updateTimeList(timeList));
        }

        int weekCount = sharedPref.getInt(this.getString(R.string.week_count), 1);
        SparseArray<List<Lesson>> lessonArray = new SparseArray<>();

        for (int i = 0; i < weekCount; i++) {
            int currentWeek = i;

            scheduleViewModel.setLessonsWeek(day, i);
            scheduleViewModel.getLessons().observe(this, lessons -> {
                lessonArray.put(currentWeek, lessons);
                scheduleViewModel.setWeek(sharedPref.getInt(getString(R.string.current_week), 0));
            });
        }

        scheduleViewModel.getWeek().observe(this, week -> {
            if (lessonArray.get(week) != null) {
                dayAdapter.updateLessonList(lessonArray.get(week));
                dayAdapter.notifyDataSetChanged();
            }

        });
    }


    private void showDialog(int position) {
        LessonDialog lessonDialog = new LessonDialog(
                context, dayAdapter.getItem(position));
        lessonDialog.show();
    }
}
