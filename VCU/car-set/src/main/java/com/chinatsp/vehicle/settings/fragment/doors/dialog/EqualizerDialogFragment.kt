package com.chinatsp.vehicle.settings.fragment.doors.dialog

import android.graphics.Color
import android.os.Bundle
import com.chinatsp.settinglib.SettingManager
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.EqualizerDialogFragmetBinding
import com.chinatsp.vehicle.settings.vm.sound.SoundEffectViewModel
import com.common.library.frame.base.BaseDialogFragment
import com.common.xui.widget.smooth.SmoothLineChartView
import com.king.base.util.LogUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EqualizerDialogFragment: BaseDialogFragment<SoundEffectViewModel, EqualizerDialogFragmetBinding>() {
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
      //  binding.smoothChartView.setOnChartClickListener { position, _ -> viewModel?.setAudioEQ(position) }

        binding.adasSideShowAreaRadio.setOnTabSelectionChangedListener { title, value ->
            val  opt =   context?.resources?.getStringArray(R.array.sound_equalizer_option)
            val x =   opt?.indexOf(value)
            LogUtils.d("value=$value title=$title x=$x")
            var postion = 0;
            x?.let {
                if(x!=-1){
                    postion = x;
                }
            }
            viewModel?.setAudioEQ(postion)
        }
        val index: Int? = viewModel?.getAudioEQ()
        index?.let {
            LogUtils.d(" index=${index}")
            var postion = 0;
            if(index!=-1){
                postion = index;
            }
            LogUtils.d(" postion=${postion}")

            binding.adasSideShowAreaRadio.setDefaultSelection(postion)

        }
    }
}