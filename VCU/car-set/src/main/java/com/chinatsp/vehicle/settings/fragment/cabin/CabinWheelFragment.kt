package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.chinatsp.settinglib.Applet
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.IRadioManager
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.cabin.WheelManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.*
import com.chinatsp.vehicle.settings.databinding.CabinWhellFragmentBinding
import com.chinatsp.vehicle.settings.fragment.cabin.dialog.ConversionDialogFragment
import com.chinatsp.vehicle.settings.fragment.cabin.dialog.SteeringHeatDialogFragment
import com.chinatsp.vehicle.settings.fragment.cabin.dialog.SteeringKeysDialogFragment
import com.chinatsp.vehicle.settings.fragment.adas.SwitchoverFailureDialogFragment
import com.chinatsp.vehicle.settings.vm.cabin.SteeringViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.utils.ResUtils
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/21 15:23
 * @desc   :
 * @version: 1.0
 */
@AndroidEntryPoint
class CabinWheelFragment : BaseFragment<SteeringViewModel, CabinWhellFragmentBinding>(),
    IOptionAction {

    private val manager: WheelManager
        get() = WheelManager.instance

    private val map: HashMap<Int, View> = HashMap()

    override fun getLayoutId(): Int {
        return R.layout.cabin_whell_fragment
    }


    override fun initData(savedInstanceState: Bundle?) {
        setCheckedChangeListener()
        initSwitchOption()
        addSwitchLiveDataListener()

        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()
        initViewsDisplay()

        updateRadioEnable(RadioNode.DRIVE_EPS_MODE)

        initClickView()
        initRouteListener()

        initSimpleSignal()
        addSimpleLiveDataListener()
    }

    private fun addSimpleLiveDataListener() {
        viewModel.keypadCustom.observe(this) {
            heelCustomKeys(it)
        }
    }

    private fun initSimpleSignal() {
        val value = viewModel.keypadCustom.value
        heelCustomKeys(value!!)
    }

    private fun initClickView() {
        map[1] = binding.wheelCustomKeys
        map[2] = binding.wheelAutomaticHeating
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
                                map[lv3Node.uid]?.run { onViewClick(this, lv3Node.uid) }
                            }
                        }
                }
            }
        }
    }

    private fun onViewClick(view: View, clickUid: Int) {
        onViewClick(view)
        obtainRouter()?.resetLevelRouter(pid, uid, clickUid)
    }

    private fun onViewClick(it: View) {
        when (it) {
            binding.wheelCustomKeys -> showDialogFragment(Constant.STEERING_CUSTOM_KEYPAD)
            binding.wheelAutomaticHeating -> showDialogFragment(Constant.STEERING_HEATING_SETTING)
        }
    }

    private fun showHintDialog(title: Int, content: Int, retract: Boolean = true) {
        HintHold.setTitle(title)
        HintHold.setContent(content)
        val fragment = SwitchoverFailureDialogFragment()
        fragment.retract = retract
        activity?.supportFragmentManager?.let {
            fragment.show(it, fragment.javaClass.simpleName)
        }
    }

    private fun initViewsDisplay() {
        if (VcuUtils.isCareLevel(Level.LEVEL3)) {
            binding.wheelAutomaticHeating.visibility = View.GONE
            binding.line3.visibility = View.GONE
        }
    }

    private fun initSwitchOption() {
        val hintId =
            if (viewModel.swhFunction.value?.get() == true) R.string.switch_turn_on else R.string.switch_turn_off
        binding.wheelAutomaticHeatingTv.text = ResUtils.getString(hintId)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.swhFunction.observe(this) {
            val hintId = if (it.get()) R.string.switch_turn_on else R.string.switch_turn_off
            binding.wheelAutomaticHeatingTv.text = ResUtils.getString(hintId)
        }
        viewModel.node322.observe(this) {
            updateRadioEnable(RadioNode.DRIVE_EPS_MODE)
        }
    }

    private fun initRadioOption() {
        initRadioOption(RadioNode.DRIVE_EPS_MODE, viewModel.epsMode)
    }

    private fun addRadioLiveDataListener() {
        viewModel.epsMode.observe(this) {
            doUpdateRadio(RadioNode.DRIVE_EPS_MODE, it, false)
            updateRadioEnable(RadioNode.DRIVE_EPS_MODE)
        }
    }

    private fun setRadioListener() {
        binding.wheelEpsModeTabView.let {
            it.setOnTabSelectionChangedListener { _, value ->
                val epsMode = viewModel.epsMode.value?.get() ?: RadioNode.DRIVE_EPS_MODE.def
                Timber.d("EPS_MODE ------ epsMode:$epsMode")
                it.setSelection(epsMode.toString(), true)
                if (VcuUtils.isEngineRunning() && VcuUtils.isPower() && Applet.isBelowCareSpeed(15f)) {
                    doUpdateRadio(RadioNode.DRIVE_EPS_MODE, value, viewModel.epsMode, it)
                    val fragment = ConversionDialogFragment()
                    activity?.supportFragmentManager?.let { manager ->
                        fragment.show(manager, fragment.javaClass.simpleName)
                    }
                } else {
                    showHintDialog(
                        R.string.vcu_action_switch_failed,
                        R.string.vcu_eps_action_switch_check, retract = false
                    )
                }
            }
        }
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return null
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    override fun findRadioByNode(node: RadioNode): TabControlView? {
        val tabView = when (node) {
            RadioNode.DRIVE_EPS_MODE -> binding.wheelEpsModeTabView
            else -> null
        }
        return tabView
    }

    override fun obtainActiveByNode(node: RadioNode): Boolean {
        return when (node) {
            RadioNode.DRIVE_EPS_MODE -> viewModel.epsMode.value?.enable() ?: false
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun obtainDependByNode(node: RadioNode): Boolean {
        return when (node) {
            RadioNode.DRIVE_EPS_MODE -> viewModel.node322.value?.get() ?: true
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun getRadioManager(): IRadioManager {
        return manager
    }


    private fun setCheckedChangeListener() {
        binding.wheelCustomKeys.setOnClickListener(this::onViewClick)
        binding.wheelAutomaticHeating.setOnClickListener(this::onViewClick)
    }

    var fragment: DialogFragment? = null

    private fun showDialogFragment(serial: String) {
        if (fragment?.isVisible == true) {
            return
        }
        if (Constant.STEERING_CUSTOM_KEYPAD == serial) {
            cleanPopupSerial(serial)
            fragment = SteeringKeysDialogFragment()
        } else if (Constant.STEERING_HEATING_SETTING == serial) {
            cleanPopupSerial(serial)
            fragment = SteeringHeatDialogFragment()
        }
        activity?.supportFragmentManager?.let {
            fragment?.show(it, fragment!!.javaClass.name)
        }
    }


    private fun cleanPopupSerial(serial: String) {
        if (activity is IRoute) {
            val iroute = activity as IRoute
            iroute.cleanPopupLiveDate(serial)
        }
    }

//    private fun initRouteListener() {
//        if (activity is IRoute) {
//            val route = activity as IRoute
//            val liveData = route.obtainPopupLiveData()
//            liveData.observe(this) {
//                if (it.equals(Constant.STEERING_CUSTOM_KEYPAD)) {
//                    showDialogFragment(it)
//                } else if (it.equals(Constant.STEERING_HEATING_SETTING)) {
//                    showDialogFragment(it)
//                }
//            }
//        }
//    }

    private fun heelCustomKeys(value: Int) {
        val viewId = when (value) {
            Constant.NAVIGATION -> R.string.cabin_wheel_navigation
            Constant.PRIVACY_MODE -> R.string.cabin_wheel_press_key_tv
            Constant.TURN_OFF_SCREEN -> R.string.cabin_wheel_turn_screen
            else -> null
        }
        binding.wheelCustomKeysTv.text = viewId?.let { ResUtils.getString(it) }
    }

}