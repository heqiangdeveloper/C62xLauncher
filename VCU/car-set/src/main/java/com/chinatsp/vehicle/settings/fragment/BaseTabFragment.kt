package com.chinatsp.vehicle.settings.fragment

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.Constant
import com.chinatsp.vehicle.settings.Node
import com.common.library.frame.base.BaseFragment
import com.common.library.frame.base.BaseViewModel

/**
 * 框架基于 Google 官方的 JetPack 构建，在使用  时，需遵循一些规范：
 *
 *
 * 如果您继承使用了 BaseFragment 或其子类，你需要参照如下方式添加 @AndroidEntryPoint 注解
 *
 *
 * Example:
 * <pre>
 * &#64;AndroidEntryPoint
 * public class YourFragment extends BaseFragment {
 *
 * }
</pre> *
 *
 *
 */
abstract class BaseTabFragment<VM : BaseViewModel<*>, VDB : ViewDataBinding> :
    BaseFragment<VM, VDB>() {

    lateinit var tabOptions: List<View>

    abstract val tabLocation: MutableLiveData<Int>

    protected fun syncRouterLocation(node: Node) {
        node.takeIf { it.valid && it.uid == uid }?.let {
            val child = it.cnode
            child?.takeIf { c -> c.valid && c.uid in tabOptions.indices }?.let { c ->
                val location = tabOptions[c.uid].id
                if (tabLocation.value != location) {
                    tabLocation.postValue(location)
                }
                resetRouter(uid, c.uid)
            }
        }
    }

    abstract fun resetRouter(lv1: Int, lv2: Int, lv3: Int = Constant.INVALID);

}