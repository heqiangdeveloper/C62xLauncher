package com.chinatsp.vehicle.settings.fragment.drive

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.manager.adas.AdasManager
import com.chinatsp.vehicle.settings.IRoute
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.chinatsp.vehicle.settings.databinding.DriveManageFragmentBinding
import com.common.library.frame.base.BaseTabFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveManageFragment : BaseTabFragment<BaseViewModel, DriveManageFragmentBinding>() {

    private val manager: AdasManager
        get() = AdasManager.instance

    override val nodeId: Int
        get() = 5

    override val tabLocation: MutableLiveData<Int> by lazy { MutableLiveData(manager.getTabSerial()) }


    private fun onClick(view: View) {
        tabLocation.takeIf { it.value != view.id }?.value = view.id
    }

    override fun getLayoutId(): Int {
        return R.layout.drive_manage_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initTabOptions()
        initTabLocation()
    }

    private fun initTabLocation() {
        tabLocation.let {
            it.observe(this) { location ->
                updateSelectTabOption(location)
            }
            initTabLocation(it, R.id.drive_intelligent_cruise)
        }
    }

    private fun initTabLocation(it: MutableLiveData<Int>, default: Int) {
        if (it.value == -1) {
            it.value = default
        } else {
            it.value = it.value
        }
    }

    private fun initTabOptions() {
        val tab = binding.driveManagerLeftTab
        val range = 0 until tab.childCount
        tabOptions = range.map {
            val child = tab.getChildAt(it)
            child.apply { setOnClickListener { onClick(this) } } }.toList()
        initRouteListener()
    }

    private fun initRouteListener() {
        if (activity is IRoute) {
            val iroute = activity as IRoute
            val liveData = iroute.obtainLevelLiveData()
            liveData.observe(this) {
                initRouteLocation(it)
            }
        }
    }

    private fun updateSelectTabOption(viewId: Int) {
        tabOptions.forEach { it.isSelected = false }
        updateDisplayFragment(viewId)
    }


    private fun updateDisplayFragment(serial: Int) {
        val fragment: Fragment? = checkOutFragment(serial)
        tabOptions.first { it.id == serial }.isSelected = true
        manager.setTabSerial(serial)
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
            R.id.drive_traffic -> {
                fragment = DriveTrafficFragment()
            }
            else -> {
            }
        }
        return fragment
    }

}