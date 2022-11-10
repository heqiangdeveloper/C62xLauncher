package com.chinatsp.iquting.service;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.chinatsp.iquting.IQuTingCardView;
import com.chinatsp.iquting.callback.INetworkChangeListener;
import com.chinatsp.iquting.callback.IQueryIqutingLoginStatus;
import com.chinatsp.iquting.callback.IQueryMusicLists;
import com.chinatsp.iquting.callback.ITabClickCallback;
import com.chinatsp.iquting.configs.IqutingConfigs;
import com.chinatsp.iquting.event.ContentConnectEvent;
import com.chinatsp.iquting.event.PlayConnectEvent;
import com.chinatsp.iquting.state.NormalState;
import com.chinatsp.iquting.state.UnLoginState;
import com.tencent.wecarflow.contentsdk.ConnectionListener;
import com.tencent.wecarflow.contentsdk.ContentManager;
import com.tencent.wecarflow.contentsdk.bean.AreaContentResponseBean;
import com.tencent.wecarflow.contentsdk.bean.BaseSongItemBean;
import com.tencent.wecarflow.contentsdk.callback.AreaContentResult;
import com.tencent.wecarflow.controlsdk.BindListener;
import com.tencent.wecarflow.controlsdk.FlowPlayControl;
import com.tencent.wecarflow.controlsdk.QueryCallback;
import com.tencent.wecarflow.controlsdk.data.UserInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import launcher.base.network.NetworkObserver;
import launcher.base.network.NetworkStateReceiver;
import launcher.base.network.NetworkUtils;

public class IqutingBindService {
    private static final String TAG = "IQuTingCardView";
    private static final String TAG_CONTENT = "heqqcontent";
    private Context mContext;
    private boolean isLogin = false;
    private boolean isNetworkEnabled = false;
    private List<INetworkChangeListener> iNetworkChangeListenerLists = new ArrayList<>();
    private AreaContentResponseBean mAreaContentResponseBeanDaily;
    private AreaContentResponseBean mAreaContentResponseBeanRank;
    public static final int TYPE_DAILYSONGS = 1;
    public static final int TYPE_RANKSONGS = 2;
    private ITabClickCallback iTabClickCallback;
    private IqutingBindService() {}

    private static class Holder {
        public static IqutingBindService serice = new IqutingBindService();
    }

    public static IqutingBindService getInstance() {
        return Holder.serice;
    }

    public void setTabClickListener(ITabClickCallback iTabClickCallback){
        this.iTabClickCallback = iTabClickCallback;
    }

    public void setTabClickEvent(int type){
        if(iTabClickCallback != null){
            iTabClickCallback.onTabChanged(type);
        }
    }

    //注册播放服务
    public void bindPlayService(Context context) {
        this.mContext = context;
        FlowPlayControl.InitParams params = new FlowPlayControl.InitParams();
        params.setAutoRebind(true);
        FlowPlayControl.getInstance().init(params);

        FlowPlayControl.getInstance().addBindListener(new BindListener() {
            @Override
            public void onServiceConnected() {
                Log.d(TAG,"onServiceConnected");
                EventBus.getDefault().post(new PlayConnectEvent(PlayConnectEvent.CONNECTED));
            }

            @Override
            public void onBindDied() {
                Log.d(TAG,"onBindDied");
            }

            @Override
            public void onServiceDisconnected() {
                Log.d(TAG,"onServiceDisconnected");
            }

            @Override
            public void onError(int i) {
                Log.d(TAG,"onError: " + i);
            }
        });
        FlowPlayControl.getInstance().bindPlayService(context);

        NetworkStateReceiver.getInstance().registerObserver(networkObserver);
    }

    public void addNetworkChangeListener(INetworkChangeListener listener){
        iNetworkChangeListenerLists.add(listener);
    }

    private NetworkObserver networkObserver = new NetworkObserver() {
        @Override
        public void onNetworkChanged(boolean isConnected) {
            isNetworkEnabled = NetworkUtils.isNetworkAvailable(mContext);
            Log.d(TAG, "onNetworkChanged:" + isNetworkEnabled);
            isLogin = false;
            for(INetworkChangeListener listener : iNetworkChangeListenerLists){
                if(listener != null){
                    listener.onNetworkChanged(isNetworkEnabled);
                }
            }
        }
    };

