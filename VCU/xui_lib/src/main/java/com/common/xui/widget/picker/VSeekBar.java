package com.common.xui.widget.picker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;

import com.common.xui.R;
import com.common.xui.utils.DensityUtils;
import com.common.xui.utils.ResUtils;
import com.common.xui.utils.ThemeUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 滑块选择器
 *
 * @since 2020-01-04 18:30
 */
public class VSeekBar extends View {

    public interface OnSeekBarListener {
        /**
         * 数字改变回调
         *
         * @param seekBar  控件
         * @param newValue 新数值
         */
        void onValueChanged(VSeekBar seekBar, int newValue);
    }

    private static int DEFAULT_TOUCH_TARGET_SIZE;
    private static int DEFAULT_TEXT_MIN_SPACE;
    private static final int DEFAULT_MAX = 100;
    /**
     * 刻度的宽度参数
     */
    private static final float DEFAULT_BIG_SCALE_WITH = 1.7f;
    private static final float DEFAULT_MIDDLE_SCALE_WITH = 1.2f;
    private static final float DEFAULT_SMALL_SCALE_WITH = 1.0f;

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paint = new Paint();
    private Paint mShadowPaint;
    private Paint mBorderPaint;
    private int mLineStartX;
    private int mLineEndX;
    private int mLineLength;
    private int mMaxPosition = 0;
    private int mRange;
    private int mMiddleY = 0;
    private final Rect mMaxTextRect = new Rect();
    private final Rect mRulerTextRect = new Rect();
    /**
     * List of event IDs touching targets
     */
    private final Set<Integer> mTouchingMinTarget = new HashSet<>();
    private final Set<Integer> mTouchingMaxTarget = new HashSet<>();
    private boolean mIsTouching = false;
    private boolean mLastTouchedMin;
    public int mSelectedNumber = -1;
    private boolean mIsFirstInit = true;
    private float mConvertFactor;
    private OnSeekBarListener mOnSeekBarListener;

    //========属性==========//
    private int mVerticalPadding;
    private int mInsideRangeColor;
    private int mOutsideRangeColor;
    private float mInsideRangeLineStrokeWidth;
    private float mOutsideRangeLineStrokeWidth;
    private int mMax = DEFAULT_MAX;
    private int mMin = 0;
    private Bitmap mSliderIcon;
    private Bitmap mSliderIconFocus;
    private boolean mIsLineRound;

    private boolean mIsShowBubble;
    private Bitmap mBubbleBitmap;
    private boolean mIsShowNumber;
    private int mNumberTextColor;
    private float mNumberTextSize;
    private float mNumberMarginBottom;

