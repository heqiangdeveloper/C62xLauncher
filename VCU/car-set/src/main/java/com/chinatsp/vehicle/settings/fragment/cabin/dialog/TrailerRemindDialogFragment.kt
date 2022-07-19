package com.chinatsp.vehicle.settings.fragment.cabin.dialog

import android.os.Bundle
import com.chinatsp.settinglib.listener.IThemeChangeListener
import com.chinatsp.settinglib.service.ThemeService
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.TrailerRemindDialogFragmentBinding
import com.chinatsp.vehicle.settings.vm.cabin.SeatViewModel
import com.common.library.frame.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrailerRemindDialogFragment :
    BaseDialogFragment<SeatViewModel, TrailerRemindDialogFragmentBinding>(), IThemeChangeListener {
    private lateinit var service: ThemeService
    override fun getLayoutId(): Int {
        return R.layout.trailer_remind_dialog_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setBackListener()
        initService()
    }

    private fun initService() {
        service = ThemeService(activity)
        service.addListener("TrailerRemindDialog", this)
    }

    private fun setBackListener() {
        binding.closeDialog.setOnClickListener {
            this.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        service.removeListener("TrailerRemindDialog")
    }

    override fun onChange(night: Boolean) {

    }
}