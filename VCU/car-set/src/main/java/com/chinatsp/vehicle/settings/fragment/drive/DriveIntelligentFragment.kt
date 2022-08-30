package com.chinatsp.vehicle.settings.fragment.drive

import android.graphics.Color
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.chinatsp.settinglib.manager.IRadioManager
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.adas.CruiseManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.HintHold
import com.chinatsp.vehicle.settings.IOptionAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveIntelligentFragmentBinding
import com.chinatsp.vehicle.settings.fragment.drive.dialog.DetailsDialogFragment
import com.chinatsp.vehicle.settings.vm.adas.CruiseViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DriveIntelligentFragment : BaseFragment<CruiseViewModel, DriveIntelligentFragmentBinding>(),
    IOptionAction {

    private val manager: CruiseManager
        get() = CruiseManager.instance

    override fun getLayoutId(): Int {
        return R.layout.drive_intelligent_fragment
    }


    override fun initData(savedInstanceState: Bundle?) {
        initSwitchOption()
        initVideoListener()
        addSwitchLiveDataListener()
        setSwitchListener()

        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()

        initDetailsClickListener()
    }

    private fun initDetailsClickListener() {
        binding.cruiseAssistantDetails.setOnClickListener {
            val fragment = DetailsDialogFragment()
            HintHold.setTitle(R.string.drive_intelligent_cruise_assistant)
            HintHold.setContent(R.string.iacc_details)
            activity?.supportFragmentManager?.let {
                fragment.show(it, fragment.javaClass.simpleName)
            }
        }
    }

    private fun initRadioOption() {
        initRadioOption(RadioNode.ADAS_LIMBER_LEAVE, viewModel.limberLeaveRadio)
    }

    private fun initVideoListener() {
        val uri = "android.resource://" + activity?.packageName + "/" + R.raw.video_acc
        binding.video.setZOrderOnTop(true)
        binding.video.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE)
        binding.video.setVideoURI(Uri.parse(uri));
        binding.video.setOnCompletionListener {
            binding.video.pause()
            binding.video.seekTo(0)
            dynamicEffect()
        }
        binding.video.setOnErrorListener { _, _, _ ->
            dynamicEffect()
            true
        }
        binding.video.setOnPreparedListener {
            it.setOnInfoListener { _, _, _ ->
                binding.video.setBackgroundColor(Color.TRANSPARENT);
                binding.intelligentCruise.visibility = View.GONE
                true
            }
        }
    }

    private fun addRadioLiveDataListener() {
        viewModel.limberLeaveRadio.observe(this) {
            doUpdateRadio(RadioNode.ADAS_LIMBER_LEAVE, it, false)
        }
    }

    private fun setRadioListener() {
        binding.accessCruiseLimberLeaveRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.ADAS_LIMBER_LEAVE, value, viewModel.limberLeaveRadio, it)
            }
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.ADAS_IACC, viewModel.cruiseAssistFunction)
        initSwitchOption(SwitchNode.ADAS_TARGET_PROMPT, viewModel.targetPromptFunction)
//        initSwitchOption(SwitchNode.ADAS_LIMBER_LEAVE, viewModel.limberLeaveFunction)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.cruiseAssistFunction.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_IACC, it)
        }
        viewModel.targetPromptFunction.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_TARGET_PROMPT, it)
        }
//        viewModel.limberLeaveFunction.observe(this) {
//            doUpdateSwitch(SwitchNode.ADAS_LIMBER_LEAVE, it)
//        }
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.ADAS_IACC -> binding.accessCruiseCruiseAssist
            SwitchNode.ADAS_TARGET_PROMPT -> binding.accessCruiseTargetPrompt
//            SwitchNode.ADAS_LIMBER_LEAVE -> binding.accessCruiseLimberLeave
            else -> null
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    override fun onPostChecked(button: SwitchButton, status: Boolean) {
        dynamicEffect()
    }

    override fun findRadioByNode(node: RadioNode): TabControlView? {
        return when (node) {
            RadioNode.ADAS_LIMBER_LEAVE -> {
//                binding.accessCruiseLimberLeaveRadio.getChildAt(0).visibility = View.GONE
                binding.accessCruiseLimberLeaveRadio
            }
            else -> null
        }
    }

    override fun getRadioManager(): IRadioManager {
        return manager
    }

    private fun setSwitchListener() {
        binding.accessCruiseCruiseAssist.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //binding.intelligentCruise.visibility = View.GONE
                val uri = "android.resource://" + activity?.packageName + "/" + R.raw.video_acc
                binding.video.setVideoURI(Uri.parse(uri));
                binding.video.start()
            } else {
                dynamicEffect()
            }
            doUpdateSwitchOption(SwitchNode.ADAS_IACC, buttonView, isChecked)
        }
        binding.accessCruiseTargetPrompt.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_TARGET_PROMPT, buttonView, isChecked)
        }
//        binding.accessCruiseLimberLeave.setOnCheckedChangeListener { buttonView, isChecked ->
//            doUpdateSwitchOption(SwitchNode.ADAS_LIMBER_LEAVE, buttonView, isChecked)
//        }
    }


    private fun dynamicEffect() {
        binding.intelligentCruise.visibility = View.VISIBLE
        if (binding.accessCruiseCruiseAssist.isChecked) {
            binding.intelligentCruise.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.intelligent_cruise_open
                )
            })
        } else {
            binding.intelligentCruise.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.intelligent_cruise
                )
            })
        }
    }
}

