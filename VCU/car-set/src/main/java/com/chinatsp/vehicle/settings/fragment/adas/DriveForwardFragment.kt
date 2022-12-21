package com.chinatsp.vehicle.settings.fragment.adas

import android.graphics.Color
import android.graphics.PixelFormat
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.adas.ForwardManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.settings.HintHold
import com.chinatsp.vehicle.settings.IRoute
import com.chinatsp.vehicle.settings.ISwitchAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.DriveForwardFragmentBinding
import com.chinatsp.vehicle.settings.vm.adas.ForwardViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriveForwardFragment : BaseFragment<ForwardViewModel, DriveForwardFragmentBinding>(),
    ISwitchAction {

    private val manager: ForwardManager
        get() = ForwardManager.instance

    private val map: HashMap<Int, View> = HashMap()

    override fun getLayoutId(): Int {
        return R.layout.drive_forward_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initVideoListener()

        initSwitchOption()
        addSwitchLiveDataListener()
        setSwitchListener()

        initDetailsClickListener()
        initClickView()
        initRouteListener()
    }

    private fun initClickView() {
        map[1] = binding.driveWarningFcwDetails
        map[2] = binding.driveAebDetails
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
                                map[lv3Node.uid]?.run { onViewClick(this, lv3Node.uid) }
                            }
                        }
                }
            }
        }
    }

    private fun onViewClick(view: View, clickUid: Int) {
        onViewClick(view)
        obtainRouter()?.resetLevelRouter(pid, uid, clickUid)
    }

    private fun onViewClick(it: View) {
        when (it) {
            binding.driveWarningFcwDetails -> updateHintMessage(R.string.drive_warning_fcw,
                R.string.fcw_details)
            binding.driveAebDetails -> updateHintMessage(R.string.drive_aeb_title,
                R.string.aeb_details)
        }
    }

    private fun initDetailsClickListener() {
        binding.driveWarningFcwDetails.setOnClickListener(this::onViewClick)
        binding.driveAebDetails.setOnClickListener(this::onViewClick)
    }

    private fun updateHintMessage(title: Int, content: Int) {
        HintHold.setTitle(title)
        HintHold.setContent(content)
        val fragment = DetailsDialogFragment()
        activity?.supportFragmentManager?.let {
            fragment.show(it, fragment.javaClass.simpleName)
        }
    }

    private fun initVideoListener() {
//        val uri = "android.resource://" + activity?.packageName + "/" + R.raw.video_fcw
        binding.video.holder.setFormat(PixelFormat.TRANSPARENT)
        binding.video.setAudioFocusRequest(AudioManager.AUDIOFOCUS_NONE)
//        binding.video.setVideoURI(Uri.parse(uri))
        binding.video.setOnCompletionListener {
            dynamicEffect()
        }
        binding.video.setOnErrorListener { _, _, _ ->
            dynamicEffect()
            true
        }
        binding.video.setOnPreparedListener {
            it.setOnInfoListener { _, _, _ ->
                binding.video.setBackgroundColor(Color.TRANSPARENT)
                binding.videoImage.visibility = View.GONE
                true
            }
        }
    }

    private fun addSwitchLiveDataListener() {
        viewModel.fcwFunction.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_FCW, it)
            executeFcwAnimation(it.get())
        }
        viewModel.aebFunction.observe(this) {
            doUpdateSwitch(SwitchNode.ADAS_AEB, it)
            executeAebAnimation(it.get())
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.ADAS_FCW, viewModel.fcwFunction)
        initSwitchOption(SwitchNode.ADAS_AEB, viewModel.aebFunction)

        updateSwitchEnable(SwitchNode.ADAS_FCW)
        updateSwitchEnable(SwitchNode.ADAS_AEB)
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.ADAS_FCW -> binding.adasForwardFcwSwitch
            SwitchNode.ADAS_AEB -> binding.adasForwardAebSwitch
            else -> null
        }
    }

    override fun obtainActiveByNode(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.ADAS_FCW -> viewModel.fcwFunction.value?.enable() ?: false
            SwitchNode.ADAS_AEB -> viewModel.aebFunction.value?.enable() ?: false
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    override fun onPostChecked(button: SwitchButton, status: Boolean) {
//        dynamicEffect()
    }

    private fun setSwitchListener() {
        binding.adasForwardFcwSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            executeFcwAnimation(isChecked)
            doUpdateSwitchOption(SwitchNode.ADAS_FCW, buttonView, isChecked)
        }
        binding.adasForwardAebSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                executeAebAnimation(true)
                doUpdateSwitchOption(SwitchNode.ADAS_AEB, buttonView, isChecked)
            } else {
                val fragment = CloseBrakeDialogFragment()
                activity?.supportFragmentManager?.let {
                    fragment.show(it, fragment.javaClass.simpleName)
                }
                binding.adasForwardAebSwitch.setCheckedImmediatelyNoEvent(!isChecked)
            }
        }
    }

    private fun executeAebAnimation(status: Boolean) {
        if (!status) {
            dynamicEffect()
        } else {
            binding.video.pause()
            val url = "android.resource://" + activity?.packageName + "/" + R.raw.video_abe
            binding.video.setVideoURI(Uri.parse(url))
            binding.video.start()
        }
    }

    private fun executeFcwAnimation(status: Boolean) {
        if (!status) {
            dynamicEffect()
        } else {
            binding.video.pause()
            val uri = "android.resource://" + activity?.packageName + "/" + R.raw.video_fcw
            binding.video.setVideoURI(Uri.parse(uri))
            binding.video.start()
        }
    }

    private fun dynamicEffect() {
        binding.videoImage.visibility = View.VISIBLE
        val isFcw = binding.adasForwardFcwSwitch.isChecked
        val isAeb = binding.adasForwardAebSwitch.isChecked
        val source = if (isFcw && isAeb) {
            R.drawable.ic_emergency_braking_1
        } else if (!isFcw && isAeb) {
            R.drawable.ic_emergency_braking
        } else if (isFcw && !isAeb) {
            R.drawable.ic_prior_collisio
        } else {
            R.drawable.intelligent_cruise
        }
        binding.videoImage.setImageDrawable(activity?.let { context ->
            ContextCompat.getDrawable(context, source)
        })
    }

    override fun onDestroy() {
        binding.video.pause()
        binding.video.stopPlayback()
        super.onDestroy()
    }
}