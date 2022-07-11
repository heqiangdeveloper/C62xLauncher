package com.chinatsp.vehicle.settings.fragment.doors.dialog

import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.manager.sound.EffectManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SoundEffect
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.EqualizerDialogFragmetBinding
import com.chinatsp.vehicle.settings.vm.sound.SoundEffectViewModel
import com.common.library.frame.base.BaseDialogFragment
import com.common.xui.widget.smooth.SmoothLineChartView
import com.common.xui.widget.tabbar.TabControlView
import com.king.base.util.LogUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EqualizerDialogFragment :
    BaseDialogFragment<SoundEffectViewModel, EqualizerDialogFragmetBinding>() {

    private val manager: EffectManager
        get() = EffectManager.instance

    private val xValue: List<String>
        get() = listOf("3-12", "3-13", "3-14", "3-15", "3-16")

    override fun getLayoutId(): Int {
        return R.layout.equalizer_dialog_fragmet
    }

    override fun initData(savedInstanceState: Bundle?) {
        initView();
        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()
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
                viewModel.doSwitchSoundEffect(value.toInt())
//                doUpdateRadio(RadioNode.SYSTEM_SOUND_EFFECT, value, viewModel.currentEffect, it)
            }
        }
    }

    private fun initRadioOption(node: RadioNode, liveData: LiveData<Int>) {
        val value = liveData.value ?: node.default
        doUpdateRadio(node, value, isInit = true)
    }

    private fun doUpdateRadio(
        node: RadioNode,
        value: String,
        liveData: LiveData<Int>,
        tabView: TabControlView
    ) {
        val result = isCanToInt(value) && manager.doSetRadioOption(node, value.toInt())
        tabView.takeIf { !result }?.setSelection(liveData.value.toString(), true)
    }

    private fun doUpdateRadio(
        node: RadioNode,
        value: Int,
        immediately: Boolean = false,
        isInit: Boolean = false
    ) {
        val tabView = when (node) {
            RadioNode.SYSTEM_SOUND_EFFECT -> binding.soundEffectRadio
            else -> null
        }
        tabView?.let {
            bindRadioData(node, tabView, isInit)
            doUpdateRadio(it, value, immediately)
        }
    }


    private fun bindRadioData(node: RadioNode, tabView: TabControlView, isInit: Boolean) {
        if (isInit) {
            val names = tabView.nameArray.map { it.toString() }.toTypedArray()
            val values = node.get.values.map { it.toString() }.toTypedArray()
            tabView.setItems(names, values)
        }
    }

    private fun doUpdateRadio(tabView: TabControlView, value: Int, immediately: Boolean = false) {
        tabView.setSelection(value.toString(), true)
        val values = viewModel.getEffectValues(SoundEffect.getEffect(value))
        val toList = values.map { it.toFloat() }.toList()
        binding.smoothChartView.setData(toList, xValue)
    }

    private fun isCanToInt(value: String?): Boolean {
        return null != value && value.isNotBlank() && value.matches(Regex("\\d+"))
    }

    private fun initView() {
        binding.smoothChartView.isCustomBorder = true
        binding.smoothChartView.setTagDrawable(R.drawable.ac_blue_52)
        binding.smoothChartView.textColor = Color.TRANSPARENT
        binding.smoothChartView.textSize = 20
        binding.smoothChartView.textOffset = 4
        binding.smoothChartView.minY = 10f
        binding.smoothChartView.maxY = 80f
        binding.smoothChartView.enableShowTag(false)
        binding.smoothChartView.enableDrawArea(true)
        binding.smoothChartView.lineColor = resources.getColor(R.color.smooth_bg_color_start)
        binding.smoothChartView.circleColor =
            resources.getColor(R.color.smooth_bg_color_end)
        binding.smoothChartView.innerCircleColor = Color.parseColor("#ffffff")
        binding.smoothChartView.nodeStyle = SmoothLineChartView.NODE_STYLE_RING
        val values = viewModel.getEffectValues(SoundEffect.getEffect(viewModel.currentEffect.value!!))
        val toList = values.map { it.toFloat() }.toList()
        binding.smoothChartView.setData(toList, xValue)
    }
}


