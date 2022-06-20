package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.manager.cabin.SafeManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CabinSafeFragmentBinding
import com.chinatsp.vehicle.settings.vm.cabin.SafeViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/21 15:23
 * @desc   :
 * @version: 1.0
 */
@AndroidEntryPoint
class CabinSafeFragment: BaseFragment<SafeViewModel, CabinSafeFragmentBinding>() {


    private val manager: SafeManager
        get() = SafeManager.instance

    override fun getLayoutId(): Int {
        return R.layout.cabin_safe_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()
    }

    private fun addSwitchLiveDataListener() {
        viewModel.fortifyToneFunction.observe(this) {
            doUpdateSwitch(SwitchNode.DRIVE_SAFE_FORTIFY_SOUND, it)
        }
        viewModel.videoModeFunction.observe(this) {
            doUpdateSwitch(SwitchNode.DRIVE_SAFE_VIDEO_PLAYING, it)
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.DRIVE_SAFE_FORTIFY_SOUND, viewModel.fortifyToneFunction,)
        initSwitchOption(SwitchNode.DRIVE_SAFE_VIDEO_PLAYING, viewModel.videoModeFunction,)
    }

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: false
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.DRIVE_SAFE_FORTIFY_SOUND -> binding.cabinSafeFortifySwitch
            SwitchNode.DRIVE_SAFE_VIDEO_PLAYING -> binding.cabinSafeMovieSwitch
            else -> null
        }
        swb?.let {
            doUpdateSwitch(it, status, immediately)
        }
    }

    private fun doUpdateSwitch(swb: SwitchButton, status: Boolean, immediately: Boolean = false) {
        if (!immediately) {
            swb.setCheckedNoEvent(status)
        } else {
            swb.setCheckedImmediatelyNoEvent(status)
        }
    }

    private fun setSwitchListener() {
        binding.cabinSafeFortifySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.DRIVE_SAFE_FORTIFY_SOUND, buttonView, isChecked)
        }
        binding.cabinSafeMovieSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.DRIVE_SAFE_VIDEO_PLAYING, buttonView, isChecked)
        }
    }

    private fun doUpdateSwitchOption(node: SwitchNode, button: CompoundButton, status: Boolean) {
        val result = manager.doSetSwitchOption(node, status)
        if (!result && button is SwitchButton) {
            button.setCheckedImmediatelyNoEvent(!status)
        }
    }
}