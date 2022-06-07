package com.chinatsp.vehicle.settings.fragment.doors

import android.content.res.AssetManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.chinatsp.settinglib.manager.access.AccessManager
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DoorsManageFragmentBinding
import com.chinatsp.vehicle.settings.fragment.cabin.*
import com.chinatsp.vehicle.settings.vm.DoorsViewModel
import com.common.library.frame.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DoorsManageFragment : BaseFragment<DoorsViewModel, DoorsManageFragmentBinding>() {

    var selectOption: View? = null

    private lateinit var tabOptions: List<View>

    override fun getLayoutId(): Int {
        return R.layout.doors_manage_fragment
    }

    private fun onClick(view: View) {
        viewModel.tabLocationLiveData.takeIf { it.value != view.id }?.value = view.id
    }

    override fun initData(savedInstanceState: Bundle?) {
        initTabOptions()
        viewModel.tabLocationLiveData.observe(this) {
            updateSelectTabOption(it)
        }
        viewModel.tabLocationLiveData.let {
            if (it.value == -1) {
                it.value = R.id.car_doors
            } else {
                it.value = it.value
            }
        }
    }

    private fun initTabOptions() {
        val tabOptionLayout = binding.doorsManagerLeftTab
        val range = 0 until tabOptionLayout.childCount
        tabOptions = range.map {
            val child = tabOptionLayout.getChildAt(it)
            child.apply { setOnClickListener { onClick(this) } }
            child
        }.toList()
    }

    private fun updateSelectTabOption(viewId: Int) {
        tabOptions.forEach { it.isSelected = false }
        updateDisplayFragment(viewId)
    }

    private fun updateDisplayFragment(serial: Int) {
        var fragment: Fragment? = checkOutFragment(serial)
        tabOptions.first { it.id == serial }.isSelected = true
        AccessManager.instance.setTabSerial(serial)
        fragment?.let {
            val manager: FragmentManager = childFragmentManager
            val transaction: FragmentTransaction = manager.beginTransaction()
            transaction.replace(R.id.doors_manager_layout, it, it::class.simpleName)
            transaction.commitAllowingStateLoss()
        }
    }

    private fun checkOutFragment(serial: Int): Fragment? {
        var fragment: Fragment? = null
        when (serial) {
            R.id.car_doors -> {
                fragment = CarDoorsFragment()
            }
            R.id.car_window -> {
                fragment = CarWindowFragment()
            }
            R.id.car_trunk -> {
                fragment = CarTrunkFragment()
            }
            R.id.car_mirror -> {
                fragment = CarMirrorFragment()
            }
            else -> {
            }
        }
        return fragment
    }
}