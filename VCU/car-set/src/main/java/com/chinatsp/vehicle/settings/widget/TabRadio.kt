package com.chinatsp.vehicle.settings.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.chinatsp.vehicle.settings.R


class TabRadio : LinearLayout {

    var tabSrc: Int = R.drawable.vcu_ic_car_door_selector
    var tabText: String = ""
    var tabBackground: Int = R.drawable.vcu_bg_layout_tab_selector

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr,
        0
    ) {
        if (null != attrs) {
            val attributes =
                context!!.theme.obtainStyledAttributes(attrs, R.styleable.TabRadio, 0, 0)
            tabSrc = attributes.getResourceId(
                R.styleable.TabRadio_tabSrc,
                R.drawable.vcu_ic_car_door_selector
            )
            tabBackground = attributes.getResourceId(
                R.styleable.TabRadio_TabBackground,
                R.drawable.vcu_bg_layout_tab_selector
            )
            tabText = attributes.getString(R.styleable.TabRadio_tabText).toString()
            attributes.recycle()
        }
        val tabRadio = inflate(context, R.layout.view_left_tab_view, this)
        val ivLogo = tabRadio.findViewById<AppCompatImageView>(R.id.iv_logo)
        val tvName = tabRadio.findViewById<TextView>(R.id.tv_name)
        tabRadio.setBackgroundResource(tabBackground)
        ivLogo.setImageResource(tabSrc)
        tvName.setText(tabText)
    }

}