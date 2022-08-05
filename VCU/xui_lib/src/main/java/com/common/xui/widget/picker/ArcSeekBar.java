package com.common.xui.widget.picker;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.common.xui.R;

public class ArcSeekBar extends View {
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 文本画笔
     */
    private TextPaint mTextPaint;
    /**
     * 文本画笔
     */
    private TextPaint mOneHundredTextPaint;
    /**
     * 文本画笔
     */
    private TextPaint mFiftyTextPaint;
    /**
     * 文本画笔
     */
    private TextPaint mSeventyTextPaint;

    /**
     * 刻度文本画笔
     */
    private TextPaint mTickTextPaint;
    /**
     * 扇形渐变画笔
     */
    private Paint fanShapePaint;
    /**
     * 刻度字体大小
     */
    private float mTuckTextSize;

    /**
     * 刻度字体颜色
     */
    private int mTuckTextColor = this.getResources().getColor(R.color.acr_seek_txt_color);

    /**
     * 笔画描边的宽度
     */
    private float mStrokeWidth;

    /**
     *
     */
    private Paint.Cap mStrokeCap = Paint.Cap.ROUND;

    /**
     * 开始角度(默认从12点钟方向开始)
     */
    private int mStartAngle = 270;
    /**
     * 扫描角度(一个圆)
     */
    private int mSweepAngle = 360;

    /**
     * 圆心坐标x
     */
    private float mCircleCenterX;
    /**
     * 圆心坐标y
     */
    private float mCircleCenterY;

    /**
     * 弧形 正常颜色
     */
    private int mNormalColor = 0xFFC8C8C8;
    /**
     * 进度颜色
     */
    private int mProgressColor;

    /**
     * 是否使用着色器
     */
    private boolean isShader = true;

    /**
     * 着色器
     */
    private Shader mShader;

    /**
     * 着色器颜色
     */
    private int[] mShaderColors;

    /**
     * 半径
     */
    private float mRadius = 180;

    /**
     * 刻度与弧形的间距
     */
    private float mTickPadding;

    /**
     * 刻度间隔的角度大小
     */
    private float mTickSplitAngle = 1;

    /**
     * 刻度的角度大小
     */
    private float mBlockAngle = 1f;

    /**
     * 刻度偏移的角度大小
     */
    private float mTickOffsetAngle = 0;

    /**
     * 总刻度数
     */
    private int mTotalTickCount;
    /**
     * 度数画笔宽度
     */
    private float mTickStrokeWidth;

    /**
     * 最大进度
     */
    private int mMax = 50;

    /**
     * 当前进度
     */
    private int mProgress = 0;

    /**
     * 动画持续的时间
     */
    private int mDuration = 500;

    /**
     * 标签内容
     */
    private String mLabelText;

    /**
     * 字体大小
     */
    private float mLabelTextSize;

    /**
     * 字体颜色
     */
    private int mLabelTextColor = 0xFF333333;

    /**
     * Label距离中心位置的内间距
     */
    private float mLabelPaddingTop;
    private float mLabelPaddingBottom;
    private float mLabelPaddingLeft;
    private float mLabelPaddingRight;
    /**
     * 进度百分比
     */
    private int mProgressPercent;

    /**
     * 是否显示标签文字
     */
    private boolean isShowLabel = true;
    /**
     * 是否默认显示百分比为标签文字
     */
    private boolean isShowPercentText = true;
    /**
     * 是否显示刻度
     */
    private boolean isShowTick = false;

    /**
     * 拖动按钮的画笔宽度
     */
    private float mThumbStrokeWidth;
    /**
     * 拖动按钮的颜色
     */
    private int mThumbColor = 0xFFE8D30F;
    /**
     * 拖动按钮的半径
     */
    private float mThumbRadius;
    /**
     * 拖动按钮的中心点X坐标
     */
    private float mThumbCenterX;
    /**
     * 拖动按钮的中心点Y坐标
     */
    private float mThumbCenterY;
    /**
     * 触摸时可偏移距离
     */
    private float mAllowableOffsets;
    /**
     * 触摸时按钮半径放大量
     */
    private float mThumbRadiusEnlarges;

    /**
     * 是否显示拖动按钮
     */
    private boolean isShowThumb = true;

    /**
     * 手势，用来处理点击事件
     */
    private GestureDetector mDetector;

    /**
     * 是否可以拖拽
     */
    private boolean isCanDrag = false;

