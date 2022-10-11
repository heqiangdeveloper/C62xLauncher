package com.chinatsp.vehicle.settings.fragment

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.chinatsp.vehicle.settings.databinding.AccessFragmentBinding
import com.common.library.frame.base.BaseTabFragment
import com.rightware.kanzi.androiddatasource.AndroidDataSourceManager
import com.rightware.kanzi.androiddatasource.AndroidNotifyListener
import com.rightware.kanzi.androiddatasource.AssetCopyer
import com.rightware.kanzi.androiddatasource.SharedData
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CommonlyFragment : BaseTabFragment<BaseViewModel, AccessFragmentBinding>() {

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
        binding.kanZiContent.alpha = 0f
        binding.kanZiContent.registerLifecycle(lifecycle)
        // Force the screen to stay on when this app is on front (no need to clear).
        binding.kanZiContent.keepScreenOn = true
//        binding.kanZiContent.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        initAndroidDataSource()
    }

    private fun setClickListener() {
        binding.closeWindow.setOnClickListener {
            GlobalManager.instance.doSwitchWindow(false)
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
        val ApkFolderPath = context?.getExternalFilesDir(null)!!.absolutePath
        mDataFeeder!!.xmlPath = "$ApkFolderPath/"
        AssetCopyer.copyAssetsToDst(context, "DataSource.xml", "$ApkFolderPath/DataSource.xml")
        mDataFeeder!!.addAndroidNotifyListener(mkanziNotifyListener) // kanzi
    }

    private val mkanziNotifyListener: AndroidNotifyListener = object : AndroidNotifyListener() {
        override fun notifyDataChanged(name: String, type: Int, value: String) {
            super.notifyDataChanged(name, type, value)
            Timber.d("notifyDataChanged() name:$name type:$type value:$value")
            if (name == "KanziInitFinish") {
                Timber.d("Set isKanziInitFinish = true")
                binding.kanZiContent.post {
                    binding.kanZiContent.alpha = 1f
                }
            }
        }
    }

    override fun resetRouter(lv1: Int, lv2: Int, lv3: Int) {

    }

}