package com.chinatsp.widgetcards.ext;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chinatsp.phone.utils.AndroidToastUtil;
import com.chinatsp.phone.widget.BTPhoneCardView;

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

    private boolean onTouchCardEvent(final MotionEvent event) {
        if (event == null) {
            return false;
        }
        Log.i(
                TAG,
                "onTouchCardEvent: event=[action=${event.action}, pointerCount=${event.pointerCount}, xy=[${event.x},${event.y}], size=[${width},${height}]]"
        );
        final Context context = getContext();
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            if (event.getPointerCount() >= 3) {
                // TODO: 实现你的 三指滑屏 功能
                AndroidToastUtil.INSTANCE.show(context, "三指滑屏");
                /**
                 * 拦截
                 */
                return true;
            } else {
                float delta = Math.abs(getWidth() - event.getX());
                if (event.getPointerCount() == 1 && delta <= 60.0f) {
                    // TODO: 实现你的 点击电话边缘, 打开电话APP 功能
                    AndroidToastUtil.INSTANCE.show(context, "点击电话边缘, 打开电话APP");
                    /**
                     * 拦截
                     */
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            // TODO: 没有拦截 把事件交给电话内部的实现
            AndroidToastUtil.INSTANCE.show(context, "没有拦截");
            return false;
        }
    }
}
