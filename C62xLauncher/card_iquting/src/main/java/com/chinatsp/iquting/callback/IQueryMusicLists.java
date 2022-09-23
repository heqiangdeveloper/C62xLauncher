package com.chinatsp.iquting.callback;

import com.tencent.wecarflow.contentsdk.bean.AreaContentResponseBean;

public interface IQueryMusicLists {
    void onSuccess(AreaContentResponseBean areaContentResponseBean);
    void onFail(int failCode);
}
