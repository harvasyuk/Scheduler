package com.scheduler;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class MultiToggleView extends View {

    private int toggleColor;
    private int backgroundColor;
    private int labelColor;

    private Paint backgroundPaint;
    private Paint togglePaint;
    private Paint shadowPaint;
    private Paint backgroundShadow;
    private Paint textPaint;
    private Path togglePath;
    private float x = 0;

    private float leftSide = 0;
    private float middle = 0;
    private float rightSide = 0;

    private float leftPoint = 0;
    private float rightPoint = 0;

    private float viewHeightHalf;
    private ValueAnimator animator;
    private PropertyValuesHolder valuesHolder;

    private StateChangeListener stateChangeListener;

    private float backgroundRadius;
    private float toggleRadius;
    private float toggleLength;

    private RectF toggleRect;
    private RectF leftOval;
    private RectF rightOval;

    private int position = 1;
    private int stateCount = 3;


    public MultiToggleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        stateChangeListener = null;

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.MultiToggleView, 0, 0);

        try {
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

        valuesHolder = PropertyValuesHolder.ofFloat("position", 0, 0);

        init();
    }


    private void init() {
        togglePath = new Path();

        backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setColor(backgroundColor);

        togglePaint = new Paint();
        togglePaint.setAntiAlias(true);
        togglePaint.setColor(toggleColor);

        shadowPaint = new Paint(0);
        shadowPaint.setColor(0xff2196F3);
        shadowPaint.setMaskFilter(new BlurMaskFilter(12, BlurMaskFilter.Blur.NORMAL));

        backgroundShadow = new Paint(0);
        backgroundShadow.setColor(0xff757575);
        backgroundShadow.setMaskFilter(new BlurMaskFilter(4, BlurMaskFilter.Blur.NORMAL));

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(labelColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(getResources().getFont(R.font.product_sans_regular));

        toggleRect = new RectF();
        rightOval = new RectF();
        leftOval = new RectF();
    }


    public void setStateCount(int stateCount) {
        this.stateCount = stateCount;
    }


    public void setToggle(int position) {
        this.position = position;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        float viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        float viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        middle = viewWidth * 0.5f;

        if (stateCount == 2) {

            leftSide = viewWidth * 0.35f;
            rightSide = viewWidth * 0.65f;

            leftPoint = middle;
            rightPoint = middle;

        } else {

            leftSide = viewWidth * 0.25f;
            rightSide = viewWidth * 0.75f;

            leftPoint = (middle + leftSide) / 2f;
            rightPoint = (middle + rightSide) / 2f;
        }

        switch (position) {
            case 1:
                x = leftSide;
                break;
            case 2:
                if (stateCount == 2) x = rightSide;
                else x = middle;
                break;
            case 3:
                x = rightSide;
                break;
        }

        viewHeightHalf = viewHeight * 0.5f;

        backgroundPaint.setStrokeWidth(viewWidth * 0.015f);
        backgroundRadius = viewWidth * 0.025f;
        toggleRadius = viewWidth * 0.06f;
        toggleLength = viewWidth * 0.07f;
        textPaint.setTextSize(viewWidth * 0.055f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawLine(leftSide, viewHeightHalf, rightSide, viewHeightHalf, backgroundPaint);

        canvas.drawCircle(leftSide, viewHeightHalf, backgroundRadius, backgroundShadow);
        canvas.drawCircle(rightSide, viewHeightHalf, backgroundRadius, backgroundShadow);

        if (stateCount > 2) {
            canvas.drawCircle(middle, viewHeightHalf, backgroundRadius, backgroundShadow);
            canvas.drawCircle(middle, viewHeightHalf, backgroundRadius, backgroundPaint);
        }
        canvas.drawCircle(rightSide, viewHeightHalf, backgroundRadius, backgroundPaint);
        canvas.drawCircle(leftSide, viewHeightHalf, backgroundRadius, backgroundPaint);


        if (x > leftPoint && x < rightPoint && stateCount > 2) {
            valuesHolder.setFloatValues(x, middle);
            animator.setValues(valuesHolder);
            position = 2;

        } else if (x < leftPoint) {
            valuesHolder.setFloatValues(x, leftSide);
            animator.setValues(valuesHolder);
            position = 1;

        } else if (x > rightPoint) {
            valuesHolder.setFloatValues(x, rightSide);
            animator.setValues(valuesHolder);
            position = stateCount;
        }

        toggleRect.set(x - toggleLength, viewHeightHalf + toggleRadius, x + toggleLength, viewHeightHalf - toggleRadius);
        rightOval.set(x - toggleRadius + toggleLength, viewHeightHalf - toggleRadius, x + toggleRadius + toggleLength, viewHeightHalf + toggleRadius);
        leftOval.set(x - toggleRadius - toggleLength, viewHeightHalf - toggleRadius, x + toggleRadius - toggleLength, viewHeightHalf + toggleRadius);

        togglePath.addArc(rightOval, -90, 180);
        togglePath.addArc(leftOval, 90, 180);
        togglePath.addRect(toggleRect, Path.Direction.CW);

        canvas.drawPath(togglePath, shadowPaint);
        canvas.drawPath(togglePath, togglePaint);

        togglePath.reset();

        canvas.drawText("Week " + position, x, viewHeightHalf + textPaint.getTextSize() * 0.35f, textPaint);
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
