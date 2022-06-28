package com.common.library.frame.base

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import com.chinatsp.vehicle.settings.Node

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
abstract class BaseTabFragment<VM : BaseViewModel<*>, VDB : ViewDataBinding> : BaseFragment<VM, VDB>() {

    lateinit var tabOptions: List<View>

    abstract val tabLocation: MutableLiveData<Int>

    abstract val nodeId: Int

    protected fun initRouteLocation(it: Node) {
        if (it.valid && it.presentId == nodeId && it.id in tabOptions.indices) {
            tabLocation.value = tabOptions[it.id].id
            it.valid = false
        }
    }

}