package com.chinatsp.vehicle.settings.fragment.doors

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.bean.SwitchState
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.access.BackMirrorManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.IRoute
import com.chinatsp.vehicle.settings.ISwitchAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.Toast
import com.chinatsp.vehicle.settings.databinding.CarMirrorFragmentBinding
import com.chinatsp.vehicle.settings.fragment.doors.dialog.AngleDialogFragment
import com.chinatsp.vehicle.settings.vm.accress.MirrorViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CarMirrorFragment : BaseFragment<MirrorViewModel, CarMirrorFragmentBinding>(),
    ISwitchAction, AngleDialogFragment.IAngleInvoke {

    private val manager: BackMirrorManager
        get() = BackMirrorManager.instance

    private var angleStatus: Int = Constant.DEFAULT

    private val map: HashMap<Int, View> = HashMap()

    override fun getLayoutId(): Int {
        return R.layout.car_mirror_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initClickView()

        initViewsDisplay()

        initSwitchOption()
        setSwitchListener()
        addSwitchLiveDataListener()

        setCheckedChangeListener()

        //=0x1 Memorize fail OR 0x2 Memorize success进行记忆成功/失败的提示。
        viewModel.angleReturnSignal.observe(this) {
            if (Constant.ANGLE_SAVE != angleStatus) {
                return@observe
            }
            Toast.showToast(context, if (0x02 == it) "保存成功" else "保存失败", true)
            angleStatus = Constant.DEFAULT
        }

        updateOptionActive()
        initRouteListener()
    }

    private fun initClickView() {
        map[1] = binding.modifyAngle
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
            binding.modifyAngle -> {
                if (isAngle) {
                    showReverseAngleFragment()
                }
            }
        }
    }

    private fun updateOptionActive() {
        updateSwitchEnable(SwitchNode.BACK_MIRROR_FOLD)
        updateSwitchEnable(SwitchNode.BACK_MIRROR_DOWN)
        updateEnable(binding.backMirrorDownAngle, true, isAngle)
    }


    private fun initViewsDisplay() {
        if (VcuUtils.isCareLevel(Level.LEVEL3)) {
            binding.reverseAngle.visibility = View.GONE
            binding.line2.visibility = View.GONE
        }
    }

    private fun setSwitchListener() {
        binding.backMirrorFoldSwitch.let {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
//                checkDisableOtherDiv(it, isChecked)
//                val node = SwitchNode.BACK_MIRROR_FOLD
//                val result = manager.doSetSwitchOption(node, isChecked)
//                takeUnless { result }?.recoverSwitch(buttonView as SwitchButton, !isChecked, false)
                doUpdateSwitchOption(SwitchNode.BACK_MIRROR_FOLD, buttonView, isChecked)

//                updateOptionActive()
                updateSwitchEnable(SwitchNode.BACK_MIRROR_FOLD)
            }
        }
        binding.backMirrorDownSwitch.let {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
//                val result = manager.doSetSwitchOption(SwitchNode.BACK_MIRROR_DOWN, isChecked)
//                takeUnless { result }?.recoverSwitch(buttonView as SwitchButton, !isChecked, false)
                doUpdateSwitchOption(SwitchNode.BACK_MIRROR_DOWN, buttonView, isChecked)
//                checkDisableOtherDiv(it, isChecked)
                updateOptionActive()
            }
        }
    }

    private fun addSwitchLiveDataListener() {
        viewModel.mirrorFoldFunction.observe(this) {
            doUpdateSwitch(SwitchNode.BACK_MIRROR_FOLD, it!!)
            updateSwitchEnable(SwitchNode.BACK_MIRROR_FOLD)
//            updateOptionActive()
        }
        viewModel.mirrorDownFunction.observe(this) {
            doUpdateSwitch(SwitchNode.BACK_MIRROR_DOWN, it!!)
            updateSwitchEnable(SwitchNode.BACK_MIRROR_DOWN)
            updateEnable(binding.backMirrorDownAngle, true, isAngle)
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.BACK_MIRROR_FOLD, viewModel.mirrorFoldFunction)
        initSwitchOption(SwitchNode.BACK_MIRROR_DOWN, viewModel.mirrorDownFunction)
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.BACK_MIRROR_FOLD -> binding.backMirrorFoldSwitch
            SwitchNode.BACK_MIRROR_DOWN -> binding.backMirrorDownSwitch
            else -> null
        }
    }

    override fun obtainActiveByNode(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.BACK_MIRROR_FOLD -> viewModel.mirrorFoldFunction.value?.enable() ?: false
            SwitchNode.BACK_MIRROR_DOWN -> viewModel.mirrorDownFunction.value?.enable() ?: false
            else -> super.obtainActiveByNode(node)
        }
    }

    override fun obtainDependByNode(node: SwitchNode): Boolean {
        return when (node) {
            SwitchNode.BACK_MIRROR_FOLD -> true
            SwitchNode.BACK_MIRROR_DOWN -> (viewModel.mirrorFoldFunction.value?.enable() ?: false)
            else -> super.obtainDependByNode(node)
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    override fun initSwitchOption(node: SwitchNode, liveData: LiveData<SwitchState>) {
        val status = liveData.value?.get() ?: false
        if (node == SwitchNode.BACK_MIRROR_FOLD) {
//            checkDisableOtherDiv(binding.accessMirrorMirrorFoldSw, status)
        }
//        checkDisableOtherDiv(binding.backMirrorDownSwitch, status)
        liveData.value?.let {
            doUpdateSwitch(node, it, true)
        }
    }

    private fun setCheckedChangeListener() {
        binding.modifyAngle.setOnClickListener(this::onViewClick)
    }

    private fun showReverseAngleFragment() {
        val fragment = AngleDialogFragment()
        activity?.supportFragmentManager?.let { it ->
            fragment.angleInvoke = this
            fragment.show(it, fragment.javaClass.simpleName)
        }
    }

//    private fun checkDisableOtherDiv(swb: SwitchButton, status: Boolean) {
//        if (swb == binding.accessMirrorMirrorFoldSw) {
//            val childCount = binding.constraint.childCount
//            val intRange = 0 until childCount
//            intRange.forEach {
//                val childAt = binding.constraint.getChildAt(it)
//                if (null != childAt && childAt != binding.carTrunkElectricFunction) {
//                    childAt.alpha = if (status) 1.0f else 0.6f
//                    updateViewEnable(childAt, status)
//                }
//            }
//        } else if (swb == binding.backMirrorDownSwitch) {
//            val childCount = binding.modifyAngle.childCount
//            val intRange = 0 until childCount
//            intRange.forEach {
//                val childAt = binding.modifyAngle.getChildAt(it)
//                if (null != childAt && childAt != binding.reverseAngle) {
//                    childAt.alpha = if (status) 1.0f else 0.6f
//                    updateViewEnable(childAt, status)
//                }
//            }
//        }
//    }
//
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

    override fun onAngleUpdate(angleValue: Int) {
        this.angleStatus = angleValue
    }

    private val isAngle: Boolean
        get() = binding.backMirrorDownSwitch.isChecked && (viewModel.mirrorFoldFunction.value?.enable()
            ?: false)

    override fun onDestroy() {
        super.onDestroy()
        //外后视镜位置
        val intent = Intent("com.chinatsp.vehiclenetwork.usercenter")
        val json = "{\"externalMirrorPosition\":\"" + 1024 + "\"}"
        intent.putExtra("app", "com.chinatsp.vehicle.settings")
        intent.putExtra("rearviewMirror", json)
        intent.setPackage("com.chinatsp.usercenter")
        activity?.startService(intent)
    }

}