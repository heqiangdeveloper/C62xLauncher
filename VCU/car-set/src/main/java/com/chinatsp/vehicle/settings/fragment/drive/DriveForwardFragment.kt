package com.chinatsp.vehicle.settings.fragment.drive

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.manager.adas.ForwardManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveForwardFragmentBinding
import com.chinatsp.vehicle.settings.fragment.drive.dialog.CloseBrakeDialogFragment
import com.chinatsp.vehicle.settings.vm.adas.ForwardViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveForwardFragment : BaseFragment<ForwardViewModel, DriveForwardFragmentBinding>() {

    private val manager: ForwardManager
        get() = ForwardManager.instance

    override fun getLayoutId(): Int {
        return R.layout.drive_forward_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initVideoListener()
        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()
    }

    private fun initVideoListener() {
        val uri = "android.resource://" + activity?.packageName + "/" + R.raw.video_fcw
        binding.video.setVideoURI(Uri.parse(uri));
        binding.video.setOnCompletionListener {
            dynamicEffect()
        }
        binding.video.setOnErrorListener { _, _, _ ->
            dynamicEffect()
            true
        }
        binding.video.setOnPreparedListener{
            it.setOnInfoListener { _, _, _ ->
                binding.video.setBackgroundColor(Color.TRANSPARENT);
                binding.videoImage.visibility = View.GONE
                true
            }
        }
    }

    private fun addSwitchLiveDataListener() {
        viewModel.fcwFunction.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_FCW, it)
        }
        viewModel.aebFunction.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_AEB, it)
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.ADAS_FCW, viewModel.fcwFunction)
        initSwitchOption(SwitchNode.ADAS_AEB, viewModel.aebFunction)
    }

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: false
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.ADAS_FCW -> binding.adasForwardFcwSwitch
            SwitchNode.ADAS_AEB -> binding.adasForwardAebSwitch
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
        dynamicEffect()
    }

    private fun setSwitchListener() {
        binding.adasForwardFcwSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //binding.videoImage.visibility = View.GONE
                val uri = "android.resource://" + activity?.packageName + "/" + R.raw.video_fcw
                binding.video.setVideoURI(Uri.parse(uri));
                binding.video.start()
            } else {
                dynamicEffect()
            }
            doUpdateSwitchOption(SwitchNode.ADAS_FCW, buttonView, isChecked)
        }
        binding.adasForwardAebSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //binding.videoImage.visibility = View.GONE
                val url = "android.resource://" + activity?.packageName + "/" + R.raw.video_abe
                binding.video.setVideoURI(Uri.parse(url));
                binding.video.start()
            } else {
                dynamicEffect()
                val fragment = CloseBrakeDialogFragment()
                activity?.supportFragmentManager?.let {
                    fragment.show(it, fragment.javaClass.simpleName)
                }
            }
            doUpdateSwitchOption(SwitchNode.ADAS_AEB, buttonView, isChecked)
        }
    }

    private fun doUpdateSwitchOption(node: SwitchNode, button: CompoundButton, status: Boolean) {
        val result = manager.doSetSwitchOption(node, status)
        if (!result && button is SwitchButton) {
            button.setCheckedImmediatelyNoEvent(!status)
        }
    }

    private fun dynamicEffect() {
        binding.videoImage.visibility = View.VISIBLE
        if (binding.adasForwardFcwSwitch.isChecked && binding.adasForwardAebSwitch.isChecked) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_emergency_braking_1
                )
            })
        } else if (!binding.adasForwardFcwSwitch.isChecked && binding.adasForwardAebSwitch.isChecked) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_emergency_braking
                )
            })
        } else if (binding.adasForwardFcwSwitch.isChecked && !binding.adasForwardAebSwitch.isChecked) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_prior_collisio
                )
            })
        } else if (!binding.adasForwardFcwSwitch.isChecked && !binding.adasForwardAebSwitch.isChecked) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.intelligent_cruise
                )
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.video.pause()
        binding.video.stopPlayback()
    }
}