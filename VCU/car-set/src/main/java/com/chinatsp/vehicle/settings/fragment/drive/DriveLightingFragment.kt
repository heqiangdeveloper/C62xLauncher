package com.chinatsp.vehicle.settings.fragment.drive

import android.graphics.Color
import android.media.AudioManager
import android.net.Uri.parse
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.adas.CombineManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.HintHold
import com.chinatsp.vehicle.settings.ISwitchAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveLightingFragmentBinding
import com.chinatsp.vehicle.settings.fragment.drive.dialog.DetailsDialogFragment
import com.chinatsp.vehicle.settings.vm.adas.CombineViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveLightingFragment : BaseFragment<CombineViewModel, DriveLightingFragmentBinding>(),
    ISwitchAction {


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

        initDetailsClickListener()
    }

    private fun initDetailsClickListener() {
        binding.driveHmaDetails.setOnClickListener {
            updateHintMessage(R.string.drive_hma_title, R.string.hma_details)
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
        val uri = "android.resource://" + activity?.packageName + "/" + R.raw.video_hma
        binding.video.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE)
        binding.video.setVideoURI(parse(uri))
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

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.ADAS_HMA, viewModel.hmaValue)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.hmaValue.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_HMA, it)
        }
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.ADAS_HMA -> binding.adasLightHmaSwitch
            else -> null
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    override fun onPostChecked(button: SwitchButton, status: Boolean) {
        dynamicEffect()
    }

    private fun setSwitchListener() {
        binding.adasLightHmaSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //binding.videoImage.visibility = View.GONE
                val uri = "android.resource://" + activity?.packageName + "/" + R.raw.video_hma
                binding.video.setVideoURI(parse(uri))
                binding.video.start()
            } else {
                dynamicEffect()
            }
            doUpdateSwitchOption(SwitchNode.ADAS_HMA, buttonView, isChecked)
        }
    }

    private fun dynamicEffect() {
        binding.videoImage.visibility = View.VISIBLE
        if (binding.adasLightHmaSwitch.isChecked) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_light_auxiliary
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