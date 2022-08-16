package com.chinatsp.vehicle.settings.fragment.drive

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.adas.CombineManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.HintHold
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveTrafficFragmentBinding
import com.chinatsp.vehicle.settings.fragment.drive.dialog.DetailsDialogFragment
import com.chinatsp.vehicle.settings.vm.adas.CombineViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveTrafficFragment : BaseFragment<CombineViewModel, DriveTrafficFragmentBinding>() {

    private val manager: ISwitchManager
        get() = CombineManager.instance

    override fun getLayoutId(): Int {
        return R.layout.drive_traffic_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initSwitchOption()
        initVideoListener()
        addSwitchLiveDataListener()
        setSwitchListener()
        initDetailsClickListener()
    }

    private fun initDetailsClickListener() {
        binding.driveSla.setOnClickListener {
            updateHintMessage(R.string.drive_sla_title, R.string.drive_sla_content)
        }
    }

    private fun updateHintMessage(title: Int, content: Int) {
        HintHold.setTitle(title)
        HintHold.setContent(content)
        val fragment = DetailsDialogFragment()
        activity?.supportFragmentManager?.let {
            fragment.show(it, fragment.javaClass.simpleName)
        }
    }

    private fun initVideoListener() {
        val uri = "android.resource://" + activity?.packageName + "/" + R.raw.video_sla
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

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.ADAS_TSR, viewModel.slaValue)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.slaValue.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_TSR, it)
        }
    }

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: node.default
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.ADAS_TSR -> binding.adasTrafficSlaSwitch
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
        binding.adasTrafficSlaSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                //binding.videoImage.visibility = View.GONE
                val uri = "android.resource://" + activity?.packageName + "/" + R.raw.video_sla
                binding.video.setVideoURI(Uri.parse(uri));
                binding.video.start()
            }else{
                dynamicEffect()
            }
            doUpdateSwitchOption(SwitchNode.ADAS_TSR, buttonView, isChecked)
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
        if (binding.adasTrafficSlaSwitch.isChecked) {
            binding.videoImage.setImageDrawable(activity?.let { ContextCompat.getDrawable(it, R.drawable.ic_traffic_signs) })
        } else {
            binding.videoImage.setImageDrawable(activity?.let { ContextCompat.getDrawable(it, R.drawable.intelligent_cruise) })
        }
    }

}