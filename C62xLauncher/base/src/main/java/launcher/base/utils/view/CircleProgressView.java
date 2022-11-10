package launcher.base.utils.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import launcher.base.R;

public class CircleProgressView extends View {
    private static final String TAG = "MyCircleProgress";
    private final int DEFAULT_PROGRESS_COLOR = Color.parseColor("#26C3FE");
    private final int DEFAULT_PROGRESS_HEIGHT = 5;
    private final int DEFAULT_BG_HEIGHT = 5;
    private final int DEFAULT_BG_COLOR = Color.parseColor("#cccccc");
    private float mProgressHeight = DEFAULT_PROGRESS_HEIGHT;
    private float mBackgroundHeight = DEFAULT_PROGRESS_HEIGHT;
    private int mColorBackground = DEFAULT_BG_COLOR;
    private int mColorProgress = DEFAULT_PROGRESS_COLOR;
    private Paint _paint;
    private RectF _rectF;
    private Rect _rect;
    private long _current = 0, _max = 100;
    private boolean isVisible;
    private String text = "";
    //圆弧（也可以说是圆环）的宽度
    private float _arcWidth = 5;
    //控件的宽度
    private float _width;
    public CircleProgressView(Context context) {
        super(context);
        init();
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        readAttrs(context, attrs);
        init();
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readAttrs(context, attrs);
        init();
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        readAttrs(context, attrs);
        init();
    }

    private void readAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView);
        mProgressHeight = typedArray.getDimension(R.styleable.CircleProgressView_circleProgressHeight, DEFAULT_PROGRESS_HEIGHT);
        mBackgroundHeight = typedArray.getDimension(R.styleable.CircleProgressView_circleProgressBgHeight, DEFAULT_BG_HEIGHT);
        mColorBackground = typedArray.getColor(R.styleable.CircleProgressView_circleProgressBgColor, DEFAULT_BG_COLOR);
        mColorProgress = typedArray.getColor(R.styleable.CircleProgressView_circleProgressColor, DEFAULT_PROGRESS_COLOR);
        typedArray.recycle();
    }

    private void init() {
        _paint = new Paint();
        _paint.setAntiAlias(true);
        _rectF = new RectF();
        _rect = new Rect();
    }

    public void setCurrent(long _current) {
        Log.i(TAG, "当前值：" + _current + "，最大值：" + _max);
        this._current = _current;
        invalidate();
    }

    public void setMax(long _max) {
        if(_max == 0){
            _max = 1L;
        }
        this._max = _max;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //getMeasuredWidth获取的是view的原始大小，也就是xml中配置或者代码中设置的大小
        //getWidth获取的是view最终显示的大小，这个大小不一定等于原始大小
        _width = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制圆形
        //设置为空心圆，如果不理解绘制弧线是什么意思就把这里的属性改为“填充”，跑一下瞬间就明白了
        _paint.setStyle(Paint.Style.STROKE);
        //设置圆弧的宽度（圆环的宽度）
        _paint.setStrokeWidth(_arcWidth);
        _paint.setColor(Color.TRANSPARENT);
        //大圆的半径
        float bigCircleRadius = _width / 2;
        //小圆的半径
        float smallCircleRadius = bigCircleRadius - _arcWidth;
        //绘制小圆
        canvas.drawCircle(bigCircleRadius, bigCircleRadius, smallCircleRadius, _paint);
        _paint.setColor(mColorProgress);
        _rectF.set(_arcWidth, _arcWidth, _width - _arcWidth, _width - _arcWidth);
        //绘制圆弧
        canvas.drawArc(_rectF, 270, _current * 360 / _max, false, _paint);
        //计算百分比
        String txt = text;
        _paint.setTextSize(30);
        _paint.setTextAlign(Paint.Align.CENTER);
        _paint.getTextBounds(txt, 0, txt.length(), _rect);
        if(isVisible){
            _paint.setColor(mColorProgress);
        }else {
            _paint.setColor(Color.TRANSPARENT);
        }
        //绘制百分比
        canvas.drawText(txt, bigCircleRadius - _rect.width() / 8, bigCircleRadius + _rect.height() / 4, _paint);
    }

    public void setTextVisible(String text,boolean isVisible){
        this.text = text;
        this.isVisible = isVisible;
        invalidate();
    }
}
