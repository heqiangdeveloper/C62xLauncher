package com.chinatsp.drawer.iquting;

import static com.chinatsp.iquting.service.IqutingBindService.TYPE_DAILYSONGS;
import static com.chinatsp.iquting.service.IqutingBindService.TYPE_RANKSONGS;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.drawer.DrawerEntity;
import com.chinatsp.iquting.callback.IQueryIqutingLoginStatus;
import com.chinatsp.iquting.callback.IQueryMusicLists;
import com.chinatsp.iquting.callback.ITabClickCallback;
import com.chinatsp.iquting.configs.IqutingConfigs;
import com.chinatsp.iquting.ipc.IqutingMediaChangeListener;
import com.chinatsp.iquting.ipc.IqutingPlayStateListener;
import com.chinatsp.iquting.service.IqutingBindService;
import com.chinatsp.widgetcards.R;
import com.tencent.wecarflow.contentsdk.ContentManager;
import com.tencent.wecarflow.contentsdk.bean.AreaContentResponseBean;
import com.tencent.wecarflow.contentsdk.bean.BaseSongItemBean;
import com.tencent.wecarflow.contentsdk.callback.MediaPlayResult;
import com.tencent.wecarflow.controlsdk.FlowPlayControl;
import com.tencent.wecarflow.controlsdk.MediaChangeListener;
import com.tencent.wecarflow.controlsdk.MediaInfo;
import com.tencent.wecarflow.controlsdk.PlayStateListener;
import com.tencent.wecarflow.controlsdk.QueryCallback;
import com.tencent.wecarflow.controlsdk.data.LaunchConfig;
import com.tencent.wecarflow.controlsdk.data.NavigationInfo;

import java.util.List;

import launcher.base.network.NetworkObserver;
import launcher.base.network.NetworkStateReceiver;
import launcher.base.network.NetworkUtils;
import launcher.base.recyclerview.BaseViewHolder;
import launcher.base.recyclerview.SimpleRcvDecoration;
import launcher.base.utils.flowcontrol.PollingTask;

public class DrawerIqutingHolder extends BaseViewHolder<DrawerEntity> {
    private static final String TAG = "DrawerIqutingHolder";
    private ImageView ivDrawerIqutingDirect;
    private RecyclerView rcvDrawerIqutingLogin;
    private SongsAdapter mSongsAdapter;
    private View layoutDrawerIqutingError;
    private TextView tvDrawerIqutingLogin;
    private ImageView ivDrawerIqutingLogin;
    private View viewBg;
    private Context mContext;
    private MediaChangeListener mediaChangeListener;
    private PlayStateListener playStateListener;
    private static final int TYPE_NO_NETWORK = 1;
    private static final int TYPE_NO_LOGIN = 2;
    private static final int TYPE_NORMAL = 3;
    private static final int TYPE_DATA_ERROR = 4;
    public static String itemUUIDInDrawer = "";
    public static boolean isPlaying = false;
    private SharedPreferences sp;
    private AreaContentResponseBean mAreaContentResponseBeanDaily;
    private AreaContentResponseBean mAreaContentResponseBeanRank;
    private int mContentId = TYPE_DAILYSONGS;
    private volatile boolean mExecuteTask = false;
    private PollingTask mServiceConnectTask = new PollingTask(0, 2000, TAG) {
        @Override
        protected void executeTask() {
            addPlayContentListener(70);
        }

        @Override
        protected boolean enableExit() {
            return isServiceConnected;
        }
    };

    private boolean isServiceConnected = false;

