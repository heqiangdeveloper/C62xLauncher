package com.common.library.frame.base;

import android.app.IntentService;


/**
 * 框架基于 Google 官方的 JetPack 构建，在使用  时，需遵循一些规范：
 *
 * <p>如果您继承使用了 BaseIntentService 或其子类，你需要参照如下方式添加 @AndroidEntryPoint 注解
 *
 * <p>Example:
 * <pre>
 *    &#64;AndroidEntryPoint
 *    public class YourIntentService extends BaseIntentService {
 *
 *    }
 * </pre>
 */
public abstract class BaseIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BaseIntentService(String name) {
        super(name);
    }

}
