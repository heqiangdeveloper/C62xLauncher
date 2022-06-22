package com.chinatsp.vehicle.settings.fragment.doors

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.manager.access.AccessManager
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.chinatsp.vehicle.settings.databinding.DoorsManageFragmentBinding
import com.common.library.frame.base.BaseTabFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DoorsManageFragment : BaseTabFragment<BaseViewModel, DoorsManageFragmentBinding>() {

    private lateinit var tabOptions: MutableList<View>

    private val manager: AccessManager
        get() = AccessManager.instance

    override val tabLocation: MutableLiveData<Int> by lazy { MutableLiveData(manager.getTabSerial()) }


    override fun getLayoutId(): Int {
        return R.layout.doors_manage_fragment
    }

    private fun onClick(view: View) {
        tabLocation.takeIf { it.value != view.id }?.value = view.id
    }

    override fun initData(savedInstanceState: Bundle?) {
        initTabOptions()
        tabLocation.observe(this) {
            updateSelectTabOption(it)
        }
        tabLocation.let {
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
        }.toMutableList()
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

    override fun onDestroyView() {
        super.onDestroyView()
        tabOptions.clear()
    }
}