    //注册内容服务
    public void bindContentService(Context context) {
        this.mContext = context;
        ConnectionListener connListener = new ConnectionListener() {
            @Override
            public void onConnected() {
                Log.d(TAG_CONTENT,"bindContentService onConnected");
                EventBus.getDefault().post(new ContentConnectEvent(ContentConnectEvent.CONNECTED));
            }

            @Override
            public void onDisconnected() {
                Log.d(TAG_CONTENT,"bindContentService onDisconnected");
                EventBus.getDefault().post(new ContentConnectEvent(ContentConnectEvent.DISCONNECTED));
            }

            @Override
            public void onConnectionDied() {
                Log.d(TAG_CONTENT,"bindContentService onConnectionDied");
                EventBus.getDefault().post(new ContentConnectEvent(ContentConnectEvent.CONNECTIONDIED));
            }
        };
        ContentManager.getInstance().init(mContext,connListener);
    }

    //查询用户登录状态
    public void checkLoginStatus(IQueryIqutingLoginStatus iQueryIqutingLoginStatus){
        FlowPlayControl.getInstance().queryLoginStatus(new QueryCallback<UserInfo>() {
            @Override
            public void onError(int i) {
                Log.d(TAG,"checkLoginStatus onError: " + i);
                isLogin = false;
                iQueryIqutingLoginStatus.onSuccess(isLogin);
            }

            @Override
            public void onSuccess(UserInfo userInfo) {
                Log.d(TAG,"checkLoginStatus onSuccess");
                if(userInfo != null){
                    if(userInfo.isLogin()){
                        isLogin = true;
                    }else {
                        isLogin = false;
                    }
                }else {
                    isLogin = false;
                }
                iQueryIqutingLoginStatus.onSuccess(isLogin);
            }
        });
    }

    //获取音乐榜单
    public void getMusicList(int contentId,IQueryMusicLists iQueryMusicLists){
        if(contentId == TYPE_DAILYSONGS && mAreaContentResponseBeanDaily != null){
            iQueryMusicLists.onSuccess(mAreaContentResponseBeanDaily);
        }else if(contentId == TYPE_RANKSONGS && mAreaContentResponseBeanRank != null){
            iQueryMusicLists.onSuccess(mAreaContentResponseBeanRank);
        }else {
            ContentManager.getInstance().getAreaContentData(new AreaContentResult() {
                @Override
                public void success(AreaContentResponseBean areaContentResponseBean) {
                    Log.d(TAG_CONTENT,"getAreaContentData success");
                    if(contentId == TYPE_DAILYSONGS){
                        mAreaContentResponseBeanDaily = areaContentResponseBean;
                    }else if(contentId == TYPE_RANKSONGS){
                        mAreaContentResponseBeanRank = areaContentResponseBean;
                    }
                    iQueryMusicLists.onSuccess(areaContentResponseBean);
                    List<BaseSongItemBean> songLists = areaContentResponseBean.getSonglist();
                    if(songLists != null){
                        for(BaseSongItemBean bean : songLists){
                            Log.d(TAG_CONTENT,"" + bean.getSong_name() +
                                    "," + bean.getSinger_name() + "," + bean.getVip() + ",Song_id = " + bean.getSong_id());
                        }
                    }else {
                        Log.d(TAG_CONTENT,"getAreaContentData songLists is null");
                    }
                }

                @Override
                public void failed(int i) {
                    iQueryMusicLists.onFail(i);
                }
            },contentId);
        }
    }

    public boolean isServiceConnect(){
        if(FlowPlayControl.getInstance().isServiceConnected() ||
                ContentManager.getInstance().isConnected()){
            return true;
        }else {
            return false;
        }
    }

    public boolean isAccountLogin(){
        return isLogin;
    }

    public boolean isNetworkConnected(){
        return isNetworkEnabled;
    }
}
