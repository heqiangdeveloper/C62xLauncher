package launcher.base.utils.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import launcher.base.R;

public class SimpleProgressView extends View {

    private static final String TAG = "SimpleProgressView";

    public SimpleProgressView(Context context) {
        super(context);
        init();
    }

    public SimpleProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        readAttrs(context, attrs);
        init();
    }

    public SimpleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readAttrs(context, attrs);
        init();
    }

    public SimpleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        readAttrs(context, attrs);
        init();
    }

    private final int DEFAULT_PROGRESS_HEIGHT = 5;
    private final int DEFAULT_BG_HEIGHT = 5;
    private final int DEFAULT_BG_COLOR = Color.parseColor("#cccccc");
    private final int DEFAULT_PROGRESS_COLOR = Color.parseColor("#ff5533");

    private Paint mProgressPaint;
    private Paint mBackgroundPaint;
    private long mMaxValue = 100;
    private long mProgressValue = 0;
    private float mProgressHeight = DEFAULT_PROGRESS_HEIGHT;
    private float mBackgroundHeight = DEFAULT_PROGRESS_HEIGHT;
    private int mLineWidth;

    private int mWidth, mHeight;
    private long mProgressStartX, mProgressStopX, mBgStartX, mBgStopX, mStartY;

    private int mColorBackground = DEFAULT_BG_COLOR;
    private int mColorProgress = DEFAULT_PROGRESS_COLOR;

    private void readAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SimpleProgressView);
        mProgressHeight = typedArray.getDimension(R.styleable.SimpleProgressView_progressHeight, DEFAULT_PROGRESS_HEIGHT);
        mBackgroundHeight = typedArray.getDimension(R.styleable.SimpleProgressView_progressBgHeight, DEFAULT_BG_HEIGHT);
        mColorBackground = typedArray.getColor(R.styleable.SimpleProgressView_progressBgColor, DEFAULT_BG_COLOR);
        mColorProgress = typedArray.getColor(R.styleable.SimpleProgressView_progressColor, DEFAULT_PROGRESS_COLOR);
        typedArray.recycle();
    }

    private void init() {
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        mBackgroundPaint.setStrokeWidth(mBackgroundHeight);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(mColorBackground);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressPaint.setStrokeWidth(mProgressHeight);
        mProgressPaint.setStyle(Paint.Style.FILL);
        mProgressPaint.setColor(mColorProgress);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(mBgStartX, mStartY, mBgStopX, mStartY, mBackgroundPaint);
        canvas.drawLine(mProgressStartX, mStartY, mProgressStopX, mStartY, mProgressPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mLineWidth = (int) (mWidth - mProgressHeight);
        mProgressStartX = (int) mProgressHeight / 2;
        mBgStartX = (int) (mBackgroundHeight / 2);
        mBgStopX = mWidth - mBgStartX;
        mProgressStopX = mProgressStartX + (mLineWidth * mProgressValue / mMaxValue);
        mStartY = mHeight / 2;
        mBackgroundPaint.setStrokeWidth(mBackgroundHeight);
        mProgressPaint.setStrokeWidth(mProgressHeight);
        Log.d(TAG, "onMeasure,  mLineWidth:" + mLineWidth + ",  mHeight:" + mHeight + ", mProgressHeight:" + mProgressHeight + ", mStartY:" + mStartY);
    }

    public void setMaxValue(long maxValue) {
        mMaxValue = maxValue;
        if (mMaxValue <= 0) {
            mMaxValue = 100;
        }
    }

    public void updateProgress(long progress) {
        this.mProgressValue = progress;
        computeProgressStopX(mProgressValue, mMaxValue, mLineWidth);
        postInvalidate();
    }

    private void computeProgressStopX(long progressValue, long maxValue, long lineWidth) {
        mProgressStopX = mProgressStartX + (lineWidth * progressValue / maxValue);
    }
}
