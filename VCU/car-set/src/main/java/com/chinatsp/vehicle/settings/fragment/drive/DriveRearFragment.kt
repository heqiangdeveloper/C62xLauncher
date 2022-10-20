package com.chinatsp.vehicle.settings.fragment.drive

import android.graphics.Color
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.constants.OffLine
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.IRadioManager
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.adas.SideBackManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.HintHold
import com.chinatsp.vehicle.settings.IOptionAction
import com.chinatsp.vehicle.settings.IRoute
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveRearFragmentBinding
import com.chinatsp.vehicle.settings.fragment.drive.dialog.DetailsDialogFragment
import com.chinatsp.vehicle.settings.vm.adas.SideViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveRearFragment : BaseFragment<SideViewModel, DriveRearFragmentBinding>(), IOptionAction {

    var index: Int = 0;

    private val map: HashMap<Int, View> = HashMap()

    private val manager: IOptionManager
        get() = SideBackManager.instance

    override fun getLayoutId(): Int {
        return R.layout.drive_rear_fragment
    }


    override fun initData(savedInstanceState: Bundle?) {
        initSwitchOption()
        initVideoListener()
        addSwitchLiveDataListener()
        setSwitchListener()

        initRadioOption()
        addRadioLiveDataListener()
        setRadioListener()

        initViewsDisplay()
        initDetailsClickListener()
        initClickView()
        initRouteListener()
    }

    private fun initClickView() {
        map[1] = binding.dowDetails
        map[2] = binding.mebDetails
        map[3] = binding.driveBsdDetails
        map[4] = binding.driveBsdCameraDetails
    }

    private fun obtainRouter(): IRoute? {
        return if (activity is IRoute) activity as IRoute else null
    }

    private fun initRouteListener() {
        val router = obtainRouter()
        if (null != router) {
            val liveData = router.obtainLevelLiveData()
            liveData.observe(this) {
                it.takeIf { it.valid && it.uid == pid }?.let { level1 ->
                    level1.cnode?.takeIf { child -> child.valid && child.uid == uid }
                        .let { level2 ->
                            level2?.cnode?.let { lv3Node ->
                                map[lv3Node.uid]?.run { onViewClick(this, lv3Node.uid, true) }
                            }
                        }
                }
            }
        }
    }

    private fun onViewClick(view: View, clickUid: Int, frank: Boolean) {
        onViewClick(view)
        obtainRouter()?.resetLevelRouter(pid, uid, clickUid)
    }

    private fun onViewClick(it: View) {
        when (it) {
            binding.dowDetails -> updateHintMessage(R.string.drive_dow_title, R.string.dow_details)
            binding.mebDetails -> updateHintMessage(R.string.adas_meb_title, R.string.meb_details)
            binding.driveBsdDetails -> updateHintMessage(R.string.drive_bsd_title,
                R.string.bsd_details)
            binding.driveBsdCameraDetails -> updateHintMessage(R.string.drive_bsd_camera_title,
                R.string.bsc_details)
        }
    }

    private fun initDetailsClickListener() {
        binding.driveBsdDetails.setOnClickListener(this::onViewClick)
        binding.driveBsdCameraDetails.setOnClickListener(this::onViewClick)
        binding.dowDetails.setOnClickListener(this::onViewClick)
        binding.mebDetails.setOnClickListener(this::onViewClick)
    }

    private fun updateHintMessage(title: Int, content: Int) {
        HintHold.setTitle(title)
        HintHold.setContent(content)
        val fragment = DetailsDialogFragment()
        activity?.supportFragmentManager?.let {
            fragment.show(it, fragment.javaClass.simpleName)
        }
    }

    private fun initViewsDisplay() {
        if (VcuUtils.isCareLevel(Level.LEVEL3, Level.LEVEL4, expect = true)) {
            binding.driveDow.visibility = View.GONE
            binding.line1.visibility = View.GONE
            binding.adasMebLayout.visibility = View.GONE
            binding.lineMeb.visibility = View.GONE
            binding.driveBsdCamera.visibility = View.GONE
            binding.driveAuxiliaryLine.visibility = View.GONE
            binding.driveDisplay.visibility = View.GONE
        }
    }

    private fun initRadioOption() {
        initRadioOption(RadioNode.ADAS_SIDE_BACK_SHOW_AREA, viewModel.showAreaValue)
    }

    private fun initVideoListener() {
        val uri = "android.resource://" + activity?.packageName + "/" + R.raw.video_bsd
        binding.video.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE)
        binding.video.setVideoURI(Uri.parse(uri));
        binding.video.setOnCompletionListener {
            binding.video.pause()
            binding.video.seekTo(0)
            dynamicEffect()
        }
        binding.video.setOnPreparedListener {
            binding.video.setBackgroundColor(Color.TRANSPARENT)
        }
        binding.video.setOnErrorListener { _, _, _ ->
            dynamicEffect()
            true
        }
        binding.video.setOnPreparedListener {
            it.setOnInfoListener { _, _, _ ->
                binding.video.setBackgroundColor(Color.TRANSPARENT);
                binding.videoImage.visibility = View.GONE
                true
            }
        }
    }


    private fun addRadioLiveDataListener() {
        viewModel.showAreaValue.observe(this) {
            doUpdateRadio(RadioNode.ADAS_SIDE_BACK_SHOW_AREA, it, false)
        }

    }

    private fun setRadioListener() {
        binding.adasSideShowAreaRadio.let {
            it.setOnTabSelectionChangedListener { _, value ->
                val node = RadioNode.ADAS_SIDE_BACK_SHOW_AREA
                doUpdateRadio(node, value, viewModel.showAreaValue, it)
            }
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.ADAS_DOW, viewModel.dowValue)
        initSwitchOption(SwitchNode.ADAS_BSC, viewModel.bscValue)
        initSwitchOption(SwitchNode.ADAS_BSD, viewModel.bsdValue)
        initSwitchOption(SwitchNode.ADAS_MEB, viewModel.mebValue)
        initSwitchOption(SwitchNode.ADAS_GUIDES, viewModel.guidesValue)

        updateSwitchEnable(SwitchNode.ADAS_DOW)
        updateSwitchEnable(SwitchNode.ADAS_BSC)
        updateSwitchEnable(SwitchNode.ADAS_BSD)
        updateSwitchEnable(SwitchNode.ADAS_MEB)
        updateSwitchEnable(SwitchNode.ADAS_GUIDES)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.dowValue.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_DOW, it)
            updateSwitchEnable(SwitchNode.ADAS_DOW)
        }
        viewModel.bscValue.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_BSC, it)
            updateSwitchEnable(SwitchNode.ADAS_BSC)
            updateSwitchEnable(SwitchNode.ADAS_GUIDES)
            updateRadioEnable(RadioNode.ADAS_SIDE_BACK_SHOW_AREA)
            val value = if (binding.adasSideBscSwitch.isChecked) "ON" else "OFF"
            setSwitchConfigParameters(OffLine.BSC, value)
        }
        viewModel.bsdValue.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_BSD, it)
            updateSwitchEnable(SwitchNode.ADAS_BSD)
        }
        viewModel.mebValue.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_MEB, it)
            updateSwitchEnable(SwitchNode.ADAS_MEB)
        }
        viewModel.guidesValue.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_GUIDES, it)
            updateSwitchEnable(SwitchNode.ADAS_GUIDES)
