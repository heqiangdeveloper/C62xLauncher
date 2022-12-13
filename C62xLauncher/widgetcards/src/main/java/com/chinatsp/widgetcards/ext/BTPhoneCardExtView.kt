package com.chinatsp.widgetcards.ext

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.chinatsp.phone.tracker.Log
import com.chinatsp.phone.utils.AndroidToastUtil
import com.chinatsp.phone.widget.BTPhoneCardView

class BTPhoneCardExtView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BTPhoneCardView(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "BTPhoneCardExtView"
    }

    init {
        OVERRIDE_BT_PHONE_CARD_VIEW = true
    }

    override fun onInterceptActuallyTouchCardEvent(event: MotionEvent?): Boolean {
        return onTouchCardEvent(event)
    }


    private fun onTouchCardEvent(event: MotionEvent?): Boolean {
        event ?: return false
        Log.i(
            TAG,
            "onTouchCardEvent: event=[action=${event.action}, pointerCount=${event.pointerCount}, xy=[${event.x},${event.y}], size=[${width},${height}]]"
        )
        return if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            if (event.pointerCount >= 3) {
                // TODO: 实现你的 三指滑屏 功能
                AndroidToastUtil.show(context, "三指滑屏")
                /**
                 * 拦截
                 */
                true
            } else {
                val delta = kotlin.math.abs(width - event.x)
                if (event.pointerCount == 1 && delta <= 60) {
                    // TODO: 实现你的 点击电话边缘, 打开电话APP 功能
                    AndroidToastUtil.show(context, "点击电话边缘, 打开电话APP")
                    /**
                     * 拦截
                     */
                    true
                } else {
                    false
                }
            }
        } else {
            // TODO: 没有拦截 把事件交给电话内部的实现
            AndroidToastUtil.show(context, "没有拦截")
            false
        }
    }
}