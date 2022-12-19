package com.chinatsp.widgetcards.ext;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chinatsp.phone.widget.BTPhoneCardView;
import com.chinatsp.widgetcards.home.ExpandStateManager;
import com.chinatsp.widgetcards.manager.CardManager;
import com.chinatsp.widgetcards.manager.Events;

import org.greenrobot.eventbus.EventBus;

import launcher.base.applists.AppLists;
import launcher.base.utils.EasyLog;
import launcher.base.utils.recent.RecentAppHelper;

/**
 * 蓝牙电话卡片父布局.
 *
 * @author puzhenwei & yangdu
 * @date 2022/12/19/17:33
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class BTPhoneCardExtView2 extends BTPhoneCardView {

    public BTPhoneCardExtView2(@NonNull Context context) {
        super(context);
        Companion.setOVERRIDE_BT_PHONE_CARD_VIEW(true);
    }

    public BTPhoneCardExtView2(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Companion.setOVERRIDE_BT_PHONE_CARD_VIEW(true);
    }

    public BTPhoneCardExtView2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Companion.setOVERRIDE_BT_PHONE_CARD_VIEW(true);
    }

    @Override
    public boolean onInterceptActuallyTouchCardEvent(final MotionEvent event) {
        return onTouchCardEvent(event);
    }

    /**
     * 拦截触摸事件时, 需要用到的几个参数
     * <p>
     * 点击距离
     */
    private float mPointsDistance = 0;
    /**
     * 点击数
     */
    private int mPointCounts = 0;
    /**
     * @see MotionEvent#ACTION_DOWN: 该时间的x, y.
     */
    private float downX, downY;
    /**
     * 三指滑屏的最小个数, 我也不懂为什么三指滑屏的最小个数是2.
     */
    private static final int FINGER_NUM_TRIGGER_SCREEN_SHOT = 2;
    /**
     * 用户的点击区域, 我们仅设置右侧100px的区域可以点击跳转到蓝牙电话.
     * TODO: 当然, 可以扩展到left, top, bottom, 计算和右侧计算类似, 但是要注意参数要用对.
     */
    private static final int TOUCH_AREA_TO_OPEN_BT_PHONE = 100;

    /**
     * 根据业务需求, 拦截事件后, 再考虑分发.
     *
     * @param event 事件
     * @return true: 拦截, false: 向下分发.
     * @see this#onInterceptActuallyTouchCardEvent(MotionEvent)
     */
    private boolean onTouchCardEvent(final MotionEvent event) {
        if (event == null) {
            return false;
        }
        Log.i(
                TAG,
                "onTouchCardEvent: event=[action=${event.action}, pointerCount=${event.pointerCount}, xy=[${event.x},${event.y}], size=[${width},${height}]]"
        );
        final int pointerIndex = event.getActionIndex();
        final float pointX = event.getX(pointerIndex);
        final float pointY = event.getY(pointerIndex);
        mPointCounts = Math.max(event.getPointerCount(), mPointCounts);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // down下时, 我们记录数据, 并且复位点击数量.
                downX = pointX;
                downY = pointY;
                mPointCounts = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                // 三指滑屏还要依赖这里对数据的处理
                if (event.getPointerCount() >= FINGER_NUM_TRIGGER_SCREEN_SHOT) {
                    mPointsDistance = Math.abs(pointX - downX) + Math.abs(pointY - downY);
                }
                break;
            case MotionEvent.ACTION_UP:
                // 三指滑屏, 真正实现交换位置
                if (mPointsDistance > TOUCH_AREA_TO_OPEN_BT_PHONE
                        // && flashTime < 1000
                        && ExpandStateManager.getInstance().getExpandState()
                        && mPointCounts >= FINGER_NUM_TRIGGER_SCREEN_SHOT) {
                    doSwipe();
                    resetTouchEvent();
                    return true;
                }
                // 用户跳转蓝牙电话的条件
                if (mPointCounts == 1) {
                    float eventX = event.getX();
                    float delta = Math.abs(getWidth() - eventX);
                    EasyLog.d("BTPhoneCardExtView2", "delta: " + delta);
                    if (delta < TOUCH_AREA_TO_OPEN_BT_PHONE) {
                        RecentAppHelper.launchApp(getContext(), AppLists.btPhone);
                        resetTouchEvent();
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                // 复位
                resetTouchEvent();
                break;
            default:
        }
        // 以上条件不拦截时, 默认分发到低层级
        return false;
    }

    /**
     * 注意本类中:
     * 这个接口是launcher开发, 杨杜维护.
     * <p>
     * 其余接口, 均是我们蓝牙电话维护.
     */
    private void doSwipe() {
        EventBus.getDefault().post(Events.createSwipeEvent(
                CardManager.getInstance().findByType(CardManager.CardType.PHONE), (View) getParent()));
    }

    /**
     * 复位触摸事件的参数.
     *
     * @see this#mPointsDistance
     * @see this#downX
     * @see this#downY
     * @see this#mPointCounts
     */
    private void resetTouchEvent() {
        mPointsDistance = 0;
        downX = 0;
        downY = 0;
        mPointCounts = 0;
    }
}
