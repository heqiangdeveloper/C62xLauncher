package com.chinatsp.vehicle.settings.fragment.doors

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.IRadioManager
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.access.SternDoorManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.IOptionAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CarTrunkFragmentBinding
import com.chinatsp.vehicle.settings.vm.accress.SternDoorViewModel
import com.common.animationlib.AnimationDrawable
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.picker.ArcSeekBar
import com.common.xui.widget.popupwindow.PopWindow
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CarTrunkFragment : BaseFragment<SternDoorViewModel, CarTrunkFragmentBinding>(),
    ArcSeekBar.OnChangeListener, IOptionAction {
    private var animationOpenDoor: AnimationDrawable = AnimationDrawable()
    private var animationCloseDoor: AnimationDrawable = AnimationDrawable()
    private var animationFlashAlarm: AnimationDrawable = AnimationDrawable()
    private var animationBuzzerAlarms: AnimationDrawable = AnimationDrawable()

    private val manager: SternDoorManager
        get() = SternDoorManager.instance

    override fun getLayoutId(): Int {
        return R.layout.car_trunk_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initArcSeekBar()
        initAnimation()
        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()

        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()

        initViewDisplay()
        initDetailsClickListener()
    }

    private fun initViewDisplay() {
        if (VcuUtils.isCareLevel(Level.LEVEL5, expect = true)) {
            binding.linearLayout.visibility = View.GONE
            binding.line2.visibility = View.GONE
        }
    }

    private fun initAnimation() {
        animationOpenDoor.setAnimation(
            activity,
            R.drawable.trunk_door_animation,
            binding.ivCarTrunk
        )
        animationCloseDoor.setAnimation(
            activity,
            R.drawable.trunk_door_close_animation,
            binding.ivCarTrunk
        )
        animationFlashAlarm.setAnimation(
            activity,
            R.drawable.flash_alarm_animation,
            binding.ivFlashAlarm
        )
        animationBuzzerAlarms.setAnimation(
            activity,
            R.drawable.buzzer_alarms_animation,
            binding.ivBuzzerAlarms
        )
    }
    private fun initDetailsClickListener() {
        binding.electricTailDetails.setOnClickListener {
            showPopWindow(R.string.car_trunk_content,it)
        }
    }
    private fun initArcSeekBar() {
        binding.arcSeekBar.progress = 75
        binding.arcSeekBar.setOnChangeListener(this)
    }

    private fun initRadioOption() {
        initRadioOption(RadioNode.STERN_SMART_ENTER, viewModel.sternSmartEnterFunction)
    }

    private fun addRadioLiveDataListener() {
        viewModel.sternSmartEnterFunction.observe(this) {
            doUpdateRadio(RadioNode.STERN_SMART_ENTER, it, false)
        }
    }

    private fun setRadioListener() {
        binding.accessSternSmartEnterRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(
                    RadioNode.STERN_SMART_ENTER,
                    value,
                    viewModel.sternSmartEnterFunction,
                    it
                )
                if (value.equals("1")) {
                    binding.carTrunkDoorHeight.visibility = View.GONE
                    binding.intelligenceInto.visibility = View.GONE
                    binding.arcSeekBar.visibility = View.VISIBLE
                } else if (value.equals("2")) {
                    binding.carTrunkDoorHeight.visibility = View.VISIBLE
                    binding.intelligenceInto.visibility = View.VISIBLE
                    binding.arcSeekBar.visibility = View.GONE
                    binding.carTrunkDoorHeight.setText(R.string.car_trunk_keep_unlock)
                } else {
                    binding.carTrunkDoorHeight.visibility = View.VISIBLE
                    binding.intelligenceInto.visibility = View.VISIBLE
                    binding.arcSeekBar.visibility = View.GONE
                    binding.carTrunkDoorHeight.setText(R.string.car_trunk_action_unlock)
                }
            }
        }
    }

    private fun setSwitchListener() {
        binding.accessSternElectricSw.let {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                doUpdateSwitchOption(SwitchNode.AS_STERN_ELECTRIC, buttonView, isChecked)
                checkDisableOtherDiv(it, isChecked)
                doElectricTrunkFollowing(it.isChecked)
            }
        }
        binding.accessSternLightAlarmSw.let {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                doUpdateSwitchOption(SwitchNode.STERN_LIGHT_ALARM, buttonView, isChecked)
                doLightBlinkFollowing(it.isChecked)
            }
        }
        binding.accessSternAudioAlarmSw.let {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                doUpdateSwitchOption(SwitchNode.STERN_AUDIO_ALARM, buttonView, isChecked)
                doBuzzerFollowing(it.isChecked)
            }
        }
    }

    private fun doBuzzerFollowing(status: Boolean) {
        if (!status) {
            binding.carTrunkDoorHeight.setText(R.string.car_trunk_buzzer_close)
            return
        }
        binding.ivBuzzerAlarms.visibility = View.VISIBLE
        binding.carTrunkDoorHeight.visibility = View.VISIBLE
        binding.carTrunkDoorHeight.setText(R.string.car_trunk_buzzer_open)
        animationBuzzerAlarms.start(
            false, 50,
            object : AnimationDrawable.AnimationLisenter {
                override fun startAnimation() {
                    binding.arcSeekBar.visibility = View.GONE
                }

                override fun endAnimation() {
                    if (binding.accessSternElectricSw.isChecked) {
                        binding.arcSeekBar.visibility = View.VISIBLE
                    } else {
                        binding.arcSeekBar.visibility = View.GONE
                    }
                    binding.ivBuzzerAlarms.visibility = View.GONE

                }
            })

    }

    private fun doLightBlinkFollowing(status: Boolean) {
        if (!status) {
            binding.carTrunkDoorHeight.setText(R.string.car_trunk_light_close)
            return
        }
        binding.ivFlashAlarm.visibility = View.VISIBLE
        binding.carTrunkDoorHeight.visibility = View.VISIBLE
        binding.carTrunkDoorHeight.setText(R.string.car_trunk_light_open)
        animationFlashAlarm.start(false, 50, object : AnimationDrawable.AnimationLisenter {
            override fun startAnimation() {
                binding.arcSeekBar.visibility = View.GONE
            }

            override fun endAnimation() {
                binding.ivFlashAlarm.visibility = View.GONE
                if (binding.accessSternElectricSw.isChecked) {
                    binding.arcSeekBar.visibility = View.VISIBLE
                } else {
                    binding.arcSeekBar.visibility = View.GONE
                }
            }
        })
    }

    private fun doElectricTrunkFollowing(status: Boolean) {
        if (!status) {
            animationCloseDoor.start(false, 50, null)
            binding.carTrunkDoorHeight.visibility = View.GONE
            binding.intelligenceInto.visibility = View.GONE
            binding.arcSeekBar.visibility = View.GONE
            return
        }
        animationOpenDoor.start(false, 50, object : AnimationDrawable.AnimationLisenter {
            override fun startAnimation() {
            }

            override fun endAnimation() {
                if (binding.accessSternElectricSw.isChecked) {
                    binding.arcSeekBar.visibility = View.VISIBLE
                } else {
                    binding.arcSeekBar.visibility = View.GONE
                }
            }
        })
        binding.carTrunkDoorHeight.visibility = View.VISIBLE
    }


    private fun addSwitchLiveDataListener() {
        viewModel.electricFunction.observe(this) {
            doUpdateSwitch(SwitchNode.AS_STERN_ELECTRIC, it)
            doElectricTrunkFollowing(it)
        }
        viewModel.lightAlarmFunction.observe(this) {
            doUpdateSwitch(SwitchNode.STERN_LIGHT_ALARM, it)
        }
        viewModel.audioAlarmFunction.observe(this) {
            doUpdateSwitch(SwitchNode.STERN_AUDIO_ALARM, it)
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.AS_STERN_ELECTRIC, viewModel.electricFunction)
        initSwitchOption(SwitchNode.STERN_LIGHT_ALARM, viewModel.lightAlarmFunction)
        initSwitchOption(SwitchNode.STERN_AUDIO_ALARM, viewModel.audioAlarmFunction)
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.AS_STERN_ELECTRIC -> {
                binding.accessSternElectricSw
            }
            SwitchNode.STERN_LIGHT_ALARM -> {
                binding.accessSternLightAlarmSw
            }
            SwitchNode.STERN_AUDIO_ALARM -> {
                binding.accessSternAudioAlarmSw
            }
            else -> null
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    override fun onPostChecked(button: SwitchButton, status: Boolean) {
        checkDisableOtherDiv(button, status)
    }

    override fun findRadioByNode(node: RadioNode): TabControlView? {
        return when (node) {
            RadioNode.STERN_SMART_ENTER -> binding.accessSternSmartEnterRadio
            else -> null
        }
    }

    override fun getRadioManager(): IRadioManager {
        return manager
    }

    private fun checkDisableOtherDiv(swb: SwitchButton, status: Boolean) {
        if (swb == binding.accessSternElectricSw) {
            binding.arcSeekBar.let {
                it.isEnabledDrag = status
                it.alpha = if (status) 1.0f else 0.7f
            }
            val childCount = binding.layoutContent.childCount
            val intRange = 0 until childCount
            intRange.forEach {
                val childAt = binding.layoutContent.getChildAt(it)
                if (null != childAt && childAt != binding.carTrunkElectricFunction) {
                    childAt.alpha = if (status) 1.0f else 0.7f
                    updateViewEnable(childAt, status)
                }
            }
        }
    }

    private fun updateViewEnable(view: View?, status: Boolean) {
        if (null == view) {
            return
        }
        if (view is SwitchButton) {
            view.isEnabled = status
            return
        }
        if (view is AppCompatImageView) {
            view.isEnabled = status
            return
        }
        if (view is TabControlView) {
            view.updateEnable(status)
            return
        }
        if (view is ViewGroup) {
            val childCount = view.childCount
            val intRange = 0 until childCount
            intRange.forEach { updateViewEnable(view.getChildAt(it), status) }
        }
    }

    override fun onStartTrackingTouch(isCanDrag: Boolean) {

    }

    override fun onProgressChanged(progress: Float, max: Float, fromUser: Boolean) {
        animationOpenDoor.progressStart(progress.toInt())
    }

    override fun onStopTrackingTouch(isCanDrag: Boolean) {

    }

    override fun onSingleTapUp() {

    }

    private fun showPopWindow(id:Int, view:View){
        val popWindow = PopWindow(activity,R.layout.pop_window)
        var text: TextView = popWindow.findViewById(R.id.content) as TextView
        text.text = resources.getString(id)
        popWindow.showDown(view)
    }

}