    private boolean mIsShowRuler;
    private int mRulerColor;
    private int mRulerTextColor;
    private float mRulerTextSize;
    private float mRulerMarginTop;
    private float mRulerDividerHeight;
    private float mRulerTextMarginTop;
    private int mRulerInterval;
    private final float mBorderWidth = 2.6f;       // 描边宽度
    private final float mShadowRadius = 13f;
    private LinearGradient linearGradient;
    private final Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);
    private final Path path = new Path();
    private final Path linePath = new Path();

    public VSeekBar(Context context) {
        this(context, null);
    }

    public VSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.XSeekBarStyle);
    }

    public VSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs, defStyleAttr);
    }

    public void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        DEFAULT_TOUCH_TARGET_SIZE = DensityUtils.dp2px(20);
        DEFAULT_TEXT_MIN_SPACE = DensityUtils.dp2px(2);
        int colorAccent = ThemeUtils.resolveColor(context, R.attr.colorAccent);
        int colorControlNormal = ThemeUtils.resolveColor(context, R.attr.colorControlNormal);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.XSeekBar, defStyleAttr, 0);
            mVerticalPadding = array.getDimensionPixelSize(R.styleable.XSeekBar_xsb_verticalPadding, DensityUtils.dp2px(10));
            //滑条
            mInsideRangeColor = array.getColor(R.styleable.XSeekBar_xsb_insideRangeLineColor, colorAccent);
            mOutsideRangeColor = array.getColor(R.styleable.XSeekBar_xsb_outsideRangeLineColor, ResUtils.getColor(R.color.default_xrs_outside_line_color));
            mInsideRangeLineStrokeWidth = array.getDimensionPixelSize(R.styleable.XSeekBar_xsb_insideRangeLineStrokeWidth, DensityUtils.dp2px(5));
            mOutsideRangeLineStrokeWidth = array.getDimensionPixelSize(R.styleable.XSeekBar_xsb_outsideRangeLineStrokeWidth, DensityUtils.dp2px(5));
            mMin = array.getInt(R.styleable.XSeekBar_xsb_min, mMin);
            mMax = array.getInt(R.styleable.XSeekBar_xsb_max, mMax);
            mSliderIcon = BitmapFactory.decodeResource(getResources(), array.getResourceId(R.styleable.XSeekBar_xsb_sliderIcon, R.drawable.xui_ic_slider_icon));
            mSliderIconFocus = BitmapFactory.decodeResource(getResources(), array.getResourceId(R.styleable.XSeekBar_xsb_sliderIconFocus, R.drawable.xui_ic_slider_icon));
            mIsLineRound = array.getBoolean(R.styleable.XSeekBar_xsb_isLineRound, true);

            //气泡
            mIsShowBubble = array.getBoolean(R.styleable.XSeekBar_xsb_isShowBubble, false);
            boolean isFitColor = array.getBoolean(R.styleable.XSeekBar_xsb_isFitColor, true);
            mIsShowNumber = array.getBoolean(R.styleable.XSeekBar_xsb_isShowNumber, false);
            mNumberTextColor = array.getColor(R.styleable.XSeekBar_xsb_numberTextColor, colorAccent);
            mNumberTextSize = array.getDimensionPixelSize(R.styleable.XSeekBar_xsb_numberTextSize, DensityUtils.sp2px(12));
            mNumberMarginBottom = array.getDimensionPixelSize(R.styleable.XSeekBar_xsb_numberMarginBottom, DensityUtils.dp2px(2));
            if (isFitColor) {
                if (mIsShowBubble) {
                    mNumberTextColor = Color.WHITE;
                }
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), array.getResourceId(R.styleable.XSeekBar_xsb_bubbleResource, R.drawable.xui_bg_bubble_blue));
                if (bitmap != null) {
                    mBubbleBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    Canvas canvas = new Canvas(mBubbleBitmap);
                    canvas.drawColor(mInsideRangeColor, PorterDuff.Mode.SRC_IN);
                }
            } else {
                mBubbleBitmap = BitmapFactory.decodeResource(getResources(), array.getResourceId(R.styleable.XSeekBar_xsb_bubbleResource, R.drawable.xui_bg_bubble_blue));
            }

            //刻度尺
            mIsShowRuler = array.getBoolean(R.styleable.XSeekBar_xsb_isShowRuler, false);
            mRulerColor = array.getColor(R.styleable.XSeekBar_xsb_rulerColor, colorControlNormal);
            mRulerTextColor = array.getColor(R.styleable.XSeekBar_xsb_rulerTextColor, colorControlNormal);
            mRulerTextSize = array.getDimensionPixelSize(R.styleable.XSeekBar_xsb_rulerTextSize, DensityUtils.sp2px(12));
            mRulerMarginTop = array.getDimensionPixelSize(R.styleable.XSeekBar_xsb_rulerMarginTop, DensityUtils.dp2px(4));
            mRulerDividerHeight = array.getDimensionPixelSize(R.styleable.XSeekBar_xsb_rulerDividerHeight, DensityUtils.dp2px(4));
            mRulerTextMarginTop = array.getDimensionPixelSize(R.styleable.XSeekBar_xsb_rulerTextMarginTop, DensityUtils.dp2px(4));
            mRulerInterval = array.getInt(R.styleable.XSeekBar_xsb_rulerInterval, 20);

            array.recycle();
        }

        mRange = mMax - mMin;
        initPaint();
    }

    private void initPaint() {
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.WHITE);

        mPaint.setAntiAlias(true);
        // 初始化光晕效果画笔
        mShadowPaint = new Paint();
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowPaint.setColor(getContext().getColor(R.color.v_seek_start_color));
        mShadowPaint.setAlpha(200);
        mShadowPaint.setMaskFilter(new BlurMaskFilter(mShadowRadius, BlurMaskFilter.Blur.OUTER));
        // 抖动处理
        mShadowPaint.setDither(true);
        //描边
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setAlpha(170);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(getContext().getColor(R.color.v_seek_start_color));
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int desiredWidth = widthSize;
        int desiredHeight;

        getTextBounds(String.valueOf(mMax), mMaxTextRect);

        if (mIsShowNumber && mIsShowBubble) {
            desiredHeight = (int) (mSliderIcon.getHeight() + mNumberMarginBottom) + mBubbleBitmap.getHeight();
        } else if (mIsShowNumber) {
            desiredHeight = (int) (mSliderIcon.getHeight() + mNumberMarginBottom);
        } else {
            desiredHeight = mSliderIcon.getHeight();
        }

        int rulerHeight = (int) (mRulerMarginTop + mRulerDividerHeight * 3 + mRulerTextMarginTop + mRulerTextRect.height());
        if (mIsShowRuler) {
            getRulerTextBounds(String.valueOf(mMin), mRulerTextRect);
            desiredHeight += rulerHeight;
        }

        int width;
        int height = desiredHeight;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = desiredHeight;
        }

        height += mVerticalPadding;

        int marginStartEnd = mIsShowBubble ? mBubbleBitmap.getWidth() : Math.max(mSliderIcon.getWidth(), mMaxTextRect.width());

        mLineLength = (width - marginStartEnd);
        mMiddleY = mIsShowRuler ? height - rulerHeight - mSliderIcon.getHeight() / 2 : height - mSliderIcon.getHeight() / 2;
        mLineStartX = marginStartEnd / 2;
        mLineEndX = mLineLength + marginStartEnd / 2;

        calculateConvertFactor();

        if (mIsFirstInit) {
            setSelectedValue(mSelectedNumber != -1 ? mSelectedNumber : mMax, false);
        }

        height += mVerticalPadding;

        setMeasuredDimension(width, height);
    }

    int layerId;
    private final PaintFlagsDrawFilter paintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(paintFlagsDrawFilter);
        layerId = canvas.saveLayer(0, 0, getWidth(), getHeight(), mPaint);
