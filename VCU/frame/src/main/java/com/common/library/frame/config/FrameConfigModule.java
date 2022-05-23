package com.common.library.frame.config;


/**
 *
 */
public abstract class FrameConfigModule implements AppliesOptions {

    /**
     * 是否启用解析配置
     *
     * @return 默认返回{@code true}
     */
    public boolean isManifestParsingEnabled() {
        return true;
    }

}
