package com.chinatsp.vehicle.settings.fragment.drive

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.adas.CombineManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveLightingFragmentBinding
import com.chinatsp.vehicle.settings.vm.adas.CombineViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveLightingFragment : BaseFragment<CombineViewModel, DriveLightingFragmentBinding>() {


    private val manager: ISwitchManager
        get() = CombineManager.instance

    override fun getLayoutId(): Int {
        return R.layout.drive_lighting_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initSwitchOption()
        initVideoListener()
        addSwitchLiveDataListener()
        setSwitchListener()
    }

    private fun initVideoListener() {
        val uri = "android.resource://" + activity?.packageName + "/" + R.raw.video_hma
        binding.video.setVideoURI(Uri.parse(uri));
        binding.video.setOnCompletionListener {
            dynamicEffect()
        }
        binding.video.setOnErrorListener { _, _, _ ->
            dynamicEffect()
            true
        }
    }
    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.ADAS_HMA, viewModel.hmaValue)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.hmaValue.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_HMA, it)
        }
    }

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: node.default
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.ADAS_HMA -> binding.adasLightHmaSwitch
            else -> null
        }
        takeIf { null != swb }?.doUpdateSwitch(swb!!, status, immediately)
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
        binding.adasLightHmaSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.videoImage.visibility = View.GONE
                binding.video.start()
            }else{
                dynamicEffect()
            }
            doUpdateSwitchOption(SwitchNode.ADAS_HMA, buttonView, isChecked)
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
        if (binding.adasLightHmaSwitch.isChecked) {
            binding.videoImage.setImageDrawable(activity?.let { ContextCompat.getDrawable(it, R.drawable.ic_light_auxiliary) })
        } else {
            binding.videoImage.setImageDrawable(activity?.let { ContextCompat.getDrawable(it, R.drawable.intelligent_cruise) })
        }
    }
}