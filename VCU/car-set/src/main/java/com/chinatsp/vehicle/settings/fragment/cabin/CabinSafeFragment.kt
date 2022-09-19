package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.cabin.SafeManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.ISwitchAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CabinSafeFragmentBinding
import com.chinatsp.vehicle.settings.vm.cabin.SafeViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.popupwindow.PopWindow
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/21 15:23
 * @desc   :
 * @version: 1.0
 */
@AndroidEntryPoint
class CabinSafeFragment : BaseFragment<SafeViewModel, CabinSafeFragmentBinding>(), ISwitchAction {


    private val manager: SafeManager
        get() = SafeManager.instance

    override fun getLayoutId(): Int {
        return R.layout.cabin_safe_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()
        initDetailsClickListener()
    }

    private fun addSwitchLiveDataListener() {
        viewModel.fortifyToneFunction.observe(this) {
            doUpdateSwitch(SwitchNode.DRIVE_SAFE_FORTIFY_SOUND, it)
        }
        viewModel.videoModeFunction.observe(this) {
            doUpdateSwitch(SwitchNode.DRIVE_SAFE_VIDEO_PLAYING, it)
        }
    }

    private fun initDetailsClickListener() {
        binding.cabinAcAutoWindsDetails.setOnClickListener {
            showPopWindow(R.string.cabin_safe_video_safe_mode_content, it)
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.DRIVE_SAFE_FORTIFY_SOUND, viewModel.fortifyToneFunction)
        initSwitchOption(SwitchNode.DRIVE_SAFE_VIDEO_PLAYING, viewModel.videoModeFunction)
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.DRIVE_SAFE_FORTIFY_SOUND -> binding.cabinSafeFortifySwitch
            SwitchNode.DRIVE_SAFE_VIDEO_PLAYING -> binding.cabinSafeMovieSwitch
            else -> null
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    private fun setSwitchListener() {
        binding.cabinSafeFortifySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.DRIVE_SAFE_FORTIFY_SOUND, buttonView, isChecked)
        }
        binding.cabinSafeMovieSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.DRIVE_SAFE_VIDEO_PLAYING, buttonView, isChecked)
        }
    }

    private fun showPopWindow(id: Int, view: View) {
        val popWindow = PopWindow(activity,
            R.layout.pop_window,
            activity?.let { AppCompatResources.getDrawable(it, R.drawable.popup_bg_qipao172_6) })
        var text: TextView = popWindow.findViewById(R.id.content) as TextView
        text.text = resources.getString(id)
        popWindow.showDownLift(view, 30, -80)
    }

}