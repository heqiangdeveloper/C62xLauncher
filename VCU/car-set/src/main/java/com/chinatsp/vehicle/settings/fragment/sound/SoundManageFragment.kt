package com.chinatsp.vehicle.settings.fragment.sound

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.manager.sound.AudioManager
import com.chinatsp.vehicle.settings.IRoute
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.chinatsp.vehicle.settings.databinding.SoundManageFragmentBinding
import com.chinatsp.vehicle.settings.fragment.BaseTabFragment
import com.common.library.frame.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SoundManageFragment : BaseTabFragment<BaseViewModel, SoundManageFragmentBinding>() {

    private val manager: AudioManager
        get() = AudioManager.instance

    override val tabLocation: MutableLiveData<Int> by lazy { MutableLiveData(manager.getTabSerial()) }

    override fun getLayoutId(): Int {
        return R.layout.sound_manage_fragment
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
            initTabLocation(it, R.id.sound_tab)
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
        val tab = binding.soundManagerLeftTab
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

    private fun onClick(view: View) {
        tabLocation.takeIf { it.value != view.id }?.value = view.id
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
            transaction.replace(R.id.sound_manager_layout, it, it::class.simpleName)
            transaction.commitAllowingStateLoss()
        }
    }

    private fun checkOutFragment(serial: Int): Fragment? {
        var fragment: BaseFragment<out BaseViewModel, out ViewDataBinding>? = null
        when (serial) {
            R.id.sound_tab -> {
                fragment = SoundFragment()
                fragment.pid = uid
                fragment.uid = 0
            }
            R.id.sound_effect -> {
                fragment = SoundEffectFragment()
                fragment.pid = uid
                fragment.uid = 1
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