//        drawSelectShadow(canvas);
        drawEntireRangeLine(canvas);
        drawSelectedRangeLine(canvas);
//        drawSelectBorder(canvas);
        if (mIsShowNumber) {
            drawSelectedNumber(canvas);
        }
//        drawRuler(canvas);
//        drawSelectedTargets(canvas);
        drawIcon(canvas);
        canvas.restoreToCount(layerId);
        layerId = canvas.saveLayer(0, 0, getWidth(), getHeight(), mPaint);
        drawSelectBorder2(canvas);
        canvas.restoreToCount(layerId);
    }

    private void drawSelectBorder2(Canvas canvas) {
        RectF rectF = new RectF();
        resetSelectRectF(rectF, selectRectF.left, selectRectF.top, mBorderWidth, selectRectF.right, selectRectF.bottom);
        canvas.clipRect(rectF);
        drawSelectBorder(canvas);
    }

    private void drawIcon(Canvas canvas) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.luminance);
        float xOffset = ((normalRectF.height() - bitmap.getWidth()) / 2);
        float yOffset = ((normalRectF.height() - bitmap.getHeight()) / 2);
        float left = normalRectF.left + xOffset;
        float top = normalRectF.top + yOffset;
        // canvas.drawBitmap(bitmap, left, top, null);
        left = normalRectF.right - xOffset - bitmap.getWidth();
        canvas.drawBitmap(bitmap, left, top, null);


        drawSplitPoint(canvas);
    }

    private void drawSplitPoint2(Canvas canvas) {
        float dis = 80;
        float pointRangeWidth = normalRectF.width() - 2 * normalRectF.height() - dis;
        int count = (int) (pointRangeWidth / dis);
        float spare = pointRangeWidth % dis;
        float append = spare / (float) count;
        float locationX = normalRectF.left + normalRectF.height() + dis / 2;
        float locationY = normalRectF.top + (normalRectF.height() / 2);
        float offset;
        for (int index = 0; index < count + 1; index++) {
            offset = (dis + append) * index - 2;
            canvas.drawCircle(locationX + offset, locationY, 2, paint);
        }
    }

    private void drawSplitPoint(Canvas canvas) {
//        float dis = 80;
        float pointRangeWidth = normalRectF.width();
        int count = mMax - mMin;
        float spare = pointRangeWidth / (float) count;
        float locationX = normalRectF.left;
        float locationY = normalRectF.top + (normalRectF.height() / 2);
        float offset;

        for (int index = 0; index < count - 1; index++) {
            offset = (index + 1) * spare - 2;
            canvas.drawCircle(locationX + offset, locationY, 2, paint);
        }
    }

    RectF normalRectF = new RectF();
    RectF selectRectF = new RectF();
    Rect rect = new Rect();

    private void drawEntireRangeLine(Canvas canvas) {
       /* if (null == normalGradient) {
            normalGradient = new LinearGradient(0, 0, getWidth(), 0, Color.parseColor("#444444"), Color.parseColor("#444444"), Shader.TileMode.CLAMP);
        }
        mPaint.setShader(normalGradient);
        normalRectF.left = mLineStartX;
        normalRectF.top = mMiddleY - mOutsideRangeLineStrokeWidth / 2;
        normalRectF.right = mLineEndX;
        normalRectF.bottom = mMiddleY + mOutsideRangeLineStrokeWidth / 2;
        canvas.drawRoundRect(normalRectF, normalRectF.height() / 2, normalRectF.height() / 2, mPaint);*/

        resetSelectRectF(normalRectF, mLineStartX, mMiddleY, mOutsideRangeLineStrokeWidth / 2, mLineEndX, mMiddleY);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.luminance_bg_blue);
        rect.left = 0;
        rect.top = 0;
        rect.right = bitmap.getWidth();
        rect.bottom = bitmap.getHeight();
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG));
        canvas.drawBitmap(bitmap, rect, normalRectF, mPaint);
        if (mIsLineRound) {
//            mPaint.setColor(mInsideRangeColor);
//            canvas.drawCircle(mLineStartX, mMiddleY, mOutsideRangeLineStrokeWidth / 2, mPaint);
//            mPaint.setColor(mOutsideRangeColor);
//            canvas.drawCircle(mLineEndX, mMiddleY, mOutsideRangeLineStrokeWidth / 2, mPaint);
        }
        mPaint.setShader(null);
    }


    private void drawSelectedRangeLine(Canvas canvas) {
//        mPaint.setStrokeWidth(mInsideRangeLineStrokeWidth);
        mPaint.setXfermode(xfermode);
        if (null == linearGradient) {
            /*int startColor = Color.parseColor("#3300B6FF");
            int endColor = Color.parseColor("#FF299EEE");*/
            int startColor = getContext().getColor(R.color.v_seek_start_color);
            int endColor = getContext().getColor(R.color.v_seek_end_color);
            linearGradient = new LinearGradient(0, 0, getWidth(), 0, startColor, endColor, Shader.TileMode.CLAMP);
        }
        mPaint.setShader(linearGradient);
        resetSelectRectF(selectRectF, mLineStartX, mMiddleY, mOutsideRangeLineStrokeWidth / 2, mMaxPosition, mMiddleY);
        canvas.drawRect(selectRectF, mPaint);
//        canvas.drawRoundRect(selectRectF, selectRectF.height() / 2, selectRectF.height() / 2, mPaint);
        mPaint.setShader(null);
        mPaint.setXfermode(null);
    }

    private void drawSelectShadow(Canvas canvas) {
        if (mIsTouching) {
            resetDrawPath();
            canvas.drawPath(path, mShadowPaint);
        }
    }

    private void resetDrawPath() {
//        resetSelectRectF(selectRectF, mLineStartX, mMiddleY, mOutsideRangeLineStrokeWidth / 2, mMaxPosition, mMiddleY);
//        float closedRadius = 35f;
//        boolean isNearEnd = mMaxPosition >= mLineEndX - 12;
//        float rightRadius = isNearEnd ? closedRadius : 0f;
        path.reset();
        RectF borderRectF = new RectF(normalRectF.left, normalRectF.top, normalRectF.right, normalRectF.bottom);
        float radius = borderRectF.height() / 2;
        path.addRoundRect(borderRectF, radius, radius, Path.Direction.CCW);
        if (selectRectF.right >= radius && selectRectF.right <= normalRectF.right - radius) {
            linePath.reset();
            linePath.moveTo(selectRectF.right - mBorderWidth, selectRectF.top);
            linePath.lineTo(selectRectF.right - mBorderWidth, selectRectF.bottom);
            path.addPath(linePath);
        }
//
//        // moveTo此点为多边形的起点
//        path.moveTo(selectRectF.right - rightRadius, selectRectF.top);
//        //上X边线
//        path.lineTo(selectRectF.right - rightRadius, selectRectF.top);
//        path.lineTo(selectRectF.left + closedRadius, selectRectF.top);
//        //圆角线
//        path.quadTo(
//                selectRectF.left, selectRectF.top,
//                selectRectF.left, selectRectF.top + closedRadius
//        );
//        path.lineTo(selectRectF.left, selectRectF.bottom - closedRadius);
//        //圆角线
//        path.quadTo(selectRectF.left, selectRectF.bottom, selectRectF.left + closedRadius, selectRectF.bottom);
//        //底部X轴边线
//        path.lineTo(selectRectF.right - rightRadius, selectRectF.bottom);
//        //最右边圆角部分
//        if (isNearEnd) {
//            //上圆角边线
//            path.moveTo(selectRectF.right - rightRadius, selectRectF.top);
//            path.lineTo(selectRectF.right - closedRadius, selectRectF.top);
//            path.quadTo(
//                    selectRectF.right, selectRectF.top,
//                    selectRectF.right, selectRectF.top + closedRadius
//            );
//            //下圆角边线
//            path.moveTo(selectRectF.right - rightRadius, selectRectF.bottom);
//            path.lineTo(selectRectF.right - closedRadius, selectRectF.bottom);
//            path.quadTo(
//                    selectRectF.right, selectRectF.bottom,
//                    selectRectF.right, selectRectF.bottom - closedRadius
//            );
//        } else {
//            path.close();
//        }
    }

    private void resetSelectRectF(RectF selectRectF, float mLineStartX, float mMiddleY, float v, float mMaxPosition, float mMiddleY2) {
        selectRectF.left = mLineStartX;
        selectRectF.top = mMiddleY - v;
        selectRectF.right = mMaxPosition;
        selectRectF.bottom = mMiddleY2 + v;
    }

    private void drawSelectBorder(Canvas canvas) {
        if (mIsTouching) {
            resetDrawPath();
            canvas.drawPath(path, mBorderPaint);
        }
    }

    private void drawSelectedNumber(Canvas canvas) {

        String max = String.valueOf(getSelectedNumber());

        getTextBounds(max, mMaxTextRect);


        float yText;
        //bubble
        if (mIsShowBubble) {
            float top = mMiddleY - mSliderIcon.getHeight() / 2F - mBubbleBitmap.getHeight() - mNumberMarginBottom;
            yText = top + mBubbleBitmap.getHeight() / 2F + mMaxTextRect.height() / 2F - 6;
            canvas.drawBitmap(mBubbleBitmap, mMaxPosition - mBubbleBitmap.getWidth() / 2F, top, mPaint);
        } else {
            yText = mMiddleY - mSliderIcon.getHeight() / 2F - mNumberMarginBottom - 36;
        }
        //text
        float maxX = mMaxPosition - mMaxTextRect.width() / 2F - 6;
        mPaint.setTextSize(mNumberTextSize);
        mPaint.setColor(mNumberTextColor);
        canvas.drawText(max, maxX, yText, mPaint);
    }

    private void drawRuler(Canvas canvas) {
        if (mIsShowRuler) {
            float startX = mLineStartX;
            float stopY = 0;
            float startY = 0;
            float divider = (float) mRulerInterval / 10f;
            float scaleLength = (float) mLineLength / ((mMax - mMin) / divider) / divider;

            boolean isMinHasText = false;
            boolean isMaxHasText = false;

            for (int i = mMin; i <= mMax; i++) {
                if (i % mRulerInterval == 0) {
                    //draw big scale
                    startY = mMiddleY + mSliderIcon.getHeight() / 2F + mRulerMarginTop;
                    stopY = startY + mRulerDividerHeight * 3;

                    mPaint.setColor(mRulerTextColor);
                    mPaint.setTextSize(mRulerTextSize);
                    getRulerTextBounds(String.valueOf(i), mRulerTextRect);
                    canvas.drawText(String.valueOf(i), startX - mRulerTextRect.width() / 2F, stopY + mRulerTextRect.height() + mRulerTextMarginTop, mPaint);

                    if (i == mMin) {
                        isMinHasText = true;
                    }
                    if (i == mMax) {
                        isMaxHasText = true;
                    }
                    mPaint.setStrokeWidth(DEFAULT_BIG_SCALE_WITH);


                    mPaint.setColor(mRulerColor);

                    canvas.drawLine(startX, startY, startX, stopY, mPaint);

                } else if (i % (mRulerInterval / 2) == 0 && mRulerInterval % 10 == 0) {
                    //draw middle scale
                    startY = mMiddleY + mSliderIcon.getHeight() / 2F + mRulerMarginTop;
                    stopY = startY + mRulerDividerHeight * 2;
                    mPaint.setStrokeWidth(DEFAULT_MIDDLE_SCALE_WITH);

                    mPaint.setColor(mRulerColor);
                    canvas.drawLine(startX, startY, startX, stopY, mPaint);


                } else {
                    //draw small scale
                    startY = mMiddleY + mSliderIcon.getHeight() / 2F + mRulerMarginTop;
                    stopY = startY + mRulerDividerHeight;
                    mPaint.setStrokeWidth(DEFAULT_SMALL_SCALE_WITH);

                    if (i % (mRulerInterval / 10) == 0) {
                        mPaint.setColor(mRulerColor);
                        canvas.drawLine(startX, startY, startX, stopY, mPaint);
                    }

                }

                if ((i == mMax && !isMaxHasText) || (i == mMin && !isMinHasText)) {

                    mPaint.setColor(mRulerTextColor);
                    mPaint.setTextSize(mRulerTextSize);
                    getRulerTextBounds(String.valueOf(i), mRulerTextRect);

                    float x = startX - mRulerTextRect.width() / 2F;
                    //修正最大值与最小值文本与满刻度文本太靠近时显示重叠问题
                    if (i == mMax && i % mRulerInterval == 1) {
                        x = startX + DEFAULT_TEXT_MIN_SPACE;
                    }

                    if (i == mMin && i % mRulerInterval == mRulerInterval - 1) {
                        x = startX - mRulerTextRect.width() / 2F - DEFAULT_TEXT_MIN_SPACE;
                    }

                    canvas.drawText(String.valueOf(i), x, startY + mRulerDividerHeight * 3 + mRulerTextRect.height() + mRulerTextMarginTop, mPaint);

                }
                startX += scaleLength;
            }
        }
    }

    private void drawSelectedTargets(Canvas canvas) {
        mPaint.setColor(mInsideRangeColor);
//        canvas.drawCircle(mMaxPosition, mMiddleY, DensityUtils.dp2px(3), mPaint);
//        if (!mIsTouching) {
//            canvas.drawBitmap(mSliderIcon, mMaxPosition - mSliderIcon.getWidth() / 2F, mMiddleY - mSliderIcon.getWidth() / 2F, mPaint);
//        } else {
//            canvas.drawBitmap(mSliderIconFocus, mMaxPosition - mSliderIcon.getWidth() / 2F, mMiddleY - mSliderIcon.getWidth() / 2F, mPaint);
//        }
    }

    private void getTextBounds(String text, Rect rect) {
        mPaint.setTextSize(mNumberTextSize);
        mPaint.getTextBounds(text, 0, text.length(), rect);
    }

    private void getRulerTextBounds(String text, Rect rect) {
        mPaint.setTextSize(mRulerTextSize);
        mPaint.getTextBounds(text, 0, text.length(), rect);
    }

    private void jumpToPosition(int index, MotionEvent event) {
        //user has touched outside the target, lets jump to that position
        if (event.getX(index) > mMaxPosition && event.getX(index) <= mLineEndX) {
            mMaxPosition = (int) event.getX(index);
//            invalidate();
            tryInvalidate();
            callMaxChangedCallbacks();
        } else if (event.getX(index) < mMaxPosition && event.getX(index) >= mLineStartX) {
            mMaxPosition = (int) event.getX(index);
//            invalidate();
            tryInvalidate();
            callMaxChangedCallbacks();
        }
    }

    boolean isChanged = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        mIsFirstInit = false;

        final int actionIndex = event.getActionIndex();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                updateTouchStatus(true);

