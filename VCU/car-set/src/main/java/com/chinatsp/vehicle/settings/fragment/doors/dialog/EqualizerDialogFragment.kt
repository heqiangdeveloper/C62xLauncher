package com.chinatsp.vehicle.settings.fragment.doors.dialog

import android.graphics.Color
import android.os.Bundle
import com.chinatsp.settinglib.manager.IRadioManager
import com.chinatsp.settinglib.manager.sound.EffectManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SoundEffect
import com.chinatsp.vehicle.settings.IRadioAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.EqualizerDialogFragmetBinding
import com.chinatsp.vehicle.settings.vm.sound.SoundEffectViewModel
import com.common.library.frame.base.BaseDialogFragment
import com.common.xui.widget.smooth.SmoothLineChartView
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class EqualizerDialogFragment :
    BaseDialogFragment<SoundEffectViewModel, EqualizerDialogFragmetBinding>(), IRadioAction {

    private val manager: EffectManager
        get() = EffectManager.instance

    private val xValue: List<String>
        get() = listOf("高音", "中高音", "中音", "中低音", "低音")

    private val xValueTop: List<String>
        get() = listOf("4dB", "-2dB", "4dB", "2dB", "4dB")

    override fun getLayoutId(): Int {
        return R.layout.equalizer_dialog_fragmet
    }

    override fun initData(savedInstanceState: Bundle?) {

        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()

        initView()
        binding.closeDialog.setOnClickListener {
            this.dismiss()
        }
    }

    private fun initRadioOption() {
        initRadioOption(RadioNode.SYSTEM_SOUND_EFFECT, viewModel.currentEffect)
    }

    private fun addRadioLiveDataListener() {
        viewModel.currentEffect.observe(this) {
            doUpdateRadio(RadioNode.SYSTEM_SOUND_EFFECT, it, false)
        }
    }

    private fun setRadioListener() {
        binding.soundEffectRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.SYSTEM_SOUND_EFFECT, value, viewModel.currentEffect, it)
                doSendEQValue(value)
            }
        }
    }

    private fun doSendEQValue(value: String?) {
        value?.run {
            val node = RadioNode.SYSTEM_SOUND_EFFECT
            val eq = node.obtainSelectValue(value.toInt())
//            manager.sendEQValue(eq)
            viewModel.setAudioEQ(eq)
            onPostSelected(RadioNode.SYSTEM_SOUND_EFFECT, eq)
        }
    }

    override fun findRadioByNode(node: RadioNode): TabControlView? {
        return when (node) {
            RadioNode.SYSTEM_SOUND_EFFECT -> binding.soundEffectRadio
            else -> null
        }
    }

    override fun getRadioManager(): IRadioManager {
        return manager
    }

    override fun onPostSelected(node: RadioNode, value: Int) {
//        val result = node.obtainSelectValue(value)
        val values = viewModel.getEffectValues(value)
        val toList = values.map { it.toFloat() }.toList()
        binding.smoothChartView.setData(toList, xValue, xValueTop)
    }

    private fun initView() {
        binding.smoothChartView.isCustomBorder = true
        binding.smoothChartView.setTagDrawable(R.drawable.ac_blue_52)
        binding.smoothChartView.textColor = Color.TRANSPARENT
        binding.smoothChartView.textSize = 20
        binding.smoothChartView.textOffset = 4
        binding.smoothChartView.minY = 0F
        binding.smoothChartView.maxY = 15F
        binding.smoothChartView.enableShowTag(false)
        binding.smoothChartView.enableDrawArea(true)
        binding.smoothChartView.lineColor = resources.getColor(R.color.smooth_line_color)
        binding.smoothChartView.circleColor = resources.getColor(R.color.smooth_circle_color)
        binding.smoothChartView.innerCircleColor = Color.parseColor("#ffffff")
        binding.smoothChartView.nodeStyle = SmoothLineChartView.NODE_STYLE_RING
        binding.smoothChartView.setOnChartClickListener { position, _ ->
            viewModel.setAudioEQ(position)
            Timber.tag("luohong").d("--------------------position:%s", position)
        }
        val eqId = viewModel.currentEffect.value!!
        val values = viewModel.getEffectValues(eqId)
        val toList = values.map { it.toFloat() }.toList()
        binding.smoothChartView.setData(toList, xValue, xValueTop)
    }
}


