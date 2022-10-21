package com.chinatsp.vehicle.settings.fragment.doors.dialog

import android.content.Intent
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
import kotlin.properties.Delegates

@AndroidEntryPoint
class EqualizerDialogFragment :
    BaseDialogFragment<SoundEffectViewModel, EqualizerDialogFragmetBinding>(), IRadioAction {

    private val manager: EffectManager
        get() = EffectManager.instance

    private val xValue: List<String>
        get() = listOf(activity?.resources?.getString(R.string.treble),
            activity?.resources?.getString(R.string.medium_treble),
            activity?.resources?.getString(R.string.medium),
            activity?.resources?.getString(R.string.medium_bass),
            activity?.resources?.getString(R.string.bass)) as List<String>

    private val offset: Float by lazy {
        if (VcuUtils.isAmplifier) 9f else 5f
    }
    private lateinit var vList:List<Float>
    private var value by Delegates.notNull<Int>()

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
        if (VcuUtils.isAmplifier) {
            eqRadio.getChildAt(0).visibility = View.GONE
        }
    }

    private fun initRadioOption() {
        initRadioOption(RadioNode.SYSTEM_SOUND_EFFECT, viewModel.currentEffect)
    }

    private fun addRadioLiveDataListener() {
        viewModel.currentEffect.observe(this) {
            doUpdateRadio(RadioNode.SYSTEM_SOUND_EFFECT, it, false)
            if (it.data == 6) {
                binding.smoothChartView.setEnableView(true)
            } else {
                binding.smoothChartView.setEnableView(false)
            }
        }
    }

    private fun setRadioListener() {
        binding.soundEffectRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.SYSTEM_SOUND_EFFECT, value, viewModel.currentEffect, it)
//                onPostSelected(it, value.toInt())
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
        val values = viewModel.getEffectValues(value).toList()
        Timber.d("onPostSelected 11111111111111 tabView:$tabView, value:$value, values:%s", values)
        val toList = values.map {
            var value = it.toFloat() - 1
            if (value < 0f) {
                value = 0f
            } else if (value > 2 * offset) {
                value = 2 * offset
            }
            value
        }.toList()
        Timber.d("onPostSelected 22222222222222 tabView:$tabView, value:$value, toList:%s", toList)
        this.vList = toList
        this.value = value
        binding.smoothChartView.setData(toList, xValue)
       if(value ==6){
           intentService(toList)
       }
    }

    private fun doSendCustomEqValue() {
        val node = RadioNode.SYSTEM_SOUND_EFFECT
        val values = node.get.values
        viewModel.currentEffect.let {
            if (it.value!!.data == values.last()) {
                val progress = binding.smoothChartView.obtainProgress()
                var lev1 = 0
                var lev2 = 0
                var lev3 = 0
                var lev4 = 0
                var lev5 = 0
                if (null != progress && progress.size == 5) {
                    lev1 = progress[0]
                    lev2 = progress[1]
                    lev3 = progress[2]
                    lev4 = progress[3]
                    lev5 = progress[4]
                }
                manager.doSetEQ(it.value!!.data, lev1, lev2, lev3, lev4, lev5)
            }
        }
    }

    private fun initView() {

        binding.smoothChartView.setInterval(-1 * offset, offset)
        binding.smoothChartView.isCustomBorder = true
        binding.smoothChartView.setTagDrawable(R.drawable.ac_blue_52)
        binding.smoothChartView.textColor = Color.TRANSPARENT
        binding.smoothChartView.textSize = 20
        binding.smoothChartView.textOffset = 4
        binding.smoothChartView.minY = -1 * offset
        binding.smoothChartView.maxY = offset
        binding.smoothChartView.enableShowTag(false)
        binding.smoothChartView.enableDrawArea(true)
        binding.smoothChartView.lineColor = resources.getColor(R.color.smooth_line_color)
        binding.smoothChartView.circleColor = resources.getColor(R.color.smooth_circle_color)
        binding.smoothChartView.innerCircleColor = Color.parseColor("#ffffff")
        binding.smoothChartView.nodeStyle = SmoothLineChartView.NODE_STYLE_RING
        binding.smoothChartView.setOnChartClickListener { position, value ->
//            viewModel.setAudioEQ(position)
            doSendCustomEqValue()
        }
//        onPostSelected(RadioNode.SYSTEM_SOUND_EFFECT, viewModel.currentEffect.value!!)
    }

    override fun onDestroy() {
        super.onDestroy()
       if(value == 6){
           val value = binding.smoothChartView.obtainProgress()
           val intent = Intent("com.chinatsp.vehiclenetwork.usercenter")
           val json = "{\"high\":\""+value[0]+"\",\"alt\":\""+
                   value[1]+"\",\"alto\":\""+
                   value[2]+"\",\"mid\":\""+
                   value[3]+"\",\"bass\":\""+
                   value[4]+"\"}"
           intent.putExtra("app", "com.chinatsp.vehicle.settings")
           intent.putExtra("soundEffects",json)
           intent.setPackage("com.chinatsp.usercenter")
           activity?.startService(intent)
       }
    }

    private fun intentService(value:List<Float>){
        val intent = Intent("com.chinatsp.vehiclenetwork.usercenter")
        val json = "{\"high\":\""+value[0]+"\",\"alt\":\""+
                value[1]+"\",\"alto\":\""+
                value[2]+"\",\"mid\":\""+
                value[3]+"\",\"bass\":\""+
                value[4]+"\"}"
        intent.putExtra("app", "com.chinatsp.vehicle.settings")
        intent.putExtra("soundEffects",json)
        intent.setPackage("com.chinatsp.usercenter")
        activity?.startService(intent)
    }
}


