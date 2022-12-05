package com.chinatsp.vehicle.settings.fragment.drive

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
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
import com.chinatsp.vehicle.settings.IRoute
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveIntelligentFragmentBinding
import com.chinatsp.vehicle.settings.fragment.drive.dialog.DetailsDialogFragment
import com.chinatsp.vehicle.settings.vm.adas.CruiseViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint
/**
 * 智能巡航
 */
@AndroidEntryPoint
class DriveIntelligentFragment : BaseFragment<CruiseViewModel, DriveIntelligentFragmentBinding>(),
    IOptionAction {

    private val manager: CruiseManager
        get() = CruiseManager.instance

    private val uri: Uri by lazy {
        Uri.parse("android.resource://" + activity?.packageName + "/" + R.raw.video_acc)
    }

    private val map: HashMap<Int, View> = HashMap()

    override fun getLayoutId(): Int {
        return R.layout.drive_intelligent_fragment
    }

    private fun showLaneLine(): Drawable? {
        val context = this.context ?: return null
        return ContextCompat.getDrawable(context, R.drawable.intelligent_cruise_open)
    }

    private fun hideLaneLine(): Drawable? {
        val context = this.context ?: return null
        return ContextCompat.getDrawable(context, R.drawable.intelligent_cruise)
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

        initClickView()
        initRouteListener()
        binding.video.setBackgroundColor(Color.TRANSPARENT)
        updateDriveLineDisplay(false)
    }

    private fun initClickView() {
        map[1] = binding.cruiseAssistantDetails
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
            binding.cruiseAssistantDetails -> {
                val fragment = DetailsDialogFragment()
                HintHold.setTitle(R.string.drive_intelligent_cruise_assistant)
                HintHold.setContent(R.string.iacc_details)
                activity?.supportFragmentManager?.let {
                    fragment.show(it, fragment.javaClass.simpleName)
                }
            }
        }
    }

    private fun initDetailsClickListener() {
        binding.cruiseAssistantDetails.setOnClickListener(this::onViewClick)
    }

    private fun initRadioOption() {
        initRadioOption(RadioNode.ADAS_LIMBER_LEAVE, viewModel.limberLeaveRadio)
        updateRadioEnable(RadioNode.ADAS_LIMBER_LEAVE)
    }



    private fun initVideoListener() {
        binding.video.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE)
        binding.video.setVideoURI(uri);
        binding.video.setOnCompletionListener {
            binding.video.seekTo(0)
            updateDriveLineDisplay(false)
        }
        binding.video.setOnErrorListener { _, _, _ ->
            updateDriveLineDisplay(false)
            true
        }
        binding.video.setOnPreparedListener {
           if (this.isAdded && this.isResumed && !this.isDetached) it.start()
        }
    }

    private fun addRadioLiveDataListener() {
        viewModel.limberLeaveRadio.observe(this) {
            doUpdateRadio(RadioNode.ADAS_LIMBER_LEAVE, it, false)
            updateRadioEnable(RadioNode.ADAS_LIMBER_LEAVE)
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
        initSwitchOption(SwitchNode.ADAS_LIMBER_LEAVE, viewModel.limberLeaveFunction)
        updateSwitchEnable(SwitchNode.ADAS_IACC)
        updateSwitchEnable(SwitchNode.ADAS_LIMBER_LEAVE)
        updateRadioEnable(RadioNode.ADAS_LIMBER_LEAVE)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.cruiseAssistFunction.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_IACC, it)
            updateSwitchEnable(SwitchNode.ADAS_IACC)
            updateSwitchEnable(SwitchNode.ADAS_LIMBER_LEAVE)
            updateRadioEnable(RadioNode.ADAS_LIMBER_LEAVE)
        }
        viewModel.limberLeaveFunction.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_LIMBER_LEAVE, it)
            updateSwitchEnable(SwitchNode.ADAS_LIMBER_LEAVE)
        }
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.ADAS_IACC -> binding.accessCruiseCruiseAssist
            SwitchNode.ADAS_LIMBER_LEAVE -> binding.adasForwardLeaveSwitch
            else -> null
        }
    }

    override fun obtainActiveByNode(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.ADAS_IACC -> viewModel.cruiseAssistFunction.value?.enable() ?: false
            SwitchNode.ADAS_LIMBER_LEAVE -> obtainActiveByNode(SwitchNode.ADAS_IACC)
                    && binding.accessCruiseCruiseAssist.isChecked
                    && (viewModel.limberLeaveFunction.value?.enable() ?: false)
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun obtainActiveByNode(node: RadioNode): Boolean {
        return when (node) {
            RadioNode.ADAS_LIMBER_LEAVE -> obtainActiveByNode(SwitchNode.ADAS_IACC)
                    && binding.accessCruiseCruiseAssist.isChecked
                    && (viewModel.limberLeaveRadio.value?.enable() ?: false)
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    override fun onPostChecked(button: SwitchButton, status: Boolean) {
//        updateDriveLineDisplay(true)
    }

    override fun findRadioByNode(node: RadioNode): TabControlView? {
        return when (node) {
            RadioNode.ADAS_LIMBER_LEAVE -> {
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
            doUpdateSwitchOption(SwitchNode.ADAS_IACC, buttonView, isChecked)
            updateDriveLineDisplay(true)
            updateSwitchEnable(SwitchNode.ADAS_IACC)
            updateSwitchEnable(SwitchNode.ADAS_LIMBER_LEAVE)
            updateRadioEnable(RadioNode.ADAS_LIMBER_LEAVE)
        }
        binding.adasForwardLeaveSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_LIMBER_LEAVE, buttonView, isChecked)
        }
    }

    private fun updateDriveLineDisplay(anim: Boolean) {
        val expect = binding.accessCruiseCruiseAssist.isChecked
        val isAnimation = expect && anim
        if (isAnimation) binding.video.setVideoURI(uri); else binding.video.suspend()
        binding.intelligentCruise.alpha = if (isAnimation) 0f else 1f
        val drawable = if (expect) showLaneLine() else hideLaneLine()
        drawable?.let { binding.intelligentCruise.setImageDrawable(it) }
    }

    override fun onPause() {
        updateDriveLineDisplay(false)
        super.onPause()
    }

}