//                if (mLastTouchedMin) {
//                    if (!checkTouchingMinTarget(actionIndex, event)
//                            && !checkTouchingMaxTarget(actionIndex, event)) {
//                        jumpToPosition(actionIndex, event);
//                    }
//                } else if (!checkTouchingMaxTarget(actionIndex, event)
//                        && !checkTouchingMinTarget(actionIndex, event)) {
//                    jumpToPosition(actionIndex, event);
//                }
//                Log.d("VSeekBar", "MotionEvent.ACTION_DOWN");
//                invalidate();
                jumpToPosition(actionIndex, event);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                updateTouchStatus(false);

                mTouchingMinTarget.remove(event.getPointerId(actionIndex));
                mTouchingMaxTarget.remove(event.getPointerId(actionIndex));
                Log.d("VSeekBar", "MotionEvent.ACTION_POINTER_UP");
//            invalidate();
                tryInvalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                updateTouchStatus(true);

//                for (int i = 0; i < event.getPointerCount(); i++) {
//                    if (mTouchingMinTarget.contains(event.getPointerId(i))) {
//                        int touchX = (int) event.getX(i);
//                        touchX = clamp(touchX, mLineStartX, mLineEndX);
//                        if (touchX >= mMaxPosition) {
//                            mMaxPosition = touchX;
//                            callMaxChangedCallbacks();
//                        }
//                    }
//                    if (mTouchingMaxTarget.contains(event.getPointerId(i))) {
//                        int touchX = (int) event.getX(i);
//                        touchX = clamp(touchX, mLineStartX, mLineEndX);
//                        mMaxPosition = touchX;
//                        callMaxChangedCallbacks();
//                    }
                int touchX = (int) event.getX();
                touchX = clamp(touchX, mLineStartX, mLineEndX);
                mMaxPosition = touchX;
                isChanged = isChanged();
                callMaxChangedCallbacks();
                if (lastValue == mMin) mMaxPosition = mLineStartX;
                if (lastValue == mMax) mMaxPosition = mLineEndX;
                if (isChanged || lastValue == mMin || lastValue == mMax) //            invalidate();
                    tryInvalidate();
