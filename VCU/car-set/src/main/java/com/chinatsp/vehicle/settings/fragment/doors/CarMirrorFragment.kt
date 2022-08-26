package com.chinatsp.vehicle.settings.fragment.doors

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.access.BackMirrorManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CarMirrorFragmentBinding
import com.chinatsp.vehicle.settings.fragment.doors.dialog.AngleDialogFragment
import com.chinatsp.vehicle.settings.vm.accress.MirrorViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint
import com.chinatsp.vehicle.controller.annotation.Level
@AndroidEntryPoint
class CarMirrorFragment : BaseFragment<MirrorViewModel, CarMirrorFragmentBinding>() {

    private val manager: BackMirrorManager
        get() = BackMirrorManager.instance

    override fun getLayoutId(): Int {
        return R.layout.car_mirror_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initViewsDisplay()
        initSwitchOption()
        setSwitchListener()
        addSwitchLiveDataListener()
        setCheckedChangeListener()
    }

    private fun initViewsDisplay() {
        if (VcuUtils.isCareLevel(Level.LEVEL3)) {
            binding.reverseAngle.visibility = View.GONE
            binding.line2.visibility = View.GONE
        }
    }

    private fun setSwitchListener() {
        binding.accessMirrorMirrorFoldSw.let {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                val node = SwitchNode.BACK_MIRROR_FOLD
                val result = manager.doSetSwitchOption(node, isChecked)
                takeUnless { result }?.doUpdateSwitch(buttonView as SwitchButton, !isChecked, false)
                if (buttonView.isChecked) {
                    binding.rearviewMirror.setText(R.string.car_mirror_automatic_folding_open)
                } else {
                    binding.rearviewMirror.setText(R.string.car_mirror_automatic_folding_close)
                }
            }
        }
        binding.backMirrorDownSwitch.let {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                val result = manager.doSetSwitchOption(SwitchNode.BACK_MIRROR_DOWN, isChecked)
                takeUnless { result }?.doUpdateSwitch(buttonView as SwitchButton, !isChecked, false)
            }
        }
    }

    private fun addSwitchLiveDataListener() {
        viewModel.mirrorFoldFunction.observe(this) {
            doUpdateSwitch(SwitchNode.BACK_MIRROR_FOLD, it!!)
        }
        viewModel.mirrorDownFunction.observe(this) {
            doUpdateSwitch(SwitchNode.BACK_MIRROR_DOWN, it!!)
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.BACK_MIRROR_FOLD, viewModel.mirrorFoldFunction)
        initSwitchOption(SwitchNode.BACK_MIRROR_DOWN, viewModel.mirrorDownFunction)
    }

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: false
        if (node == SwitchNode.BACK_MIRROR_FOLD) {
            if (status) {
                binding.rearviewMirror.setText(R.string.car_mirror_automatic_folding_open)
            } else {
                binding.rearviewMirror.setText(R.string.car_mirror_automatic_folding_close)
            }
        }
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.BACK_MIRROR_FOLD -> binding.accessMirrorMirrorFoldSw
            SwitchNode.BACK_MIRROR_DOWN -> binding.backMirrorDownSwitch
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

    private fun setCheckedChangeListener() {
        binding.alterReverseAngle.setOnClickListener {
            showReverseAngleFragment()
        }
    }

    private fun showReverseAngleFragment() {
        val fragment = AngleDialogFragment()
        activity?.supportFragmentManager?.let { it ->
            fragment.show(it, fragment.javaClass.simpleName)
        }
    }
}