//            val value = if (binding.adasSideGuidesSwitch.isChecked) "ON" else "OFF"
//            setSwitchConfigParameters(OffLine.GUIDES, value)
        }
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.ADAS_DOW -> binding.adasSideDowSwitch
            SwitchNode.ADAS_BSC -> binding.adasSideBscSwitch
            SwitchNode.ADAS_BSD -> binding.adasSideBsdSwitch
            SwitchNode.ADAS_MEB -> binding.adasSideMebSwitch
            SwitchNode.ADAS_GUIDES -> binding.adasSideGuidesSwitch
            else -> null
        }
    }

    override fun obtainActiveByNode(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.ADAS_DOW -> viewModel.dowValue.value?.enable() ?: false
            SwitchNode.ADAS_BSC -> viewModel.bscValue.value?.enable() ?: false
            SwitchNode.ADAS_BSD -> viewModel.bsdValue.value?.enable() ?: false
            SwitchNode.ADAS_MEB -> viewModel.mebValue.value?.enable() ?: false
            SwitchNode.ADAS_GUIDES -> viewModel.guidesValue.value?.enable() ?: false
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun obtainDependByNode(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.ADAS_GUIDES -> binding.adasSideBscSwitch.isChecked
                    && obtainActiveByNode(SwitchNode.ADAS_BSC)
                    && (viewModel.guidesValue.value?.enable() ?: false)
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun obtainActiveByNode(node: RadioNode): Boolean {
        return when (node) {
            RadioNode.ADAS_SIDE_BACK_SHOW_AREA -> viewModel.showAreaValue.value?.enable() ?: false
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun obtainDependByNode(node: RadioNode): Boolean {
        return when (node) {
            RadioNode.ADAS_SIDE_BACK_SHOW_AREA -> binding.adasSideBscSwitch.isChecked
                    && obtainActiveByNode(SwitchNode.ADAS_BSC)
                    && (viewModel.showAreaValue.value?.enable() ?: false)
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    override fun onPostChecked(button: SwitchButton, status: Boolean) {
        if (0 == index) {
            dynamicEffect()
//            checkDisableOtherDiv(button, status)
        }
    }

    override fun findRadioByNode(node: RadioNode): TabControlView? {
        return when (node) {
            RadioNode.ADAS_SIDE_BACK_SHOW_AREA -> binding.adasSideShowAreaRadio
            else -> null
        }
    }

    override fun getRadioManager(): IRadioManager {
        return manager
    }

    private fun setSwitchListener() {
        binding.adasSideMebSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            //doUpdateSwitchOption(SwitchNode.ADAS_MEB, buttonView, isChecked)
            if (isChecked) {
                startVideo(R.raw.video_meb)
            } else {
                dynamicEffect()
            }
        }
        binding.adasSideDowSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_DOW, buttonView, isChecked)
            if (isChecked) {
                startVideo(R.raw.video_dow)
            } else {
                dynamicEffect()
            }
        }

        binding.adasSideBsdSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_BSD, buttonView, isChecked)
            if (isChecked) {
                startVideo(R.raw.video_bsd)
            } else {
                dynamicEffect()
            }
        }
        binding.adasSideBscSwitch.let {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                doUpdateSwitchOption(SwitchNode.ADAS_BSC, buttonView, isChecked)
                updateSwitchEnable(SwitchNode.ADAS_GUIDES)
                updateRadioEnable(RadioNode.ADAS_SIDE_BACK_SHOW_AREA)
                if (isChecked) {
                    startVideo(R.raw.video_camera)
                } else {
                    dynamicEffect()
                }
            }
        }
        binding.adasSideGuidesSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_GUIDES, buttonView, isChecked)
            if (isChecked) {
                startVideo(R.raw.video_auxiliary_line)
            } else {
                dynamicEffect()
            }
        }

        binding.adasSideMebSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_MEB, buttonView, isChecked)
        }

    }

    override fun doUpdateSwitchOption(node: SwitchNode, button: CompoundButton, status: Boolean) {
        val result = manager.doSetSwitchOption(node, status)
        index++
        if (!result && button is SwitchButton) {
            button.setCheckedImmediatelyNoEvent(!status)
        }
    }


    private fun startVideo(path: Int) {
        val url = "android.resource://" + activity?.packageName + "/" + path
        //binding.videoImage.visibility = View.GONE
        binding.video.setVideoURI(Uri.parse(url));
        binding.video.seekTo(0)
        binding.video.start()
    }

    private fun dynamicEffect() {
        binding.videoImage.visibility = View.VISIBLE
        if (VcuUtils.isCareLevel(Level.LEVEL3, Level.LEVEL4, expect = true)) {
            if (binding.adasSideBsdSwitch.isChecked) {
                binding.videoImage.setImageDrawable(activity?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.ic_lientang_auxiliary_9
                    )
                })
            } else {
                binding.videoImage.setImageDrawable(activity?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.intelligent_cruise
                    )
                })
            }
            return
        }
        if (binding.adasSideDowSwitch.isChecked && !binding.adasSideMebSwitch.isChecked &&
            !binding.adasSideBsdSwitch.isChecked && !binding.adasSideBscSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_1
                )
            })
        } else if (!binding.adasSideDowSwitch.isChecked && binding.adasSideMebSwitch.isChecked &&
            !binding.adasSideBsdSwitch.isChecked && !binding.adasSideBscSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_2
                )
            })
        } else if (!binding.adasSideDowSwitch.isChecked && !binding.adasSideMebSwitch.isChecked &&
            binding.adasSideBsdSwitch.isChecked && !binding.adasSideBscSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_3
                )
            })
        } else if (!binding.adasSideDowSwitch.isChecked && !binding.adasSideMebSwitch.isChecked &&
            !binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && !binding.adasSideGuidesSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_4
                )
            })
        } else if (!binding.adasSideDowSwitch.isChecked && !binding.adasSideMebSwitch.isChecked &&
            !binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && binding.adasSideGuidesSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_5
                )
            })
        } else if (binding.adasSideDowSwitch.isChecked && binding.adasSideMebSwitch.isChecked &&
            !binding.adasSideBsdSwitch.isChecked && !binding.adasSideBscSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_6
                )
            })
        } else if (binding.adasSideDowSwitch.isChecked && !binding.adasSideMebSwitch.isChecked &&
            binding.adasSideBsdSwitch.isChecked && !binding.adasSideBscSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_7
                )
            })
        } else if (binding.adasSideDowSwitch.isChecked && !binding.adasSideMebSwitch.isChecked &&
            !binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && !binding.adasSideGuidesSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_8
                )
            })
        } else if (binding.adasSideDowSwitch.isChecked && !binding.adasSideMebSwitch.isChecked &&
            !binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && binding.adasSideGuidesSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_9
                )
            })
        } else if (!binding.adasSideDowSwitch.isChecked && binding.adasSideMebSwitch.isChecked &&
            binding.adasSideBsdSwitch.isChecked && !binding.adasSideBscSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_10
                )
            })
        } else if (!binding.adasSideDowSwitch.isChecked && binding.adasSideMebSwitch.isChecked &&
            !binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && !binding.adasSideGuidesSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_11
                )
            })
        } else if (!binding.adasSideDowSwitch.isChecked && binding.adasSideMebSwitch.isChecked &&
            !binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && binding.adasSideGuidesSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_12
                )
            })
        } else if (!binding.adasSideDowSwitch.isChecked && !binding.adasSideMebSwitch.isChecked &&
            binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && binding.adasSideGuidesSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_13
                )
            })
        } else if (!binding.adasSideDowSwitch.isChecked && !binding.adasSideMebSwitch.isChecked &&
            binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && !binding.adasSideGuidesSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_14
                )
            })
        } else if (binding.adasSideDowSwitch.isChecked && binding.adasSideMebSwitch.isChecked &&
            binding.adasSideBsdSwitch.isChecked && !binding.adasSideBscSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_15
                )
            })
        } else if (!binding.adasSideDowSwitch.isChecked && binding.adasSideMebSwitch.isChecked &&
            binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && binding.adasSideGuidesSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_16
                )
            })
        } else if (binding.adasSideDowSwitch.isChecked && !binding.adasSideMebSwitch.isChecked &&
            binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && !binding.adasSideGuidesSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_17
                )
            })
        } else if (binding.adasSideDowSwitch.isChecked && binding.adasSideMebSwitch.isChecked &&
            !binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && !binding.adasSideGuidesSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_18
                )
            })
        } else if (binding.adasSideDowSwitch.isChecked && binding.adasSideMebSwitch.isChecked &&
            !binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && binding.adasSideGuidesSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_19
                )
            })
        } else if (!binding.adasSideDowSwitch.isChecked && binding.adasSideMebSwitch.isChecked &&
            binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && binding.adasSideGuidesSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_20
                )
            })
        } else if (!binding.adasSideDowSwitch.isChecked && binding.adasSideMebSwitch.isChecked &&
            binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && !binding.adasSideGuidesSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_21
                )
            })
        } else if (binding.adasSideDowSwitch.isChecked && !binding.adasSideMebSwitch.isChecked &&
            binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && binding.adasSideGuidesSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_22
                )
            })
        } else if (binding.adasSideDowSwitch.isChecked && binding.adasSideMebSwitch.isChecked &&
            binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && !binding.adasSideGuidesSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_23
                )
            })
        } else if (binding.adasSideDowSwitch.isChecked && binding.adasSideMebSwitch.isChecked &&
            binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && binding.adasSideGuidesSwitch.isChecked
        ) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_24
                )
            })
        } else {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.intelligent_cruise
                )
            })
        }
    }

//    private fun checkDisableOtherDiv(swb: SwitchButton, status: Boolean) {
//        if (swb == binding.adasSideBscSwitch) {
//            val childCount = binding.container.childCount
//            val intRange = 0 until childCount
//            intRange.forEach {
//                val childAt = binding.container.getChildAt(it)
//                if (null != childAt && childAt != binding.driveBsdCamera) {
//                    childAt.alpha = if (status) 1.0f else 0.6f
//                    updateViewEnable(childAt, status)
//                }
//            }
//        }
//    }

//    private fun updateViewEnable(view: View?, status: Boolean) {
//        if (null == view) {
//            return
//        }
//        if (view is SwitchButton) {
//            view.isEnabled = status
//            return
//        }
//        if (view is AppCompatImageView) {
//            view.isEnabled = status
//            return
//        }
//        if (view is TabControlView) {
//            view.updateEnable(status)
//            return
//        }
//        if (view is ViewGroup) {
//            val childCount = view.childCount
//            val intRange = 0 until childCount
//            intRange.forEach { updateViewEnable(view.getChildAt(it), status) }
//        }
//    }
}