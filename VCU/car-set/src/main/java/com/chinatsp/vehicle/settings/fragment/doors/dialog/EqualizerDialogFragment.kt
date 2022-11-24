package com.chinatsp.vehicle.settings.fragment.doors.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.IRadioManager
import com.chinatsp.settinglib.manager.sound.EffectManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.vehicle.controller.CollapseController
import com.chinatsp.vehicle.controller.ICollapseListener
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

    private lateinit var xValue: List<String>

    private val offset: Float by lazy {
        if (VcuUtils.isAmplifier) 9f else 5f
    }
    private lateinit var vList: List<Float>
    private var value by Delegates.notNull<Int>()
    private var mCollapseController: CollapseController? = null
    override fun getLayoutId(): Int {
        return R.layout.equalizer_dialog_fragmet
    }

    override fun initData(savedInstanceState: Bundle?) {
        initData()
        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()

        initView()
        registerController()
        initViewDisplay()
        binding.closeDialog.setOnClickListener {
            this.dismiss()
        }
    }
   private fun initData(){
       if (VcuUtils.isAmplifier) {
           xValue = listOf(
               activity?.resources?.getString(R.string.bass),
               activity?.resources?.getString(R.string.medium_bass),
               activity?.resources?.getString(R.string.medium),
               activity?.resources?.getString(R.string.medium_treble),
               activity?.resources?.getString(R.string.treble)
           ) as List<String>
       } else {
           xValue = listOf(
               activity?.resources?.getString(R.string.sixty_five),
               activity?.resources?.getString(R.string.two_hundred_fifty),
               activity?.resources?.getString(R.string.seven_hundred_fifty),
               activity?.resources?.getString(R.string.one_thousand_three_hundred),
               activity?.resources?.getString(R.string.two_thousand_three_hundred),
               activity?.resources?.getString(R.string.three_thousand_five_hundred),
               activity?.resources?.getString(R.string.six_thousand_five_hundred),
               activity?.resources?.getString(R.string.eight_thousand_five_hundred),
               activity?.resources?.getString(R.string.eighteen_thousand)
           ) as List<String>
       }
   }
    private fun registerController(){
        mCollapseController = CollapseController(activity, mDrawerCollapseListener)
        mCollapseController!!.register()
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
            binding.smoothChartView.setEnableView(it.data == 6)
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
    }

    private fun doSendCustomEqValue() {
        val node = RadioNode.SYSTEM_SOUND_EFFECT
        val values = node.get.values
        viewModel.currentEffect.let {
            if (it.value!!.data == values.last()) {
                val progress = binding.smoothChartView.obtainProgress()
                if (null != progress && progress.size == Constant.EQ_SIZE) {
                    manager.doSetEQ(it.value!!.data, progress)
                }
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

    override fun getWidthRatio(): Float {
        return if (VcuUtils.isAmplifier) {
            1000f / 1920f
        }else{
            960f / 1920f
        }
    }
    private var mDrawerCollapseListener: ICollapseListener? = object : ICollapseListener {
        override fun onCollapse(key: Int) {
            initEQ()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mCollapseController!!.unRegister()
    }

    private fun initEQ(){
        EffectManager.instance.onRadioChanged(RadioNode.SYSTEM_SOUND_EFFECT, EffectManager.instance.eqMode, EffectManager.instance.getDefaultEqSerial())
    }
}


