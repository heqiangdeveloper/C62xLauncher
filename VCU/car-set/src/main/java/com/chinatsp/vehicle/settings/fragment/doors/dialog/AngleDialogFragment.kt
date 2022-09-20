package com.chinatsp.vehicle.settings.fragment.doors.dialog

import android.os.Bundle
import android.view.View
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.AngleDialogFragmentBinding
import com.chinatsp.vehicle.settings.vm.sound.SoundEffectViewModel
import com.common.library.frame.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AngleDialogFragment : BaseDialogFragment<SoundEffectViewModel, AngleDialogFragmentBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.angle_dialog_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setBackListener()
    }

    override fun getWidthRatio(): Float {
        return 640f / 1920f
    }

    private fun setBackListener() {
        binding.hintConform.setOnClickListener {
            binding.setLayout.visibility = View.GONE
            binding.saveLinear.visibility = View.VISIBLE
        }
        binding.hintCancel.setOnClickListener {
            this.dismiss()
        }
        binding.carMirrorCancelSet.setOnClickListener { this.dismiss() }
        binding.carMirrorSaveSet.setOnClickListener { this.dismiss() }
    }
}