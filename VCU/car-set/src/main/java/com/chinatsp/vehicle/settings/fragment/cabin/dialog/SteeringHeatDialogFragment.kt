package com.chinatsp.vehicle.settings.fragment.cabin.dialog

import android.os.Bundle
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.cabin.WheelManager
import com.chinatsp.settinglib.optios.Progress
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.ISwitchAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.Toast
import com.chinatsp.vehicle.settings.databinding.SteeringHeatingDialogFragmentBinding
import com.chinatsp.vehicle.settings.vm.cabin.SteeringViewModel
import com.common.library.frame.base.BaseDialogFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SteeringHeatDialogFragment :
    BaseDialogFragment<SteeringViewModel, SteeringHeatingDialogFragmentBinding>(), ISwitchAction {

    val manager: WheelManager by lazy {
        WheelManager.instance
    }

    override fun getLayoutId(): Int {
        return R.layout.steering_heating_dialog_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setBackListener()

        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()

        initRangeProgress()
        addRangeLiveDataListener()
        setRangeListener()
    }

    private fun setRangeListener() {
        binding.steeringHeatingStartTemperatureSeekBar.setOnSeekBarListener { _, value ->
            manager.doSetVolume(Progress.STEERING_ONSET_TEMPERATURE, value)
        }
    }

    private fun addRangeLiveDataListener() {
        viewModel.sillTemp.observe(this) {
            binding.steeringHeatingStartTemperatureSeekBar.setValueNoEvent(it.pos)
        }
    }

    private fun initRangeProgress() {
        binding.steeringHeatingStartTemperatureSeekBar.apply {
            val volume = viewModel.sillTemp.value
            volume?.let {
                this.min = it.min
                this.max = it.max
                this.setValueNoEvent(it.pos)
            }
        }
    }

    private fun setBackListener() {
        binding.closeDialog.setOnClickListener {
            this.dismiss()
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.DRIVE_WHEEL_AUTO_HEAT, viewModel.swhFunction)
//        checkDisableOtherDiv(binding.steeringAutomaticHeatingSwitch.isChecked, binding.container)
        updateEnable(binding.steeringHeatingStartTemperatureSeekBar,
            true,
            binding.steeringAutomaticHeatingSwitch.isChecked)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.swhFunction.observe(this) {
            doUpdateSwitch(SwitchNode.DRIVE_WHEEL_AUTO_HEAT, it)
//            checkDisableOtherDiv(it.get(), binding.container)
            updateEnable(binding.steeringHeatingStartTemperatureSeekBar, true, it.get())

        }
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.DRIVE_WHEEL_AUTO_HEAT -> binding.steeringAutomaticHeatingSwitch
            else -> null
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    private fun setSwitchListener() {
        binding.steeringAutomaticHeatingSwitch.let {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                doUpdateSwitchOption(SwitchNode.DRIVE_WHEEL_AUTO_HEAT, buttonView, isChecked)
//                checkDisableOtherDiv(it.isChecked, binding.container)
                updateEnable(binding.steeringHeatingStartTemperatureSeekBar, true, it.isChecked)
                if(!isChecked){
                    Toast.showToast(context, getString(R.string.cabin_wheel_automatic_heating_close), true)
                }
            }
        }
    }

//    private fun checkDisableOtherDiv(status: Boolean, view: View) {
//        if (view is ViewGroup) {
//            for (index in 0 until view.childCount) {
//                val child = view.getChildAt(index)
//                checkDisableOtherDiv(status, child)
//            }
//        } else {
//            view.alpha = if (status) 1.0f else 0.6f
//            view.isEnabled = status
//        }
//    }

//    private fun updateViewEnable(view: View?, status: Boolean) {
//        if (null == view) {
//            return
//        }
//        if (view is SwitchButton) {
//            view.isEnabled = status
//            return
//        }
//        if (view is AppCompatImageView) {
//            view.isEnabled = status
//            return
//        }
//        if (view is TabControlView) {
//            view.updateEnable(status)
//            return
//        }
//        if (view is VSeekBar) {
//            view.isEnabled = status
//            return
//        }
//        if (view is ViewGroup) {
//            val childCount = view.childCount
//            val intRange = 0 until childCount
//            intRange.forEach { updateViewEnable(view.getChildAt(it), status) }
//        }
//    }

}

