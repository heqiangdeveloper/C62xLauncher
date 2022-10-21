package com.chinatsp.vehicle.settings.fragment.doors.dialog

import android.content.Intent
import android.os.Bundle
import com.chinatsp.settinglib.SettingManager
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.VolumeDialogFragmentBinding
import com.chinatsp.vehicle.settings.vm.sound.SoundEffectViewModel
import com.chinatsp.vehicle.settings.widget.SoundFieldView
import com.common.library.frame.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class VolumeDialogFragment :
    BaseDialogFragment<SoundEffectViewModel, VolumeDialogFragmentBinding>() {

    private var OFFSET = 1
    private var DEFALUT_BALANCE = 0
    private var DEFALUT_FADE = 0

    private val TAG = "VolumeDialogFragment"

    override fun getLayoutId(): Int {

        return R.layout.volume_dialog_fragment
    }


    override fun initData(savedInstanceState: Bundle?) {
        binding.closeDialog.setOnClickListener {
            this.dismiss()
        }
        Timber.d("getAmpType type=${SettingManager.getAmpType()}")
        if (SettingManager.getAmpType() == 0) {// 1 外置 1-11  ||  0 内置 ——》1-19
            SoundFieldView.BALANCE_MAX = 18.0;
            SoundFieldView.FADE_MAX = 18.0;
            Timber.d("getAmpType OFFSET=${OFFSET} BALANCE_MAX= ${SoundFieldView.BALANCE_MAX}   FADE_MAX =${SoundFieldView.FADE_MAX}")
        }

        DEFALUT_BALANCE = (SoundFieldView.BALANCE_MAX / 2).toInt();
        DEFALUT_FADE = (SoundFieldView.FADE_MAX / 2).toInt();



        binding?.apply {

//            soundField.onValueChangedListener =
//                SoundFieldView.OnValueChangedListener { balance, fade, x, y ->
//                    Timber.d("onValueChange balance:$balance fade:$fade")
////                    viewModel?.setAudioBalance(balance - OFFSET, -(fade - OFFSET))
//                    viewModel?.setAudioBalance(balance+OFFSET, fade+OFFSET)
//                }

            soundField.onValueChangedListener = object : SoundFieldView.OnValueChangedListener {
                override fun onValueChange(balance: Int, fade: Int, x: Float, y: Float) {
                    Timber.d("onValueChange balance:$balance fade:$fade")
                    viewModel?.setAudioBalance(balance + OFFSET, fade + OFFSET)
                }

                override fun onDoubleClickChange(balance: Int, fade: Int, x: Float, y: Float) {
                    soundField.reset()
                    Timber.d("onDoubleClickChange reset balance:${DEFALUT_BALANCE + OFFSET} fade:${DEFALUT_FADE + OFFSET}")
                    viewModel?.setAudioBalance(DEFALUT_BALANCE + OFFSET, DEFALUT_FADE + OFFSET)
                }
            }
            refreshDialog.setOnClickListener {
                soundField.reset()
                Timber.d("onValueChange reset balance:${DEFALUT_BALANCE + OFFSET} fade:${DEFALUT_FADE + OFFSET}")
                viewModel?.setAudioBalance(DEFALUT_BALANCE + OFFSET, DEFALUT_FADE + OFFSET)
            }
            initBlance()
        }
    }

    private fun initBlance() {
        try {
            val balance: Int? = viewModel?.getAudioBalance()
            val fade: Int? = viewModel?.getAudioFade()
            Timber.d("before getAudioBalFadInfo balance:$balance fade:$fade")
            binding?.apply {
                if (balance != null) {
                    // soundField?.balanceValue = balance + OFFSET
                    soundField?.balanceValue = balance - OFFSET
                }
                if (fade != null) {
                    //  soundField.fadeValue = -fade + OFFSET
                    soundField.fadeValue = fade - OFFSET
                }
                Timber.d("after getAudioBalFadInfo balance:${soundField?.balanceValue} fade:${soundField.fadeValue}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent("com.chinatsp.vehiclenetwork.usercenter")
        val json = "{\"fadeValue\":\""+binding.soundField.fadeValue+"\",\"balanceValue\":\""+
                binding.soundField.balanceValue+"\"}"
        intent.putExtra("app", "com.chinatsp.vehicle.settings")
        intent.putExtra("soundEffects",json)
        intent.setPackage("com.chinatsp.usercenter")
        activity?.startService(intent)
    }
}