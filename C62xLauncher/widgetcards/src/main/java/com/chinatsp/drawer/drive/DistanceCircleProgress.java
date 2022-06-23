package com.chinatsp.drawer.drive;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import launcher.base.utils.EasyLog;

public class DistanceCircleProgress extends View {
    private static final String TAG = "DistanceCircleProgress";
    private Paint mFramePaint;
    private Paint mArcPaint;
    private Paint mProgressPaint;
    private float mStartAngle;
    private float mSweepAngle;
    private boolean mUseCenter;
    private RectF mOval;
    private int mCenterX;
    private int mCenterY;
    private int mOutRadius;


    private SweepGradient mSweepGradient;
    private int mStrokeWidth;
    private float mPercent = 0.5f;
    private float mOffset;
    private float mOriginOffset;

    public DistanceCircleProgress(Context context) {
        super(context);
        init();
    }

    public DistanceCircleProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DistanceCircleProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DistanceCircleProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        int width = 220;
        int height = 158;
        mStrokeWidth = 12;
        int innerRadius = width / 2 - mStrokeWidth / 2;
        mOutRadius = innerRadius + mStrokeWidth;
        int diameter = innerRadius * 2;
        int left = width / 2 - innerRadius, top = mStrokeWidth / 2;
        mCenterX = left + innerRadius;
        mCenterY = top + innerRadius;
        mOval = new RectF(left, top, left + diameter, top + diameter);
        mUseCenter = false;

        initArcParams();
        initArcPaint();
        initProgressPaint();

        mFramePaint = new Paint();
        mFramePaint.setStyle(Paint.Style.STROKE);
        mFramePaint.setColor(Color.BLUE);
//        startAnim();
    }

    private void startAnim() {
        postDelayed(mRunnable, 100);

    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            float v = mPercent + 0.02f;
            if (v > 1.00001f) {
                v = 0;
            }
            setProgress(v);
            startAnim();
        }
    };

    private void initArcParams() {
        mOriginOffset = (float) Math.toDegrees(Math.asin((double) (mStrokeWidth >> 1) / (mOutRadius - (mStrokeWidth >> 1))));
        mOffset = mOriginOffset;
        mStartAngle = 158;
        mSweepAngle = (180 - mStartAngle) * 2 + 180;
    }

    private void initArcPaint() {
        mArcPaint = new Paint();
        mArcPaint.setColor(Color.RED);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStrokeWidth(mStrokeWidth);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setColor(Color.parseColor("#146277"));
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void initProgressPaint() {
        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStrokeWidth(mStrokeWidth);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        updateProgressGradient();
    }

    public void setProgress(float percent) {
        this.mPercent = percent;
        updateProgressGradient();
        invalidate();
    }

    private void updateProgressGradient() {

        int startColor = Color.parseColor("#7D85DE");
        int endColor = Color.parseColor("#46FCFF");
        if (mPercent < 0.3) {
            endColor = startColor;
        }
        int[] colors = new int[]{startColor, endColor};
        float[] positions = new float[2];
        positions[0] = 0;
        positions[1] = mPercent * mSweepAngle / 360;
        EasyLog.d(TAG, "updateProgressGradient : " + positions[1]);

        mSweepGradient = new SweepGradient(mCenterX, mCenterY, colors, positions);
        mProgressPaint.setShader(mSweepGradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        if (mPercent == 0) {
            mOffset = 0;
        } else {
            mOffset = mOriginOffset;
        }
        canvas.rotate(mStartAngle - mOffset / 2, mCenterX, mCenterY);
        float currentAngle = mSweepAngle * mPercent - mOffset;
        if (currentAngle < mOffset) {
            currentAngle = mOffset;
        }
        EasyLog.d(TAG, "onDraw offset:" + mOffset+" , currentAngle:"+currentAngle);
        canvas.drawArc(mOval, currentAngle, mSweepAngle - currentAngle, mUseCenter, mArcPaint);
        canvas.drawArc(mOval, mOffset, currentAngle, mUseCenter, mProgressPaint);
        canvas.restore();
//        canvas.drawRect(mOval, mFramePaint);
    }

    private void updateArcPaint() {

    }


}
