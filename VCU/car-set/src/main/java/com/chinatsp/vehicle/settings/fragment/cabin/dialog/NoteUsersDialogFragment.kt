package com.chinatsp.vehicle.settings.fragment.cabin.dialog

import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.NoteUsersDialogFragmentBinding
import com.chinatsp.vehicle.settings.vm.cabin.SeatViewModel
import com.common.library.frame.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteUsersDialogFragment :
    BaseDialogFragment<SeatViewModel, NoteUsersDialogFragmentBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.note_users_dialog_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setBackListener()
    }

    override fun getWidthRatio(): Float {
        return 1750f / 1920f
    }

    private fun setBackListener() {
        binding.noteUsersAgree.setOnClickListener {
            dismiss()
        }
        binding.noteUsersCancel.setOnClickListener {
            dismiss()
        }
    }
}