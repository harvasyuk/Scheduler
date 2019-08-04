package com.scheduler;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class MultiToggleView extends View {

    private int toggleColor;
    private int backgroundColor;
    private int labelColor;
    private String toggleText;

    private Paint paint;
    private float x = 0;
    private float leftSide = 0;
    private float middle = 0;
    private float rightSide = 0;
    private float viewHeightHalf;
    private ValueAnimator animator;
    PropertyValuesHolder propertyLeft;
    PropertyValuesHolder propertyMiddle;
    PropertyValuesHolder propertyRight;
    private int position;
    private StateChangeListener stateChangeListener;


    public MultiToggleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        stateChangeListener = null;

        paint = new Paint();

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.MultiToggleView, 0, 0);

        try {
            toggleText = typedArray.getString(R.styleable.MultiToggleView_toggleLabel);
            toggleColor = typedArray.getInteger(R.styleable.MultiToggleView_toggleColor, 0);
            backgroundColor = typedArray.getInteger(R.styleable.MultiToggleView_backgroundToggleColor, 0);
            labelColor = typedArray.getInteger(R.styleable.MultiToggleView_labelColor, 0);
        } finally {
            typedArray.recycle();
        }

        animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(250);

        animator.addUpdateListener(valueAnimator -> {
            x = (float) valueAnimator.getAnimatedValue("position");
            invalidate();
        });
    }


    public void setToggle(int position) {
        this.position = position;
        new Thread(() -> {
            do {
                switch (position) {
                    case 0:
                        x = leftSide;
                        break;
                    case 1:
                        x = middle;
                        break;
                    case 2:
                        x = rightSide;
                        break;
                    default:
                        x = middle;
                }
            } while (middle == 0);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        float parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        float parentHeight = MeasureSpec.getSize(heightMeasureSpec);

        leftSide = parentWidth * 0.15f;
        rightSide = parentWidth * 0.85f;
        middle = parentWidth * 0.5f;

        viewHeightHalf = parentHeight * 0.5f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(backgroundColor);

        paint.setStrokeWidth(10);
        canvas.drawLine(leftSide, viewHeightHalf, rightSide, viewHeightHalf, paint);

        canvas.drawCircle(middle, viewHeightHalf, 15, paint);
        canvas.drawCircle(leftSide, viewHeightHalf, 15, paint);
        canvas.drawCircle(rightSide, viewHeightHalf, 15, paint);

        if ((x - leftSide > middle - x) && (rightSide - x > x - middle)) {
            propertyMiddle = PropertyValuesHolder.ofFloat("position", x, middle);
            animator.setValues(propertyMiddle);
            position = 1;

        } else if (x - leftSide < middle - x) {
            propertyLeft = PropertyValuesHolder.ofFloat("position", x, leftSide);
            animator.setValues(propertyLeft);
            position = 0;

        } else if (rightSide - x < x - middle) {
            propertyRight = PropertyValuesHolder.ofFloat("position", x, rightSide);
            animator.setValues(propertyRight);
            position = 2;
        }

        paint.setColor(toggleColor);
        canvas.drawCircle(x, viewHeightHalf, 35, paint);

        paint.setColor(labelColor);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(viewHeightHalf * 0.6f);
        canvas.drawText(String.valueOf(position + 1), x, viewHeightHalf + paint.getTextSize() * 0.35f, paint);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                this.getParent().requestDisallowInterceptTouchEvent(true);
                if (leftSide < x && x < rightSide) invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                if (leftSide < x && x < rightSide) invalidate();
                break;
            case MotionEvent.ACTION_UP:
                animator.start();
                performClick();

        }
        return true;
    }

    @Override
    public boolean onFilterTouchEventForSecurity(MotionEvent event) {
        return super.onFilterTouchEventForSecurity(event);
    }

    @Override
    public boolean performClick() {
        super.performClick();

        if (stateChangeListener != null) {
            stateChangeListener.onStateChange(position);
        }
        return true;
    }


    public void setOnStateChangeListener(StateChangeListener stateChangeListener) {
        this.stateChangeListener = stateChangeListener;
    }


    public interface StateChangeListener {
        void onStateChange(int position);
    }
}
