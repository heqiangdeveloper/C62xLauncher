package com.chinatsp.iquting.service;

import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.Log;

import com.chinatsp.iquting.callback.INetworkChangeListener;
import com.chinatsp.iquting.callback.IQueryIqutingLoginStatus;
import com.chinatsp.iquting.callback.IQueryMusicLists;
import com.chinatsp.iquting.callback.ITabClickCallback;
import com.chinatsp.iquting.configs.IqutingConfigs;
import com.chinatsp.iquting.ipc.IqutingMediaChangeListener;
import com.chinatsp.iquting.ipc.IqutingPlayStateListener;
import com.chinatsp.iquting.utils.VisualizerTool;
import com.tencent.wecarflow.contentsdk.ConnectionListener;
import com.tencent.wecarflow.contentsdk.ContentManager;
import com.tencent.wecarflow.contentsdk.bean.AreaContentResponseBean;
import com.tencent.wecarflow.contentsdk.callback.AreaContentResult;
import com.tencent.wecarflow.controlsdk.AudioFocusChangeListener;
import com.tencent.wecarflow.controlsdk.BindListener;
import com.tencent.wecarflow.controlsdk.FlowPlayControl;
import com.tencent.wecarflow.controlsdk.MediaChangeListener;
import com.tencent.wecarflow.controlsdk.MediaInfo;
import com.tencent.wecarflow.controlsdk.PlayStateListener;
import com.tencent.wecarflow.controlsdk.QueryCallback;
import com.tencent.wecarflow.controlsdk.data.LaunchConfig;
import com.tencent.wecarflow.controlsdk.data.NavigationInfo;
import com.tencent.wecarflow.controlsdk.data.UserInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import launcher.base.network.NetworkObserver;
import launcher.base.network.NetworkStateReceiver;
import launcher.base.network.NetworkUtils;

public class IqutingBindService {
    private static final String TAG = "IqutingBindService";
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
    private Set<IqutingPlayStateListener> playStateListenerSets = new HashSet<>();
    private Set<IqutingMediaChangeListener> mediaChangeListenerSets = new HashSet<>();
    private volatile boolean isPlaying = false;
    private VisualizerTool visualizerTool;
    private int currentSessionId = -1;//?????????sessonid???
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

