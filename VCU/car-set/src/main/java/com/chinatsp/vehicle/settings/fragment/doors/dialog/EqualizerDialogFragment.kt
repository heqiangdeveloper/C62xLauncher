package com.chinatsp.vehicle.settings.fragment.doors.dialog

import android.graphics.Color
import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.EqualizerDialogFragmetBinding
import com.chinatsp.vehicle.settings.vm.sound.SoundViewModel
import com.common.library.frame.base.BaseDialogFragment
import com.common.xui.widget.smooth.SmoothLineChartView
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class EqualizerDialogFragment: BaseDialogFragment<SoundViewModel, EqualizerDialogFragmetBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.equalizer_dialog_fragmet
    }

    override fun initData(savedInstanceState: Bundle?) {
        initView();
    }
    private fun initView() {
        binding.smoothChartView.isCustomBorder = true
        binding.smoothChartView.setTagDrawable(R.drawable.ac_blue_52)
        binding.smoothChartView.textColor = Color.TRANSPARENT
        binding.smoothChartView.textSize = 20
        binding.smoothChartView.textOffset = 4
        binding.smoothChartView.minY = 40F
        binding.smoothChartView.maxY = 58F
        binding.smoothChartView.enableShowTag(false)
        binding.smoothChartView.enableDrawArea(true)
        binding.smoothChartView.lineColor = resources.getColor(R.color.smooth_bg_color_start)
        binding.smoothChartView.circleColor =
            resources.getColor(R.color.smooth_bg_color_end)
        binding.smoothChartView.innerCircleColor = Color.parseColor("#ffffff")
        binding.smoothChartView.nodeStyle = SmoothLineChartView.NODE_STYLE_RING
        var data: MutableList<Float> = ArrayList()
        data.add(55f)
        data.add(54f)
        data.add(51f)
        data.add(49f)
        data.add(51f)
        var x: MutableList<String> = ArrayList()
        x.add("3-12")
        x.add("3-13")
        x.add("3-14")
        x.add("3-15")
        x.add("3-16")
        binding.smoothChartView.setData(data, x)
    }
}