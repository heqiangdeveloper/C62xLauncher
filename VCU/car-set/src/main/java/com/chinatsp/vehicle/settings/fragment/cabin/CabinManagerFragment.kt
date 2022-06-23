package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.manager.cabin.CabinManager
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.chinatsp.vehicle.settings.databinding.CabinFragmentBinding
import com.common.library.frame.base.BaseTabFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/21 14:20
 * @desc   :
 * @version: 1.0
 */
@AndroidEntryPoint
class CabinManagerFragment : BaseTabFragment<BaseViewModel, CabinFragmentBinding>() {

    private val manager: CabinManager
        get() = CabinManager.instance

    private lateinit var tabOptions: List<View>

    override val tabLocation: MutableLiveData<Int> by lazy { MutableLiveData(manager.getTabSerial()) }

    override fun getLayoutId(): Int {
        return R.layout.cabin_fragment
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
            if (tabOptions.map { item -> item.id }.contains(it.value)) {
                it.value = it.value
            } else {
                it.value = R.id.cabin_wheel
            }
        }
    }

    private fun initTabOptions() {
        val tabOptionLayout = binding.cabinManagerLeftTab
        val range = 0 until tabOptionLayout.childCount
        tabOptions = range.map {
            val child = tabOptionLayout.getChildAt(it)
            child.isSelected = false
            child.apply { setOnClickListener { onClick(this) } }
            child
        }.toList()
    }

    private fun updateSelectTabOption(viewId: Int) {
        tabOptions.forEach { it.isSelected = false }
        updateDisplayFragment(viewId)
    }


    private fun updateDisplayFragment(serial: Int) {
        tabOptions.first { it.id == serial }.isSelected = true
        manager.setTabSerial(serial)
        val fragment: Fragment? = checkOutFragment(serial)
        fragment?.let {
            val manager: FragmentManager = childFragmentManager
            val transaction: FragmentTransaction = manager.beginTransaction()
            transaction.replace(R.id.cabin_manager_layout, it, it::class.simpleName)
            transaction.commitAllowingStateLoss()
        }
    }

    private fun checkOutFragment(serial: Int): Fragment? {
        var fragment: Fragment? = null
        when (serial) {
            R.id.cabin_wheel -> {
                fragment = CabinWheelFragment()
            }
            R.id.cabin_seat -> {
                fragment = CabinSeatFragment()
            }
            R.id.cabin_air_conditioner -> {
                fragment = CabinACFragment()
            }
            R.id.cabin_safety -> {
                fragment = CabinSafeFragment()
            }
            R.id.cabin_instrument -> {
                fragment = CabinMeterFragment()
            }
            R.id.cabin_other -> {
                fragment = CabinOtherFragment()
            }
            else -> {}
        }
        return fragment
    }


}