//                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                updateTouchStatus(true);

//                for (int i = 0; i < event.getPointerCount(); i++) {
//                    if (mLastTouchedMin) {
//                        if (!checkTouchingMinTarget(i, event)
//                                && !checkTouchingMaxTarget(i, event)) {
//                            jumpToPosition(i, event);
//                        }
//                    } else if (!checkTouchingMaxTarget(i, event)
//                            && !checkTouchingMinTarget(i, event)) {
//                        jumpToPosition(i, event);
//                    }
//                }
                jumpToPosition(actionIndex, event);
                Log.d("VSeekBar", "MotionEvent.ACTION_POINTER_DOWN");
                break;

            case MotionEvent.ACTION_CANCEL:
                updateTouchStatus(false);

//                mTouchingMinTarget.clear();
//                mTouchingMaxTarget.clear();
                Log.d("VSeekBar", "MotionEvent.ACTION_CANCEL");

//                invalidate();
                jumpToPosition(actionIndex, event);

                break;

            default:
                break;
        }

        return true;
    }

    /**
     * 更新触摸状态
     *
     * @param isTouching 是否触摸
     */
    private void updateTouchStatus(boolean isTouching) {
        mIsTouching = isTouching;
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(isTouching);
        }
    }

    /**
     * Checks if given index is touching the min target.  If touching start animation.
     */
    private boolean checkTouchingMinTarget(int index, MotionEvent event) {
        if (isTouchingMinTarget(index, event)) {
            mLastTouchedMin = true;
            mTouchingMinTarget.add(event.getPointerId(index));
            return true;
        }
        return false;
    }

    /**
     * Checks if given index is touching the max target.  If touching starts animation.
     */
    private boolean checkTouchingMaxTarget(int index, MotionEvent event) {
        if (isTouchingMaxTarget(index, event)) {
            mLastTouchedMin = false;
            mTouchingMaxTarget.add(event.getPointerId(index));
            return true;
        }
        return false;
    }


    private int lastValue = -1;

    private int displayValue = -1;

    private void callMaxChangedCallbacks() {
        if (mOnSeekBarListener != null) {
            int number = getSelectedNumber();
            if (number != lastValue) {
                lastValue = number;
                mOnSeekBarListener.onValueChanged(this, number);
            }
        }
    }

    private void tryInvalidate() {
        int number = getSelectedNumber();
        if (displayValue != number) {
            mMaxPosition = Math.round(((number - mMin) / mConvertFactor) + mLineStartX);
            invalidate();
            displayValue = number;
        }
    }

    private boolean isChanged() {
        int number = getSelectedNumber();
        return number != lastValue;
    }

    private boolean isTouchingMinTarget(int pointerIndex, MotionEvent event) {
        return false;
    }

    private boolean isTouchingMaxTarget(int pointerIndex, MotionEvent event) {
        return event.getX(pointerIndex) > mMaxPosition - DEFAULT_TOUCH_TARGET_SIZE
                && event.getX(pointerIndex) < mMaxPosition + DEFAULT_TOUCH_TARGET_SIZE
                && event.getY(pointerIndex) > mMiddleY - DEFAULT_TOUCH_TARGET_SIZE
                && event.getY(pointerIndex) < mMiddleY + DEFAULT_TOUCH_TARGET_SIZE;
    }

    private void calculateConvertFactor() {
        mConvertFactor = ((float) mRange) / mLineLength;
    }

    public int getSelectedNumber() {
        return Math.round((mMaxPosition - mLineStartX) * mConvertFactor + mMin);
    }

    public void setDefaultValue(int value) {
        mSelectedNumber = value;
        setSelectedValue(value, true);
        invalidate();
    }

    public void setValueNoEvent(int value) {
        mSelectedNumber = value;
        setSelectedValue(value, false);
//        invalidate();
        tryInvalidate();
    }

    private void setSelectedValue(int selectedMax, boolean isCallback) {
        mMaxPosition = Math.round(((selectedMax - mMin) / mConvertFactor) + mLineStartX);
        if (isCallback) {
            callMaxChangedCallbacks();
        }
    }

    public void setOnSeekBarListener(OnSeekBarListener listener) {
        mOnSeekBarListener = listener;
    }

    public int getMin() {
        return mMin;
    }

    public void setMin(int min) {
        mMin = min;
        mRange = mMax - min;
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        mMax = max;
        mRange = max - mMin;
    }

    public void setInterval(int rulerInterval) {
        mRulerInterval = rulerInterval;
        invalidate();
    }

    /**
     * Resets selected values to MIN and MAX.
     */
    public void reset() {
        mMaxPosition = mLineEndX;
        callMaxChangedCallbacks();
        invalidate();
    }


    /**
     * Keeps Number value inside min/max bounds by returning min or max if outside of
     * bounds.  Otherwise will return the value without altering.
     */
    private <T extends Number> T clamp(@NonNull T value, @NonNull T min, @NonNull T max) {
        if (value.doubleValue() > max.doubleValue()) {
            return max;
        } else if (value.doubleValue() < min.doubleValue()) {
            return min;
        }
        return value;
    }

    public boolean isTouching() {
        return mIsTouching;
    }

}