    /**
     * 是否启用拖拽改变进度
     */
    private boolean isEnabledDrag = true;

    /**
     * 是否启用点击改变进度
     */
    private boolean isEnabledSingle = true;

    private boolean isMeasureCircle = false;

    private OnChangeListener mOnChangeListener;
    private Shader shader;//渐变

    public ArcSeekBar(Context context) {
        this(context, null);
    }

    public ArcSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 初始化
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        mShaderColors = new int[]{context.getResources().getColor(R.color.acr_seek_start_color), context.getResources().getColor(R.color.acr_seek_end_color)};
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ArcSeekBar);
        DisplayMetrics displayMetrics = getDisplayMetrics();
        mStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, displayMetrics);

        mLabelTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 30, displayMetrics);

        mTuckTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 24, displayMetrics);

        mTickPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, displayMetrics);

        mTickStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.8f, displayMetrics);


        mThumbRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 23, displayMetrics);

        mThumbStrokeWidth = mThumbRadius;


        mAllowableOffsets = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, displayMetrics);

        mThumbRadiusEnlarges = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, displayMetrics);

        int size = a.getIndexCount();
        for (int i = 0; i < size; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.ArcSeekBar_arcStrokeWidth) {
                mStrokeWidth = a.getDimension(attr, mStrokeWidth);
            } else if (attr == R.styleable.ArcSeekBar_arcStrokeCap) {
                mStrokeCap = getStrokeCap(a.getInt(attr, 3));
            } else if (attr == R.styleable.ArcSeekBar_arcNormalColor) {
                mNormalColor = a.getColor(attr, mNormalColor);
            } else if (attr == R.styleable.ArcSeekBar_arcProgressColor) {
                mProgressColor = a.getColor(attr, mProgressColor);
                isShader = false;
            } else if (attr == R.styleable.ArcSeekBar_arcStartAngle) {
                mStartAngle = a.getInt(attr, mStartAngle);
            } else if (attr == R.styleable.ArcSeekBar_arcSweepAngle) {
                mSweepAngle = a.getInt(attr, mSweepAngle);
            } else if (attr == R.styleable.ArcSeekBar_arcMax) {
                int max = a.getInt(attr, mMax);
                if (max > 0) {
                    mMax = max;
                }
            } else if (attr == R.styleable.ArcSeekBar_arcProgress) {
                mProgress = a.getInt(attr, mProgress);
            } else if (attr == R.styleable.ArcSeekBar_arcDuration) {
                mDuration = a.getInt(attr, mDuration);
            } else if (attr == R.styleable.ArcSeekBar_arcLabelText) {
                mLabelText = a.getString(attr);
            } else if (attr == R.styleable.ArcSeekBar_arcLabelTextSize) {
                mLabelTextSize = a.getDimension(attr, mLabelTextSize);
            } else if (attr == R.styleable.ArcSeekBar_arcLabelTextColor) {
                mLabelTextColor = a.getColor(attr, mLabelTextColor);
            } else if (attr == R.styleable.ArcSeekBar_arcLabelPaddingTop) {
                mLabelPaddingTop = a.getDimension(attr, 0);
            } else if (attr == R.styleable.ArcSeekBar_arcLabelPaddingBottom) {
                mLabelPaddingBottom = a.getDimension(attr, 0);
            } else if (attr == R.styleable.ArcSeekBar_arcLabelPaddingLeft) {
                mLabelPaddingLeft = a.getDimension(attr, 0);
            } else if (attr == R.styleable.ArcSeekBar_arcLabelPaddingRight) {
                mLabelPaddingRight = a.getDimension(attr, 0);
            } else if (attr == R.styleable.ArcSeekBar_arcShowLabel) {
                isShowLabel = a.getBoolean(attr, true);
            } else if (attr == R.styleable.ArcSeekBar_arcShowTick) {
                isShowTick = a.getBoolean(attr, true);
            } else if (attr == R.styleable.ArcSeekBar_arcTickStrokeWidth) {
                mTickStrokeWidth = a.getDimension(attr, mTickStrokeWidth);
            } else if (attr == R.styleable.ArcSeekBar_arcTickPadding) {
                mTickPadding = a.getDimension(attr, mTickPadding);
            } else if (attr == R.styleable.ArcSeekBar_arcTickSplitAngle) {
                mTickSplitAngle = a.getInt(attr, 5);
            } else if (attr == R.styleable.ArcSeekBar_arcBlockAngle) {
                mBlockAngle = a.getInt(attr, 1);
            } else if (attr == R.styleable.ArcSeekBar_arcTickOffsetAngle) {
                mTickOffsetAngle = a.getInt(attr, 0);
            } else if (attr == R.styleable.ArcSeekBar_arcThumbStrokeWidth) {
                mThumbStrokeWidth = a.getDimension(attr, mThumbStrokeWidth);
            } else if (attr == R.styleable.ArcSeekBar_arcThumbColor) {
                mThumbColor = a.getColor(attr, mThumbColor);
            } else if (attr == R.styleable.ArcSeekBar_arcThumbRadius) {
                mThumbRadius = a.getDimension(attr, mThumbRadius);
            } else if (attr == R.styleable.ArcSeekBar_arcThumbRadiusEnlarges) {
                mThumbRadiusEnlarges = a.getDimension(attr, mThumbRadiusEnlarges);
            } else if (attr == R.styleable.ArcSeekBar_arcShowThumb) {
                isShowThumb = a.getBoolean(attr, isShowThumb);
            } else if (attr == R.styleable.ArcSeekBar_arcAllowableOffsets) {
                mAllowableOffsets = a.getDimension(attr, mAllowableOffsets);
            } else if (attr == R.styleable.ArcSeekBar_arcEnabledDrag) {
                isEnabledDrag = a.getBoolean(attr, true);
            } else if (attr == R.styleable.ArcSeekBar_arcEnabledSingle) {
                isEnabledSingle = a.getBoolean(attr, true);
            }
        }
        isShowPercentText = TextUtils.isEmpty(mLabelText);
        a.recycle();
        mProgressPercent = (int) (mProgress * 100.0f / mMax);
        mPaint = new Paint();
        mTextPaint = new TextPaint();
        mTickTextPaint = new TextPaint();
        mOneHundredTextPaint = new TextPaint();
        mSeventyTextPaint = new TextPaint();
        mFiftyTextPaint = new TextPaint();
        fanShapePaint = new Paint();

        mTotalTickCount = (int) (mSweepAngle / (mTickSplitAngle + mBlockAngle));

        mDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent event) {

                if (isInArc(event.getX(), event.getY())) {
                    updateDragThumb(event.getX(), event.getY(), true);
                    if (mOnChangeListener != null) {
                        mOnChangeListener.onSingleTapUp();
                    }
                    return true;
                }

                return super.onSingleTapUp(event);
            }
        });

    }

    private Paint.Cap getStrokeCap(int value) {
        switch (value) {
            case 1:
                return Paint.Cap.BUTT;
            case 2:
                return Paint.Cap.SQUARE;
            default:
                return Paint.Cap.ROUND;
        }
    }


    private DisplayMetrics getDisplayMetrics() {
        return getResources().getDisplayMetrics();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int defaultValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 220, getDisplayMetrics());
        int defaultWidthValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 505, getDisplayMetrics());

        int width = measureHandler(widthMeasureSpec, defaultWidthValue);
        int height = measureHandler(heightMeasureSpec, defaultValue);
        //int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        //int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        //圆心坐标
        mCircleCenterX = (width + getPaddingLeft() - getPaddingRight()) / 2.0f;
        mCircleCenterY = (height + getPaddingTop() - getPaddingBottom()) / 2.0f;
        //计算间距
        int padding = Math.max(getPaddingLeft() + getPaddingRight(), getPaddingTop() + getPaddingBottom());
        //半径=视图宽度-横向或纵向内间距值 - 画笔宽度
        //mRadius = (width - padding - Math.max(mStrokeWidth, mThumbStrokeWidth)) / 2.0f - mThumbRadius;
        //默认着色器
        mShader = new SweepGradient(mCircleCenterX, mCircleCenterX, mShaderColors, null);
        isMeasureCircle = true;
        setMeasuredDimension(width, height);

    }

    /**
     * 测量
     *
     * @param measureSpec
     * @param defaultSize
     * @return
     */
    private int measureHandler(int measureSpec, int defaultSize) {

        int result = defaultSize;
        int measureMode = MeasureSpec.getMode(measureSpec);
        int measureSize = MeasureSpec.getSize(measureSpec);
        if (measureMode == MeasureSpec.EXACTLY) {
            result = measureSize;
        } else if (measureMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, measureSize);
        }
        return result;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawFanShape(canvas);
        drawArc(canvas);
        drawThumb(canvas);
        drawText(canvas);
        drawTickText(canvas);
    }


    /**
     * 绘制弧形(默认为一个圆)
     *
     * @param canvas
     */
    private void drawArc(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

        if (isShowTick) {//是否显示刻度
            mPaint.setStrokeWidth(mTickStrokeWidth);
            float circleRadius = mRadius - mTickPadding - mStrokeWidth;
            float tickDiameter = circleRadius * 2;
            float tickStartX = mCircleCenterX - circleRadius;
            float tickStartY = mCircleCenterY - circleRadius;
            RectF rectF = new RectF(tickStartX, tickStartY, tickStartX + tickDiameter, tickStartY + tickDiameter);

            final int currentBlockIndex = (int) (mProgressPercent / 100f * mTotalTickCount);

            for (int i = 0; i < mTotalTickCount; i++) {
                if (i < currentBlockIndex) {
                    //已选中的刻度
                    if (isShader && mShader != null) {
                        mPaint.setShader(mShader);
                    } else {
                        mPaint.setColor(mProgressColor);
                    }
                    //绘制刻度
                    canvas.drawArc(rectF, i * (mBlockAngle + mTickSplitAngle) + mStartAngle + mTickOffsetAngle, mBlockAngle, false, mPaint);
                } else {
                    if (mNormalColor != 0) {
                        //未选中的刻度
                        mPaint.setShader(null);
                        mPaint.setColor(mNormalColor);
                        //绘制刻度
                        canvas.drawArc(rectF, i * (mBlockAngle + mTickSplitAngle) + mStartAngle + mTickOffsetAngle, mBlockAngle, false, mPaint);
                    }
                }
            }

        }

        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setShader(null);
        mPaint.setStrokeCap(mStrokeCap);

        //进度圆半径
        float diameter = mRadius * 2;
        float startX = mCircleCenterX - mRadius;
        float startY = mCircleCenterY - mRadius;
        RectF rectF1 = new RectF(startX, startY, startX + diameter, startY + diameter);

        if (mNormalColor != 0) {
            mPaint.setColor(mNormalColor);
            //绘制底层弧形
            canvas.drawArc(rectF1, mStartAngle, mSweepAngle, false, mPaint);
        }

        //着色器不为空则设置着色器，反之用纯色
        if (isShader && mShader != null) {
            mPaint.setShader(mShader);
        } else {
            mPaint.setColor(mProgressColor);
        }

        float ratio = getRatio();
        if (ratio != 0) {
            //绘制当前进度弧形
            canvas.drawArc(rectF1, mStartAngle, mSweepAngle * ratio, false, mPaint);
        }

    }

    private void drawThumb(Canvas canvas) {
        if (isShowThumb) {
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.STROKE);
           /* float thumbAngle = mStartAngle + mSweepAngle * getRatio();
            //已知圆心，半径，角度，求圆上的点坐标
            mThumbCenterX = (float) (mCircleCenterX + mRadius * Math.cos(Math.toRadians(thumbAngle)));
            mThumbCenterY = (float) (mCircleCenterY + mRadius * Math.sin(Math.toRadians(thumbAngle)));
            if (isCanDrag) {
                mPaint.setStrokeWidth(8);
                mPaint.setColor(mContext.getResources().getColor(R.color.seek_chick_color));
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                canvas.drawCircle(mThumbCenterX, mThumbCenterY, mThumbRadius + mThumbRadiusEnlarges, mPaint);
            }

            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setStrokeWidth(6);
            mPaint.setColor(mContext.getResources().getColor(R.color.smooth_circle_color));
            canvas.drawCircle(mThumbCenterX, mThumbCenterY, mThumbRadius, mPaint);

            mPaint.setColor(mContext.getResources().getColor(R.color.smooth_bg_color_node));
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(mThumbCenterX, mThumbCenterY, mThumbRadius, mPaint);*/
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.circular_click);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            float thumbAngle = (mStartAngle + 5) + mSweepAngle * getRatio() + 3.5f;
            //已知圆心，半径，角度，求圆上的点坐标
            mThumbCenterX = (float) (mCircleCenterX + mRadius * Math.cos(Math.toRadians(thumbAngle)));
            mThumbCenterY = (float) (mCircleCenterY + mRadius * Math.sin(Math.toRadians(thumbAngle)));
            canvas.drawBitmap(bitmap, mThumbCenterX - thumbAngle / 2 + mRadius / 2 - 20, mThumbCenterY, mPaint);
        }

    }

    /**
     * 绘制中间的文本
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        if (!isShowLabel) {
            return;
        }
        mTextPaint.reset();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setTextSize(mLabelTextSize);
        mTextPaint.setColor(mLabelTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        // 计算文字高度
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        // 计算文字baseline
        float textBaseX = getWidth() / 2 + mLabelPaddingLeft - mLabelPaddingRight;
        float textBaseY = getHeight() - (getHeight() - fontHeight) / 2 - fontMetrics.bottom + mLabelPaddingTop - mLabelPaddingBottom;
        if (isShowPercentText) {//是否显示百分比
            canvas.drawText(mProgressPercent + "%", textBaseX, textBaseY, mTextPaint);
        } else if (!TextUtils.isEmpty(mLabelText)) {//显示自定义文本
            canvas.drawText(mLabelText, textBaseX, textBaseY, mTextPaint);
        }

    }

    /**
     * 绘制刻度文本
     *
     * @param canvas
     */
    private void drawTickText(Canvas canvas) {
        mOneHundredTextPaint.reset();
        mOneHundredTextPaint.setAntiAlias(true);
        mOneHundredTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mOneHundredTextPaint.setTextSize(mTuckTextSize);
        mOneHundredTextPaint.setColor(mTuckTextColor);
        mOneHundredTextPaint.setTextAlign(Paint.Align.CENTER);

        mFiftyTextPaint.reset();
        mFiftyTextPaint.setAntiAlias(true);
        mFiftyTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mFiftyTextPaint.setTextSize(mTuckTextSize);
        mFiftyTextPaint.setColor(mTuckTextColor);
        mFiftyTextPaint.setTextAlign(Paint.Align.CENTER);

        mSeventyTextPaint.reset();
        mSeventyTextPaint.setAntiAlias(true);
        mSeventyTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mSeventyTextPaint.setTextSize(mTuckTextSize);
        mSeventyTextPaint.setColor(mTuckTextColor);
        mSeventyTextPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = mTickTextPaint.getFontMetrics();
        //计算文字高度
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        float textBaseX = getWidth() / 2 + mLabelPaddingLeft - mLabelPaddingRight;
        float textBaseY = getHeight() - (getHeight() - fontHeight) / 2 - fontMetrics.bottom + mLabelPaddingTop - mLabelPaddingBottom;
        float ratio = getRatio();
        float startX = mCircleCenterX - mRadius - 30;
        float startY = mCircleCenterY - mRadius;
        float mun = ratio * 100;
        if (mun + 25 == 75) {
            mSeventyTextPaint.setColor(this.getResources().getColor(R.color.acr_seek_txt_old_color));
            mSeventyTextPaint.setTextSize(36);
        } else if (mun == 100) {
            mOneHundredTextPaint.setColor(this.getResources().getColor(R.color.acr_seek_txt_old_color));
            mOneHundredTextPaint.setTextSize(36);
        } else if (mun + 50 == 50) {
            mFiftyTextPaint.setColor(this.getResources().getColor(R.color.acr_seek_txt_old_color));
            mFiftyTextPaint.setTextSize(36);
        }
        canvas.drawText(75 + "°", startX - 4, textBaseY + 15, mSeventyTextPaint);
        canvas.drawText(50 + "°", startX + 15, getHeight() - 10, mFiftyTextPaint);
        canvas.drawText(100 + "°", startX + 5, 30, mOneHundredTextPaint);
    }

    /**
     * 绘制扇形背景
     *
     * @param canvas
     */
    private void drawFanShape(Canvas canvas) {
        fanShapePaint.reset();
        fanShapePaint.setStyle(Paint.Style.FILL);
        fanShapePaint.setAntiAlias(true);
        float startX = mCircleCenterX - mRadius;
        float startY = mCircleCenterY - mRadius;
        float diameter = mRadius * 2;
        RectF rectF = new RectF(0, 0, getWidth(), getHeight());
        RectF rectF1 = new RectF(startX + 12f, startY, startX + diameter, startY + diameter);
        int[] mColors = {this.getResources().getColor(R.color.smooth_bg_color_start),
                this.getResources().getColor(R.color.smooth_bg_color_end),
                this.getResources().getColor(R.color.smooth_bg_color_end),
                Color.TRANSPARENT};
        float[] loaction = new float[]{0.25F, 0.5F, 0.75F, 0.6F};
        shader = new LinearGradient(0, 0, rectF.right - 20, 50, mColors, loaction, Shader.TileMode.REPEAT);
        fanShapePaint.setShader(shader);
        float ratio = getRatio();
        canvas.drawArc(rectF1, mStartAngle, mSweepAngle * ratio, true, fanShapePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isEnabledDrag) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    checkCanDrag(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isCanDrag) {
                        updateDragThumb(event.getX(), event.getY(), false);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    getParent().requestDisallowInterceptTouchEvent(false);
                    if (mOnChangeListener != null) {
                        mOnChangeListener.onStopTrackingTouch(isCanDrag);
                    }
                    isCanDrag = false;
                    invalidate();
                    break;

            }
        }

        if (isEnabledSingle) {
            mDetector.onTouchEvent(event);
        }

        return isEnabledSingle || isEnabledDrag || super.onTouchEvent(event);
    }

    /**
     * 判断坐标点是否在弧形上
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isInArc(float x, float y) {
        float distance = getDistance(mCircleCenterX, mCircleCenterY, x, y);
        if (Math.abs(distance - mRadius) <= mStrokeWidth / 2f + mAllowableOffsets) {
            if (mSweepAngle < 360) {
                float angle = (getTouchDegrees(x, y) + mStartAngle) % 360;
                if (mStartAngle + mSweepAngle <= 360) {
                    return angle >= mStartAngle && angle <= mStartAngle + mSweepAngle;
                } else {
                    return angle >= mStartAngle || angle <= (mStartAngle + mSweepAngle) % 360;
                }
            }

            return true;

        }
        return false;
    }

    /**
     * 获取两点间距离
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private float getDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /**
     * 更新多拽进度
     *
     * @param x
     * @param y
     * @param isSingle
     */
    private void updateDragThumb(float x, float y, boolean isSingle) {
        int progress = getProgressForAngle(getTouchDegrees(x, y));
        if (!isSingle) {
            int tempProgressPercent = (int) (progress * 100.0f / mMax);
            //当滑动至至边界值时，增加进度校准机制
            if (mProgressPercent < 10 && tempProgressPercent > 90) {
                progress = 0;
            } else if (mProgressPercent > 90 && tempProgressPercent < 10) {
                progress = mMax;
            }
            int progressPercent = (int) (progress * 100.0f / mMax);
            //拖动进度突变不允许超过30%
            if (Math.abs(progressPercent - mProgressPercent) > 30) {
                return;
            }
        }

        setProgress(progress, true);
    }

    /**
     * 通过弧度换算得到当前精度
     *
     * @param angle
     * @return
     */
    private int getProgressForAngle(float angle) {
        int touchProgress = Math.round(1.0f * mMax / mSweepAngle * angle);
        return touchProgress;
    }

    /**
     * 获取触摸坐标的夹角度数
     *
     * @param x
     * @param y
     * @return
     */
    private float getTouchDegrees(float x, float y) {
        float x1 = x - mCircleCenterX;
        float y1 = y - mCircleCenterY;
        //求触摸点弧形的夹角度数
        float angle = (float) (Math.atan2(y1, x1) * 180 / Math.PI);
        angle -= mStartAngle;
        while (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    /**
     * 检测是否可拖拽
     *
     * @param x
     * @param y
     */
    private void checkCanDrag(float x, float y) {
        float distance = getDistance(mThumbCenterX, mThumbCenterY, x, y);
        isCanDrag = distance <= mThumbRadius + mAllowableOffsets;
        if (mOnChangeListener != null) {
            mOnChangeListener.onStartTrackingTouch(isCanDrag);
        }
        invalidate();
    }

    /**
     * 显示进度动画效果（根据当前已有进度开始）
     *
     * @param progress
     */
    public void showAppendAnimation(int progress) {
        showAnimation(mProgress, progress, mDuration);
    }

    /**
     * 显示进度动画效果
     *
     * @param progress
     */
    public void showAnimation(int progress) {
        showAnimation(progress, mDuration);
    }

    /**
     * 显示进度动画效果
     *
     * @param progress
     * @param duration 动画时长
     */
    public void showAnimation(int progress, int duration) {
        showAnimation(0, progress, duration);
    }

    /**
     * 显示进度动画效果，从from到to变化
     *
     * @param from
     * @param to
     * @param duration 动画时长
     */
    public void showAnimation(int from, int to, int duration) {
        showAnimation(from, to, duration, null);
    }

    /**
     * 显示进度动画效果，从from到to变化
     *
     * @param from
     * @param to
     * @param duration 动画时长
     * @param listener
     */
    public void showAnimation(int from, int to, int duration, Animator.AnimatorListener listener) {
        this.mDuration = duration;
        this.mProgress = from;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(from, to);
        valueAnimator.setDuration(duration);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setProgress((int) animation.getAnimatedValue());
            }
        });

        if (listener != null) {
            valueAnimator.removeAllUpdateListeners();
            valueAnimator.addListener(listener);
        }

        valueAnimator.start();
    }

    /**
     * 进度比例
     *
     * @return
     */
    private float getRatio() {
        return mProgress * 1.0f / mMax;
    }

    /**
     * 设置最大进度
     *
     * @param max
     */
    public void setMax(int max) {
        if (max > 0) {
            this.mMax = max;
            invalidate();
        }
    }

    /**
     * 设置当前进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        if (progress <= 50) {
            progress = 0;
        } else {
            progress = progress - mMax;
        }
        setProgress(progress, false);
    }

    private void setProgress(int progress, boolean fromUser) {
        if (progress < 0) {
            progress = 0;
        } else if (progress > mMax) {
            progress = mMax;
        }
        this.mProgress = progress;
        mProgressPercent = (int) (mProgress * 100.0f / mMax);
        invalidate();

        if (mOnChangeListener != null) {
            mOnChangeListener.onProgressChanged(mProgress + mMax, mMax, fromUser);
        }
    }

    /**
     * 设置正常颜色
     *
     * @param color
     */
    public void setNormalColor(int color) {
        this.mNormalColor = color;
        invalidate();
    }


    /**
     * 设置着色器
     *
     * @param shader
     */
    public void setShader(Shader shader) {
        isShader = true;
        this.mShader = shader;
        invalidate();
    }

    /**
     * 设置进度颜色（通过着色器实现渐变色）
     *
     * @param colors
     */
    public void setProgressColor(int... colors) {
        if (isMeasureCircle) {
            Shader shader = new SweepGradient(mCircleCenterX, mCircleCenterX, colors, null);
            setShader(shader);
        } else {
            mShaderColors = colors;
            isShader = true;
        }
    }

    /**
     * 设置进度颜色（纯色）
     *
     * @param color
     */
    public void setProgressColor(int color) {
        isShader = false;
        this.mProgressColor = color;
        invalidate();
    }

    /**
     * 设置进度颜色
     *
     * @param resId
     */
    public void setProgressColorResource(int resId) {
        int color = getResources().getColor(resId);
        setProgressColor(color);
    }

    /**
     * 设置是否显示外环刻度
     *
     * @param isShowTick
     */
    public void setShowTick(boolean isShowTick) {
        this.isShowTick = isShowTick;
        invalidate();
    }

    public int getStartAngle() {
        return mStartAngle;
    }

    public int getSweepAngle() {
        return mSweepAngle;
    }

    public float getCircleCenterX() {
        return mCircleCenterX;
    }

    public float getCircleCenterY() {
        return mCircleCenterY;
    }

    public float getRadius() {
        return mRadius;
    }

    public int getMax() {
        return mMax;
    }

    public int getProgress() {
        return mProgress;
    }

    public String getLabelText() {
        return mLabelText;
    }

    public float getThumbRadius() {
        return mThumbRadius;
    }

    public float getThumbCenterX() {
        return mThumbCenterX;
    }

    public float getThumbCenterY() {
        return mThumbCenterY;
    }

    public float getAllowableOffsets() {
        return mAllowableOffsets;
    }

    public boolean isEnabledDrag() {
        return isEnabledDrag;
    }

    public boolean isEnabledSingle() {
        return isEnabledSingle;
    }

    public boolean isShowPercentText() {
        return isShowPercentText;
    }

    public boolean isShowTick() {
        return isShowTick;
    }

    public boolean isShowThumb() {
        return isShowThumb;
    }


    public float getThumbRadiusEnlarges() {
        return mThumbRadiusEnlarges;
    }

    /**
     * 触摸时按钮半径放大量
     *
     * @param thumbRadiusEnlarges
     */
    public void setThumbRadiusEnlarges(float thumbRadiusEnlarges) {
        this.mThumbRadiusEnlarges = thumbRadiusEnlarges;
    }

    /**
     * 是否默认显示百分比为标签文字
     *
     * @param showPercentText
     */
    public void setShowPercentText(boolean showPercentText) {
        isShowPercentText = showPercentText;
        invalidate();
    }

    /**
     * 是否显示拖动按钮
     *
     * @param showThumb
     */
    public void setShowThumb(boolean showThumb) {
        isShowThumb = showThumb;
        invalidate();
    }

    /**
     * 触摸时可偏移距离：偏移量越大，触摸精度越小
     *
     * @param allowableOffsets
     */
    public void setAllowableOffsets(float allowableOffsets) {
        this.mAllowableOffsets = allowableOffsets;
    }

    /**
     * 是否启用拖拽
     *
     * @param enabledDrag 默认为 true，为 false 时 相当于{@link android.widget.ProgressBar}
     */
    public void setEnabledDrag(boolean enabledDrag) {
        isEnabledDrag = enabledDrag;
    }


    /**
     * 设置是否启用点击改变进度
     *
     * @param enabledSingle
     */
    public void setEnabledSingle(boolean enabledSingle) {
        isEnabledSingle = enabledSingle;
    }

    /**
     * 设置中间文本标签内间距
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setLabelPadding(float left, float top, float right, float bottom) {
        this.mLabelPaddingLeft = left;
        this.mLabelPaddingTop = top;
        this.mLabelPaddingRight = right;
        this.mLabelPaddingBottom = bottom;
        invalidate();
    }

    /**
     * 设置标签文本
     *
     * @param labelText
     */
    public void setLabelText(String labelText) {
        this.mLabelText = labelText;
        this.isShowPercentText = TextUtils.isEmpty(labelText);
        invalidate();
    }

    /**
     * 进度百分比
     *
     * @return
     */
    public int getProgressPercent() {
        return mProgressPercent;
    }

    /**
     * 如果自定义设置过{@link #setLabelText(String)} 或通过xml设置过{@code app:labelText}则
     * 返回{@link #mLabelText}，反之默认返回百分比{@link #mProgressPercent}
     *
     * @return
     */
    public String getText() {
        if (isShowPercentText) {
            return mProgressPercent + "%";
        }

        return mLabelText;
    }

    public int getLabelTextColor() {
        return mLabelTextColor;
    }

    /**
     * 设置文本颜色
     *
     * @param color
     */
    public void setLabelTextColor(int color) {
        this.mLabelTextColor = color;
        invalidate();
    }

    /**
     * 设置文本颜色
     *
     * @param resId 颜色资源id
     */
    public void setLabelTextColorResource(int resId) {
        int color = getResources().getColor(resId);
        setLabelTextColor(color);
    }

    /**
     * 设置文本标签字体大小
     *
     * @param textSize 单位：sp
     */
    public void setLabelTextSize(float textSize) {
        setLabelTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    /**
     * 设置文本标签字体大小
     *
     * @param unit     单位 一般使用{@link TypedValue#COMPLEX_UNIT_SP}
     * @param textSize
     */
    public void setLabelTextSize(int unit, float textSize) {
        float size = TypedValue.applyDimension(unit, textSize, getDisplayMetrics());
        if (mLabelTextSize != size) {
            this.mLabelTextSize = size;
            invalidate();
        }

    }

    /**
     * 设置进度改变监听
     *
     * @param onChangeListener
     */
    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.mOnChangeListener = onChangeListener;
    }


    public interface OnChangeListener {
        /**
         * 跟踪触摸事件开始时回调此方法 {@link MotionEvent#ACTION_DOWN}
         *
         * @param isCanDrag
         */
        void onStartTrackingTouch(boolean isCanDrag);

        /**
         * 进度改变时回调此方法
         *
         * @param progress
         * @param max
         * @param fromUser
         */
        void onProgressChanged(float progress, float max, boolean fromUser);

        /**
         * 跟踪触摸事件停止时回调此方法 {@link MotionEvent#ACTION_UP}
         */
        void onStopTrackingTouch(boolean isCanDrag);

        /**
         * 通过点击事件改变进度后回调此方法 {@link GestureDetector#GestureDetector#onSingleTapUp()}
         */
        void onSingleTapUp();
    }

    public abstract class OnSimpleChangeListener implements OnChangeListener {
        @Override
        public void onStartTrackingTouch(boolean isCanDrag) {

        }

        @Override
        public void onStopTrackingTouch(boolean isCanDrag) {

        }

        @Override
        public void onSingleTapUp() {

        }
    }
}