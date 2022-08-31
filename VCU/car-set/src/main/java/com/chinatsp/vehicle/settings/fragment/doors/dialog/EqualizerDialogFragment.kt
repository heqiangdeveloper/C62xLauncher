package com.chinatsp.vehicle.settings.fragment.doors.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.IRadioManager
import com.chinatsp.settinglib.manager.sound.EffectManager
import com.chinatsp.settinglib.optios.RadioNode
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

    private val xTestValueTop: List<String>
        get() = listOf("10dB", "-6dB", "20dB", "11dB", "65dB")

    override fun getLayoutId(): Int {
        return R.layout.equalizer_dialog_fragmet
    }

    override fun initData(savedInstanceState: Bundle?) {
        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()

        initView()

        initViewDisplay()
        binding.closeDialog.setOnClickListener {
            this.dismiss()
        }
    }

    private fun initViewDisplay() {
        val eqRadio = binding.soundEffectRadio
        if (VcuUtils.isAmplifier()) {
            eqRadio.getChildAt(0).visibility = View.GONE
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
                onPostSelected(it, value.toInt())
            }
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

    override fun onPostSelected(tabView: TabControlView, value: Int) {
//        val result = node.obtainSelectValue(value)
        Timber.d("onPostSelected tabView:$tabView, value:$value")
        val values = viewModel.getEffectValues(value)
        val toList = values.map { it.toFloat() }.toList()
        binding.smoothChartView.setData(toList, xValue, xValueTop)
        //动态设置上边X轴数据
        binding.smoothChartView.setXValueTop(xTestValueTop)
    }

    private fun doSendCustomEqValue() {
        val node = RadioNode.SYSTEM_SOUND_EFFECT
        val values = node.get.values
        viewModel.currentEffect.let {
            Timber.d("doSendCustomEqValue it.value:${it.value}, coreId:${values[values.size -1]}")
            if (it.value == values[values.size -1]) {
                val lev1 = (binding.smoothChartView.maxY / 5).toInt()
                val lev2 = (binding.smoothChartView.maxY / 4).toInt()
                val lev3 = (binding.smoothChartView.maxY / 3).toInt()
                val lev4 = (binding.smoothChartView.maxY / 2).toInt()
                val lev5 = (binding.smoothChartView.maxY / 1).toInt()
                manager.doSetEQ(node.obtainSelectValue(it.value!!), lev1, lev2, lev3, lev4, lev5)
            }
        }
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
        binding.smoothChartView.setOnChartClickListener { position, value ->
//            viewModel.setAudioEQ(position)
            doSendCustomEqValue()
            Timber.tag("luohong").d("--------------------position:%s", position)
            Timber.tag("luohong").d("--------------------value:%s", value)
        }
        onPostSelected(RadioNode.SYSTEM_SOUND_EFFECT, viewModel.currentEffect.value!!)
    }


}


