package com.chinatsp.vehicle.settings

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.navigation.RouterSerial
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.bean.TabPage
import com.chinatsp.vehicle.settings.databinding.MainActivityTablayout2Binding
import com.chinatsp.vehicle.settings.fragment.CommonlyFragment
import com.chinatsp.vehicle.settings.fragment.drive.DriveManageFragment
import com.chinatsp.vehicle.settings.vm.MainViewModel
import com.common.library.frame.base.BaseActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity2 : BaseActivity<MainViewModel, MainActivityTablayout2Binding>(),
    OnTabSelectedListener, IRoute {

    val manager: GlobalManager
        get() = GlobalManager.instance

    private var firstCreate = true

    private val tabLocation: MutableLiveData<Int> by lazy {
        MutableLiveData(manager.getTabSerial())
    }

    private val level2Node: MutableLiveData<Node> by lazy { MutableLiveData(Node()) }

    private val popupLiveData: MutableLiveData<String> by lazy { MutableLiveData("") }


    override fun getLayoutId(): Int {
        return R.layout.main_activity_tablayout2
    }

    override fun isBinding(): Boolean = true

    override fun initData(savedInstanceState: Bundle?) {
        initTabLayout()
        checkOutRoute(intent)
        tabLocation.observe(this) { position ->
            manager.setTabSerial(position)
            binding.tabLayout.let { tabLayout ->
                if (position != tabLayout.selectedTabPosition) {
                    tabLayout.selectTab(tabLayout.getTabAt(position), true)
                }
            }
        }
        binding.deviceUpgrade.setOnClickListener {
            doRouteToDeviceUpgrade()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        checkOutRoute(intent)
    }

    private fun checkOutRoute(intent: Intent?) {
        intent?.let {
            val action = intent.action
            val routeValue: Int
            val popupSerial: String
            if (Constant.VCU_AUDIO_VOLUME == action) {
                routeValue = RouterSerial.makeRouteSerial(3, 0, 0)
                popupSerial = Constant.DEVICE_AUDIO_VOLUME
            } else if (Constant.VCU_SCREEN_BRIGHTNESS == action) {
                routeValue = RouterSerial.makeRouteSerial(2, 2, 0)
                popupSerial = ""
            } else if (Constant.VCU_AMBIENT_LIGHTING == action) {
                routeValue = RouterSerial.makeRouteSerial(2, 1, 0)
                popupSerial = it.getStringExtra(Constant.DIALOG_SERIAL) ?: ""
            } else if (Constant.VCU_CUSTOM_KEYPAD == action) {
                routeValue = RouterSerial.makeRouteSerial(4, 0, 0)
                popupSerial = it.getStringExtra(Constant.DIALOG_SERIAL) ?: ""
            } else if (Constant.VCU_GENERAL_ROUTER == action) {
                routeValue = it.getIntExtra(Constant.ROUTE_SERIAL, Constant.INVALID)
                popupSerial = it.getStringExtra(Constant.DIALOG_SERIAL) ?: ""
            } else {
                routeValue = Constant.INVALID
                popupSerial = ""
            }
            doNavigation(routeValue, popupSerial)
        }
    }

    private fun doNavigation(routeValue: Int, popupSerial: String) {
        if (Constant.INVALID != routeValue) {
            val level1 = RouterSerial.getLevel(routeValue, 1)
            val level2 = RouterSerial.getLevel(routeValue, 2)
            val level3 = RouterSerial.getLevel(routeValue, 3)
            val node2 = level2Node.value
            node2?.id = level2
            node2?.presentId = level1
            node2?.valid = true
            tabLocation.value = level1
            level2Node.value = node2
            popupLiveData.value = popupSerial
        }
    }

    private fun initTabLayout() {
        with(this) {
            binding.tabLayout.tabMode = TabLayout.MODE_AUTO
            binding.tabLayout.addOnTabSelectedListener(this)
            var values = TabPage.values()
            if (VcuUtils.isCareLevel(Level.LEVEL3, expect = true)) {
                values = values.dropWhile { it == TabPage.COMMONLY }.toTypedArray()
            }
            values.forEach {
                val tab = binding.tabLayout.newTab()
                tab.text = it.desc
                tab.tag = it
                binding.tabLayout.addTab(tab)
                tab.view.isLongClickable = false
                tab.view.tooltipText = null
            }
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.back -> this.onBackPressed()
            else -> {
            }
        }
    }

    private fun doRouteToDeviceUpgrade() {
        try {
            val intent = Intent(Intent.ACTION_MAIN)
            val packageName = "com.hmi.beic62.pc"
            val className = "com.hmi.beic62.pc.FMainActivity"
            intent.component = ComponentName(packageName, className)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("type", "from downbar2022") //这里Intent传值
            startActivity(intent)
        } catch (e: Exception) {
            Timber.e(e.message)
        }
    }

    private fun showFragment(name: String) {
        val manager = supportFragmentManager ?: return
        var fragment = manager.findFragmentByTag(name)
        if (null == fragment) {
            fragment = Class.forName(name).newInstance() as Fragment
            val transaction = supportFragmentManager.beginTransaction()
//            transaction.setCustomAnimations(R.anim.activity_enter, R.anim.activity_exit);
            transaction.replace(R.id.vcu_content_container, fragment!!, name)
            transaction.commitAllowingStateLoss()
        }
    }


    override fun onTabUnselected(tab: TabLayout.Tab) {

    }

    override fun onTabReselected(tab: TabLayout.Tab) {

    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        val tag = tab.tag
        if (tag is TabPage) {
            when (tag.className) {
                CommonlyFragment::class.java.simpleName -> {
                    binding.constraint.setBackgroundResource(R.drawable.bg_changyong_1920)
                }
                DriveManageFragment::class.java.simpleName -> {
                    binding.constraint.setBackgroundResource(R.drawable.bg)
                }
                else -> {
                    binding.constraint.setBackgroundResource(R.drawable.right_bg)
                }
            }
            showFragment(tag.className)
        }

        if (firstCreate) {
            firstCreate = false
            return
        }
        tabLocation.postValue(binding.tabLayout.selectedTabPosition)
    }

    override fun onDestroy() {
        manager.setTabSerial(tabLocation.value!!)
        super.onDestroy()
    }

    override fun obtainLevelLiveData(): LiveData<Node> {
        return level2Node
    }

    override fun obtainPopupLiveData(): LiveData<String> {
        return popupLiveData
    }

    override fun cleanPopupLiveDate(serial: String): Boolean {
        if (serial == popupLiveData.value) {
            popupLiveData.value = ""
            return true
        }
        return false
    }

}


