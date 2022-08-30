package com.chinatsp.vehicle.settings.fragment.drive

import android.graphics.Color
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.chinatsp.settinglib.manager.IRadioManager
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.adas.LaneManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.HintHold
import com.chinatsp.vehicle.settings.IOptionAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveLaneFragmentBinding
import com.chinatsp.vehicle.settings.fragment.drive.dialog.DetailsDialogFragment
import com.chinatsp.vehicle.settings.vm.adas.LaneViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveLaneFragment : BaseFragment<LaneViewModel, DriveLaneFragmentBinding>(), IOptionAction {

    private val manager: LaneManager
        get() = LaneManager.instance

    override fun getLayoutId(): Int {
        return R.layout.drive_lane_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initVideoListener()

        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()

        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()

        initDetailsClickListener()
    }

    private fun initDetailsClickListener() {
        binding.laneAssistSystemDetails.setOnClickListener {
            updateHintMessage(R.string.drive_Lane_assist_system, R.string.lane_assist_details)
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
        val uri = "android.resource://" + activity?.packageName + "/" + R.raw.video_auxiliary_system
        binding.video.setZOrderOnTop(true)
        binding.video.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE)
        binding.video.setVideoURI(Uri.parse(uri))
        binding.video.setOnCompletionListener {
            dynamicEffect()
        }
        binding.video.setOnErrorListener { _, _, _ ->
            dynamicEffect()
            true
        }
        binding.video.setOnPreparedListener {
            it.setOnInfoListener { _, _, _ ->
                binding.video.setBackgroundColor(Color.TRANSPARENT)
                binding.videoImage.visibility = View.GONE
                true
            }
        }
    }

    private fun initRadioOption() {
        initRadioOption(RadioNode.ADAS_LANE_ASSIST_MODE, viewModel.laneAssistMode)
        initRadioOption(RadioNode.ADAS_LDW_STYLE, viewModel.ldwStyle)
        initRadioOption(RadioNode.ADAS_LDW_SENSITIVITY, viewModel.ldwSensitivity)
    }

    private fun addRadioLiveDataListener() {
        viewModel.laneAssistMode.observe(this) {
            doUpdateRadio(RadioNode.ADAS_LANE_ASSIST_MODE, it, false)
        }
        viewModel.ldwStyle.observe(this) {
            doUpdateRadio(RadioNode.ADAS_LDW_STYLE, it, false)
        }
        viewModel.ldwSensitivity.observe(this) {
            doUpdateRadio(RadioNode.ADAS_LDW_SENSITIVITY, it, false)
        }
    }

    private fun setRadioListener() {
        binding.adasLaneLaneAssistRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.ADAS_LANE_ASSIST_MODE, value, viewModel.laneAssistMode, it)
            }
        }
        binding.adasLaneLdwStyleRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(RadioNode.ADAS_LDW_STYLE, value, viewModel.ldwStyle, it)
            }
        }
        binding.adasLaneLdwSensitivityRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                doUpdateRadio(
                    RadioNode.ADAS_LDW_SENSITIVITY, value, viewModel.ldwSensitivity, it
                )
            }
        }
    }


    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.ADAS_LANE_ASSIST, viewModel.laneAssistFunction)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.laneAssistFunction.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_LANE_ASSIST, it)
        }
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.ADAS_LANE_ASSIST -> binding.adasLaneLaneAssistSwitch
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
            RadioNode.ADAS_LANE_ASSIST_MODE -> {
//                binding.adasLaneLaneAssistRadio.getChildAt(0).visibility = View.GONE
                binding.adasLaneLaneAssistRadio
            }
            RadioNode.ADAS_LDW_STYLE -> binding.adasLaneLdwStyleRadio
            RadioNode.ADAS_LDW_SENSITIVITY -> binding.adasLaneLdwSensitivityRadio
            else -> null
        }
    }

    override fun getRadioManager(): IRadioManager {
        return manager
    }

    private fun setSwitchListener() {
        binding.adasLaneLaneAssistSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //binding.videoImage.visibility = View.GONE
                val uri =
                    "android.resource://" + activity?.packageName + "/" + R.raw.video_auxiliary_system
                binding.video.setVideoURI(Uri.parse(uri));
                binding.video.start()
            } else {
                dynamicEffect()
            }
            doUpdateSwitchOption(SwitchNode.ADAS_LANE_ASSIST, buttonView, isChecked)
        }
    }

    private fun dynamicEffect() {
        binding.videoImage.visibility = View.VISIBLE
        if (binding.adasLaneLaneAssistSwitch.isChecked) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lane_assist
                )
            })
        } else {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.intelligent_cruise
                )
            })
        }
    }
}

