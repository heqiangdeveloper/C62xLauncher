package com.chinatsp.vehicle.settings.fragment.cabin.dialog

import android.os.Bundle
import android.view.View
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.cabin.WheelManager
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.SteeringDialogFragmentBinding
import com.chinatsp.vehicle.settings.vm.cabin.SteeringViewModel
import com.common.library.frame.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SteeringKeysDialogFragment :
    BaseDialogFragment<SteeringViewModel, SteeringDialogFragmentBinding>() {

    val PRIVACY_MODE = 0x11
    val TURN_OFF_SCREEN = 0x21
    val NAVIGATION = 0x31

    var keypad: View? = null

    val manager: WheelManager by lazy {
        WheelManager.instance
    }

    override fun getLayoutId(): Int {
        return R.layout.steering_dialog_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        updateKeypadStatus()
        setBackListener()
    }

    private fun updateKeypadStatus() {
        keypad?.isEnabled = true
        val view = when (VcuUtils.getInt(key = Constant.CUSTOM_KEYPAD, value = PRIVACY_MODE)) {
            NAVIGATION -> binding.navigation
            PRIVACY_MODE -> binding.privacyMode
            TURN_OFF_SCREEN -> binding.turnScreen
            else -> null
        }
        view?.isEnabled = false
        keypad = view ?: keypad
    }

    private fun setBackListener() {
        binding.closeDialog.setOnClickListener {
            this.dismiss()
        }
        binding.navigation.setOnClickListener {
            VcuUtils.putInt(key = Constant.CUSTOM_KEYPAD, value = NAVIGATION)
            updateKeypadStatus()
        }
        binding.privacyMode.setOnClickListener {
            VcuUtils.putInt(key = Constant.CUSTOM_KEYPAD, value = PRIVACY_MODE)
            updateKeypadStatus()
        }
        binding.turnScreen.setOnClickListener {
            VcuUtils.putInt(key = Constant.CUSTOM_KEYPAD, value = TURN_OFF_SCREEN)
            updateKeypadStatus()
        }
    }

    override fun getWidthRatio(): Float {
        return 880f / 1920f
    }


}

