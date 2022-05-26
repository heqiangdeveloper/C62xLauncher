package com.chinatsp.vehicle.settings.fragment.drive

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveManageFragmentBinding
import com.chinatsp.vehicle.settings.vm.DriveViewModel
import com.common.library.frame.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveManageFragment : BaseFragment<DriveViewModel, DriveManageFragmentBinding>() {
    var selectOption: View? = null
    private lateinit var tabOptions: List<View>
    private fun onClick(view: View) {
        if (view != selectOption) {
            viewModel.tabLocationLiveData.let {
                it.value = view.id
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.drive_manage_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initTabOptions()

        viewModel.tabLocationLiveData.observe(this) {
            updateSelectTabOption(it)
        }
        viewModel.tabLocationLiveData.let {
            if (it.value == -1) {
                it.value = R.id.drive_intelligent_cruise
            }
        }
    }

    private fun initTabOptions() {
        val tabOptionLayout = binding.driveManagerLeftTab
        val range = 0 until tabOptionLayout.childCount
        tabOptions = range.map {
            val child = tabOptionLayout.getChildAt(it)
            child.apply { setOnClickListener { onClick(this) } }
            child
        }.toList()
    }

    private fun updateSelectTabOption(viewId: Int) {
        if (viewId != selectOption?.id) {
            selectOption?.isSelected = false
            selectOption = tabOptions.first { it.id == viewId }
            selectOption?.isSelected = true
            updateDisplayFragment(viewId)
        }
    }


    private fun updateDisplayFragment(serial: Int) {
        val fragment: Fragment? = checkOutFragment(serial)
        fragment?.let {
            val manager: FragmentManager = childFragmentManager
            val transaction: FragmentTransaction = manager.beginTransaction()
            transaction.replace(R.id.drive_manager_layout, it, it::class.simpleName)
            transaction.commitAllowingStateLoss()
        }
    }

    private fun checkOutFragment(serial: Int): Fragment? {
        var fragment: Fragment? = null
        when (serial) {
            R.id.drive_intelligent_cruise -> {
                fragment = DriveIntelligentFragment()
            }
            R.id.drive_forward_assist -> {
                fragment = DriveForwardFragment()
            }
            R.id.drive_lane_assist -> {
                fragment = DriveLaneFragment()
            }
            R.id.drive_rear_assist -> {
                fragment = DriveRearFragment()
            }
            R.id.drive_lighting_assist -> {
                fragment = DriveLightingFragment()
            }
            else -> {
            }
        }
        return fragment
    }

}