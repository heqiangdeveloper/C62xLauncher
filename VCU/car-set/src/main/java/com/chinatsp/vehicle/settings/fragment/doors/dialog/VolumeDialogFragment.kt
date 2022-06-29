package com.chinatsp.vehicle.settings.fragment.doors.dialog

import android.os.Bundle
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.VolumeDialogFragmentBinding
import com.chinatsp.vehicle.settings.vm.sound.SoundViewModel
import com.common.library.frame.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VolumeDialogFragment: BaseDialogFragment<SoundViewModel, VolumeDialogFragmentBinding>()  {
    override fun getLayoutId(): Int {
       return R.layout.volume_dialog_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
    }
}