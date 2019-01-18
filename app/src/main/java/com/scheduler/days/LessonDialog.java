package com.scheduler.days;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.scheduler.Lesson;
import com.scheduler.R;

import androidx.annotation.NonNull;

public class LessonDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private EditText subjectText;
    private EditText teacherText ;
    private EditText roomText;
    private Lesson lesson;
    private LessonDialogListener lessonDialogListener;


    LessonDialog(@NonNull Context context, Lesson lesson) {
        super(context, R.style.LessonDialog);
        this.context = context;
        this.lesson = lesson;
    }


    public interface LessonDialogListener {
        void saveData(String lessonName, String teacher, String room, Lesson lesson);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_lesson_dialog);

        Button saveButton = findViewById(R.id.save_lesson_button);
        Button cancelButton = findViewById(R.id.cancel_lesson_button);
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        subjectText = findViewById(R.id.subjectEditText);
        teacherText = findViewById(R.id.teacherEditText);
        roomText = findViewById(R.id.roomEditText);

        subjectText.setText(lesson.getSubjectName().trim());
        teacherText.setText(lesson.getTeacherName().trim());
        roomText.setText(lesson.getRoom().trim());

        subjectText.requestFocus();

        lessonDialogListener = (LessonDialogListener) context;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_lesson_button:
                saveLesson();
                break;
            case R.id.cancel_lesson_button:
                dismiss();
                break;
            default:
                break;
        }
    }


    private void saveLesson() {
        lessonDialogListener.saveData(
                subjectText.getText().toString().trim(),
                teacherText.getText().toString().trim(),
                roomText.getText().toString().trim(),
                lesson);
        dismiss();
    }
}
