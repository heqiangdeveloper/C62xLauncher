package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.CabinFragmentBinding
import com.chinatsp.vehicle.settings.vm.CabinViewModel
import com.common.library.frame.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/5/21 14:20
 * @desc   :
 * @version: 1.0
 */
@AndroidEntryPoint
class CabinManagerFragment : BaseFragment<CabinViewModel, CabinFragmentBinding>() {

    var selectOption: View? = null

    private val tabOptions: List<View> by lazy {
        val tabOptionLayout = binding.cabinManagerLeftTab
        val range = 0 until tabOptionLayout.childCount
        return@lazy range.map {
            val child = tabOptionLayout.getChildAt(it)
            child.isEnabled = true
            child.setOnClickListener { it -> onClick(it) }
            child
        }.toList()
    }

    override fun getLayoutId(): Int {
        return R.layout.cabin_fragment
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
                it.value = R.id.cabin_wheel
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


    fun updateDisplayFragment(serial: Int) {
        var fragment: Fragment? = checkOutFragment(serial)
        fragment?.let {
            val manager: FragmentManager = childFragmentManager
            val transaction: FragmentTransaction = manager.beginTransaction()
            transaction.replace(R.id.cabin_manager_layout, it, it::class.simpleName)
            transaction.commitAllowingStateLoss()
        }
    }

    fun checkOutFragment(serial: Int): Fragment?{
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
            else -> {}
        }
        return fragment
    }

}