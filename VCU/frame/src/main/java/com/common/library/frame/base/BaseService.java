package com.common.library.frame.base;

import android.app.Service;


/**
 * 框架基于Google官方的 JetPack 构建，在使用  时，需遵循一些规范：
 *
 * <p>如果您继承使用了 BaseService 或其子类，你需要参照如下方式添加 @AndroidEntryPoint 注解
 *
 * <p>Example:
 * <pre>
 *    &#64;AndroidEntryPoint
 *    public class YourService extends BaseService {
 *
 *    }
 * </pre>
 */
public abstract class BaseService extends Service {

}
