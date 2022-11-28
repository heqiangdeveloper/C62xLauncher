package com.common.xui.widget.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.common.xui.R;


@SuppressLint("AppCompatCustomView")
public class CustomSeekbar extends SeekBar {

    private int mHeight;
    private int mWidth;
    private Paint mPaint;

    private float mBorderWidth = 2.6f;       // 描边宽度
    private float mShadowRadius = 13f;
    private Paint mShadowPaint;
    private Paint mBorderPaint;
    private float radius = 35;
    private boolean isTouched;

    public CustomSeekbar(Context context) {
        super(context);
        init();
    }

    public CustomSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomSeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBorderWidth = getResources().getDimension(R.dimen.cardview_compat_inset_shadow);
        mShadowRadius = getResources().getDimension(R.dimen.shade_width);
        radius = getResources().getDimension(R.dimen.seekbar_bg_radius_small);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        // 初始化光晕效果画笔
        mShadowPaint = new Paint();
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowPaint.setColor(getContext().getColor(R.color.seekbar_start_color));
        mShadowPaint.setAlpha(200);
        mShadowPaint.setMaskFilter(new BlurMaskFilter(mShadowRadius - 1, BlurMaskFilter.Blur.OUTER));
        // 抖动处理
        mShadowPaint.setDither(true);
        //描边
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setAlpha(170);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(getContext().getColor(R.color.seekbar_start_color));
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPonit(canvas);
        if (isTouched)
            drawShade(canvas);
    }

    private int error = 3;

    private void drawShade(Canvas canvas) {
        int offset = 17;
        int progress = getProgress();
        int max = getMax();

        max = max < 1 ? 1 : max;// max 不能为0
        progress = progress < 0 ? 0 : progress;// progress 不能小于0
        // 当最大进度小于10，刻度变化太大，放大10倍
        if (max < 10) {
            max = max * 10;
            progress = progress * 10;
        }
        int max2 = max / 2;
        int a = (max2 - progress) * offset / max2;
        float rightProgress = mWidth * progress / max + a;

        float left = mShadowRadius + mBorderWidth;
        float top = mShadowRadius;
        float right = mWidth - mShadowRadius - mBorderWidth;
        float bottom = mHeight - mShadowRadius;

//        Logcat.e("radius: " + radius);

        Path path = new Path();
        path.moveTo(left + radius, top);
        path.quadTo(left + error, top + error, left + 2.5f, top + radius);
        path.lineTo(left + 2.5f, bottom - radius);
        path.quadTo(left + error, bottom - error, left + radius, bottom);

        path.lineTo(right - radius, bottom);
        path.quadTo(right - error, bottom - error, right - 2, bottom - radius);
        path.lineTo(right - 2, top + radius);
        path.quadTo(right - error, top + error, right - radius, top);
        path.close();

        Path oPath = new Path();
        oPath.moveTo(left, top);
        oPath.lineTo(rightProgress, top);
        oPath.lineTo(rightProgress, bottom);
        oPath.lineTo(left, bottom);
        oPath.close();
        path.op(oPath, Path.Op.INTERSECT);

        canvas.drawPath(path, mShadowPaint);
        canvas.drawPath(path, mBorderPaint);
    }

    private void drawPonit(Canvas canvas) {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.luminance_bg_blue);
//        canvas.drawBitmap(bitmap, mShadowRadius, mShadowRadius, mPaint);
        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF dst = new RectF(mShadowRadius + 2, mShadowRadius, mWidth - mShadowRadius - 2, mHeight - mShadowRadius);
        canvas.drawBitmap(bitmap, src, dst, mPaint);

        int interval = mWidth / 10;
        int y = mHeight / 2;
//        mPaint.setColor(Color.parseColor("#7591A4"));
        mPaint.setColor(Color.parseColor("#ffffff"));
        for (int i = 1; i < 10; i++) {
            canvas.drawCircle(i * interval, y, 2, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouched = true;
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isTouched = false;
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }
}
