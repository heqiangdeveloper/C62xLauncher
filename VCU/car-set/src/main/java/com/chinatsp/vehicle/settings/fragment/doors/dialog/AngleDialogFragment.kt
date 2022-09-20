package com.chinatsp.vehicle.settings.fragment.doors.dialog

import android.os.Bundle
import android.view.View
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.manager.IMirrorAction
import com.chinatsp.settinglib.manager.access.BackMirrorManager
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.AngleDialogFragmentBinding
import com.chinatsp.vehicle.settings.vm.sound.SoundEffectViewModel
import com.common.library.frame.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AngleDialogFragment : BaseDialogFragment<SoundEffectViewModel, AngleDialogFragmentBinding>() {

    lateinit var angleInvoke: IAngleInvoke

    override fun getLayoutId(): Int {
        return R.layout.angle_dialog_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setBackListener()
    }

    override fun getWidthRatio(): Float {
        return 640f / 1920f
    }

    fun registerAngleInvoke(listener: IAngleInvoke): Unit {
        this.angleInvoke = listener
    }

    private fun setBackListener() {
        binding.hintCancel.setOnClickListener { this.dismiss() }
        binding.carMirrorCancelSet.setOnClickListener { this.dismiss() }
        binding.carMirrorSaveSet.setOnClickListener {
            doBackMirrorAction(Constant.ANGLE_SAVE)
            this.dismiss()
        }
        binding.hintConform.setOnClickListener {
            binding.setLayout.visibility = View.GONE
            binding.saveLinear.visibility = View.VISIBLE
            doBackMirrorAction(Constant.ANGLE_ADJUST)
        }
    }

    private fun doBackMirrorAction(@IMirrorAction action: Int) {
        val manager = BackMirrorManager.instance
        manager.doBackMirrorAction(action)
        angleInvoke.onAngleUpdate(action)
    }

    interface IAngleInvoke {
        fun onAngleUpdate(@IMirrorAction angleValue: Int)
    }

}