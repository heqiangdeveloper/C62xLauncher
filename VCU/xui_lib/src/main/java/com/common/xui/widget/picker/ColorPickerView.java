package com.common.xui.widget.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.common.xui.R;

import java.util.List;

public class ColorPickerView extends View {
    /**
     * 指示点颜色
     */
    private int mIndicatorColor;
    /**
     * 是否启用指示点
     */
    private boolean mIndicatorEnable;

    /**
     * View 和 bitmapForColor 的画笔
     */
    private final Paint paint;

    /**
     * 指示点专用画笔，这样可以避免 mIndicatorColor 有 alpha 时，alpha 作用于 View
     */
    private final Paint paintForIndicator;

    private LinearGradient linearGradient;

    /**
     * 除去上下 padding 的端点坐标
     */
    private int mTop, mLeft, mRight, mBottom;

    /**
     * 颜色条圆角矩形边界
     */
    private final Rect rect = new Rect();

    /**
     * bitmapForIndicator 在 View 上的绘制位置
     */
    private final Rect rectForIndicator = new Rect();

    /**
     * 指示点半径
     */
    private int mRadius = 59;

    /**
     * 控件方向
     */
    private Orientation orientation;

    // 默认状态下长边与短边的比例为 6 ：1
    private static final int defaultSizeShort = 160; // * 6
    private static final int defaultSizeLong = 420;

    /**
     * 颜色分区
     */
    private int colorIndex = 64;
    public int pickerIndex = 0;

    // 不直接绘制在 View 提供的画布上的原因是：选取颜色时需要提取 Bitmap 上的颜色，View 的 Bitmap 无法获取，
    // 而且有指示点时指示点会覆盖主颜色条(重绘颜色条的颜色)
    private Bitmap bitmapForColor;
    private Bitmap bitmapForIndicator;

    /**
     * 是否需要绘制颜色条(指示点)，颜色条在选取颜色时不需要再次生成(bitmapForColor)，直接绘制就行
     */
    private boolean needReDrawColorTable = true;
    private boolean needReDrawIndicator = true;

    /**
     * 手指在颜色条上的坐标
     */
    private int curX, curY;

    private int[] colors = null;

    private int currentColor;
    private int topIndex = 0;//记录上一次颜色段
    private List<Color> colorInfoList;
    private boolean isTouchEvent = false;//是否是按下还是抬起

    private long touchTime = 0;
    public boolean isSlide = true;//是否可以滑动

    /**
     * 控件方向
     */
    public enum Orientation {
        /**
         * 水平
         */
        HORIZONTAL, // 0
        /**
         * 竖直
         */
        VERTICAL // 1
    }

    {
        bitmapForColor = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        //bitmapForIndicator = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        //bitmapForIndicator = BitmapFactory.decodeResource(getResources(), R.drawable.img_yanse_blue);
        bitmapForIndicator = BitmapFactory.decodeResource(getResources(), R.drawable.img_yanse_blue).copy(Bitmap.Config.ARGB_8888, true);
        //Android4.0（API14）之后硬件加速功能就被默认开启了,setShadowLayer 在开启硬件加速的情况下无效，需要关闭硬件加速
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        paint = new Paint();
        paint.setAntiAlias(true);

        paintForIndicator = new Paint();
        paintForIndicator.setAntiAlias(true);

        curX = curY = Integer.MAX_VALUE;
    }

    public ColorPickerView(Context context) {
        super(context);
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ColorPickerView, defStyleAttr, 0);
        mIndicatorColor = array.getColor(R.styleable.ColorPickerView_indicatorColor, Color.WHITE);
        int or = array.getInteger(R.styleable.ColorPickerView_orientation, 0);
        orientation = or == 0 ? Orientation.HORIZONTAL : Orientation.VERTICAL;
        mIndicatorEnable = array.getBoolean(R.styleable.ColorPickerView_indicatorEnable, true);
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {//xml中宽度设为warp_content
            width = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom();
        }

        width = Math.max(width, orientation == Orientation.HORIZONTAL ? defaultSizeLong : defaultSizeShort);
        height = Math.max(height, orientation == Orientation.HORIZONTAL ? defaultSizeShort : defaultSizeLong);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mTop = getPaddingTop();
        mLeft = getPaddingLeft();
        mBottom = getMeasuredHeight() - getPaddingBottom();
        mRight = getMeasuredWidth() - getPaddingRight();

        if (curX == curY || curY == Integer.MAX_VALUE) {
            //curX = getWidth() / 2;
            curY = getHeight() / 2;
        }

        calculBounds();
        if (colors == null) {
            setColors(createDefaultColorTable());
        } else {
            setColors(colors);
        }
        createBitmap();