    public DrawerIqutingHolder(@NonNull View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        sp = mContext.getSharedPreferences(IqutingConfigs.IQUTINGSP, Context.MODE_PRIVATE);
        rcvDrawerIqutingLogin = itemView.findViewById(R.id.rcvDrawerIqutingLogin);
        initSongsRcv();
        layoutDrawerIqutingError = itemView.findViewById(R.id.layoutDrawerIqutingError);
        tvDrawerIqutingLogin = itemView.findViewById(R.id.tvDrawerIqutingLogin);
        ivDrawerIqutingLogin = itemView.findViewById(R.id.ivDrawerIqutingLogin);
        ivDrawerIqutingDirect = itemView.findViewById(R.id.ivDrawerIqutingDirect);
        viewBg  = itemView.findViewById(R.id.viewBg);
        ivDrawerIqutingDirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlowPlayControl.getInstance().startPlayActivity(mContext);
            }
        });

        //注册监听桌面爱趣听卡片的每日推荐，音乐排行榜tab切换
        IqutingBindService.getInstance().setTabClickListener(new ITabClickCallback() {
            @Override
            public void onTabChanged(int type) {
                getMusicList(type);
            }
        });

        //注册网络动态监听
        NetworkStateReceiver.getInstance().registerObserver(networkObserver);
        //入口
        //addPlayContentListener(89);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FlowPlayControl.getInstance().startPlayActivity(itemView.getContext());
            }
        });
    }

    private NetworkObserver networkObserver = new NetworkObserver() {
        @Override
        public void onNetworkChanged(boolean isConnected) {
            addPlayContentListener(95);
        }
    };

    private void initSongsRcv() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvDrawerIqutingLogin.setLayoutManager(layoutManager);
        SimpleRcvDecoration divider = new SimpleRcvDecoration(23, layoutManager);
        rcvDrawerIqutingLogin.addItemDecoration(divider);
        mSongsAdapter = new SongsAdapter(mContext, new IPlayItemCallback() {
            @Override
            public void onItemClick(int position, long songId) {
                Log.d(TAG, "onItemClick,position = " + position + ",songId = " + songId + ",itemUUIDInDrawer = " + itemUUIDInDrawer);
                if (itemUUIDInDrawer.equals(String.valueOf(songId))) {
                    if (isPlaying) {
                        FlowPlayControl.getInstance().doPause();
                    } else {
                        FlowPlayControl.getInstance().doPlay();
                    }
                } else {
                    if (isPlaying) {
                        FlowPlayControl.getInstance().doPause();
                    }
                    ContentManager.getInstance().playAreaContentData(position,
                            mContentId == TYPE_DAILYSONGS ? mAreaContentResponseBeanDaily : mAreaContentResponseBeanRank,
                            mContentId, false,
                            new MediaPlayResult() {
                                @Override
                                public void success() {
                                    Log.d(TAG, "MediaPlayResult success");
                                }

                                @Override
                                public void failed(int i) {
                                    Log.d(TAG, "MediaPlayResult failed: " + i);
                                    if (i == PLAY_ERROR_NO_AUTHORITY) {
                                        Toast.makeText(mContext, "当前是vip歌曲没有权限,已为你播放其他歌曲", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
        rcvDrawerIqutingLogin.setAdapter(mSongsAdapter);
    }

    private void addPlayContentListener(int line) {
        boolean isConnected = NetworkUtils.isNetworkAvailable(mContext);
        Log.d(TAG, "addPlayContentListener network: " + isConnected + ",Line: " + line);
        if (!isConnected) {
            showUI(TYPE_NO_NETWORK);
        } else {
            if (IqutingBindService.getInstance().isServiceConnect()) {
                isServiceConnected = true;
                Log.d(TAG, "addPlayContentListener PlayContentService Connect");
                //查询用户登录状态
                IqutingBindService.getInstance().checkLoginStatus(new IQueryIqutingLoginStatus() {
                    @Override
                    public void onSuccess(boolean mIsLogin) {
                        Log.d(TAG, "addPlayContentListener checkLoginStatus: " + mIsLogin);
                        if (mIsLogin) {
                            addIqutingMediaChangeListener2();//监听爱趣听媒体的变化
                            addIqutingPlayStateListener2();//监听爱趣听播放状态变化

                            queryPlayStatus();//查看当前的歌曲信息
                            showUI(TYPE_NORMAL);
                            int currentTab = sp.getInt(IqutingConfigs.CURRENTTAB, 1);
                            if (currentTab == TYPE_DAILYSONGS) {
                                if (mAreaContentResponseBeanDaily == null) {
                                    getMusicList(TYPE_DAILYSONGS);
                                }
                            } else {
                                if (mAreaContentResponseBeanRank == null) {
                                    getMusicList(TYPE_RANKSONGS);
                                }
                            }
                        } else {
                            removePlayStateListener();
                            removeMediaChangeListener();
                            showUI(TYPE_NO_LOGIN);
                        }
                    }
                });
            } else {
                Log.d(TAG, "PlayContentService disConnected");
                isServiceConnected = false;
                removePlayStateListener();
                removeMediaChangeListener();
                showUI(TYPE_NO_LOGIN);
                if (mServiceConnectTask != null && !mExecuteTask) {
                    mExecuteTask = true;
                    mServiceConnectTask.execute();
                }
            }
        }
    }

    //查询播放状态
    private void queryPlayStatus() {
        FlowPlayControl.getInstance().queryPlaying(new QueryCallback<Boolean>() {
            @Override
            public void onError(int i) {
                Log.d(TAG, "queryPlayStatus onError: " + i);
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                Log.d(TAG, "queryPlayStatus onSuccess: isPlaying=" + aBoolean);
//                if (!aBoolean) {
//                    //爱趣听没有播放，需要先调起爱趣听播放服务
//                    LaunchConfig launchConfig = new LaunchConfig(true, false,true);
//                    FlowPlayControl.getInstance().launchPlayService(mContext, launchConfig);
//                }
                isPlaying = aBoolean;
                getCurrentMediaInfo();
            }
        });
    }

    //获取当前的媒体信息
    private void getCurrentMediaInfo() {
        FlowPlayControl.getInstance().queryCurrent(new QueryCallback<MediaInfo>() {
            @Override
            public void onError(int i) {
                Log.d(TAG, "getCurrentMediaInfo onError i = " + i);
            }

            @Override
            public void onSuccess(MediaInfo mediaInfo) {
                if (mediaInfo != null) {
                    itemUUIDInDrawer = mediaInfo.getItemUUID();
                    Log.d(TAG, "getCurrentMediaInfo onSuccess " + itemUUIDInDrawer);
                } else {
                    itemUUIDInDrawer = "";
                    Log.d(TAG, "mediaInfo is null");
                }
            }
        });
    }

    private void getMusicList(int contentId) {
        Log.d(TAG, "getMusicList,contentId = " + contentId);
        mContentId = contentId;
        IqutingBindService.getInstance().getMusicList(contentId, new IQueryMusicLists() {
            @Override
            public void onSuccess(AreaContentResponseBean areaContentResponseBean) {
                Log.d(TAG, "getMusicList success");
                if (contentId == TYPE_DAILYSONGS) {
                    mAreaContentResponseBeanDaily = areaContentResponseBean;
                } else if (contentId == TYPE_RANKSONGS) {
                    mAreaContentResponseBeanRank = areaContentResponseBean;
                }
                List<BaseSongItemBean> songLists = areaContentResponseBean.getSonglist();
                if (songLists != null && songLists.size() != 0) {
                    mSongsAdapter.setData(songLists);
                } else {
                    Log.d(TAG, "getMusicList songLists is null");
                    showUI(TYPE_DATA_ERROR);
                }
            }

            @Override
            public void onFail(int failCode) {
                Log.d(TAG, "getMusicList fail: " + failCode);
                showUI(TYPE_DATA_ERROR);
            }
        });
    }

    private void showUI(int type) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (type == TYPE_NO_NETWORK) {
                    viewBg.setVisibility(View.VISIBLE);
                    layoutDrawerIqutingError.setVisibility(View.VISIBLE);
                    rcvDrawerIqutingLogin.setVisibility(View.GONE);
                    ivDrawerIqutingLogin.setImageResource(R.drawable.card_icon_wifi_disconnect);
                    ivDrawerIqutingLogin.setVisibility(View.VISIBLE);
                    tvDrawerIqutingLogin.setText(com.chinatsp.iquting.R.string.iquting_disconnect_tip);
                } else if (type == TYPE_NO_LOGIN) {
                    viewBg.setVisibility(View.VISIBLE);
                    layoutDrawerIqutingError.setVisibility(View.VISIBLE);
                    rcvDrawerIqutingLogin.setVisibility(View.GONE);
                    ivDrawerIqutingLogin.setVisibility(View.GONE);
                    tvDrawerIqutingLogin.setText(com.chinatsp.iquting.R.string.iquting_unlogin_slogan);
                } else if (type == TYPE_DATA_ERROR) {
                    viewBg.setVisibility(View.VISIBLE);
                    layoutDrawerIqutingError.setVisibility(View.VISIBLE);
                    rcvDrawerIqutingLogin.setVisibility(View.GONE);
                    ivDrawerIqutingLogin.setVisibility(View.VISIBLE);
                    ivDrawerIqutingLogin.setImageResource(R.drawable.card_icon_wifi_disconnect);
                    tvDrawerIqutingLogin.setText(com.chinatsp.iquting.R.string.iquting_get_data_error);
                } else {//正常登陆了
                    layoutDrawerIqutingError.setVisibility(View.GONE);
                    rcvDrawerIqutingLogin.setVisibility(View.VISIBLE);
                    viewBg.setVisibility(View.GONE);
                }
            }
        });
    }

    IqutingMediaChangeListener iqutingMediaChangeListener = new IqutingMediaChangeListener() {
        @Override
        public void onMediaChange(MediaInfo mediaInfo) {
            if (mediaInfo != null) {
                itemUUIDInDrawer = mediaInfo.getItemUUID();
                Log.d(TAG, "onMediaChange " + itemUUIDInDrawer);
            } else {
                itemUUIDInDrawer = "";
                Log.d(TAG, "onMediaChange, mediaInfo is null");
            }
        }

        @Override
        public void onMediaChange(MediaInfo mediaInfo, NavigationInfo navigationInfo) {

        }

        @Override
        public void onFavorChange(boolean b, String s) {

        }

        @Override
        public void onModeChange(int i) {

        }

        @Override
        public void onPlayListChange() {

        }
    };

    //监听爱趣听媒体的变化
    private void addIqutingMediaChangeListener2() {
        IqutingBindService.getInstance().registerMediaChangeListener(iqutingMediaChangeListener);
    }

    IqutingPlayStateListener iqutingPlayStateListener = new IqutingPlayStateListener() {
        @Override
        public void onStart() {
            Log.d(TAG, "onStart");
            isPlaying = true;
            //更新播放按钮状态
            checkStatusInList();
        }

        @Override
        public void onPause() {
            Log.d(TAG, "onPause");
            isPlaying = false;
            //更新播放按钮状态
            checkStatusInList();
        }

        @Override
        public void onStop() {
            Log.d(TAG, "onStop");
            isPlaying = false;
            //更新播放按钮状态
            checkStatusInList();
        }

        @Override
        public void onProgress(String s, long l, long l1) {

        }

        @Override
        public void onBufferingStart() {

        }

        @Override
        public void onBufferingEnd() {

        }

        @Override
        public void onPlayError(int i, String s) {

        }

        @Override
        public void onAudioSessionId(int i) {

        }
    };

    //监听爱趣听播放状态变化
    private void addIqutingPlayStateListener2() {
        IqutingBindService.getInstance().registerPlayStateListener(iqutingPlayStateListener);
    }

    //更新推荐列表中的播放选中状态
    private void checkStatusInList() {
        updatePlayStatusInList();
    }

    private int getCurrentItemPosition(String id, List<BaseSongItemBean> beans) {
        if (beans == null || beans.size() == 0) {
            return -1;
        }
        for (int i = 0; i < beans.size(); i++) {
            if (id.equals(String.valueOf(beans.get(i).getSong_id()))) {
                return i;
            }
        }
        return -1;
    }

    /*
     *当页面可见时（onResume()）回调此方法
     */
    @Override
    public void bind(int position, DrawerEntity drawerEntity) {
        super.bind(position, drawerEntity);
        Log.d(TAG, "bind");
        //入口
        addPlayContentListener(380);
    }

    public void updatePlayStatusInList() {
        if (mSongsAdapter.getData() != null && mSongsAdapter.getData().size() != 0) {
            mSongsAdapter.notifyDataSetChanged();
        }
    }

    private void removeMediaChangeListener() {
        IqutingBindService.getInstance().removeRegistedMediaChangeListener(iqutingMediaChangeListener);
    }

    private void removePlayStateListener() {
        IqutingBindService.getInstance().removeRegistedPlayStateListener(iqutingPlayStateListener);
    }
}
