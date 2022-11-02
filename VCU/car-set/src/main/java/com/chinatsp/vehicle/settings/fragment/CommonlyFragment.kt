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
            val controller = mDataFeeder?.kanziController
            val keyObject = kzDataTypeInt(KanZiKeys.Trunk)
            controller?.setDataObjectValue(keyObject, 1)
        }
        binding.openWindow.setOnClickListener {
            GlobalManager.instance.doSwitchWindow(true)
        }
        binding.refreshWindow.setOnClickListener {
            GlobalManager.instance.resetSwitchWindow()
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
                    initVehicleStatus()
                }
            }
        }
    }

    override fun resetRouter(lv1: Int, lv2: Int, lv3: Int) {

    }

}