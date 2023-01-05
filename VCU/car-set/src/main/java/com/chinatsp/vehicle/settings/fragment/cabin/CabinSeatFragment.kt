package com.chinatsp.vehicle.settings.fragment.cabin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.listener.sound.ISoundManager
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.cabin.SeatManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.IRoute
import com.chinatsp.vehicle.settings.ISwitchAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.Toast
import com.chinatsp.vehicle.settings.databinding.CabinSeatFragmentBinding
import com.chinatsp.vehicle.settings.fragment.cabin.dialog.SeatHeatingDialogFragment
import com.chinatsp.vehicle.settings.vm.cabin.SeatViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/21 15:23
 * @desc   :
 * @version: 1.0
 */
@AndroidEntryPoint
class CabinSeatFragment : BaseFragment<SeatViewModel, CabinSeatFragmentBinding>(), ISwitchAction {

    private val manager: ISoundManager
        get() = SeatManager.instance

    private val map: HashMap<Int, View> = HashMap()

    override fun getLayoutId(): Int {
        return R.layout.cabin_seat_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setCheckedChangeListener()
        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()

        initClickView()
        initRouteListener()
    }

    private fun initClickView() {
        map[1] = binding.cabinSeatCopilotGuests
        map[2] = binding.cabinSeatAutomaticHeating
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
            /*binding.cabinSeatCopilotGuests -> {
                activity?.supportFragmentManager?.let {
                    showDialogFragment(CopilotGuestsDialogFragment())
                }
            }*/
            binding.cabinSeatAutomaticHeating -> {
                activity?.supportFragmentManager?.let {
                    showDialogFragment(SeatHeatingDialogFragment())
                }
            }
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.MAIN_SEAT_WELCOME, viewModel.mainMeet)
        initSwitchOption(SwitchNode.FORK_SEAT_WELCOME, viewModel.forkMeet)
        updateSwitchTextHint(binding.seatSeatHeatTv, viewModel.seatHeat)
    }

    private fun updateSwitchTextHint(textView: TextView, liveData: LiveData<SwitchState>) {
        val static = liveData.value?.get() ?: false
        val hintId = if (static) {
            R.string.switch_turn_on
        } else {
            R.string.switch_turn_off
        }
        textView.setText(hintId)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.mainMeet.observe(this) {
            doUpdateSwitch(binding.seatMainMeetSwitch, it)
        }
        viewModel.forkMeet.observe(this) {
            doUpdateSwitch(binding.seatForkMeetSwitch, it)
        }
        viewModel.seatHeat.observe(this) {
            updateSwitchTextHint(binding.seatSeatHeatTv, viewModel.seatHeat)
        }
        viewModel.node654.observe(this){
            updateSwitchEnable(SwitchNode.MAIN_SEAT_WELCOME)
            updateSwitchEnable(SwitchNode.FORK_SEAT_WELCOME)
        }
    }

    override fun obtainDependByNode(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.MAIN_SEAT_WELCOME -> viewModel.node654.value?.get() ?: true
            SwitchNode.FORK_SEAT_WELCOME -> viewModel.node654.value?.get() ?: true
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.MAIN_SEAT_WELCOME -> binding.seatMainMeetSwitch
            SwitchNode.FORK_SEAT_WELCOME -> binding.seatForkMeetSwitch
            else -> null
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    private fun setSwitchListener() {
        binding.seatMainMeetSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.MAIN_SEAT_WELCOME, buttonView, isChecked)
            if (!isChecked) {
                Toast.showToast(context, getString(R.string.cabin_seat_welcomes_guests_close), true)
            }
        }
        binding.seatForkMeetSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.FORK_SEAT_WELCOME, buttonView, isChecked)
            if (!isChecked) {
                Toast.showToast(context, getString(R.string.cabin_seat_fork_welcome_close), true)
            }
        }
    }

    private fun setCheckedChangeListener() {
        binding.cabinSeatAutomaticHeating.setOnClickListener(this::onViewClick)
        binding.cabinSeatCopilotGuests.setOnClickListener(this::onViewClick)
    }

    override fun onDestroy() {
        super.onDestroy()
        //主副驾座椅位置
        val intent = Intent("com.chinatsp.vehiclenetwork.usercenter")
        val json = "{\"mainPassengerSeatPosition\":\"" + 132.125 + "\"}"
        intent.putExtra("app", "com.chinatsp.vehicle.settings")
        intent.putExtra("seat", json)
        intent.setPackage("com.chinatsp.usercenter")
        activity?.startService(intent)
    }
}