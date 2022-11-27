package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.cabin.SafeManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.IRoute
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

    private val map: HashMap<Int, View> = HashMap()

    override fun getLayoutId(): Int {
        return R.layout.cabin_safe_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()
        initDetailsClickListener()
        initClickView()
        initRouteListener()
    }

    private fun initClickView() {
        map[1] = binding.cabinAcAutoWindsDetails
    }

    private fun obtainRouter(): IRoute? {
        return if (activity is IRoute) activity as IRoute else null
    }

    private fun initRouteListener() {
        val router = obtainRouter()
        if (null != router) {
            val liveData = router.obtainLevelLiveData()
            liveData.observe(this) {
                it.takeIf { it.valid && it.uid == pid }?.let { level1 ->
                    level1.cnode?.takeIf { child -> child.valid && child.uid == uid }
                        .let { level2 ->
                            level2?.cnode?.let { lv3Node ->
                                map[lv3Node.uid]?.run { onViewClick(this, lv3Node.uid, true) }
                            }
                        }
                }
            }
        }
    }

    private fun onViewClick(view: View, clickUid: Int, frank: Boolean) {
        onViewClick(view)
        obtainRouter()?.resetLevelRouter(pid, uid, clickUid)
    }

    private fun onViewClick(it: View) {
        when (it) {
            binding.cabinAcAutoWindsDetails -> {
                showPopWindow(R.string.cabin_safe_video_safe_mode_content, it)
            }
        }
    }

    private fun addSwitchLiveDataListener() {
//        viewModel.fortifyHint.observe(this) {
//            doUpdateSwitch(SwitchNode.DRIVE_SAFE_FORTIFY_SOUND, it)
//            updateSwitchEnable(SwitchNode.DRIVE_SAFE_FORTIFY_SOUND)
//        }
        viewModel.lockFailedHint.observe(this) {
            doUpdateSwitch(SwitchNode.LOCK_FAILED_AUDIO_HINT, it)
        }
        viewModel.lockSuccessHint.observe(this) {
            doUpdateSwitch(SwitchNode.LOCK_SUCCESS_AUDIO_HINT, it)
        }
        viewModel.videoModeFunction.observe(this) {
            doUpdateSwitch(SwitchNode.DRIVE_SAFE_VIDEO_PLAYING, it)
        }
    }

    private fun initDetailsClickListener() {
        binding.cabinAcAutoWindsDetails.setOnClickListener(this::onViewClick)
    }

    private fun initSwitchOption() {
//        initSwitchOption(SwitchNode.DRIVE_SAFE_FORTIFY_SOUND, viewModel.fortifyToneFunction)
        initSwitchOption(SwitchNode.DRIVE_SAFE_VIDEO_PLAYING, viewModel.videoModeFunction)
        initSwitchOption(SwitchNode.LOCK_FAILED_AUDIO_HINT, viewModel.lockFailedHint)
        initSwitchOption(SwitchNode.LOCK_SUCCESS_AUDIO_HINT, viewModel.lockSuccessHint)

//        updateSwitchEnable(SwitchNode.DRIVE_SAFE_FORTIFY_SOUND)
        updateSwitchEnable(SwitchNode.LOCK_FAILED_AUDIO_HINT)
        updateSwitchEnable(SwitchNode.LOCK_SUCCESS_AUDIO_HINT)
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
//            SwitchNode.DRIVE_SAFE_FORTIFY_SOUND -> binding.cabinSafeFortifySwitch
            SwitchNode.DRIVE_SAFE_VIDEO_PLAYING -> binding.cabinSafeMovieSwitch
            SwitchNode.LOCK_FAILED_AUDIO_HINT -> binding.lockFailedHintSwitch
            SwitchNode.LOCK_SUCCESS_AUDIO_HINT -> binding.lockSuccessHintSwitch
            else -> null
        }
    }

    override fun obtainActiveByNode(node: SwitchNode): Boolean {
        return when (node) {
//            SwitchNode.DRIVE_SAFE_FORTIFY_SOUND -> viewModel.fortifyHint.value?.enable() ?: false
            SwitchNode.LOCK_FAILED_AUDIO_HINT -> viewModel.lockFailedHint.value?.enable() ?: false
            SwitchNode.LOCK_SUCCESS_AUDIO_HINT -> viewModel.lockSuccessHint.value?.enable() ?: false
            SwitchNode.DRIVE_SAFE_VIDEO_PLAYING -> true
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    private fun setSwitchListener() {
//        binding.cabinSafeFortifySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
//            doUpdateSwitchOption(SwitchNode.DRIVE_SAFE_FORTIFY_SOUND, buttonView, isChecked)
//            updateSwitchEnable(SwitchNode.DRIVE_SAFE_FORTIFY_SOUND)
//        }
        binding.lockFailedHintSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.LOCK_FAILED_AUDIO_HINT, buttonView, isChecked)
            updateSwitchEnable(SwitchNode.LOCK_FAILED_AUDIO_HINT)
        }
        binding.lockSuccessHintSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.LOCK_SUCCESS_AUDIO_HINT, buttonView, isChecked)
            updateSwitchEnable(SwitchNode.LOCK_SUCCESS_AUDIO_HINT)
        }
        binding.cabinSafeMovieSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.DRIVE_SAFE_VIDEO_PLAYING, buttonView, isChecked)
        }
    }

    private fun showPopWindow(id: Int, view: View) {
        val popWindow = PopWindow(activity,
            R.layout.pop_window,
            activity?.let { AppCompatResources.getDrawable(it, R.drawable.popup_bg_qipao172_6) })
        val text: TextView = popWindow.findViewById(R.id.content) as TextView
        text.text = resources.getString(id)
        popWindow.showDownLift(view, 30, -80)
    }

}