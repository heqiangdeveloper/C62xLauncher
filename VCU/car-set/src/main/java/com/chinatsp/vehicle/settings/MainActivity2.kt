package com.chinatsp.vehicle.settings

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.navigation.RouterSerial
import com.chinatsp.vehicle.controller.ICollapseListener
import com.chinatsp.vehicle.controller.VersionController
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.bean.TabPage
import com.chinatsp.vehicle.settings.databinding.MainActivityTablayout2Binding
import com.chinatsp.vehicle.settings.vm.MainViewModel
import com.common.library.frame.base.BaseActivity
import com.common.library.frame.base.BaseFragment
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


    //    private val level1: MutableLiveData<Node> by lazy { MutableLiveData(Node()) }
    private val level1: MutableLiveData<Node> by lazy { MutableLiveData(Node()) }
//    private val level3: MutableLiveData<Node> by lazy { MutableLiveData(Node()) }

    private val popupLiveData: MutableLiveData<String> by lazy { MutableLiveData("") }

    private var versionController: VersionController? = null

    override fun getLayoutId(): Int {
        return R.layout.main_activity_tablayout2
    }

    override fun isBinding(): Boolean = true

    override fun initData(savedInstanceState: Bundle?) {
        initTabLayout()
        checkOutRoute(intent)
        observeLocation()
        registerController()
        binding.deviceUpgrade.setOnClickListener {
            doRouteToDeviceUpgrade()
        }
    }

    private fun observeLocation() {
        tabLocation.observe(this) { position ->
            manager.setTabSerial(position)
            Timber.e("-------------------------observeLocation--position:$position")
            binding.tabLayout.takeIf { position != it.selectedTabPosition }?.let { tabLayout ->
                tabLayout.selectTab(tabLayout.getTabAt(position), true)
            }
        }
    }

    private fun registerController() {
        versionController = VersionController(this, mDrawerCollapseListener)
        versionController?.register()
    }

    override fun onStart() {
        super.onStart()
        val value = Settings.System.getInt(
            context.contentResolver,
            Constant.VERSION_LEVEL,
            Constant.STATUS_HIDE
        )
        Timber.d("-------------------------Settings.System.getInt--value:$value")
        if (value == Constant.STATUS_HIDE) {
            binding.redVersion.visibility = View.GONE
        } else {
            binding.redVersion.visibility = View.VISIBLE
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
            var intentPath = ""
            if (Constant.VCU_AUDIO_VOLUME == action) {
                routeValue = RouterSerial.makeRouteSerial(3, 0, 0)
                popupSerial = Constant.DEVICE_AUDIO_VOLUME
            } else if (Constant.VCU_SCREEN_BRIGHTNESS == action) {
                routeValue = RouterSerial.makeRouteSerial(2, 2, 0)
                popupSerial = ""
            } else if (Constant.VCU_AMBIENT_LIGHTING == action) {
                routeValue = RouterSerial.makeRouteSerial(2, 1, 0)
                popupSerial = it.getStringExtra(Constant.DIALOG_SERIAL) ?: ""
                intentPath = it.getStringExtra(Constant.INTENT_PATH) ?: ""
            } else if (Constant.VCU_CUSTOM_KEYPAD == action) {
                routeValue = RouterSerial.makeRouteSerial(4, 0, 0)
                popupSerial = "1004_2000_3001"
                doNavigation(routeValue, popupSerial, intentPath, general = true)
                return
            } else if (Constant.VCU_GENERAL_ROUTER == action) {
                routeValue = it.getIntExtra(Constant.ROUTE_SERIAL, Constant.INVALID)
                popupSerial = it.getStringExtra(Constant.DIALOG_SERIAL) ?: ""
                intentPath = it.getStringExtra(Constant.INTENT_PATH) ?: ""
                doNavigation(routeValue, popupSerial, intentPath, general = true)
                return
            } else {
                routeValue = Constant.INVALID
                popupSerial = ""
            }
            doNavigation(routeValue, popupSerial, intentPath)
        }
    }

    private fun doNavigation(value: Int, route: String, path: String, general: Boolean = false) {
        if (general) {
            val list = route.split("_")
            Timber.e("==================route:%s, size:%s", route, list.size)
            /*if (intentPath == Constant.LAUNCHER_SEARCH) {
                binding.homeBack.visibility = View.VISIBLE
            }*/
            if (list.size == 3) {
                val locations = list.map { it.toInt() }
                if (1007 == locations[0]) {
                    doRouteToDeviceUpgrade()
                    return
                }
                val node1 = Node(uid = locations[0] - 1000)
                val node2 = Node(uid = locations[1] - 2000)
                val node3 = Node(uid = locations[2] - 3000)
                node1.cnode = node2
                node2.cnode = node3
                node3.pnode = node2
                node2.pnode = node1
                if (node1.valid && node1.uid in obtainTabs().map { tab -> tab.uid }.toSet()) {
                    val isLevel3 = VcuUtils.isCareLevel(Level.LEVEL3)
                    val position = if (isLevel3) node1.uid - 1 else node1.uid
                    Timber.d("doNavigation-------position:$position")
                    manager.setTabSerial(position)
                    updatePosition(position)
                    level1.postValue(node1)
                }
            }
            return
        }
//        binding.homeBack.visibility = View.GONE
//        if (Constant.INVALID != routeValue) {
//            val level1 = RouterSerial.getLevel(routeValue, 1)
//            val level2 = RouterSerial.getLevel(routeValue, 2)
//            val level3 = RouterSerial.getLevel(routeValue, 3)
//            val node2 = this.level1.value
//            node2?.uid = level2
//            node2?.pid = level1
////            node2?.valid = true
//            tabLocation.value = level1
//            this.level1.value = node2
//            popupLiveData.value = route
//        }
    }

    private fun updatePosition(position: Int) {
        if (position != tabLocation.value) {
            tabLocation.postValue(position)
        }
    }

    private fun initTabLayout() {
        with(this) {
            binding.tabLayout.tabMode = TabLayout.MODE_AUTO
            binding.tabLayout.addOnTabSelectedListener(this)
            val values = obtainTabs()
            values.forEach {
                val tab = binding.tabLayout.newTab()
                //tab.text = it.desc
                tab.text = resources.getString(it.desc)
                tab.tag = it
                binding.tabLayout.addTab(tab)
                tab.view.isLongClickable = false
                tab.view.tooltipText = null
            }
        }
    }

    private fun obtainTabs(): Array<TabPage> {
        var values = TabPage.values()
        if (VcuUtils.isCareLevel(Level.LEVEL3, expect = true)) {
            values = values.dropWhile { it == TabPage.COMMONLY }.toTypedArray()
        }
        return values
    }

    fun onClick(view: View) {
        if (view.id == R.id.back) {
            onBackPressed()
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

    private fun showFragment(name: String, uid: Int) {
        val manager = supportFragmentManager
        var fragment = manager.findFragmentByTag(name)
        if (null == fragment) {
            val newInstance = Class.forName(name).newInstance() as BaseFragment<*, *>
            newInstance.uid = uid
            val transaction = supportFragmentManager.beginTransaction()
//            transaction.setCustomAnimations(R.anim.activity_enter, R.anim.activity_exit);
            fragment = newInstance
            transaction.replace(R.id.vcu_content_container, fragment, name)
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
            when (tag) {
                TabPage.COMMONLY -> {
                    binding.constraint.setBackgroundResource(R.drawable.bg_changyong_1920)
                }
                TabPage.ADAS -> {
                    binding.constraint.setBackgroundResource(R.drawable.bg)
                }
                else -> {
                    binding.constraint.setBackgroundResource(R.drawable.right_bg)
                }
            }
            showFragment(tag.className, tag.uid)
        }

        if (firstCreate) {
            firstCreate = false
            return
        }
        updatePosition(binding.tabLayout.selectedTabPosition)
        manager.setTabSerial(tabLocation.value!!)
    }


    override fun obtainLevelLiveData(): LiveData<Node> {
        return level1
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

    override fun resetLevelRouter(lv1: Int, lv2: Int, lv3: Int) {
        level1.value?.let {
            if (it.valid && it.uid == lv1 && it.cnode?.uid == lv2 && it.cnode?.cnode?.uid == lv3) {
                resetNode(it)
            }
        }
    }

    private fun resetNode(node: Node) {
        if (node.valid) {
            if (null != node.cnode) {
                resetNode(node.cnode!!)
            }
            node.valid = false
        }
    }

    fun homeBack() {
        finish()
    }

    private var mDrawerCollapseListener: ICollapseListener? = object : ICollapseListener {
        override fun onCollapse(key: Int) {
            Timber.e("onCollapse-------key:$key")
            if (key == Constant.STATUS_HIDE) {
                binding.redVersion.visibility = View.GONE
            } else {
                binding.redVersion.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroy() {
        versionController?.unRegister()
        super.onDestroy()
    }

}


