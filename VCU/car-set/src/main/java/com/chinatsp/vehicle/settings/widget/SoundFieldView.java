/*
 * 版权：2012-2013 ChinaTsp Co.,Ltd
 × 描述：
 * 创建人：liuderu
 * 创建时间：2018-1-18
 */
package com.chinatsp.vehicle.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.chinatsp.settinglib.VcuUtils;
import com.chinatsp.vehicle.controller.annotation.Level;
import com.chinatsp.vehicle.settings.R;


/**
 * @author liuderu
 * @version [版本号， 2018-1-18]
 * @since [产品/模块版本]
 */
public class SoundFieldView extends LinearLayout {
    protected static final String TAG = "SoundFieldView";

    public static double BALANCE_MAX = 10.0;
    public static double FADE_MAX = 10.0;
    public static  int H_PADDING = 160; //160
    public static  int V_PADDING = 80; //130

    long mLastTime;
    long mCurTime;

    private View mSFView = null;
    private RelativeLayout rlRoot = null;
    private ImageView mImgPoint = null;
    private ImageView imgSouncPointBg = null;
    private boolean mIsMouseDown = false;
    private boolean mDoubleClick = false;

    private ImageView mImgSoundLineH = null;
    private ImageView mImgSoundLineV = null;

    //初始化的x, y的坐标值
    private final float INVALID_XY_VALUE = -100;
    private float mPosX = INVALID_XY_VALUE;
    private float mPosY = INVALID_XY_VALUE;

    public static int mBalanceValue = (int) (BALANCE_MAX / 2);
    public static int mFadeValue = (int) (FADE_MAX / 2);

    private OnValueChangedListener mOnValueChangedListener;

    public SoundFieldView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSFView = inflate(context, R.layout.layout_sound_field, this);

        initViews();
       if(VcuUtils.INSTANCE.isCareLevel(new int[]{Level.LEVEL3,Level.LEVEL4},true)){
            H_PADDING = 120;
            if(VcuUtils.INSTANCE.isCareLevel(new int[]{Level.LEVEL3},true)){
                rlRoot.setBackgroundResource(R.drawable.right_car_vioce_3);
            }else {
                rlRoot.setBackgroundResource(R.drawable.right_car_vioce_4);
            }
            mImgPoint.setImageResource(R.drawable.audio_point_high);
            imgSouncPointBg.setBackgroundResource(R.drawable.yinlinag_dian_mix_high);
            mImgSoundLineH.setImageResource(R.drawable.line_heng_high);
            mImgSoundLineV.setImageResource(R.drawable.line_shu_high);
        }else {
            H_PADDING = 160;
            rlRoot.setBackgroundResource(R.drawable.right_car_vioce);
            mImgPoint.setImageResource(R.drawable.audio_point);
            imgSouncPointBg.setBackgroundResource(R.drawable.yinlinag_dian_mix);
            mImgSoundLineH.setImageResource(R.drawable.audio_heng_line);
            mImgSoundLineV.setImageResource(R.drawable.audio_shu_line);
        }
        initEvents();

//		mImgPoint.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
//		mImgSoundLineH.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
//		mImgSoundLineV.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
//		measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
    }

    /**
     *
     */
    private void initViews() {
        rlRoot = (RelativeLayout) mSFView.findViewById(R.id.rl_root);
        mImgPoint = (ImageView) mSFView.findViewById(R.id.imgSouncPoint);
        imgSouncPointBg = (ImageView) mSFView.findViewById(R.id.imgSouncPointBg);
        mImgSoundLineH = (ImageView) mSFView.findViewById(R.id.imgSoundLineH);
        mImgSoundLineV = (ImageView) mSFView.findViewById(R.id.imgSoundLineV);
    }



    /**
     *
     */
    private void initEvents() {

    }


    float beforeX = 0;
    float beforeY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mIsMouseDown = true;
            dispatchSetPressed(true);
            if (super.getParent() != null) {
                super.getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mLastTime = mCurTime;
                mCurTime = System.currentTimeMillis();
                float xx = event.getX() - beforeX;
                float yy = event.getY() - beforeY;
                beforeX = event.getX();
                beforeY = event.getY();
                if ((mCurTime - mLastTime < 500) && Math.abs(xx) <= 10.0f && Math.abs(yy) <= 10.0f) {
                    mDoubleClick = true;
                    Log.d(TAG, "这就是传说中的双击事件 mDoubleClick=" + mDoubleClick + " xx=" + xx + " yy=" + yy);
                    if (mOnValueChangedListener != null) {
                        mOnValueChangedListener.onDoubleClickChange(mBalanceValue, mFadeValue, mPosX, mPosY);
                    }
                    return true;
                } else {
                    mDoubleClick = false;
                }
            }
            return true;
        }


        if ((event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_UP)
                && mIsMouseDown && !mDoubleClick) {
            Log.d(TAG, "onTouchEvent getX=" + event.getX() + " getY=" + event.getY());
            mPosX = event.getX() < H_PADDING ? H_PADDING :
                    event.getX() > getWidth() - H_PADDING ? getWidth() - H_PADDING : event.getX();
            mPosY = event.getY() < V_PADDING ? V_PADDING :
                    event.getY() > getHeight() - V_PADDING ? getHeight() - V_PADDING : event.getY();

            setPointPos();

            mBalanceValue = xValue2Balance(mPosX);
            mFadeValue = yValue2Fade(mPosY);

            if (mOnValueChangedListener != null) {
                mOnValueChangedListener.onValueChange(mBalanceValue, mFadeValue, mPosX, mPosY);
            }
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            mIsMouseDown = false;
            dispatchSetPressed(false);
        }

        return true;
