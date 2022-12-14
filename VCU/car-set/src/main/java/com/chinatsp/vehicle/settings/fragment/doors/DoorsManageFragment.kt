package com.chinatsp.vehicle.settings.fragment.doors

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.manager.cabin.access.AccessManager
import com.chinatsp.vehicle.settings.IRoute
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.chinatsp.vehicle.settings.databinding.DoorsManageFragmentBinding
import com.chinatsp.vehicle.settings.fragment.BaseTabFragment
import com.common.library.frame.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DoorsManageFragment : BaseTabFragment<BaseViewModel, DoorsManageFragmentBinding>() {

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
        initTabLocation()
    }

    private fun initTabLocation() {
        tabLocation.let {
            it.observe(this) { location ->
                updateSelectTabOption(location)
            }
            initTabLocation(it, R.id.car_doors)
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
        val tab = binding.doorsManagerLeftTab
        val range = 0 until tab.childCount
        tabOptions = range.map {
            val child = tab.getChildAt(it)
            child.apply { setOnClickListener { onClick(this) } }
        }.toList()
        initRouteListener()
    }

    private fun initRouteListener() {
        val router = obtainRouter()
        if (null != router) {
            val liveData = router.obtainLevelLiveData()
            liveData.observe(this) {
                syncRouterLocation(it)
            }
        }
    }

    private fun obtainRouter(): IRoute? {
        return if (activity is IRoute) activity as IRoute else null
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
        var fragment: BaseFragment<out BaseViewModel, out ViewDataBinding>? = null
        when (serial) {
            R.id.car_doors -> {
                fragment = CarDoorsFragment()
                fragment.pid = uid
                fragment.uid = 0
            }
            R.id.car_window -> {
                fragment = CarWindowFragment()
                fragment.pid = uid
                fragment.uid = 1
            }
            R.id.car_trunk -> {
                fragment = CarTrunkFragment()
                fragment.pid = uid
                fragment.uid = 2
            }
            R.id.car_mirror -> {
                fragment = CarMirrorFragment()
                fragment.pid = uid
                fragment.uid = 3
            }
            else -> {
            }
        }
        return fragment
    }

    override fun resetRouter(lv1: Int, lv2: Int, lv3: Int) {
        obtainRouter()?.resetLevelRouter(lv1, lv2, lv3)
    }

}