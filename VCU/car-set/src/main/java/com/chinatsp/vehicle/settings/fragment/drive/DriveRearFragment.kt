package com.chinatsp.vehicle.settings.fragment.drive

import android.graphics.Color
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.IOptionManager
import com.chinatsp.settinglib.manager.adas.SideBackManager
import com.chinatsp.settinglib.optios.RadioNode
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.HintHold
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveRearFragmentBinding
import com.chinatsp.vehicle.settings.fragment.drive.dialog.DetailsDialogFragment
import com.chinatsp.vehicle.settings.vm.adas.SideViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveRearFragment : BaseFragment<SideViewModel, DriveRearFragmentBinding>() {
    var index: Int = 0;
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
    }

    private fun initDetailsClickListener() {
        binding.driveBsdDetails.setOnClickListener {
            updateHintMessage(R.string.drive_bsd_title, R.string.bsd_details)
        }
        binding.driveBsdCameraDetails.setOnClickListener {
            updateHintMessage(R.string.drive_bsd_camera_title, R.string.bsc_details)
        }
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
                doUpdateRadio(
                    RadioNode.ADAS_SIDE_BACK_SHOW_AREA,
                    value,
                    viewModel.showAreaValue,
                    it
                )
            }
        }
    }

    private fun initRadioOption(node: RadioNode, liveData: LiveData<Int>) {
        val value = liveData.value ?: node.default
        doUpdateRadio(node, value, isInit = true)
    }

    private fun doUpdateRadio(
        node: RadioNode,
        value: String,
        liveData: LiveData<Int>,
        tabView: TabControlView
    ) {
        val result = isCanToInt(value) && manager.doSetRadioOption(node, value.toInt())
        tabView.takeIf { !result }?.let {
            val result = node.obtainSelectValue(liveData.value!!)
            it.setSelection(result.toString(), true)
        }
    }

    private fun doUpdateRadio(
        node: RadioNode,
        value: Int,
        immediately: Boolean = false,
        isInit: Boolean = false
    ) {
        val tabView = when (node) {
            RadioNode.ADAS_SIDE_BACK_SHOW_AREA -> binding.adasSideShowAreaRadio
            else -> null
        }
        tabView?.let {
            bindRadioData(node, tabView, isInit)
            val result = node.obtainSelectValue(value)
            doUpdateRadio(it, result, immediately)
        }
    }

    private fun doUpdateRadio(tabView: TabControlView, value: Int, immediately: Boolean = false) {
        tabView.setSelection(value.toString(), true)
    }

    private fun bindRadioData(node: RadioNode, tabView: TabControlView, isInit: Boolean) {
        if (isInit) {
            val names = tabView.nameArray.map { it.toString() }.toTypedArray()
            val values = node.set.values.map { it.toString() }.toTypedArray()
            tabView.setItems(names, values)
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.ADAS_DOW, viewModel.dowValue)
        initSwitchOption(SwitchNode.ADAS_BSC, viewModel.bscValue)
        initSwitchOption(SwitchNode.ADAS_BSD, viewModel.bsdValue)
        initSwitchOption(SwitchNode.ADAS_GUIDES, viewModel.guidesValue)
    }

    private fun addSwitchLiveDataListener() {
        viewModel.dowValue.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_DOW, it)
        }
        viewModel.bscValue.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_BSC, it)
        }
        viewModel.bsdValue.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_BSD, it)
        }
        viewModel.guidesValue.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_GUIDES, it)
        }
    }

    private fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: node.default
        doUpdateSwitch(node, status, true)
    }

    private fun doUpdateSwitch(node: SwitchNode, status: Boolean, immediately: Boolean = false) {
        val swb = when (node) {
            SwitchNode.ADAS_DOW -> binding.adasSideDowSwitch
            SwitchNode.ADAS_BSC -> binding.adasSideBscSwitch
            SwitchNode.ADAS_BSD -> binding.adasSideBsdSwitch
            SwitchNode.ADAS_GUIDES -> binding.adasSideGuidesSwitch
            else -> null
        }
        takeIf { null != swb }?.doUpdateSwitch(swb!!, status, immediately)
    }

    private fun doUpdateSwitch(swb: SwitchButton, status: Boolean, immediately: Boolean = false) {
        if (!immediately) {
            swb.setCheckedNoEvent(status)
        } else {
            swb.setCheckedImmediatelyNoEvent(status)
        }
        if (index == 0) {//第一次进来加载一次
            dynamicEffect()
        }
    }

    private fun setSwitchListener() {
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
        binding.adasSideBscSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            doUpdateSwitchOption(SwitchNode.ADAS_BSC, buttonView, isChecked)
            if (isChecked) {
                startVideo(R.raw.video_camera)
            } else {
                dynamicEffect()
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
    }

    private fun doUpdateSwitchOption(node: SwitchNode, button: CompoundButton, status: Boolean) {
        val result = manager.doSetSwitchOption(node, status)
        index++
        if (!result && button is SwitchButton) {
            button.setCheckedImmediatelyNoEvent(!status)
        }
    }

    private fun isCanToInt(value: String?): Boolean {
        return null != value && value.isNotBlank() && value.matches(Regex("\\d+"))
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
        if (binding.adasSideDowSwitch.isChecked && binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && binding.adasSideGuidesSwitch.isChecked) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_1
                )
            })
        } else if (binding.adasSideDowSwitch.isChecked && !binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && !binding.adasSideGuidesSwitch.isChecked) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_2
                )
            })
        } else if (binding.adasSideDowSwitch.isChecked && binding.adasSideBsdSwitch.isChecked && !binding.adasSideBscSwitch.isChecked && !binding.adasSideGuidesSwitch.isChecked) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_3
                )
            })
        } else if (!binding.adasSideDowSwitch.isChecked && binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && binding.adasSideGuidesSwitch.isChecked) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_4
                )
            })
        } else if (!binding.adasSideDowSwitch.isChecked && binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && !binding.adasSideGuidesSwitch.isChecked) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_5
                )
            })
        } else if (binding.adasSideDowSwitch.isChecked && !binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && binding.adasSideGuidesSwitch.isChecked) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_6
                )
            })
        } else if (!binding.adasSideDowSwitch.isChecked && !binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && binding.adasSideGuidesSwitch.isChecked) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_7
                )
            })
        } else if (binding.adasSideDowSwitch.isChecked && !binding.adasSideBsdSwitch.isChecked && !binding.adasSideBscSwitch.isChecked && !binding.adasSideGuidesSwitch.isChecked) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_8
                )
            })
        } else if (!binding.adasSideDowSwitch.isChecked && binding.adasSideBsdSwitch.isChecked && !binding.adasSideBscSwitch.isChecked && !binding.adasSideGuidesSwitch.isChecked) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_9
                )
            })
        } else if (!binding.adasSideDowSwitch.isChecked && !binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && !binding.adasSideGuidesSwitch.isChecked) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_10
                )
            })
        } else if (binding.adasSideDowSwitch.isChecked && binding.adasSideBsdSwitch.isChecked && binding.adasSideBscSwitch.isChecked && !binding.adasSideGuidesSwitch.isChecked) {
            binding.videoImage.setImageDrawable(activity?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.ic_lientang_auxiliary_11
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
}