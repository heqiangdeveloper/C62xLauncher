package com.chinatsp.vehicle.settings.fragment

import android.graphics.PixelFormat
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.vcu.kanzi.KanZiKeys
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.AccessFragmentBinding
import com.chinatsp.vehicle.settings.vm.KanziViewModel
import com.rightware.kanzi.androiddatasource.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CommonlyFragment : BaseTabFragment<KanziViewModel, AccessFragmentBinding>() {

    private var mDataFeeder: AndroidDataSourceManager? = null

    private var mSharedData: SharedData? = null

    override val tabLocation: MutableLiveData<Int> by lazy { MutableLiveData(0) }


    companion object {
        init {
            System.loadLibrary("kanzi")
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.access_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        setClickListener()
//        binding.kanZiContent.alpha = 0f
        binding.kanZiContent.registerLifecycle(lifecycle)
        // Force the screen to stay on when this app is on front (no need to clear).
        binding.kanZiContent.keepScreenOn = true
//        binding.kanZiContent.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //清除SurfaceView 黑色背景
        binding.kanZiContent.setZOrderOnTop(true)
        val holder = binding.kanZiContent.holder
        holder.setFormat(PixelFormat.TRANSPARENT)
        initAndroidDataSource()
    }

    private fun setClickListener() {
        binding.closeWindow.setOnClickListener {
            GlobalManager.instance.doSwitchWindow(false)
            mDataFeeder?.kanziController?.let {
                val value = 0
                sendDoubleValue(it, KanZiKeys.LFWindowUpDown, value)
                sendDoubleValue(it, KanZiKeys.LRWindowUpDown, value)
                sendDoubleValue(it, KanZiKeys.RFWindowUpDown, value)
                sendDoubleValue(it, KanZiKeys.RRWindowUpDown, value)
            }

        }
        binding.openWindow.setOnClickListener {
            GlobalManager.instance.doSwitchWindow(true)
            mDataFeeder?.kanziController?.let {
                val value = -40
                sendDoubleValue(it, KanZiKeys.LFWindowUpDown, value)
                sendDoubleValue(it, KanZiKeys.LRWindowUpDown, value)
                sendDoubleValue(it, KanZiKeys.RFWindowUpDown, value)
                sendDoubleValue(it, KanZiKeys.RRWindowUpDown, value)
            }
        }
        binding.refreshWindow.setOnClickListener {
            GlobalManager.instance.resetSwitchWindow()
            sendIntValue(KanZiKeys.Restoration, 1)
            sendIntValue(KanZiKeys.Restoration, 0)
        }
    }

    private fun initAndroidDataSource() {
        mDataFeeder = AndroidDataSourceManager("AndroidDataSourceManager")
        mSharedData = SharedData.get()
        mSharedData?.addManager(mDataFeeder)
        val apkFolderPath = context?.getExternalFilesDir(null)!!.absolutePath
        mDataFeeder!!.xmlPath = "$apkFolderPath/"
        AssetCopyer.copyAssetsToDst(context, "DataSource.xml", "$apkFolderPath/DataSource.xml")
        mDataFeeder!!.addAndroidNotifyListener(kanziNotifyListener) // kanzi
    }

    private fun initVehicleStatus() {
        val controller = mDataFeeder?.kanziController
        controller?.let {
            sendIntValue(it, KanZiKeys.Hood, viewModel.headDoor.value)
            sendIntValue(it, KanZiKeys.Trunk, viewModel.tailDoor.value)
            sendIntValue(it, KanZiKeys.LFDoor, viewModel.lfDoor.value)
            sendIntValue(it, KanZiKeys.LRDoor, viewModel.lrDoor.value)
            sendIntValue(it, KanZiKeys.RFDoor, viewModel.rfDoor.value)
            sendIntValue(it, KanZiKeys.RRDoor, viewModel.rrDoor.value)

            sendIntValue(it, KanZiKeys.FWiper, viewModel.fWiper.value)
            sendIntValue(it, KanZiKeys.RWiper, viewModel.rWiper.value)

            sendIntValue(it, KanZiKeys.LeftIndicator, viewModel.lIndicator.value)
            sendIntValue(it, KanZiKeys.RightIndicator, viewModel.rIndicator.value)

            sendIntValue(it, KanZiKeys.HeadLamps, viewModel.headLamps.value)
            sendIntValue(it, KanZiKeys.BrakeLights, viewModel.brakeLamps.value)
            sendIntValue(it, KanZiKeys.PositionLight, viewModel.positionLamps.value)
            sendIntValue(it, KanZiKeys.RearFogLamp, viewModel.rearFogLamps.value)

            sendDoubleValue(it, KanZiKeys.LFWindowUpDown, viewModel.lfWindow.value)
            sendDoubleValue(it, KanZiKeys.LRWindowUpDown, viewModel.lrWindow.value)
            sendDoubleValue(it, KanZiKeys.RFWindowUpDown, viewModel.rfWindow.value)
            sendDoubleValue(it, KanZiKeys.RRWindowUpDown, viewModel.rrWindow.value)
        }
    }

    private fun sendIntValue(controller: DataSourceKanziController, key: String, value: Int?) {
        if (null == value) {
            return
        }
        val keyObj = kzDataTypeInt(key)
        controller.setDataObjectValue(keyObj, value)
    }

    private fun sendIntValue(key: String, value: Int?) {
        if (null == value) {
            return
        }
        val controller = mDataFeeder?.kanziController
        if (null != controller) {
            val keyObj = kzDataTypeInt(key)
            controller.setDataObjectValue(keyObj, value)
        }
    }

    private fun sendDoubleValue(key: String, value: Int?) {
        if (null == value) {
            return
        }
        val controller = mDataFeeder?.kanziController
        if (null != controller) {
            val keyObj = kzDataTypeReal(key)
            controller.setDataObjectValue(keyObj, value.toDouble())
        }
    }

    private fun sendDoubleValue(controller: DataSourceKanziController, key: String, value: Int?) {
        if (null == value) {
            return
        }
        val keyObj = kzDataTypeReal(key)
        controller.setDataObjectValue(keyObj, value.toDouble())
    }


    private val kanziNotifyListener: AndroidNotifyListener = object : AndroidNotifyListener() {
        override fun notifyDataChanged(name: String, type: Int, value: String) {
            super.notifyDataChanged(name, type, value)
            Timber.d("notifyDataChanged() name:$name type:$type value:$value")
            if (name == "KanziInitFinish") {
                Timber.d("Set isKanziInitFinish = true")
                binding.kanZiContent.post {
                    observeAccessState()
//                    initVehicleStatus()
                }
            }
        }
    }

    private fun observeAccessState() {
        if (null != context && null != viewModel) {
            viewModel.headDoor.observe(this) {
                sendIntValue(KanZiKeys.Hood, it)
            }
            viewModel.tailDoor.observe(this) {
                sendIntValue(KanZiKeys.Trunk, it)
            }

            viewModel.lfDoor.observe(this) {
                sendIntValue(KanZiKeys.LFDoor, it)
            }
            viewModel.lrDoor.observe(this) {
                sendIntValue(KanZiKeys.LRDoor, it)
            }
            viewModel.rfDoor.observe(this) {
                sendIntValue(KanZiKeys.RFDoor, it)
            }
            viewModel.rrDoor.observe(this) {
                sendIntValue(KanZiKeys.RRDoor, it)
            }

            viewModel.lfWindow.observe(this) {
                sendDoubleValue(KanZiKeys.LFWindowUpDown, it)
            }
            viewModel.lrWindow.observe(this) {
                sendDoubleValue(KanZiKeys.LRWindowUpDown, it)
            }
            viewModel.rfWindow.observe(this) {
                sendDoubleValue(KanZiKeys.RFWindowUpDown, it)
            }
            viewModel.rrWindow.observe(this) {
                sendDoubleValue(KanZiKeys.RRWindowUpDown, it)
            }

            viewModel.fWiper.observe(this) {
                sendIntValue(KanZiKeys.FWiper, it)
            }
            viewModel.rWiper.observe(this) {
                sendIntValue(KanZiKeys.RWiper, it)
            }

            viewModel.lIndicator.observe(this) {
                sendIntValue(KanZiKeys.LeftIndicator, it)
            }
            viewModel.rIndicator.observe(this) {
                sendIntValue(KanZiKeys.RightIndicator, it)
            }

            viewModel.headLamps.observe(this) {
                sendIntValue(KanZiKeys.HeadLamps, it)
            }
            viewModel.brakeLamps.observe(this) {
                sendIntValue(KanZiKeys.BrakeLights, it)
            }
            viewModel.positionLamps.observe(this) {
                sendIntValue(KanZiKeys.PositionLight, it)
            }
            viewModel.rearFogLamps.observe(this) {
                sendIntValue(KanZiKeys.RearFogLamp, it)
            }
        }
    }

    override fun resetRouter(lv1: Int, lv2: Int, lv3: Int) {

    }

}