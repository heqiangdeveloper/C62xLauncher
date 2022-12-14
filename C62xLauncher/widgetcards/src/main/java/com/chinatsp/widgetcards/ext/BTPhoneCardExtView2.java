package com.chinatsp.widgetcards.ext;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chinatsp.phone.utils.AndroidToastUtil;
import com.chinatsp.phone.widget.BTPhoneCardView;
import com.chinatsp.widgetcards.home.ExpandStateManager;
import com.chinatsp.widgetcards.manager.CardManager;
import com.chinatsp.widgetcards.manager.Events;

import org.greenrobot.eventbus.EventBus;

import card.base.LauncherCard;
import launcher.base.applists.AppLists;
import launcher.base.utils.EasyLog;
import launcher.base.utils.recent.RecentAppHelper;

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

    private float mPointsDistance = 0;
    private int mPointCounts = 0;
    private float downX, downY;
    private static final int FINGER_NUM_TRIGGER_SCREEN_SHOT = 2;

    private boolean onTouchCardEvent(final MotionEvent event) {
        if (event == null) {
            return false;
        }
        Log.i(
                TAG,
                "onTouchCardEvent: event=[action=${event.action}, pointerCount=${event.pointerCount}, xy=[${event.x},${event.y}], size=[${width},${height}]]"
        );
        final Context context = getContext();
        final int pointerIndex = event.getActionIndex();
        final float pointX = event.getX(pointerIndex);
        final float pointY = event.getY(pointerIndex);
        mPointCounts = Math.max(event.getPointerCount(), mPointCounts);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downX = pointX;
                downY = pointY;
                mPointCounts = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() >= FINGER_NUM_TRIGGER_SCREEN_SHOT) {
                    mPointsDistance = Math.abs(pointX - downX) + Math.abs(pointY - downY);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mPointsDistance > 100
//                            && flashTime < 1000
                        && ExpandStateManager.getInstance().getExpandState()
                        && mPointCounts >= FINGER_NUM_TRIGGER_SCREEN_SHOT) {
                    doSwipe();
                    return true;
                }
                if (mPointCounts == 1 ) {
                    float eventX = event.getX();
                    float delta = Math.max(Math.abs(getWidth() - eventX), eventX);
                    EasyLog.d("BTPhoneCardExtView2", "delta: "+delta);
                    if (delta < 100) {
                        RecentAppHelper.launchApp(getContext(), AppLists.btPhone);
                        return true;
                    }
                }
                resetTouchEvent();
                break;
            case MotionEvent.ACTION_CANCEL:
                resetTouchEvent();
                break;
        }
        return false;

//        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
//            if (event.getPointerCount() >= 2) {
////                AndroidToastUtil.INSTANCE.show(context, "三指滑屏");
//                EventBus.getDefault().post(Events.createSwipeEvent(
//                        CardManager.getInstance().findByType(CardManager.CardType.PHONE), (View) getParent()));
//                if (mPointsDistance > 100
////                            && flashTime < 1000
//                        && ExpandStateManager.getInstance().getExpandState()
//                        && mPointCounts >= FINGER_NUM_TRIGGER_SCREEN_SHOT) {
//                    doSwipe();
//                }
//                return true;
//            } else {
//                float delta = Math.abs(getWidth() - event.getX());
//                if (event.getPointerCount() == 1 && delta <= 100.0f) {
////                    AndroidToastUtil.INSTANCE.show(context, "点击电话边缘, 打开电话APP");
//                    RecentAppHelper.launchApp(getContext(), AppLists.btPhone);
//                    /**
//                     * 拦截
//                     */
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        } else {
////             TODO: 没有拦截 把事件交给电话内部的实现
////            AndroidToastUtil.INSTANCE.show(context, "没有拦截");
//            return false;
//        }
    }

    private void doSwipe() {
        EventBus.getDefault().post(Events.createSwipeEvent(
                CardManager.getInstance().findByType(CardManager.CardType.PHONE), (View) getParent()));
    }

    private void resetTouchEvent() {
        mPointsDistance = 0;
        downX = 0;
        downY = 0;
        mPointCounts = 0;
    }
}