        if (mIndicatorEnable) {
            needReDrawIndicator = true;
        }

    }

    private void createBitmap() {

        int hc = rect.height();
        int wc = rect.width();
        int hi = mRadius * 2;
        int wi = hi;


        if (bitmapForColor != null) {
            if (!bitmapForColor.isRecycled()) {
                bitmapForColor.recycle();
                bitmapForColor = null;
            }
        }

       /* if (bitmapForIndicator != null) {
            if (!bitmapForIndicator.isRecycled()) {
                bitmapForIndicator.recycle();
                bitmapForIndicator = null;
            }
        }*/

        bitmapForColor = Bitmap.createBitmap(wc, hc, Bitmap.Config.ARGB_8888);
        //bitmapForIndicator = Bitmap.createBitmap(wi, hi, Bitmap.Config.ARGB_8888);
        // bitmapForIndicator = BitmapFactory.decodeResource(getResources(), R.drawable.img_yanse_blue);
    }

    /**
     * 计算颜色条边界
     */
    private void calculBounds() {

        /*
         * 将控件可用高度(除去上下 padding )均分为 6 份，以此计算指示点半径，颜色条宽高
         * 控件方向为 HORIZONTAL 时，从上往下依次占的份额为：
         * 1/9 留白
         * 2/9 颜色条上面部分圆
         * 3/9 颜色条宽
         * 2/9 颜色条上面部分圆
         * 1/9 留白
         */
        final int average = 9;

        /*
         * 每一份的高度
         */
        int each;

        int h = mBottom - mTop;
        int w = mRight - mLeft;
        int size = Math.min(w, h);

        if (orientation == Orientation.HORIZONTAL) {
            if (w <= h) { // HORIZONTAL 模式，然而宽却小于高，以 6 ：1 的方式重新计算高
                size = w / 6;
            }
        } else {
            if (w >= h) {
                size = h / 6;
            }
        }

        each = size / average;
        mRadius = each * 7 / 2;

        int t, l, b, r;
        final int s = each * 3 / 2;

        if (orientation == Orientation.HORIZONTAL) {
            l = mLeft + mRadius;
            r = mRight - mRadius;

            t = (getHeight() / 2) - s;
            b = (getHeight() / 2) + s;
        } else {
            t = mTop + mRadius;
            b = mBottom - mRadius;

            l = getWidth() / 2 - s;
            r = getWidth() / 2 + s;
        }

        rect.set(l, t, r, b);
    }

    /**
     * 设置颜色条的渐变颜色，不支持具有 alpha 的颜色，{@link Color#TRANSPARENT}会被当成 {@link Color#BLACK}处理
     * 如果想设置 alpha ，可以在{@link OnColorPickerChangeListener#(ColorPickerView, int)} 回调
     * 中调用{@linkolorUtils#setAlphaComponent(int, int)}方法添加 alpha 值。
     *
     * @param colors 颜色值
     */
    public void setColors(int... colors) {
        linearGradient = null;
        this.colors = colors;

        if (orientation == Orientation.HORIZONTAL) {
            linearGradient = new LinearGradient(
                    rect.left, rect.top,
                    rect.right, rect.top,
                    colors,
                    null,
                    Shader.TileMode.CLAMP
            );
        } else {
            linearGradient = new LinearGradient(
                    rect.left, rect.top,
                    rect.left, rect.bottom,
                    colors,
                    null,
                    Shader.TileMode.CLAMP
            );
        }

        needReDrawColorTable = true;
        invalidate();
    }

    public int[] createDefaultColorTable() {
        //return colorInfoList.stream().mapToInt(Color::toArgb).toArray();
        int[] cs = new int[colorInfoList.size()];
        for (int i = 0; i < colorInfoList.size(); i++) {
            cs[i] = Color.rgb((int) colorInfoList.get(i).red(), (int) colorInfoList.get(i).green(), (int) colorInfoList.get(i).blue());
        }
       /* int[] cs = {
                Color.rgb(255, 0, 0),
                Color.rgb(255, 255, 0),
                Color.rgb(0, 255, 0),
                Color.rgb(0, 255, 255),
                Color.rgb(0, 0, 255),
                Color.rgb(255, 0, 255),
                Color.rgb(255, 0, 0)
        };*/
        return cs;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        if (needReDrawColorTable) {
            createColorTableBitmap();
        }
        // 绘制颜色条
        canvas.drawBitmap(bitmapForColor, null, rect, paint);
        paint.setAntiAlias(true);
        if (mIndicatorEnable) {
            //if (needReDrawIndicator) {
            //createIndicatorBitmap(canvas);
            // }
            // 绘制指示点
            rectForIndicator.set(curX - mRadius, 105 - mRadius, curX + mRadius - 15, 52 + mRadius);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                    | Paint.FILTER_BITMAP_FLAG));
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            canvas.drawBitmap(bitmapForIndicator, null, rectForIndicator, paint);

            createIndicatorBitmap(canvas);
        }
    }

    private void createIndicatorBitmap(Canvas c) {

        //Canvas c = new Canvas();
        int radius = 55;
        //paintForIndicator.setColor(Color.WHITE);
        //c.drawRoundRect(new RectF(0, 0, bitmapForIndicator.getWidth(), bitmapForIndicator.getHeight()), 35, 66, paintForIndicator);

        paintForIndicator.setColor(mIndicatorColor);
        //paintForIndicator.setShadowLayer(20, 20, 20, Color.WHITE);
        c.drawRoundRect(new RectF(curX - mRadius + 5, 105 - mRadius + 5, curX + mRadius - 15 - 5, 52 + mRadius - 5), 35, (bitmapForIndicator.getHeight() / 2) + 10, paintForIndicator);
        c.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG));
        needReDrawIndicator = false;
    }

    Rect rectBg = new Rect();

    private void createColorTableBitmap() {

        Canvas c = new Canvas(bitmapForColor);
        c.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG));
        RectF rf = new RectF(0, 0, bitmapForColor.getWidth(), bitmapForColor.getHeight());

        // 圆角大小
        int r;
        if (orientation == Orientation.HORIZONTAL) {
            r = bitmapForColor.getHeight() / 2;
        } else {
            r = bitmapForColor.getWidth() / 2;
        }
        // 先绘制黑色背景，否则有 alpha 时绘制不正常
        /*paint.setColor(Color.BLACK);
        c.drawRoundRect(rf, r, r, paint);*/

        //paint.setShader(linearGradient);
        //paint.setAlpha(110);
        //c.drawRoundRect(rf, r, r, paint);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_yanse_bg);
        rectBg.left = 0;
        rectBg.top = 0;
        rectBg.right = bitmap.getWidth();
        rectBg.bottom = bitmap.getHeight();
        c.drawBitmap(bitmap, rectBg, rf, paint);
        paint.setAntiAlias(true);
        paint.setShader(null);
        needReDrawColorTable = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int ex = (int) event.getX();
        int ey = (int) event.getY();
        if(!isSlide){
            return false;
        }
        if (!inBoundOfColorTable(ex, ey)) {
            return true;
        }
        if (orientation == Orientation.HORIZONTAL) {
            curX = ex;
            curY = getHeight() / 2;
        } else {
            curX = getWidth() / 2;
            curY = ey;
        }
        int index = (curX - (mLeft + mRadius + 30)) / (875 / colorIndex);
        if (index <= 0) {
            index = 1;
        } else if (index > colorIndex) {
            index = colorIndex;
        }
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {//按下
            if (colorPickerChangeListener != null) {
                colorPickerChangeListener.onStartTrackingTouch(this);
                calcuColor();
                if (topIndex != index) {
                    isTouchEvent = true;
                    colorPickerChangeListener.onColorChanged(this, currentColor, index);
                }
            }

        } else if (event.getActionMasked() == MotionEvent.ACTION_UP) { //手抬起
            touchTime = SystemClock.elapsedRealtime();
            if (colorPickerChangeListener != null) {
                colorPickerChangeListener.onStopTrackingTouch(this);
                calcuColor();
                isTouchEvent = false;
                if (topIndex != index) {
                    colorPickerChangeListener.onColorChanged(this, currentColor, index);
                }
            }

        } else { //按着+拖拽
            if (colorPickerChangeListener != null) {
                calcuColor();
                if (topIndex != index) {
                    isTouchEvent = true;
                    colorPickerChangeListener.onColorChanged(this, currentColor, index);
                }
            }
        }

        invalidate();
        return true;
    }

    /**
     * 获得当前指示点所指颜色
     *
     * @return 颜色值
     */
    public int getColor() {
        return calcuColor();
    }

    private boolean inBoundOfColorTable(int ex, int ey) {
        if (orientation == Orientation.HORIZONTAL) {
            if (ex <= mLeft + mRadius + 50 || ex >= (mRight - mRadius) - 40) {
                return false;
            }
        } else {
            if (ey <= mTop + mRadius || ey >= mBottom - mRadius) {
                return false;
            }
        }
        return true;
    }

    private int calcuColor() {
        int x, y;
        if (orientation == Orientation.HORIZONTAL) { // 水平
            y = (rect.bottom - rect.top) / 2;
            if (curX < rect.left) {
                x = 1;
            } else if (curX > rect.right) {
                x = bitmapForColor.getWidth() - 1;
            } else {
                x = curX - rect.left;
            }
        } else { // 竖直
            x = (rect.right - rect.left) / 2;
            if (curY < rect.top) {
                y = 1;
            } else if (curY > rect.bottom) {
                y = bitmapForColor.getHeight() - 1;
            } else {
                y = curY - rect.top;
            }
        }
        int pixel = bitmapForColor.getPixel(x, y);
        currentColor = pixelToColor(pixel);
        return currentColor;
    }

    private int pixelToColor(int pixel) {
        int alpha = Color.alpha(pixel);
        int red = Color.red(pixel);
        int green = Color.green(pixel);
        int blue = Color.blue(pixel);
        //return Color.argb(alpha, red, green, blue);
        return Color.rgb(red, green, blue);
    }

    private OnColorPickerChangeListener colorPickerChangeListener;

    public void setOnColorPickerChangeListener(OnColorPickerChangeListener l) {
        this.colorPickerChangeListener = l;
    }

    public interface OnColorPickerChangeListener {

        /**
         * 选取的颜色值改变时回调
         *
         * @param picker ColorPickerView
         * @param color  颜色
         */
        void onColorChanged(ColorPickerView picker, int color, int index);

        /**
         * 开始颜色选取
         *
         * @param picker ColorPickerView
         */
        void onStartTrackingTouch(ColorPickerView picker);

        /**
         * 停止颜色选取
         *
         * @param picker ColorPickerView
         */
        void onStopTrackingTouch(ColorPickerView picker);
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        SavedState ss = new SavedState(parcelable);
        ss.selX = curX;
        ss.selY = curY;
        ss.color = bitmapForColor;
        if (mIndicatorEnable) {
            ss.indicator = bitmapForIndicator;
        }
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        curX = ss.selX;
        curY = ss.selY;
        colors = ss.colors;

        bitmapForColor = ss.color;
        if (mIndicatorEnable) {
            bitmapForIndicator = ss.indicator;
            needReDrawIndicator = true;
        }
        needReDrawColorTable = true;

    }

    private class SavedState extends BaseSavedState {
        int selX, selY;
        int[] colors;
        Bitmap color;
        Bitmap indicator = null;

        SavedState(Parcelable source) {
            super(source);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(selX);
            out.writeInt(selY);
            out.writeParcelable(color, flags);
            out.writeIntArray(colors);
            if (indicator != null) {
                out.writeParcelable(indicator, flags);
            }
        }
    }

    public void setPosition(int x, int y) {
        if (inBoundOfColorTable(x, y)) {
            curX = x;
            curY = y;
            if (mIndicatorEnable) {
                needReDrawIndicator = true;
            }
            invalidate();
        }
    }


    /**
     * 显示默认的颜色选择器
     */
    public void showDefaultColorTable() {
        setColors(createDefaultColorTable());
    }

    public int getIndicatorColor() {
        return mIndicatorColor;
    }

    public void setIndicatorColor(int r, int g, int b) {
        this.mIndicatorColor = Color.rgb(r, g, b);
        needReDrawIndicator = true;
        invalidate();
    }

    public void setIndicatorIndex(int index) {
        if (isTouchEvent || isInDelayTime()) {
            return;
        }
        if (index <= 0) {
            index = 1;
        } else if (index > colorIndex) {
            index = colorIndex;
        }
        int x = (index * (959 / colorIndex)) + (getPaddingLeft() + mRadius + 70);
        if (x > 900) {
            curX = x - 60;
        } else {
            curX = x - 30;
        }
        boolean isSameIndex = pickerIndex == index;
        this.pickerIndex = index;
        Color color = colorInfoList.get(index - 1);
        this.mIndicatorColor = Color.rgb((int) color.red(), (int) color.green(), (int) color.blue());
        needReDrawIndicator = true;
        if (isSameIndex) {
            return;
        }
        invalidate();
    }

    public void setIndicatorColorIndex(int index) {
        Color color = colorInfoList.get(index - 1);
        this.mIndicatorColor = Color.rgb((int) color.red(), (int) color.green(), (int) color.blue());
        needReDrawIndicator = true;
        boolean isSameIndex = pickerIndex == index;
        this.pickerIndex = index;
        this.topIndex = index;
        if (isSameIndex || isInDelayTime()) {
            return;
        }
        invalidate();
    }

    private boolean isInDelayTime() {
        long current = SystemClock.elapsedRealtime();
        return Math.abs(current - touchTime) < 300;
    }

    public void setIndicatorColor(int color) {
        this.mIndicatorColor = color;
        needReDrawIndicator = true;
        invalidate();
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        needReDrawIndicator = true;
        needReDrawColorTable = true;
        requestLayout();
    }

    public void setSupportColors(List<Color> colors) {
        this.colorInfoList = colors;
    }

}
