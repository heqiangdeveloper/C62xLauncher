package com.chinatsp.vehicle.settings.fragment.doors.dialog

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
class VolumeDialogFragment : BaseDialogFragment<SoundEffectViewModel, VolumeDialogFragmentBinding>() {

    private var OFFSET = 9

    private val TAG = "VolumeDialogFragment"

    override fun getLayoutId(): Int {

        return R.layout.volume_dialog_fragment
    }


    override fun initData(savedInstanceState: Bundle?) {
        binding.closeDialog.setOnClickListener {
            this.dismiss()
        }
        Timber.d("getAmpType type=${SettingManager.getAmpType()}")
        if(SettingManager.getAmpType() == 0){
            OFFSET = 5;
            SoundFieldView.BALANCE_MAX = 10.0;
            SoundFieldView.FADE_MAX = 10.0;
            Timber.d("getAmpType OFFSET=${OFFSET} BALANCE_MAX= ${SoundFieldView.BALANCE_MAX}   FADE_MAX =${SoundFieldView.FADE_MAX}")
        }
        binding?.apply {
            soundField?.onValueChangedListener =
                SoundFieldView.OnValueChangedListener { balance, fade, x, y ->
                    Timber.d( "onValueChange balance:$balance fade:$fade")
                    viewModel?.setAudioBalance(balance - OFFSET, -(fade - OFFSET))
                }
            refreshDialog?.setOnClickListener {
                soundField.reset()
                viewModel?.setAudioBalance(0, 0)
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
                    soundField?.balanceValue = balance + OFFSET
                }
                if (fade != null) {
                    soundField.fadeValue = -fade + OFFSET
                }
                Timber.d("after getAudioBalFadInfo balance:$soundField?.balanceValue fade:${soundField.fadeValue}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}