    //??????????????????
    public void bindPlayService(Context context) {
        this.mContext = context;
        FlowPlayControl.InitParams params = new FlowPlayControl.InitParams();
        params.setAutoRebind(true);
        FlowPlayControl.getInstance().init(params);

        FlowPlayControl.getInstance().addBindListener(new BindListener() {
            @Override
            public void onServiceConnected() {
                Log.d(TAG,"onServiceConnected");
                addAudioFocusChangeListener();
                //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????? "com.aiquting.play"
                //LaunchConfig launchConfig = new LaunchConfig(true, false,true);
                LaunchConfig launchConfig = new LaunchConfig(false,false);
                FlowPlayControl.getInstance().launchPlayService(mContext, launchConfig);
                //EventBus.getDefault().post(new PlayConnectEvent(PlayConnectEvent.CONNECTED));
                addPlayStateListener();
            }

            @Override
            public void onBindDied() {
                Log.d(TAG,"onBindDied");
            }

            @Override
            public void onServiceDisconnected() {
                Log.d(TAG,"onServiceDisconnected");
                removePlayStateListener();
                removeAudioFocusChangeListener();
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

    //??????????????????
    public void bindContentService(Context context) {
        this.mContext = context;
        ConnectionListener connListener = new ConnectionListener() {
            @Override
            public void onConnected() {
                Log.d(TAG_CONTENT,"bindContentService onConnected");
                //EventBus.getDefault().post(new ContentConnectEvent(ContentConnectEvent.CONNECTED));
                addMediaChangeListener();
            }

            @Override
            public void onDisconnected() {
                Log.d(TAG_CONTENT,"bindContentService onDisconnected");
                //EventBus.getDefault().post(new ContentConnectEvent(ContentConnectEvent.DISCONNECTED));
                removeMediaChangeListener();
            }

            @Override
            public void onConnectionDied() {
                Log.d(TAG_CONTENT,"bindContentService onConnectionDied");
                //EventBus.getDefault().post(new ContentConnectEvent(ContentConnectEvent.CONNECTIONDIED));
            }
        };
        ContentManager.getInstance().init(mContext,connListener);
    }

    //????????????????????????
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

    //??????????????????
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
                }

                @Override
                public void failed(int i) {
                    iQueryMusicLists.onFail(i);
                }
            },contentId);
        }
    }

    //????????????????????????
    public boolean isServiceConnect(){
        if(FlowPlayControl.getInstance().isServiceConnected() ||
                ContentManager.getInstance().isConnected()){
            return true;
        }else {
            return false;
        }
    }

    private synchronized void doMusicRhythm(){
        Log.d(TAG,"doMusicRhythm isPlaying: " + isPlaying);
        if(isPlaying){
            FlowPlayControl.getInstance().queryAudioSessionId(new QueryCallback<Integer>() {
                @Override
                public void onError(int code) {
                    Log.d(TAG,"queryAudioSessionId onError: " + code);
                }
                @Override
                public void onSuccess(Integer s) {
                    //??????????????????sessionId?????????????????????????????????sessionId??????????????????????????????onAudioSessionId()????????????
                    Log.d(TAG,"sessionId: " + s.intValue() + ",currentSessionId: " + currentSessionId);
                    if(s.intValue() == currentSessionId){
                        if(visualizerTool != null){
                            visualizerTool.releaseVisualizer();
                        }
                        visualizerTool = new VisualizerTool(s.intValue());
                        visualizerTool.setPlayStatus(true);
                    }
                }
            });
        }else {
            if(visualizerTool != null){
                visualizerTool.setPlayStatus(false);
                visualizerTool.releaseVisualizer();
                visualizerTool = null;
            }
        }
    }

    MediaChangeListener mediaChangeListener = new MediaChangeListener() {
        @Override
        public void onMediaChange(MediaInfo mediaInfo) {
            Log.d(TAG,"onMediaChange mediaChangeListenerSets size = " + mediaChangeListenerSets.size());
            for(IqutingMediaChangeListener listener : mediaChangeListenerSets){
                listener.onMediaChange(mediaInfo);
            }
        }

        @Override
        public void onMediaChange(MediaInfo mediaInfo, NavigationInfo navigationInfo) {
            Log.d(TAG,"onMediaChange with navigationInfo");
            //mediaChangeListenerList = mediaChangeListenerList.stream().distinct().collect(Collectors.toList());//???????????????
            for(IqutingMediaChangeListener listener : mediaChangeListenerSets){
                listener.onMediaChange(mediaInfo,navigationInfo);
            }
        }

        @Override
        public void onFavorChange(boolean b, String s) {
            //b true???????????????false????????????  s ??????itemUUID
            Log.d(TAG,"onFavorChange: b = " + b + ",s = " + s);
            //mediaChangeListenerList = mediaChangeListenerList.stream().distinct().collect(Collectors.toList());//???????????????
            for(IqutingMediaChangeListener listener : mediaChangeListenerSets){
                listener.onFavorChange(b,s);
            }
        }

        @Override
        public void onModeChange(int i) {
            Log.d(TAG,"onModeChange: " + i);
            //mediaChangeListenerList = mediaChangeListenerList.stream().distinct().collect(Collectors.toList());//???????????????
            for(IqutingMediaChangeListener listener : mediaChangeListenerSets){
                listener.onModeChange(i);
            }
        }

        @Override
        public void onPlayListChange() {
            Log.d(TAG,"onPlayListChange");
            //mediaChangeListenerList = mediaChangeListenerList.stream().distinct().collect(Collectors.toList());//???????????????
            for(IqutingMediaChangeListener listener : mediaChangeListenerSets){
                listener.onPlayListChange();
            }
        }
    };

    //??????????????????
    private void addMediaChangeListener(){
        FlowPlayControl.getInstance().addMediaChangeListener(mediaChangeListener);
    }

    PlayStateListener playStateListener = new PlayStateListener() {
        @Override
        public void onStart() {
            Log.d(TAG,"onStart");
            isPlaying = true;
            doMusicRhythm();//????????????
            Settings.System.putString(mContext.getContentResolver(), IqutingConfigs.SAVE_SOURCE, IqutingConfigs.AQT);//?????????????????????
            Settings.System.putInt(mContext.getContentResolver(), IqutingConfigs.AQT_PLAYING, 1);//????????????????????????????????????
            //playStateListenerList = playStateListenerList.stream().distinct().collect(Collectors.toList());//???????????????
            Log.d(TAG,"onStart playStateListenerSets size = " + playStateListenerSets.size());
            for(IqutingPlayStateListener listener : playStateListenerSets){
                listener.onStart();
            }
        }

        @Override
        public void onPause() {
            Log.d(TAG,"onPause");
            isPlaying = false;
            doMusicRhythm();//????????????
            Settings.System.putInt(mContext.getContentResolver(), IqutingConfigs.AQT_PLAYING, 0);//????????????????????????????????????
            //playStateListenerList = playStateListenerList.stream().distinct().collect(Collectors.toList());//???????????????
            for(IqutingPlayStateListener listener : playStateListenerSets){
                listener.onPause();
            }
        }

        @Override
        public void onStop() {
            Log.d(TAG,"onStop");
            isPlaying = false;
            doMusicRhythm();//????????????
            Settings.System.putInt(mContext.getContentResolver(), IqutingConfigs.AQT_PLAYING, 0);//????????????????????????????????????
            //playStateListenerList = playStateListenerList.stream().distinct().collect(Collectors.toList());//???????????????
            for(IqutingPlayStateListener listener : playStateListenerSets){
                listener.onStop();
            }
        }

        @Override
        public void onProgress(String s, long l, long l1) {//s ?????????l ??????????????? l1?????????
            //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            //Log.d(TAG,"onProgress " + s + "," +l + "," +l1);
            //playStateListenerList = playStateListenerList.stream().distinct().collect(Collectors.toList());//???????????????
            for(IqutingPlayStateListener listener : playStateListenerSets){
                listener.onProgress(s,l,l1);
            }
        }

        @Override
        public void onBufferingStart() {
            Log.d(TAG,"onBufferingStart");
            currentSessionId = -1;//??????currentSessionId????????????????????????????????????????????????onMediaChange,???????????????onMediaChange???
            //playStateListenerList = playStateListenerList.stream().distinct().collect(Collectors.toList());//???????????????
            for(IqutingPlayStateListener listener : playStateListenerSets){
                listener.onBufferingStart();
            }
        }

        @Override
        public void onBufferingEnd() {
            Log.d(TAG,"onBufferingEnd");
            //playStateListenerList = playStateListenerList.stream().distinct().collect(Collectors.toList());//???????????????
            for(IqutingPlayStateListener listener : playStateListenerSets){
                listener.onBufferingEnd();
            }
        }

        @Override
        public void onPlayError(int i, String s) {
            Log.d(TAG,"onPlayError: i = " + i + ",s = " + s);
            //playStateListenerList = playStateListenerList.stream().distinct().collect(Collectors.toList());//???????????????
            for(IqutingPlayStateListener listener : playStateListenerSets){
                listener.onPlayError(i,s);
            }
        }

        @Override
        public void onAudioSessionId(int i) {
            Log.d(TAG,"onAudioSessionId: " + i);
            currentSessionId = i;
            doMusicRhythm();//????????????
            //playStateListenerList = playStateListenerList.stream().distinct().collect(Collectors.toList());//???????????????
            for(IqutingPlayStateListener listener : playStateListenerSets){
                listener.onAudioSessionId(i);
            }
        }
    };

    //???????????????????????????
    private void addPlayStateListener(){
        FlowPlayControl.getInstance().addPlayStateListener(playStateListener);
    }

    AudioFocusChangeListener audioFocusChangeListener = new AudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusState) {
            switch (focusState){
                case AudioManager.AUDIOFOCUS_GAIN:
                    // 1:?????????????????????0???????????????
                    Log.d("iqutingfocus","AUDIOFOCUS_GAIN");
                    Settings.System.putInt(mContext.getContentResolver(),"aqt_focus",1);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    Log.d("iqutingfocus","AUDIOFOCUS_LOSS");
                    Settings.System.putInt(mContext.getContentResolver(),"aqt_focus",0);
                    break;
            }
        }
    };

    //?????????????????????????????????
    private void addAudioFocusChangeListener(){
        FlowPlayControl.getInstance().addAudioFocusChangeListener(audioFocusChangeListener);
    }

    //??????????????????????????????????????????
    private void removeAudioFocusChangeListener(){
        FlowPlayControl.getInstance().removeAudioFocusChangeListener(audioFocusChangeListener);
    }

    //???????????????????????????
    private void removeMediaChangeListener(){
        FlowPlayControl.getInstance().removeMediaChangeListener(mediaChangeListener);
    }

    //???????????????????????????
    private void removePlayStateListener(){
        FlowPlayControl.getInstance().removePlayStateListener(playStateListener);
    }

    //??????????????????????????????????????????
    public void registerMediaChangeListener(IqutingMediaChangeListener listener){
        if(listener != null && !mediaChangeListenerSets.contains(listener)){
            Log.d("mediaChangeListenerSets","mediaChangeListenerSets add Sets size =" + listener);
            mediaChangeListenerSets.add(listener);
        }
    }

    //??????????????????????????????????????????
    public void registerPlayStateListener(IqutingPlayStateListener listener){
        if(listener != null && !playStateListenerSets.contains(listener)){
            Log.d("playStateListenerSets","playStateListenerSets add Sets size =" + listener);
            playStateListenerSets.add(listener);
        }
    }

    //??????????????????????????????????????????
    public void removeRegistedPlayStateListener(IqutingPlayStateListener listener){
        playStateListenerSets.remove(listener);
    }

    //??????????????????????????????????????????
    public void removeRegistedMediaChangeListener(IqutingMediaChangeListener listener){
        mediaChangeListenerSets.remove(listener);
    }

    //???????????????media???play?????????
    public void removeMediaAndPlayListener(){
        playStateListenerSets.clear();
        mediaChangeListenerSets.clear();
    }

    //???????????????????????????????????????????????????????????????
    public void notifyCurrentMediaInfo(MediaInfo mediaInfo){
        for(IqutingMediaChangeListener listener : mediaChangeListenerSets){
            listener.onMediaChange(mediaInfo);
        }
    }

    public boolean isAccountLogin(){
        return isLogin;
    }

    public boolean isNetworkConnected(){
        return isNetworkEnabled;
    }
}
