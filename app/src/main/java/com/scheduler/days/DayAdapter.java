package com.scheduler.days;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scheduler.Lesson;
import com.scheduler.LessonTime;
import com.scheduler.R;
import com.scheduler.logic.TimeManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {

    private List<Lesson> lessonsList = new ArrayList<>();
    private List<LessonTime> timeList = new ArrayList<>();
    private Context context;
    private int time;
    private int overallTime;
    private int dayOfWeek;
    private byte[] item;
    private TimeManager timeManager;


    DayAdapter(int dayOfWeek, Application application) {
        this.dayOfWeek = dayOfWeek;
        this.context = application;
        timeManager = new TimeManager(application);
    }


    private void updateActiveItem() {
        item = timeManager.getCurrentItem(timeList);
        time = timeManager.getTime();
        overallTime = timeManager.getOverallTime();
        notifyDataSetChanged();
    }


    void updateLessonList(List<Lesson> lessons) {
        this.lessonsList = lessons;
    }


    void updateTimeList(List<LessonTime> timeList) {
        this.timeList = timeList;
        updateActiveItem();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView lessonNumber;
        private TextView subjectName;
        private TextView teacherName;
        private TextView room;
        private TextView countdownText;
        private ConstraintLayout cardView;
        private ProgressBar lessonProgress;

        ViewHolder(View itemView) {
            super(itemView);

            lessonNumber = itemView.findViewById(R.id.lesson_number);
            subjectName = itemView.findViewById(R.id.subject_name);
            teacherName = itemView.findViewById(R.id.teacher_name);
            room = itemView.findViewById(R.id.room);
            countdownText = itemView.findViewById(R.id.left_time);
            cardView = itemView.findViewById(R.id.view);
            lessonProgress = itemView.findViewById(R.id.lesson_progress);
            lessonProgress.setMax(overallTime);
        }
    }


    @NonNull
    @Override
    public DayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycler_item_layout, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        Lesson lesson = lessonsList.get(position);

        //lesson
        viewHolder.lessonNumber.setText(String.format(Locale.getDefault(), "%d", lesson.getId() + 1));
        viewHolder.lessonNumber.setText(lesson.getLessonNumber());
        viewHolder.subjectName.setText(lesson.getSubjectName());
        viewHolder.teacherName.setText(lesson.getTeacherName());
        viewHolder.room.setText(lesson.getRoom());

        //progressbar
        viewHolder.countdownText.setVisibility(View.GONE);
        viewHolder.lessonProgress.setVisibility(View.GONE);
        viewHolder.lessonProgress.setProgress(overallTime - time);

        //cardView properties
        viewHolder.cardView.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_recycler_item));
        viewHolder.cardView.setElevation(0f);

        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        if (today == dayOfWeek) {

            if (item[1] == 0 && viewHolder.getAdapterPosition() == item[0]) {

                runTimer(viewHolder);

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    viewHolder.cardView.setOutlineSpotShadowColor(ContextCompat.getColor(context, R.color.orangeShadow));
                    viewHolder.cardView.setElevation(18f);
                } else {
                    viewHolder.cardView.setElevation(12f);
                }
                viewHolder.cardView.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_active_recycler_item));
                viewHolder.lessonProgress.setVisibility(View.VISIBLE);
                viewHolder.countdownText.setVisibility(View.VISIBLE);

            } else if (item[1] == 1 && viewHolder.getAdapterPosition() == item[0]) {

                runTimer(viewHolder);

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    viewHolder.cardView.setOutlineSpotShadowColor(ContextCompat.getColor(context, R.color.colorAccent));
                    viewHolder.cardView.setElevation(18f);
                } else {
                    viewHolder.cardView.setElevation(14f);
                }
                viewHolder.cardView.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_breack_recycler_item));
                viewHolder.lessonProgress.setVisibility(View.VISIBLE);
                viewHolder.countdownText.setVisibility(View.VISIBLE);
            }
        }
    }


    private void runTimer(final ViewHolder viewHolder) {
        CountDownTimer timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                viewHolder.countdownText.setText(
                        String.format(Locale.getDefault(), "%d", millisUntilFinished / 60000));
                viewHolder.lessonProgress.setProgress(
                        (int) (overallTime - (millisUntilFinished / 60000)));
            }
            @Override
            public void onFinish() {
                updateActiveItem();
            }
        };
        timer.start();
    }


    @Override
    public int getItemCount() {
        return lessonsList.size();
    }


    Lesson getItem(int position) {
        return lessonsList.get(position);
    }

}