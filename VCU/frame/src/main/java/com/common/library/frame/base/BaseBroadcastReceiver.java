package com.common.library.frame.base;

import android.content.BroadcastReceiver;


/**
 * 框架基于 Google 官方的 JetPack 构建，在使用  时，需遵循一些规范：
 *
 * <p>如果您继承使用了 BaseBroadcastReceiver 或其子类，你需要参照如下方式添加 @AndroidEntryPoint 注解
 *
 * <p>Example:
 * <pre>
 *    &#64;AndroidEntryPoint
 *    public class YourBroadcastReceiver extends BaseBroadcastReceiver {
 *
 *    }
 * </pre>
 */
public abstract class BaseBroadcastReceiver extends BroadcastReceiver {

}
