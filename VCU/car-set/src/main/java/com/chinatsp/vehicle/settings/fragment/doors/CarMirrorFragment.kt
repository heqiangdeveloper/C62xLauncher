package com.chinatsp.vehicle.settings.fragment.doors

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.LiveData
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.ISwitchManager
import com.chinatsp.settinglib.manager.access.BackMirrorManager
import com.chinatsp.settinglib.optios.SwitchNode
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.ISwitchAction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.Toast
import com.chinatsp.vehicle.settings.databinding.CarMirrorFragmentBinding
import com.chinatsp.vehicle.settings.fragment.doors.dialog.AngleDialogFragment
import com.chinatsp.vehicle.settings.vm.accress.MirrorViewModel
import com.common.library.frame.base.BaseFragment
import com.common.xui.widget.button.switchbutton.SwitchButton
import com.common.xui.widget.tabbar.TabControlView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CarMirrorFragment : BaseFragment<MirrorViewModel, CarMirrorFragmentBinding>(),
    ISwitchAction, AngleDialogFragment.IAngleInvoke {

    private val manager: BackMirrorManager
        get() = BackMirrorManager.instance

    private var angleStatus: Int = Constant.DEFAULT

    override fun getLayoutId(): Int {
        return R.layout.car_mirror_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
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

    }

    private fun initViewsDisplay() {
        if (VcuUtils.isCareLevel(Level.LEVEL3)) {
            binding.reverseAngle.visibility = View.GONE
            binding.line2.visibility = View.GONE
        }
    }

    private fun setSwitchListener() {
        binding.accessMirrorMirrorFoldSw.let {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                checkDisableOtherDiv(it, isChecked)
                val node = SwitchNode.BACK_MIRROR_FOLD
                val result = manager.doSetSwitchOption(node, isChecked)
                takeUnless { result }?.doUpdateSwitch(buttonView as SwitchButton, !isChecked, false)
                if (buttonView.isChecked) {
                    binding.rearviewMirror.setText(R.string.car_mirror_automatic_folding_open)
                } else {
                    binding.rearviewMirror.setText(R.string.car_mirror_automatic_folding_close)
                }
            }
        }
        binding.backMirrorDownSwitch.let {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                val result = manager.doSetSwitchOption(SwitchNode.BACK_MIRROR_DOWN, isChecked)
                takeUnless { result }?.doUpdateSwitch(buttonView as SwitchButton, !isChecked, false)
                checkDisableOtherDiv(it, isChecked)
            }
        }
    }

    private fun addSwitchLiveDataListener() {
        viewModel.mirrorFoldFunction.observe(this) {
            doUpdateSwitch(SwitchNode.BACK_MIRROR_FOLD, it!!)
        }
        viewModel.mirrorDownFunction.observe(this) {
            doUpdateSwitch(SwitchNode.BACK_MIRROR_DOWN, it!!)
        }
    }

    private fun initSwitchOption() {
        initSwitchOption(SwitchNode.BACK_MIRROR_FOLD, viewModel.mirrorFoldFunction)
        initSwitchOption(SwitchNode.BACK_MIRROR_DOWN, viewModel.mirrorDownFunction)
    }

    override fun findSwitchByNode(node: SwitchNode): SwitchButton? {
        return when (node) {
            SwitchNode.BACK_MIRROR_FOLD -> binding.accessMirrorMirrorFoldSw
            SwitchNode.BACK_MIRROR_DOWN -> binding.backMirrorDownSwitch
            else -> null
        }
    }

    override fun getSwitchManager(): ISwitchManager {
        return manager
    }

    override fun initSwitchOption(node: SwitchNode, liveData: LiveData<Boolean>) {
        val status = liveData.value ?: false
        if (node == SwitchNode.BACK_MIRROR_FOLD) {
            val resId = if (status) R.string.car_mirror_automatic_folding_open
                        else R.string.car_mirror_automatic_folding_close
            binding.rearviewMirror.setText(resId)
            checkDisableOtherDiv(binding.accessMirrorMirrorFoldSw, status)
        }
        checkDisableOtherDiv(binding.backMirrorDownSwitch, status)
        doUpdateSwitch(node, status, true)
    }

    private fun setCheckedChangeListener() {
        binding.modifyAngle.setOnClickListener {
            if (binding.accessMirrorMirrorFoldSw.isChecked
                && binding.backMirrorDownSwitch.isChecked) {
                showReverseAngleFragment()
            }
        }
    }

    private fun showReverseAngleFragment() {
        val fragment = AngleDialogFragment()
        activity?.supportFragmentManager?.let { it ->
            fragment.angleInvoke = this
            fragment.show(it, fragment.javaClass.simpleName)
        }
    }

    private fun checkDisableOtherDiv(swb: SwitchButton, status: Boolean) {
        if (swb == binding.accessMirrorMirrorFoldSw) {
            val childCount = binding.constraint.childCount
            val intRange = 0 until childCount
            intRange.forEach {
                val childAt = binding.constraint.getChildAt(it)
                if (null != childAt && childAt != binding.carTrunkElectricFunction) {
                    childAt.alpha = if (status) 1.0f else 0.6f
                    updateViewEnable(childAt, status)
                }
            }
        } else if (swb == binding.backMirrorDownSwitch) {
            val childCount = binding.modifyAngle.childCount
            val intRange = 0 until childCount
            intRange.forEach {
                val childAt = binding.modifyAngle.getChildAt(it)
                if (null != childAt && childAt != binding.reverseAngle) {
                    childAt.alpha = if (status) 1.0f else 0.6f
                    updateViewEnable(childAt, status)
                }
            }
        }
    }

    private fun updateViewEnable(view: View?, status: Boolean) {
        if (null == view) {
            return
        }
        if (view is SwitchButton) {
            view.isEnabled = status
            return
        }
        if (view is AppCompatImageView) {
            view.isEnabled = status
            return
        }
        if (view is TabControlView) {
            view.updateEnable(status)
            return
        }
        if (view is ViewGroup) {
            val childCount = view.childCount
            val intRange = 0 until childCount
            intRange.forEach { updateViewEnable(view.getChildAt(it), status) }
        }
    }

    override fun onAngleUpdate(angleValue: Int) {
        this.angleStatus = angleValue
    }

}