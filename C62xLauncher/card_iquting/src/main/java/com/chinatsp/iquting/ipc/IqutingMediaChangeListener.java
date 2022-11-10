package com.chinatsp.iquting.ipc;

import com.tencent.wecarflow.controlsdk.MediaInfo;
import com.tencent.wecarflow.controlsdk.data.NavigationInfo;

public interface IqutingMediaChangeListener {
    void onMediaChange(MediaInfo mediaInfo);
    void onMediaChange(MediaInfo mediaInfo, NavigationInfo navigationInfo);
    void onFavorChange(boolean b, String s);
    void onModeChange(int i);
    void onPlayListChange();
}
