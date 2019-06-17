package com.scheduler.days;

import android.app.Application;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
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
import com.scheduler.LessonTime;
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
    int day;
    private Context context;

    //user interface
    private RecyclerView mRecyclerView;
    private DayAdapter dayAdapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_layout, container, false);
        context = getContext();

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
            timeViewModel.getTimeList().observe(this, new androidx.lifecycle.Observer<List<LessonTime>>() {
                @Override
                public void onChanged(List<LessonTime> timeList) {
                    dayAdapter.updateTimeList(timeList);
                }
            });
        }

        ScheduleViewModel scheduleViewModel = ViewModelProviders.of(this).get(ScheduleViewModel.class);
        scheduleViewModel.setDay(day);
        scheduleViewModel.getLessons().observe(this, new androidx.lifecycle.Observer<List<Lesson>>() {
            @Override
            public void onChanged(List<Lesson> lessons) {
                dayAdapter.updateLessonList(lessons);
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
