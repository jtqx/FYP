package com.example.fyp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircularProgressBar extends View {
    private Paint mCirclePaint;
    private Paint mProgressPaint;
    private RectF mCircleBounds;
    private float mProgress;
    private float mMaxProgress;

    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(Color.GRAY);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(40);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(getResources().getColor(R.color.green));
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(40);

        mProgress = 0;
        mMaxProgress = 100;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(mCircleBounds, 0, 360, false, mCirclePaint);
        float angle = 360 * (mProgress / mMaxProgress);
        canvas.drawArc(mCircleBounds, -90, angle, false, mProgressPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        int diameter = Math.min(w, h);
        int padding = 10;
        mCircleBounds = new RectF(padding, padding, diameter - padding, diameter - padding);
    }
    public void setProgress(float progress) {
        mProgress = progress;
        invalidate();
    }

    public void setMaxProgress(float maxProgress) {
        mMaxProgress = maxProgress;
        invalidate();
    }
}