//		return super.onTouchEvent(event);
    }

    public void reset() {
        mPosX = getWidth() / 2;
        mPosY = getHeight() / 2;
        Log.d(TAG, "reset mPosX=" + mPosX + " mPosY=" + mPosX);

        if (mOnValueChangedListener != null) {
            mOnValueChangedListener.onValueChange((int) BALANCE_MAX / 2, (int) FADE_MAX / 2, mPosX, mPosY);
        }
        post(new Runnable() {

            @Override
            public void run() {
                setPointPos();
            }
        });

    }

    private void setPointPos() {
        mImgPoint.setX(mPosX - mImgPoint.getWidth() / 2);
        mImgPoint.setY(mPosY - mImgPoint.getHeight() / 2);

        imgSouncPointBg.setX(mPosX - imgSouncPointBg.getWidth() / 2);
        imgSouncPointBg.setY(mPosY - imgSouncPointBg.getHeight() / 2);


        mImgSoundLineH.setX(mPosX - mImgSoundLineH.getWidth() / 2);
        mImgSoundLineH.setY(mPosY - mImgSoundLineH.getHeight() / 2);

        mImgSoundLineV.setX(mPosX - mImgSoundLineV.getWidth() / 2);
        mImgSoundLineV.setY(mPosY - mImgSoundLineV.getHeight() / 2);
    }

    //获取有效计算区域的宽度值
    private int getValidWidth() {
        return getWidth() - H_PADDING * 2;
    }

    //获取有效计算区域的高度值
    private int getValidHeight() {
        return getHeight() - V_PADDING * 2;
    }

    private int xValue2Balance(float x) {
        int balance = (int) ((x - H_PADDING) * BALANCE_MAX / getValidWidth());
        Log.d(TAG, "xValue2Balance, x: " + (x - H_PADDING) + ", balance: " + balance + ", getWidth: " + getValidWidth());
        return balance;
    }

    private int yValue2Fade(float y) {
        int fade = (int) (((y - V_PADDING) * FADE_MAX) / getValidHeight());
        Log.d(TAG, "yValue2Fade, y: " + (y - V_PADDING) + ", fade: " + fade + ", getHeight: " + getValidHeight());
        return fade;
    }

    private float balance2x(int balance, int width) {
        float x = (float) (balance * (width - H_PADDING * 2) / BALANCE_MAX);
        Log.d(TAG, "balance2x, balance: " + balance + ", x: " + x + ", width: " + width);
        return x + H_PADDING;
    }

    private float fade2y(int fade, int height) {
        float y = (float) (fade * (height - V_PADDING * 2) / FADE_MAX);
        Log.d(TAG, "fade2y, fade: " + fade + ", y: " + y + ", height: " + height);
        return y + V_PADDING;
    }

    public int getBalanceValue() {
        return mBalanceValue;
    }

    public void setBalanceValue(int balanceValue) {
        mBalanceValue = balanceValue;
    }

    public int getFadeValue() {
        return mFadeValue;
    }


    public void setFadeValue(int fadeValue) {
        mFadeValue = fadeValue;
    }

    private void setXPosByBalance() {
        float x = 0f;
        if (getWidth() == 0) {
            mPosX = balance2x(mBalanceValue, getMeasuredWidth());
        } else {
            mPosX = balance2x(mBalanceValue, getWidth());
        }
        setPointPos();
    }

    private void setYPosByFade() {
        float y = 0f;
        if (getHeight() == 0) {
            mPosY = fade2y(mFadeValue, getMeasuredHeight());
        } else {
            mPosY = fade2y(mFadeValue, getHeight());
        }
        setPointPos();
    }

    /**
     * 用于在配置文件中读取并恢复x坐标值
     *
     * @param x 　横向坐标值
     */
    public void setXPos(float x) {
        mPosX = x;
        setPointPos();
    }

    /**
     * 用于在配置文件中读取并恢复y坐标值
     *
     * @param y 　纵向坐标值
     */
    public void setYPos(float y) {
        mPosY = y;
        setPointPos();
    }

    public OnValueChangedListener getOnValueChangedListener() {
        return mOnValueChangedListener;
    }

    public void setOnValueChangedListener(OnValueChangedListener onValueChangedListener) {
        mOnValueChangedListener = onValueChangedListener;
    }

    public interface OnValueChangedListener {
        public void onValueChange(int balance, int fade, float x, float y);

        public void onDoubleClickChange(int balance, int fade, float x, float y);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.d(TAG, "onLayout");
        if (mPosX == INVALID_XY_VALUE) {
            setXPosByBalance();
        }

        if (mPosY == INVALID_XY_VALUE) {
            setYPosByFade();
        }
        setPointPos();
    }


}
