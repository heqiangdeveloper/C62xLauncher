package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.cabin.OtherManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.ISwitchAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.RechargeToast.showToast
import com.chinatsp.vehicle.settings.app.Toast
import com.chinatsp.vehicle.settings.databinding.CabinOtherFragmentBinding
import com.chinatsp.vehicle.settings.fragment.cabin.dialog.AbnormalChargeDialogFragment
import com.chinatsp.vehicle.settings.fragment.cabin.dialog.ForeignMatterDialogFragment
import com.chinatsp.vehicle.settings.fragment.cabin.dialog.NoteUsersDialogFragment
import com.chinatsp.vehicle.settings.fragment.cabin.dialog.TrailerRemindDialogFragment
import com.chinatsp.vehicle.settings.vm.cabin.OtherViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.popupwindow.PopWindow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CabinOtherFragment : BaseFragment<OtherViewModel, CabinOtherFragmentBinding>(),
    ISwitchAction {

    private val manager: OtherManager
        get() = OtherManager.instance

    override fun getLayoutId(): Int {
        return R.layout.cabin_other_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setCheckedChangeListener()
        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()
        initViewsDisplay()
        initDetailsClickListener()
    }

    private fun initViewsDisplay() {
        val levelValue = VcuUtils.VEHICLE_LEVEL
        //level3, level4 配置无手机无线充电
        if (Level.LEVEL3 == levelValue || Level.LEVEL4 == levelValue) {
            binding.cabinOtherWirelessCharging.visibility = View.GONE
            binding.line3.visibility = View.GONE
        }
        if (Level.LEVEL3 == levelValue) {
            binding.cabinOtherWirelessChargingLamp.visibility = View.GONE
            binding.line4.visibility = View.GONE
        }

    }

    private fun initDetailsClickListener() {
        binding.cabinOtherTrailerRemindDetails.setOnClickListener {
            showPopWindow(R.string.cabin_other_trailer_remind_content, it)
        }
        binding.cabinOtherBatteryOptimizationDetails.setOnClickListener {
            showPopWindow(R.string.cabin_other_battery_optimization_content, it)
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.DRIVE_BATTERY_OPTIMIZE, viewModel.batteryOptimize)
        initSwitchOption(SwitchNode.DRIVE_WIRELESS_CHARGING, viewModel.wirelessCharging)
        initSwitchOption(SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP, viewModel.wirelessChargingLamp)
        updateSwitchTextHint(binding.otherTrailerRemindTextView, viewModel.trailerRemind)

        updateSwitchEnable(SwitchNode.DRIVE_BATTERY_OPTIMIZE)
        updateSwitchEnable(SwitchNode.DRIVE_WIRELESS_CHARGING)
        updateSwitchEnable(SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP)
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
        viewModel.batteryOptimize.let { ld ->
            ld.observe(this) {
                doUpdateSwitch(SwitchNode.DRIVE_BATTERY_OPTIMIZE, it)
                updateSwitchEnable(SwitchNode.DRIVE_BATTERY_OPTIMIZE)
            }
        }
        viewModel.wirelessCharging.let { ld ->
            ld.observe(this) {
                doUpdateSwitch(SwitchNode.DRIVE_WIRELESS_CHARGING, it)
                updateSwitchEnable(SwitchNode.DRIVE_WIRELESS_CHARGING)
            }
        }
        viewModel.wirelessChargingLamp.let { ld ->
            ld.observe(this) {
                doUpdateSwitch(SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP, it)
                updateSwitchEnable(SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP)
            }
        }
        viewModel.trailerRemind.let { ld ->
            ld.observe(this) {
                updateSwitchTextHint(binding.otherTrailerRemindTextView, ld)
            }
        }
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.DRIVE_BATTERY_OPTIMIZE -> binding.otherBatteryOptimizeSwitch
            SwitchNode.DRIVE_WIRELESS_CHARGING -> binding.otherWirelessChargingSwitch
            SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP -> binding.otherWirelessChargingLampSwitch
            else -> null
        }
    }

    override fun obtainActiveByNode(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.DRIVE_BATTERY_OPTIMIZE -> viewModel.batteryOptimize.value?.enable() ?: false
            SwitchNode.DRIVE_WIRELESS_CHARGING -> viewModel.wirelessCharging.value?.enable()
                ?: false
            SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP -> viewModel.wirelessChargingLamp.value?.enable()
                ?: false
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    private fun setSwitchListener() {
        binding.otherBatteryOptimizeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.DRIVE_BATTERY_OPTIMIZE, buttonView, isChecked)
        }
        binding.otherWirelessChargingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.DRIVE_WIRELESS_CHARGING, buttonView, isChecked)
            if (isChecked) {
                abnormalCharge()
            }
        }
        binding.otherWirelessChargingLampSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                Toast.showToast(context, getString(R.string.cabin_other_wireless_charging_lamp_open), true)
            }else{
                Toast.showToast(context, getString(R.string.cabin_other_wireless_charging_lamp_close), true)
            }
            doUpdateSwitchOption(SwitchNode.DRIVE_WIRELESS_CHARGING_LAMP, buttonView, isChecked)
        }
    }

    private fun setCheckedChangeListener() {
        binding.cabinOtherTrailerRemind.setOnClickListener {
            val fragment = TrailerRemindDialogFragment()
            activity?.supportFragmentManager?.let {
                fragment.show(it, fragment.javaClass.simpleName)
            }
        }
        binding.notesUsers.setOnClickListener {
            val fragment = NoteUsersDialogFragment()
            activity?.supportFragmentManager?.let {
                fragment.show(it, fragment.javaClass.simpleName)
            }
        }
    }

    private fun showPopWindow(id: Int, view: View) {
        val popWindow: PopWindow
        if (view.id == binding.cabinOtherTrailerRemindDetails.id) {
            popWindow = PopWindow(activity,
                R.layout.pop_window,
                activity?.let {
                    AppCompatResources.getDrawable(
                        it,
                        R.drawable.popup_bg_qipao172_7
                    )
                })
            popWindow.showDownLift(view, 30, -80)
        } else if (view.id == binding.cabinOtherBatteryOptimizationDetails.id) {
            popWindow = PopWindow(activity,
                R.layout.pop_window,
                activity?.let {
                    AppCompatResources.getDrawable(
                        it,
                        R.drawable.popup_bg_qipao172_8
                    )
                })
            popWindow.showDownLift(view, 30, -80)
        } else {
            popWindow = PopWindow(activity,
                R.layout.pop_window,
                activity?.let {
                    AppCompatResources.getDrawable(
                        it,
                        R.drawable.popup_bg_qipao172_1
                    )
                })
            popWindow.showDown(view)
        }
        val text: TextView = popWindow.findViewById(R.id.content) as TextView
        text.text = resources.getString(id)

    }

    private fun abnormalCharge() {
        //模拟数据
        val wcm = true//检测到异物
        val triggerElectrical = true//触发电上电
        val wcmSwitch = true;//WCM开关处于打开状态，
        val peps = true//PEPS不在寻钥匙状态
        val wcmStr = true//WCM无故障信息
        val receiving = true;//检测到接收端(移动端)

        if (triggerElectrical && wcmSwitch && peps && wcmStr && receiving) {
            //充电成功
            showToast(context,
                context?.resources?.getString(R.string.cabin_other_wireless_charging_working_properly),
                true)
            return
        } else if (triggerElectrical && wcmSwitch && peps && wcm) {
            //检测到异物
            val fragment = ForeignMatterDialogFragment()
            activity?.supportFragmentManager?.let {
                fragment.show(it, fragment.javaClass.simpleName)
            }
        } else {
            //无线充电异常
            val fragment = AbnormalChargeDialogFragment()
            activity?.supportFragmentManager?.let {
                fragment.show(it, fragment.javaClass.simpleName)
            }
        }


    }
}