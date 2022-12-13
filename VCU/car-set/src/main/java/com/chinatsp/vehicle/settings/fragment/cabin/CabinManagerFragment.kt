package com.chinatsp.vehicle.settings.fragment.cabin

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.manager.cabin.CabinManager
import com.chinatsp.vehicle.settings.IRoute
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.chinatsp.vehicle.settings.databinding.CabinFragmentBinding
import com.chinatsp.vehicle.settings.fragment.BaseTabFragment
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
class CabinManagerFragment : BaseTabFragment<BaseViewModel, CabinFragmentBinding>() {

    private val manager: CabinManager
        get() = CabinManager.instance

    override val tabLocation: MutableLiveData<Int> by lazy { MutableLiveData(manager.getTabSerial()) }

    override fun getLayoutId(): Int {
        return R.layout.cabin_fragment
    }

    private fun onClick(view: View) {
        tabLocation.takeIf { it.value != view.id }?.value = view.id
    }

    override fun initData(savedInstanceState: Bundle?) {
        initTabOptions()
        initTabLocation()
    }

    private fun obtainRouter(): IRoute? {
        return if (activity is IRoute) activity as IRoute else null
    }

    private fun initTabLocation() {
        tabLocation.let {
            it.observe(this) { location ->
                updateSelectTabOption(location)
            }
            initTabLocation(it, R.id.cabin_wheel)
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
        val tab = binding.cabinManagerLeftTab
        val range = 0 until tab.childCount
        tabOptions = range.map {
            val child = tab.getChildAt(it)
            child.apply { setOnClickListener { onClick(this) } }
        }.toList()
        //最新需求，LV3显示座椅功能，解决BUG 73942
        /*if (VcuUtils.isCareLevel(Level.LEVEL3)) {
            binding.cabinManagerLeftTab[2].visibility = View.GONE
        }*/
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

    private fun updateSelectTabOption(viewId: Int) {
        tabOptions.forEach { it.isSelected = false }
        updateDisplayFragment(viewId)
        tabLocation.let {
            tabOptions.first { it.id == uid }
        }
    }


    private fun updateDisplayFragment(serial: Int) {
        tabOptions.first { it.id == serial }.isSelected = true
        manager.setTabSerial(serial)
        val manager: FragmentManager = childFragmentManager
        val clazz = getFragmentClass(serial)
        if (null != clazz) {
            val cache = manager.findFragmentByTag(clazz.simpleName)
            if (null == cache) {
                val fragment: Fragment? = checkOutFragment(serial)
                fragment?.let {
                    val transaction: FragmentTransaction = manager.beginTransaction()
                    transaction.replace(R.id.cabin_manager_layout, it, it::class.simpleName)
                    transaction.commitAllowingStateLoss()
                }
            }
        }
    }

    private fun checkOutFragment(serial: Int): Fragment? {
        var fragment: BaseFragment<out BaseViewModel, out ViewDataBinding>? = null
        when (serial) {
            R.id.cabin_wheel -> {
                fragment = CabinWheelFragment()
                fragment.pid = uid
                fragment.uid = 0
            }
            R.id.cabin_seat -> {
                fragment = CabinSeatFragment()
                fragment.pid = uid
                fragment.uid = 1
            }
            R.id.cabin_air_conditioner -> {
                fragment = CabinACFragment()
                fragment.pid = uid
                fragment.uid = 2
            }
            R.id.cabin_safety -> {
                fragment = CabinSafeFragment()
                fragment.pid = uid
                fragment.uid = 3
            }
            R.id.cabin_instrument -> {
                fragment = CabinMeterFragment()
                fragment.pid = uid
                fragment.uid = 4
            }
            R.id.cabin_other -> {
                fragment = CabinOtherFragment()
                fragment.pid = uid
                fragment.uid = 5
            }
            else -> {}
        }
        return fragment
    }

    private fun getFragmentClass(serial: Int): Class<*>? {
        var fragment: Class<*>? = null
        when (serial) {
            R.id.cabin_wheel -> {
                fragment = CabinWheelFragment::class.java
            }
            R.id.cabin_seat -> {
                fragment = CabinSeatFragment::class.java
            }
            R.id.cabin_air_conditioner -> {
                fragment = CabinACFragment::class.java
            }
            R.id.cabin_safety -> {
                fragment = CabinSafeFragment::class.java
            }
            R.id.cabin_instrument -> {
                fragment = CabinMeterFragment::class.java
            }
            R.id.cabin_other -> {
                fragment = CabinOtherFragment::class.java
            }
            else -> {}
        }
        return fragment
    }

    override fun resetRouter(lv1: Int, lv2: Int, lv3: Int) {
        obtainRouter()?.resetLevelRouter(lv1, lv2, lv3)
    }

}