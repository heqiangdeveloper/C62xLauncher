package com.chinatsp.vehicle.settings.fragment.sound

import android.graphics.Color
import android.os.Bundle
import com.chinatsp.settinglib.SettingManager
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.SoundEffectFragmentBinding
import com.chinatsp.vehicle.settings.fragment.doors.dialog.EqualizerDialogFragment
import com.chinatsp.vehicle.settings.fragment.doors.dialog.VolumeDialogFragment
import com.chinatsp.vehicle.settings.vm.sound.SoundEffectViewModel
import com.chinatsp.vehicle.settings.vm.sound.SoundViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.smooth.SmoothLineChartView
import com.king.base.util.LogUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class SoundEffectFragment : BaseFragment<SoundEffectViewModel, SoundEffectFragmentBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.sound_effect_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initView()
        setCheckedChangeListener()
    }

    private fun initView() {
        binding.smoothChartView.isCustomBorder = true
        binding.smoothChartView.setTagDrawable(R.drawable.ac_blue_52)
        binding.smoothChartView.textColor = Color.TRANSPARENT
        binding.smoothChartView.textSize = 20
        binding.smoothChartView.textOffset = 4
        binding.smoothChartView.minY = -7F
        binding.smoothChartView.maxY = 7F
        binding.smoothChartView.enableShowTag(false)
        binding.smoothChartView.enableDrawArea(true)
        binding.smoothChartView.lineColor = resources.getColor(R.color.md_material_blue_600)
        binding.smoothChartView.circleColor =
            resources.getColor(R.color.default_shadow_button_color_pressed)
        binding.smoothChartView.innerCircleColor = Color.parseColor("#ffffff")
        binding.smoothChartView.nodeStyle = SmoothLineChartView.NODE_STYLE_RING
        var data: MutableList<Float> = ArrayList()
        data.add(3f)
        data.add(-7f)
        data.add(1f)
        data.add(7f)
        data.add(-3f)
        var x: MutableList<String> = ArrayList()
        x.add("3-12")
        x.add("3-13")
        x.add("3-14")
        x.add("3-15")
        x.add("3-16")
        binding.smoothChartView.setData(data, x)
        binding.smoothChartView.setOnChartClickListener { position, _ -> viewModel?.setAudioEQ(position) }
    }

    private fun setCheckedChangeListener() {
        binding.soundEqualizerCompensation.setOnClickListener {
            val fragment = EqualizerDialogFragment()
            activity?.supportFragmentManager?.let {
                fragment.show(it, fragment.javaClass.simpleName)
            }
        }
        binding.soundVolumeBalanceCompensation.setOnClickListener{
            val fragment = VolumeDialogFragment()
            activity?.supportFragmentManager?.let {
                fragment.show(it, fragment.javaClass.simpleName)
            }
        }
    }

}