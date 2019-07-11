package com.scheduler.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.scheduler.LessonTime;
import com.scheduler.R;

import java.util.ArrayList;
import java.util.List;


public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder>{

    private boolean addingItem = false;
    private List<LessonTime> timeList = new ArrayList<>();
    private OnItemClick onItemClick;


    ScheduleAdapter() { }


    public interface OnItemClick {
        void getPosition(int position, char state, String time);
    }


    void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }


    void updateTimeList(List<LessonTime> timeList) {
        this.timeList = timeList;
        notifyDataSetChanged();
    }


    void addItem(boolean flag) {
        addingItem = flag;
        if (flag) {
            notifyItemInserted(getItemCount());
        }
    }


    LessonTime getLessonTime(int position) {
        return timeList.get(position);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView lessonTextView;
        TextView lessonNumber;
        EditText startLessonText;
        EditText endLessonText;
        ConstraintLayout itemLayout;

        private ViewHolder(View itemView) {
            super(itemView);
            lessonTextView = itemView.findViewById(R.id.lessonTextView);
            lessonNumber = itemView.findViewById(R.id.lessonNumber);
            startLessonText = itemView.findViewById(R.id.startTime);
            endLessonText = itemView.findViewById(R.id.endTime);
            itemLayout = itemView.findViewById(R.id.item_view);
        }
    }


    @NonNull
    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.schedule_editor_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position){

        if (timeList.size() > 0 && position < timeList.size()) {
            LessonTime lessonTime = timeList.get(position);

            viewHolder.lessonNumber.setText(String.valueOf(viewHolder.getAdapterPosition() + 1));
            viewHolder.startLessonText.setText(lessonTime.getLessonStart());
            viewHolder.endLessonText.setText(lessonTime.getLessonEnd());

        } else if (viewHolder.getAdapterPosition() == timeList.size()) {
            viewHolder.lessonNumber.setText(String.valueOf(viewHolder.getAdapterPosition() + 1));
        }

        viewHolder.startLessonText.setOnClickListener(v ->
                onItemClick.getPosition(position, 's', viewHolder.startLessonText.getText().toString()));

        viewHolder.endLessonText.setOnClickListener(v ->
                onItemClick.getPosition(position, 'e', viewHolder.endLessonText.getText().toString()));

//        // If the item wasn't previously displayed on screen, it's animated
//        if (viewHolder.getAdapterPosition() > lastPosition) {
//            Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_animation_fall_down);
//            viewHolder.itemView.startAnimation(animation);
//            lastPosition = viewHolder.getAdapterPosition();
//        }
    }


    @Override
    public int getItemCount() {
        if (addingItem) return timeList.size() + 1;
        else return timeList.size();
    }

}