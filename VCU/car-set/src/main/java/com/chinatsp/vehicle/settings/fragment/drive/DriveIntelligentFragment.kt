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

    private val map: HashMap<Int, View> = HashMap()

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

        initClickView()
        initRouteListener()
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
        val uri = "android.resource://" + activity?.packageName + "/" + R.raw.video_acc
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
    }

    private fun addSwitchLiveDataListener() {
        viewModel.cruiseAssistFunction.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_IACC, it)
            updateSwitchEnable(SwitchNode.ADAS_IACC)
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
            SwitchNode.ADAS_LIMBER_LEAVE -> viewModel.limberLeaveFunction.value?.enable() ?: false
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun obtainActiveByNode(node: RadioNode): Boolean {
        return when (node) {
            RadioNode.ADAS_LIMBER_LEAVE -> viewModel.limberLeaveRadio.value?.enable() ?: false
            else -> super.obtainActiveByNode(node)
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
            doUpdateSwitchOption(SwitchNode.ADAS_IACC, buttonView, isChecked)
            if (buttonView.isChecked) {
                //binding.intelligentCruise.visibility = View.GONE
                val uri = "android.resource://" + activity?.packageName + "/" + R.raw.video_acc
                binding.video.setVideoURI(Uri.parse(uri));
                binding.video.start()
            } else {
                dynamicEffect()
            }
        }
        binding.adasForwardLeaveSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_LIMBER_LEAVE, buttonView, isChecked)
        }
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

    override fun onPause() {
        super.onPause()
        binding.intelligentCruise.visibility = View.VISIBLE
        binding.intelligentCruise.setImageDrawable(activity?.let {
            ContextCompat.getDrawable(
                it,
                R.drawable.intelligent_cruise
            )
        })
        binding.video.pause()
    }

}

