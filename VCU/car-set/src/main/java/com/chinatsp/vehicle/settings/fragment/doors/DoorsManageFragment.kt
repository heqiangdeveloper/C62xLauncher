package com.chinatsp.vehicle.settings.fragment.doors

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DoorsManageFragmentBinding
import com.chinatsp.vehicle.settings.fragment.cabin.*
import com.chinatsp.vehicle.settings.vm.DoorsViewModel
import com.common.library.frame.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DoorsManageFragment : BaseFragment<DoorsViewModel, DoorsManageFragmentBinding>() {
    var selectOption: View? = null
    private val tabOptions: List<View> by lazy {
        val tabOptionLayout = binding.doorsManagerLeftTab
        val range = 0 until tabOptionLayout.childCount
        return@lazy range.map {
            val child = tabOptionLayout.getChildAt(it)
            child.isEnabled = true
            child.setOnClickListener { it -> onClick(it) }
            child
        }.toList()
    }

    override fun getLayoutId(): Int {
        return R.layout.doors_manage_fragment
    }

    private fun onClick(view: View) {
        if (view != selectOption) {
            viewModel.tabLocationLiveData.let {
                it.value = view.id
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        viewModel.tabLocationLiveData.observe(this) {
            updateSelectTabOption(it)
        }
        viewModel.tabLocationLiveData.let {
            if (it.value == -1) {
                it.value = R.id.car_doors
            }
        }
    }

    private fun updateSelectTabOption(viewId: Int) {
        if (viewId != selectOption?.id) {
            selectOption?.isEnabled = true
            selectOption = tabOptions.first { it.id == viewId }
            selectOption?.isEnabled = false
            updateDisplayFragment(viewId)
        }
    }

    private fun updateDisplayFragment(serial: Int) {
        var fragment: Fragment? = checkOutFragment(serial)
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
            else -> {
            }
        }
        return fragment
    }
}