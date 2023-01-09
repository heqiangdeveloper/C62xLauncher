package com.chinatsp.iquting;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.iquting.callback.IQueryIqutingLoginStatus;
import com.chinatsp.iquting.callback.IQueryMusicLists;
import com.chinatsp.iquting.configs.IqutingConfigs;
import com.chinatsp.iquting.event.BootEvent;
import com.chinatsp.iquting.event.ContentConnectEvent;
import com.chinatsp.iquting.event.ControlEvent;
import com.chinatsp.iquting.event.Event;
import com.chinatsp.iquting.event.PlayConnectEvent;
import com.chinatsp.iquting.ipc.IqutingMediaChangeListener;
import com.chinatsp.iquting.ipc.IqutingPlayStateListener;
import com.chinatsp.iquting.service.IqutingBindService;
import com.chinatsp.iquting.songs.IQuTingSongsAdapter;
import com.chinatsp.iquting.state.DataErrorState;
import com.chinatsp.iquting.state.NetWorkDisconnectState;
import com.chinatsp.iquting.state.NormalState;
import com.chinatsp.iquting.state.IQuTingState;
import com.chinatsp.iquting.state.UnLoginState;
import com.chinatsp.iquting.utils.ToolUtils;
import com.tencent.wecarflow.contentsdk.ContentManager;
import com.tencent.wecarflow.contentsdk.bean.AreaContentResponseBean;
import com.tencent.wecarflow.contentsdk.bean.BaseSongItemBean;
import com.tencent.wecarflow.contentsdk.callback.MediaPlayResult;
import com.tencent.wecarflow.controlsdk.FlowPlayControl;
import com.tencent.wecarflow.controlsdk.MediaChangeListener;
import com.tencent.wecarflow.controlsdk.MediaInfo;
import com.tencent.wecarflow.controlsdk.PlayStateListener;
import com.tencent.wecarflow.controlsdk.QueryCallback;
import com.tencent.wecarflow.controlsdk.data.NavigationInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import card.service.ICardStyleChange;
import launcher.base.network.NetworkObserver;
import launcher.base.network.NetworkStateReceiver;
import launcher.base.network.NetworkUtils;
import launcher.base.recyclerview.SimpleRcvDecoration;
import launcher.base.utils.EasyLog;
import launcher.base.utils.flowcontrol.PollingTask;
import launcher.base.utils.glide.GlideHelper;
import launcher.base.utils.recent.RecentAppHelper;
import launcher.base.utils.view.CircleProgressView;
import launcher.base.utils.view.LayoutParamUtil;
import launcher.base.utils.view.SimpleProgressView;


public class IQuTingCardView extends ConstraintLayout implements ICardStyleChange, LifecycleOwner, View.OnClickListener {
    public IQuTingCardView(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }

    public IQuTingCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IQuTingCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public IQuTingCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private static final String TAG = "IQuTingCardView";
    private static final String TAG_CONTENT = "heqqcontent";
    private Context context;
    private IQuTingController mController;
    private NormalSmallCardViewHolder mNormalSmallCardViewHolder;
    private NormalBigCardViewHolder mNormalBigCardViewHolder;

    private int mSmallWidth;
    private int mLargeWidth;

    private View mLargeCardView;
    private View mSmallCardView;
    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    private IQuTingState mState;
    private boolean mExpand = false;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private ImageView mIvIQuTingPlayPauseBtn;
    private ImageView mIvIQuTingPlayPauseBtnBig;
    private TextView mTvCardIQuTingLoginTip;
    private TextView mTvIQuTingMediaName;
    private TextView mTvIQuTingMediaNameBig;
    private TextView mTvIQuTingArtistBig;
    private SimpleProgressView mProgressHorizontalIQuTing;
    private ImageView mIvCover;
    private ImageView mIvIQuTingCoverBig;
    private ImageView mIvIQuTingPreBtn;
    private ImageView mIvIQuTingPreBtnBig;
    private ImageView mIvIQuTingNextBtn;
    private ImageView mIvIQuTingNextBtnBig;
    private ImageView mIvIQuTingLike;
    private ImageView mIvIQuTingLikeBtnBig;
    private ImageView mIvCardIQuTingButton;
    private TextView mTvIQuTingDailySongs;
    private TextView mTvCardIQuTingNetTip;
    private ImageView mIvCardIQuTingRefresh;
    private ImageView mIvCardIQuTingRefreshBig;
    private TextView mTvIQuTingRankSongs;
    private TextView mTvIQuTingPlayPosition;
    private TextView mTvIQuTingPlayDuration;
    private CircleProgressView mCircleProgressView;
    public static boolean isPlaying = false;
    private boolean isConnectContent = false;
    private MediaInfo currentMediaInfo;
    private MediaChangeListener mediaChangeListener;
    private PlayStateListener playStateListener;
    private static boolean isHasMediaPlay = false;
    private List<BaseSongItemBean> dailySongLists;
    private List<BaseSongItemBean> rankSongLists;
    private AreaContentResponseBean mAreaContentResponseBeanDaily;
    private AreaContentResponseBean mAreaContentResponseBeanRank;
    private static final int RADIUS = 10;
    private static final int TYPE_DAILYSONGS = 1;
    private static final int TYPE_RANKSONGS = 2;
    private static final int TYPE_NEWS = 3;
    private int mContentId = TYPE_DAILYSONGS;
    private String artist = "";
    private String name = "";
    private String iconUrl = "";
    public static String itemUUID = "";
    private long currentDuration = 0l;//单位毫秒
    private long totalDuration = 1l;//单位毫秒
    private boolean isLogin = false;

    private boolean isServiceConnected = false;
    private volatile boolean mExecuteTask = false;
    private ObjectAnimator mRefreshAnimator;
    private ObjectAnimator mRefreshBigAnimator;
    private final int MIN_LOADING_ANIM_TIME = 1000;
    private boolean isDataError = false;

    private PollingTask mServiceConnectTask = new PollingTask(0, 2000, TAG) {
        @Override
        protected void executeTask() {
            ((Activity)getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addPlayContentListener(IQuTingCardView.this);
                }
            });
        }

        @Override
        protected boolean enableExit() {
            if(isServiceConnected){
                mExecuteTask = false;
            }
            return isServiceConnected;
        }
    };

    private void init() {
        Log.d(TAG,"init");
        LayoutInflater.from(getContext()).inflate(R.layout.card_iquting, this);
        sp = getContext().getSharedPreferences(IqutingConfigs.IQUTINGSP,Context.MODE_PRIVATE);
        editor = sp.edit();
        mSmallCardView = findViewById(R.id.layoutSmallCardView);
        mSmallWidth = (int) getResources().getDimension(R.dimen.card_width);
        mLargeWidth = (int) getResources().getDimension(R.dimen.card_width_large);

        mTvCardIQuTingLoginTip = (TextView) findViewById(R.id.tvCardIQuTingLoginTip);
        mIvIQuTingPlayPauseBtn = (ImageView) findViewById(R.id.ivIQuTingPlayPauseBtn);
        mTvIQuTingMediaName = (TextView) findViewById(R.id.tvIQuTingMediaName);
        mProgressHorizontalIQuTing = (SimpleProgressView) findViewById(R.id.progressHorizontalIQuTing);
        mIvIQuTingNextBtn = (ImageView) findViewById(R.id.ivIQuTingNextBtn);
        mIvIQuTingPreBtn = (ImageView) findViewById(R.id.ivIQuTingPreBtn);
        mIvIQuTingLike = (ImageView) findViewById(R.id.ivIQuTingLike);
        mIvCardIQuTingButton = (ImageView) findViewById(R.id.ivCardIQuTingButton);
        mTvIQuTingPlayPosition = (TextView) findViewById(R.id.tvIQuTingPlayPosition);
        mTvIQuTingPlayDuration = (TextView) findViewById(R.id.tvIQuTingPlayDuration);
        mTvCardIQuTingNetTip = (TextView) findViewById(R.id.tvCardIQuTingNetTip);
        mIvCardIQuTingRefresh = (ImageView) findViewById(R.id.ivCardIQuTingRefresh);
        mNormalSmallCardViewHolder = new NormalSmallCardViewHolder();
//        mNormalSmallCardViewHolder.updateMediaInfo();
        mController = new IQuTingController(this);

        mIvIQuTingPlayPauseBtn.setOnClickListener(this);
        mIvIQuTingPreBtn.setOnClickListener(this);
        mIvIQuTingNextBtn.setOnClickListener(this);
        mIvIQuTingLike.setOnClickListener(this);
        mIvCardIQuTingButton.setOnClickListener(this);
        mIvCardIQuTingRefresh.setOnClickListener(this);
        //点击空白处跳转至爱趣听
//        setOnClickListener(this);

        //注册网络动态监听
        NetworkStateReceiver.getInstance().registerObserver(networkObserver);
        //入口
        addIqutingMediaChangeListener2();//监听爱趣听媒体的变化
        addIqutingPlayStateListener2();//监听爱趣听播放状态变化
        addPlayContentListener(IQuTingCardView.this);
    }

    private NetworkObserver networkObserver = new NetworkObserver() {
        @Override
        public void onNetworkChanged(boolean isConnected) {
            addPlayContentListener(IQuTingCardView.this);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event){
        if(event instanceof PlayConnectEvent){
            if(((PlayConnectEvent)event).getType() == PlayConnectEvent.CONNECTED){
                //addPlayContentListener(IQuTingCardView.this);
            }
        }else if(event instanceof ContentConnectEvent){
            if(((ContentConnectEvent)event).getType() == ContentConnectEvent.CONNECTED){
                Log.d(TAG_CONTENT,"ContentService connect addContentListener");
                //addContentListener();
                //addPlayContentListener(IQuTingCardView.this);
            }
        }else if(event instanceof BootEvent){
            //addPlayContentListener(IQuTingCardView.this);
            Log.d(TAG_CONTENT,"boot addContentListener");
            //addContentListener();
        }else if(event instanceof ControlEvent){
            int type = ((ControlEvent)event).getType();
            int position = ((ControlEvent)event).getPosition();
            String songId = ((ControlEvent)event).getSongId();
            Log.d(TAG_CONTENT,"ControlEvent,position = " + position + ",songId = " + songId + ",type = " + type);
            if(itemUUID.equals(songId) && type == IqutingConfigs.CLICK_TYPE_ITEM){
                if(isPlaying){
                    FlowPlayControl.getInstance().doPause();
                }else {
                    FlowPlayControl.getInstance().doPlay();
                }
            }else if(itemUUID.equals(songId) && type == IqutingConfigs.CLICK_TYPE_COVER){
                if(!isPlaying){
                    FlowPlayControl.getInstance().doPlay();
                }
                FlowPlayControl.getInstance().openPlayDetail(getContext());
            }else {
                if(isPlaying){
                    FlowPlayControl.getInstance().doPause();
                }
                ContentManager.getInstance().playAreaContentData(position,
                        mContentId == TYPE_DAILYSONGS ? mAreaContentResponseBeanDaily : mAreaContentResponseBeanRank,
                        mContentId, false,
                        new MediaPlayResult() {
                            @Override
                            public void success() {
                                Log.d(TAG_CONTENT,"MediaPlayResult success");
                                if(type == IqutingConfigs.CLICK_TYPE_COVER){
                                    FlowPlayControl.getInstance().openPlayDetail(getContext());
                                }
                            }

                            @Override
                            public void failed(int i) {
                                Log.d(TAG_CONTENT,"MediaPlayResult failed: " + i);
                                if(i == PLAY_ERROR_NO_AUTHORITY){
                                    Toast.makeText(getContext(),"当前是vip歌曲没有权限,已为你播放其他歌曲",Toast.LENGTH_LONG).show();
                                    if(type == IqutingConfigs.CLICK_TYPE_COVER){
                                        FlowPlayControl.getInstance().openPlayDetail(getContext());
                                    }
                                }
                            }
                        });
            }
        }
    }

    private void addPlayContentListener(View view){
        boolean isConnected = NetworkUtils.isNetworkAvailable(context);
        Log.d(TAG,"addPlayContentListener network: " + isConnected);
        if(!isConnected){
            isLogin = false;
            if(isPlaying){
                FlowPlayControl.getInstance().doPause();
            }
//            removePlayStateListener();
//            removeMediaChangeListener();
            mState = new NetWorkDisconnectState();
            mState.updateViewState(IQuTingCardView.this, mExpand);
        }else {
            if(IqutingBindService.getInstance().isServiceConnect()){
                isServiceConnected = true;
                Log.d(TAG,"addPlayContentListener PlayContentService Connect");
                //查询用户登录状态
                IqutingBindService.getInstance().checkLoginStatus(new IQueryIqutingLoginStatus() {
                    @Override
                    public void onSuccess(boolean mIsLogin) {
                        Log.d(TAG,"addPlayContentListener checkLoginStatus: " + mIsLogin);
                        if(mIsLogin){
                            isLogin = true;
                            //if(mState == null || mState instanceof UnLoginState || mState instanceof NetWorkDisconnectState){
                                mState = new NormalState();
                                mState.updateViewState(view, mExpand);

                                queryPlayStatus();//查询播放状态
                           // }
                            if(mAreaContentResponseBeanDaily == null){
                                getMusicList(TYPE_DAILYSONGS);//获取每日推荐
                            }
                            if(mAreaContentResponseBeanRank == null){
                                getMusicList(TYPE_RANKSONGS);//获取每日推荐
                            }
                        }else {
                            isLogin = false;
//                            removePlayStateListener();
//                            removeMediaChangeListener();
                            mState = new UnLoginState();
                            mState.updateViewState(IQuTingCardView.this, mExpand);
                        }
                    }
                });
            }else {
                isServiceConnected = false;
                isLogin = false;
//                removePlayStateListener();
//                removeMediaChangeListener();
                mState = new UnLoginState();
                mState.updateViewState(IQuTingCardView.this, mExpand);

                Log.d(TAG,"PlayContentService disConnected");
                if (mServiceConnectTask != null && !mExecuteTask) {
                    mExecuteTask = true;
                    mServiceConnectTask.execute();
                }
            }
        }
    }

    /**
     * 判断Activity是否Destroy
     * @param mActivity
     * @return
     */
    public static boolean isDestroy(Activity mActivity) {
        if (mActivity== null || mActivity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed())) {
            return true;
        } else {
            return false;
        }
    }

    IqutingMediaChangeListener iqutingMediaChangeListener = new IqutingMediaChangeListener() {
        @Override
        public void onMediaChange(MediaInfo mediaInfo) {
            currentMediaInfo = mediaInfo;
            if(mediaInfo != null){
                isHasMediaPlay = true;
                artist = mediaInfo.getMediaAuthor();
                name = mediaInfo.getMediaName();
                iconUrl = mediaInfo.getMediaImage();
                itemUUID = mediaInfo.getItemUUID();
                currentDuration = mediaInfo.getCurrentDuration();
                totalDuration = mediaInfo.getTotalTime();
                Log.d(TAG,"onMediaChange " + name + "," + artist +
                        "," + mediaInfo.getMediaType() + "," + itemUUID + "," + totalDuration);
                if(mExpand){//中卡
                    mCircleProgressView.setMax(totalDuration / 1000);
                    mCircleProgressView.setCurrent(currentDuration / 1000);
                    mTvIQuTingMediaNameBig.setText(name);
                    mTvIQuTingArtistBig.setText(artist);
                    mIvIQuTingCoverBig = findViewById(R.id.ivIQuTingCoverBig);
                    if(!isDestroy((Activity) context) && (mIvIQuTingCoverBig != null)){
                        GlideHelper.loadUrlImage(context,mIvIQuTingCoverBig,iconUrl);
                    }

                    if(mediaInfo.getMediaType() != null){
                        showFavor(false,mIvIQuTingLikeBtnBig,mediaInfo.getMediaType().trim(),mediaInfo.isFavored());
                    }
                }else {
                    mTvIQuTingPlayPosition.setText(ToolUtils.formatTime(currentDuration / 1000));
                    mTvIQuTingPlayDuration.setText(ToolUtils.formatTime(totalDuration / 1000));
                    mProgressHorizontalIQuTing.setMaxValue(totalDuration / 1000);
                    mProgressHorizontalIQuTing.updateProgress(currentDuration / 1000);
                    mTvIQuTingMediaName.setText(name + "-" + artist);

                    mIvCover = findViewById(R.id.ivIQuTingCover);
                    if(!isDestroy((Activity) context)  && (mIvCover != null)){
                        GlideHelper.loadUrlAlbumCoverRadius(context,mIvCover,iconUrl,RADIUS);
                    }

                    if(mediaInfo.getMediaType() != null){
                        showFavor(false,mIvIQuTingLike,mediaInfo.getMediaType().trim(),mediaInfo.isFavored());
                    }
                }
            }else {
                Log.d(TAG,"onMediaChange, mediaInfo is null");
                isHasMediaPlay = false;
                mState = new NormalState();
                mState.updateViewState(IQuTingCardView.this, mExpand);

                if(mExpand){
                    GlideHelper.loadLocalCircleImage(getContext(), mIvIQuTingCoverBig, R.drawable.test_cover2);
                    mTvIQuTingMediaNameBig.setText("暗里着迷");
                    mTvIQuTingArtistBig.setText("刘德华");
                    mCircleProgressView.setCurrent(0);
                }else {
                    if(!isDestroy((Activity) getContext())){
                        GlideHelper.loadLocalAlbumCoverRadius(getContext(), mIvCover, R.drawable.test_cover2, RADIUS);
                    }
                    mTvIQuTingMediaName.setText("暗里着迷—刘德华");
                    mProgressHorizontalIQuTing.updateProgress(0);
                }
            }
        }

        @Override
        public void onMediaChange(MediaInfo mediaInfo, NavigationInfo navigationInfo) {

        }

        @Override
        public void onFavorChange(boolean b, String s) {
            if(currentMediaInfo != null){
                if(s != null && s.equals(currentMediaInfo.getItemUUID())){
                    if(mExpand){
                        showFavor(true,mIvIQuTingLikeBtnBig,currentMediaInfo.getMediaType().trim(),b);
                    }else {
                        showFavor(true,mIvIQuTingLike,currentMediaInfo.getMediaType().trim(),b);
                    }
                }
            }
        }

        @Override
        public void onModeChange(int i) {

        }

        @Override
        public void onPlayListChange() {

        }
    };

    //监听爱趣听媒体的变化
    private void addIqutingMediaChangeListener2(){
        IqutingBindService.getInstance().registerMediaChangeListener(iqutingMediaChangeListener);
    }

    private void showFavor(boolean isFavorChanged,ImageView iv,String type,boolean isFavor){
        //sdk目前收藏功能只对音乐有效
        if("song".equals(type)){
            if(isFavor){
                iv.setTag("like");
                //用户主动点击的收藏需要动画，爱趣听页面点击的收藏，由于卡片此时处于后台，移除了收藏监听事件，不受影响
                if(isFavorChanged){
                    post(new Runnable() {
                        @Override
                        public void run() {
                            iv.setImageResource(R.drawable.favor_anim);
                            AnimationDrawable AD = (AnimationDrawable) iv.getDrawable();
                            AD.setOneShot(true);//只播一次
                            AD.start();
                        }
                    });
                }else {
                    iv.setImageResource(R.drawable.card_iquting_icon_like);
                }
            }else {
                Drawable drawable = iv.getDrawable();
                if(drawable != null && (drawable instanceof AnimationDrawable)){
                    AnimationDrawable AD = (AnimationDrawable) drawable;
                    AD.stop();//停止安装中的动画
                }

                iv.setImageResource(R.drawable.card_iquting_icon_unlike);
                iv.setTag("unlike");
            }
            iv.setVisibility(View.VISIBLE);
        }else {
            //当不显示收藏时，使用GONE会导致依赖的下方控制区位置错乱
            iv.setVisibility(View.INVISIBLE);
        }
    }

    private int getCurrentItemPosition(String id,List<BaseSongItemBean> beans){
        if(beans == null || beans.size() == 0){
            return -1;
        }
        for(int i = 0; i < beans.size(); i++){
            if(id.equals(String.valueOf(beans.get(i).getSong_id()))){
                return i;
            }
        }
        return -1;
    }

    //更新推荐列表中的播放选中状态
    private void checkStatusInList(){
        if(mContentId == TYPE_DAILYSONGS){
            int position = getCurrentItemPosition(itemUUID,dailySongLists);
            if(mNormalBigCardViewHolder != null) mNormalBigCardViewHolder.updatePlayStatusInList(position);
        }else {
            int position = getCurrentItemPosition(itemUUID,rankSongLists);
            if(mNormalBigCardViewHolder != null) mNormalBigCardViewHolder.updatePlayStatusInList(position);
        }
    }

    IqutingPlayStateListener iqutingPlayStateListener = new IqutingPlayStateListener() {
        @Override
        public void onStart() {
            isPlaying = true;
            if(mExpand){
                //更新选中框
                if(mNormalBigCardViewHolder != null){
                    mNormalBigCardViewHolder.updateAllInStatus();
                }
                mIvIQuTingPlayPauseBtnBig.setImageResource(R.drawable.play_card_iquting_selector);
                checkStatusInList();
            }else {
                mIvIQuTingPlayPauseBtn.setImageResource(R.drawable.play_card_iquting_selector_small);
            }
        }

        @Override
        public void onPause() {
            isPlaying = false;
            if(mExpand){
                mIvIQuTingPlayPauseBtnBig.setImageResource(R.drawable.pause_card_iquting_selector);
                checkStatusInList();
            }else {
                mIvIQuTingPlayPauseBtn.setImageResource(R.drawable.pause_card_iquting_selector_small);
            }
        }

        @Override
        public void onStop() {
            isPlaying = false;
            if(mExpand){
                mIvIQuTingPlayPauseBtnBig.setImageResource(R.drawable.pause_card_iquting_selector);
                checkStatusInList();
            }else {
                mIvIQuTingPlayPauseBtn.setImageResource(R.drawable.pause_card_iquting_selector_small);
            }
        }

        @Override
        public void onProgress(String s, long l, long l1) {
            //播放进度，如果是音乐，新闻，电台类音频，按毫秒为单位，如果是有声书，按字数为单位。
            //Log.d(TAG,"onProgress " + s + "," +l + "," +l1);
            isPlaying = true;
            currentDuration = l;
            //由于VIP歌曲，这里总时长l1显示的是实际可以试听的时长1:00,故不用，还是按照queryCurrent拿到的mediaInfo.getTotalTime()显示总时长
            //totalDuration = l1;
            if(mExpand){
                mCircleProgressView.setMax(totalDuration / 1000);
                mCircleProgressView.setCurrent(currentDuration / 1000);
            }else {
                mTvIQuTingPlayPosition.setText(ToolUtils.formatTime(currentDuration / 1000));
                mTvIQuTingPlayDuration.setText(ToolUtils.formatTime(totalDuration / 1000));
                mProgressHorizontalIQuTing.setMaxValue(totalDuration / 1000);
                mProgressHorizontalIQuTing.updateProgress(currentDuration / 1000);
            }
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
    private void addIqutingPlayStateListener2(){
        IqutingBindService.getInstance().registerPlayStateListener(iqutingPlayStateListener);
    }

    //获取音乐榜单
    private void getMusicList(int contentId){
        Log.d(TAG_CONTENT,"getMusicList,contentId = " + contentId);
        mContentId = contentId;
        IqutingBindService.getInstance().getMusicList(contentId, new IQueryMusicLists() {
            @Override
            public void onSuccess(AreaContentResponseBean areaContentResponseBean) {
                Log.d(TAG_CONTENT,"getAreaContentData success");
                List<BaseSongItemBean> songLists = areaContentResponseBean.getSonglist();
                if(songLists != null){
                    isDataError = false;
                    int currentTab = sp.getInt(IqutingConfigs.CURRENTTAB,1);
                    if(contentId == TYPE_DAILYSONGS){
                        Log.d(TAG_CONTENT,"getAreaContentData dailySongLists");
                        dailySongLists = songLists;
                        mAreaContentResponseBeanDaily = areaContentResponseBean;
                        if(currentTab == TYPE_DAILYSONGS) {
                            if(mNormalBigCardViewHolder != null) mNormalBigCardViewHolder.updateSongs(dailySongLists);
                        }
                    }else if(contentId == TYPE_RANKSONGS){
                        Log.d(TAG_CONTENT,"getAreaContentData rankSongLists");
                        rankSongLists = songLists;
                        mAreaContentResponseBeanRank = areaContentResponseBean;
                        if(currentTab == TYPE_RANKSONGS) {
                            if(mNormalBigCardViewHolder != null) mNormalBigCardViewHolder.updateSongs(rankSongLists);
                        }
                    }
//                    for(BaseSongItemBean bean : songLists){
//                        Log.d(TAG_CONTENT,"" + bean.getSong_name() +
//                                "," + bean.getSinger_name() + "," + bean.getVip() + ",Song_id = " + bean.getSong_id());
//                    }
                }else {
                    Log.d(TAG_CONTENT,"getAreaContentData songLists is null");
                    isDataError = true;
                    //显示获取数据失败页面
                    ((Activity)getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mState = new DataErrorState();
                            mState.updateViewState(IQuTingCardView.this, mExpand);
                        }
                    });
                }
            }

            @Override
            public void onFail(int failCode) {
                Log.d(TAG_CONTENT,"getAreaContentData onFail: " + failCode);
                isDataError = true;
                //显示获取数据失败页面
                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mState = new DataErrorState();
                        mState.updateViewState(IQuTingCardView.this, mExpand);
                    }
                });
            }
        });
    }

    //查询播放状态
    private void queryPlayStatus(){
        //爱趣听没有播放，需要先调起爱趣听播放服务
//        LaunchConfig launchConfig = new LaunchConfig(false,false);
//        FlowPlayControl.getInstance().launchPlayService(context,launchConfig);
        FlowPlayControl.getInstance().queryPlaying(new QueryCallback<Boolean>() {
            @Override
            public void onError(int i) {
                Log.d(TAG,"queryPlayStatus onError: " + i);
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                Log.d(TAG,"queryPlayStatus onSuccess: isPlaying=" + aBoolean);
//                if(!aBoolean){
//                    //爱趣听没有播放，需要先调起爱趣听播放服务
//                    LaunchConfig launchConfig = new LaunchConfig(true,false,true);
//                    FlowPlayControl.getInstance().launchPlayService(context,launchConfig);
//                }
                isPlaying = aBoolean;
                if(isPlaying){
                    if(mExpand){
                        mIvIQuTingPlayPauseBtnBig.setImageResource(R.drawable.play_card_iquting_selector);
                    }else {
                        mIvIQuTingPlayPauseBtn.setImageResource(R.drawable.play_card_iquting_selector_small);
                    }
                }else {
                    if(mExpand){
                        mIvIQuTingPlayPauseBtnBig.setImageResource(R.drawable.pause_card_iquting_selector);
                    }else {
                        mIvIQuTingPlayPauseBtn.setImageResource(R.drawable.pause_card_iquting_selector_small);
                    }
                }
                getCurrentMediaInfo();
            }
        });
    }

    //获取当前的媒体信息
    private void getCurrentMediaInfo(){
        FlowPlayControl.getInstance().queryCurrent(new QueryCallback<MediaInfo>() {
            @Override
            public void onError(int i) {
                Log.d(TAG,"getCurrentMediaInfo onError i = " + i);
            }

            @Override
            public void onSuccess(MediaInfo mediaInfo) {
                //通知所有的卡片对象，同步刷新
                IqutingBindService.getInstance().notifyCurrentMediaInfo(mediaInfo);
                if(mediaInfo != null){
                    isHasMediaPlay = true;
                    currentMediaInfo = mediaInfo;
                    artist = mediaInfo.getMediaAuthor();
                    name = mediaInfo.getMediaName();
                    iconUrl = mediaInfo.getMediaImage();
                    itemUUID = mediaInfo.getItemUUID();
                    currentDuration = mediaInfo.getCurrentDuration();
                    totalDuration = mediaInfo.getTotalTime();
                    Log.d(TAG,"getCurrentMediaInfo onSuccess " + name + "," + artist +
                            "," + mediaInfo.getMediaType() + "," + itemUUID + "," + iconUrl);
                    if(mExpand){//中卡
                        mTvIQuTingMediaNameBig.setText(name);
                        mTvIQuTingArtistBig.setText(artist);

                        mCircleProgressView.setMax(totalDuration / 1000);
                        mCircleProgressView.setCurrent(currentDuration / 1000);
                        if(!TextUtils.isEmpty(iconUrl)){
                            GlideHelper.loadUrlImage(context,mIvIQuTingCoverBig,iconUrl);
                        }else {
                            GlideHelper.loadLocalCircleImage(context,mIvIQuTingCoverBig,R.drawable.test_cover2);
                        }
                        showFavor(false,mIvIQuTingLikeBtnBig,mediaInfo.getMediaType().trim(),mediaInfo.isFavored());
                    }else {
                        mTvIQuTingMediaName.setText(name + "-" + artist);

                        mTvIQuTingPlayPosition.setText(ToolUtils.formatTime(currentDuration / 1000));
                        mTvIQuTingPlayDuration.setText(ToolUtils.formatTime(totalDuration / 1000));

                        mProgressHorizontalIQuTing.setMaxValue(totalDuration / 1000);
                        mProgressHorizontalIQuTing.updateProgress(currentDuration / 1000);
                        if(!TextUtils.isEmpty(iconUrl)){
                            GlideHelper.loadUrlAlbumCoverRadius(context,mIvCover,iconUrl,RADIUS);
                        }else {
                            if(!isDestroy((Activity) context)){
                                GlideHelper.loadLocalAlbumCoverRadius(context,mIvCover,R.drawable.test_cover2,10);
                            }
                        }
                        showFavor(false,mIvIQuTingLike,mediaInfo.getMediaType().trim(),mediaInfo.isFavored());
                    }
                }else{
                    Log.d(TAG,"mediaInfo is null");
                    isHasMediaPlay = false;
                    mState = new NormalState();
                    mState.updateViewState(IQuTingCardView.this, mExpand);
                    if(mExpand){
                        GlideHelper.loadLocalCircleImage(getContext(), mIvIQuTingCoverBig, R.drawable.test_cover2);
                        mTvIQuTingMediaNameBig.setText("暗里着迷");
                        mTvIQuTingArtistBig.setText("刘德华");
                        mCircleProgressView.setCurrent(0);
                    }else {
                        if(!isDestroy((Activity) getContext())){
                            GlideHelper.loadLocalAlbumCoverRadius(getContext(), mIvCover, R.drawable.test_cover2, RADIUS);
                        }
                        mTvIQuTingMediaName.setText("暗里着迷—刘德华");
                        mProgressHorizontalIQuTing.updateProgress(0);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivIQuTingPlayPauseBtn || v.getId() == R.id.ivIQuTingPlayPauseBtnBig) {//暂停播放
            Log.d(TAG,"onClick ivIQuTingPlayPauseBtn isLogin: " + isLogin);
            if(!isLogin) return;
            checkHasMediaPlay();
            Log.d(TAG,"isPlaying: " + isPlaying);
            if(isPlaying){
                FlowPlayControl.getInstance().doPause();
            }else {
                FlowPlayControl.getInstance().doPlay();
            }
        }else if(v.getId() == R.id.ivIQuTingPreBtn || v.getId() == R.id.ivIQuTingPreBtnBig){//上一曲
            Log.d(TAG,"onClick ivIQuTingPreBtn isLogin: " + isLogin);
            if(!isLogin) return;
            checkHasMediaPlay();
//            FlowPlayControl.getInstance().doPre();
            if(mNormalBigCardViewHolder != null){
                mNormalBigCardViewHolder.updatePlayStatusInList(getCurrentItemPosition(itemUUID,
                        mContentId == TYPE_DAILYSONGS ? dailySongLists : rankSongLists));
            }
            FlowPlayControl.getInstance().doPre(new QueryCallback<Integer>() {
                @Override
                public void onError(int i) {
                    Log.d(TAG,"doPre onError: " + i);
                }

                @Override
                public void onSuccess(Integer integer) {
                    Log.d(TAG,"doPre onSuccess: " + integer.intValue());
                }
            });
        }else if(v.getId() == R.id.ivIQuTingNextBtn || v.getId() == R.id.ivIQuTingNextBtnBig){//下一曲
            Log.d(TAG,"onClick ivIQuTingNextBtn isLogin: " + isLogin);
            if(!isLogin) return;
            checkHasMediaPlay();
//            FlowPlayControl.getInstance().doNext();
            if(mNormalBigCardViewHolder != null){
                mNormalBigCardViewHolder.updatePlayStatusInList(getCurrentItemPosition(itemUUID,
                        mContentId == TYPE_DAILYSONGS ? dailySongLists : rankSongLists));
            }
            FlowPlayControl.getInstance().doNext(new QueryCallback<Integer>() {
                @Override
                public void onError(int i) {
                    Log.d(TAG,"doNext onError: " + i);
                }

                @Override
                public void onSuccess(Integer integer) {
                    Log.d(TAG,"doNext onSuccess: " + integer.intValue());
                }
            });
        }else if(v.getId() == R.id.ivIQuTingLike || v.getId() == R.id.ivIQuTingLikeBtnBig){//收藏
            Log.d(TAG,"onClick ivIQuTingLike isLogin: " + isLogin);
            if(!isLogin) return;
            checkHasMediaPlay();
            if(currentMediaInfo != null){
                if(mExpand){
                    commandFavor(mIvIQuTingLikeBtnBig);
                }else {
                    commandFavor(mIvIQuTingLike);
                }
            }
        }else if(v.getId() == R.id.ivCardIQuTingButton){//跳转至爱趣听
            Log.d(TAG,"onClick ivCardIQuTingButton");
            RecentAppHelper.launchApp(context,"com.tencent.wecarflow");
        }else if(v.getId() == R.id.tvIQuTingDailySongs){//每日推荐
            mTvIQuTingRankSongs.setTextColor(getResources().getColor(R.color.card_blue_default));
            mTvIQuTingDailySongs.setTextColor(getResources().getColor(R.color.card_main_theme));

            Log.d(TAG,"onClick tvIQuTingDailySongs  isLogin: " + isLogin);
            if(isLogin){
                if(mContentId == TYPE_RANKSONGS && (dailySongLists == null || dailySongLists.size() == 0)){
                    getMusicList(TYPE_DAILYSONGS);
                }else if(mContentId == TYPE_RANKSONGS && (dailySongLists != null && dailySongLists.size() != 0)){
                    if(mNormalBigCardViewHolder != null) mNormalBigCardViewHolder.updateSongs(dailySongLists);
                }
            }
            mContentId = TYPE_DAILYSONGS;
            editor.putInt(IqutingConfigs.CURRENTTAB,TYPE_DAILYSONGS);
            editor.commit();

            IqutingBindService.getInstance().setTabClickEvent(TYPE_DAILYSONGS);
        }else if(v.getId() == R.id.tvIQuTingRankSongs){//音乐排行榜
            mTvIQuTingDailySongs.setTextColor(getResources().getColor(R.color.card_blue_default));
            mTvIQuTingRankSongs.setTextColor(getResources().getColor(R.color.card_main_theme));

            Log.d(TAG,"onClick tvIQuTingRankSongs isLogin: " + isLogin);
            if(isLogin){
                if(mContentId == TYPE_DAILYSONGS && (rankSongLists == null || rankSongLists.size() == 0)){
                    getMusicList(TYPE_RANKSONGS);
                }else if(mContentId == TYPE_DAILYSONGS && (rankSongLists != null && rankSongLists.size() != 0)){
                    if(mNormalBigCardViewHolder != null) mNormalBigCardViewHolder.updateSongs(rankSongLists);
                }
            }
            mContentId = TYPE_RANKSONGS;
            editor.putInt(IqutingConfigs.CURRENTTAB,TYPE_RANKSONGS);
            editor.commit();
            IqutingBindService.getInstance().setTabClickEvent(TYPE_RANKSONGS);
        }else if(v.getId() == R.id.ivCardIQuTingRefreshBig){
            Log.d(TAG,"onClick ivCardIQuTingRefreshBig");
            showRefreshBigAnimation();
            if(isDataError){
                addPlayContentListener(IQuTingCardView.this);
            }
        }else if(v.getId() == R.id.ivCardIQuTingRefresh){
            Log.d(TAG,"onClick ivCardIQuTingRefresh");
            showRefreshAnimation();
            if(isDataError){
                addPlayContentListener(IQuTingCardView.this);
            }
        }else {
            //打开爱趣听界面
            FlowPlayControl.getInstance().startPlayActivity(context);
        }
    }

    private void showRefreshAnimation(){
        if (mRefreshAnimator == null) {
            mRefreshAnimator = createRefreshAnimator();
        } else {
            mRefreshAnimator.cancel();
        }
        mRefreshAnimator.start();
    }

    private ObjectAnimator createRefreshAnimator() {
        EasyLog.d(TAG, "createRefreshAnimator");
        ObjectAnimator animator = ObjectAnimator.ofFloat(mIvCardIQuTingRefresh, "rotation", 0f, 360f).setDuration(MIN_LOADING_ANIM_TIME);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(1);
        return animator;
    }

    private void showRefreshBigAnimation(){
        if (mRefreshBigAnimator == null) {
            mRefreshBigAnimator = createRefreshBigAnimator();
        } else {
            mRefreshBigAnimator.cancel();
        }
        mRefreshBigAnimator.start();
    }

    private ObjectAnimator createRefreshBigAnimator() {
        EasyLog.d(TAG, "createRefreshBigAnimator");
        ObjectAnimator animator = ObjectAnimator.ofFloat(mIvCardIQuTingRefreshBig, "rotation", 0f, 360f).setDuration(MIN_LOADING_ANIM_TIME);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(1);
        return animator;
    }

    private void checkHasMediaPlay(){
        Log.d(TAG,"isHasMediaPlay = " + isHasMediaPlay);
        if(!isHasMediaPlay){
            Toast.makeText(context,"当前无媒体在播放，请先在爱趣听中播放音频",Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void commandFavor(ImageView ivFavor){
        if(("like").equals((String)ivFavor.getTag())){//已收藏
            FlowPlayControl.getInstance().cancelFavor();
//            ivFavor.setImageResource(R.drawable.card_iquting_icon_unlike);
//            ivFavor.setTag("unlike");
        }else {//未收藏
            if(currentMediaInfo.isFavorable()){//当前节目是否可以收藏
                FlowPlayControl.getInstance().addFavor();
//                ivFavor.setImageResource(R.drawable.card_iquting_icon_like);
//                ivFavor.setTag("like");
            }else {
                ivFavor.setTag("unlike");
                Toast.makeText(context,"当前节目不可以收藏",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void expand() {
        mExpand = true;
        if(!isDataError) addPlayContentListener(IQuTingCardView.this);
        int currentTab = sp.getInt(IqutingConfigs.CURRENTTAB,1);
        if (mLargeCardView == null) {
            mLargeCardView = LayoutInflater.from(getContext()).inflate(R.layout.card_iquting_large, this, false);
            initBigCardView(mLargeCardView);
            mNormalBigCardViewHolder.updateSongs(currentTab == 1 ? dailySongLists : rankSongLists);
        }
        if(!isDataError){
            if(currentTab == TYPE_DAILYSONGS){
                mTvIQuTingDailySongs.setTextColor(getResources().getColor(R.color.card_main_theme));
                mTvIQuTingRankSongs.setTextColor(getResources().getColor(R.color.card_blue_default));
            }else {
                mTvIQuTingDailySongs.setTextColor(getResources().getColor(R.color.card_blue_default));
                mTvIQuTingRankSongs.setTextColor(getResources().getColor(R.color.card_main_theme));
            }

            mContentId = currentTab;
            mTvIQuTingMediaNameBig.setText(name);
            mTvIQuTingArtistBig.setText(artist);
            if(!TextUtils.isEmpty(iconUrl)){
                GlideHelper.loadUrlImage(getContext(),mIvIQuTingCoverBig,iconUrl);
            }else {
                GlideHelper.loadLocalCircleImage(getContext(),mIvIQuTingCoverBig,R.drawable.test_cover2);
            }

            if(isPlaying){
                mIvIQuTingPlayPauseBtnBig.setImageResource(R.drawable.play_card_iquting_selector);
            }else {
                mIvIQuTingPlayPauseBtnBig.setImageResource(R.drawable.pause_card_iquting_selector);
            }
            if("like".equals(mIvIQuTingLike.getTag())){
                mIvIQuTingLikeBtnBig.setImageResource(R.drawable.card_iquting_icon_like);
                mIvIQuTingLikeBtnBig.setTag("like");
            }else {
                mIvIQuTingLikeBtnBig.setImageResource(R.drawable.card_iquting_icon_unlike);
                mIvIQuTingLikeBtnBig.setTag("unlike");
            }
        }
        addView(mLargeCardView);
        if(mState != null) mState.updateViewState(this, mExpand);

        mLargeCardView.setVisibility(VISIBLE);
        mSmallCardView.setVisibility(GONE);

        LayoutParamUtil.setWidth(mLargeWidth, this);
        runExpandAnim();
        //更新列表中歌曲的选中状态
        if(mNormalBigCardViewHolder != null){
            mNormalBigCardViewHolder.updatePlayStatusInList(getCurrentItemPosition(itemUUID,
                    mContentId == TYPE_DAILYSONGS ? dailySongLists : rankSongLists));
        }
    }


    private void runExpandAnim() {
        ObjectAnimator.ofFloat(mLargeCardView, "translationX", -500, 0).setDuration(150).start();
        ObjectAnimator.ofFloat(mLargeCardView, "alpha", 0.1f, 1.0f).setDuration(500).start();
    }

    @Override
    public void collapse() {
        mExpand = false;
        if(isDataError){
            //显示获取数据失败页面
            mState = new DataErrorState();
            mState.updateViewState(IQuTingCardView.this, mExpand);
        }else {
            addPlayContentListener(IQuTingCardView.this);
            if(!TextUtils.isEmpty(iconUrl)){
                GlideHelper.loadUrlAlbumCoverRadius(getContext(),mIvCover,iconUrl,RADIUS);
            }else {
                if(!isDestroy((Activity) getContext())){
                    GlideHelper.loadLocalAlbumCoverRadius(getContext(),mIvCover,R.drawable.test_cover2,RADIUS);
                }
            }
            mTvIQuTingMediaName.setText(name + "-" + artist);
            if(isPlaying){
                mIvIQuTingPlayPauseBtn.setImageResource(R.drawable.play_card_iquting_selector_small);
            }else {
                mIvIQuTingPlayPauseBtn.setImageResource(R.drawable.pause_card_iquting_selector_small);
            }
            if("like".equals(mIvIQuTingLikeBtnBig.getTag())){
                mIvIQuTingLike.setImageResource(R.drawable.card_iquting_icon_like);
                mIvIQuTingLike.setTag("like");
            }else {
                mIvIQuTingLike.setImageResource(R.drawable.card_iquting_icon_unlike);
                mIvIQuTingLike.setTag("unlike");
            }
            mTvIQuTingPlayPosition.setText(ToolUtils.formatTime(currentDuration / 1000));
            mTvIQuTingPlayDuration.setText(ToolUtils.formatTime(totalDuration / 1000));
            mProgressHorizontalIQuTing.setMaxValue(totalDuration / 1000);
            mProgressHorizontalIQuTing.updateProgress(currentDuration / 1000);
        }

        //addPlayContentListener(IQuTingCardView.this);
        mSmallCardView.setVisibility(VISIBLE);
        mLargeCardView.setVisibility(GONE);
        removeView(mLargeCardView);
        LayoutParamUtil.setWidth(mSmallWidth, this);
    }

    private void initBigCardView(View largeCardView) {
        mNormalBigCardViewHolder = new NormalBigCardViewHolder(mLargeCardView);
        mIvIQuTingPlayPauseBtnBig = (ImageView) largeCardView.findViewById(R.id.ivIQuTingPlayPauseBtnBig);
        mTvIQuTingMediaNameBig = (TextView) largeCardView.findViewById(R.id.tvIQuTingMediaNameBig);
        mTvIQuTingArtistBig = (TextView) largeCardView.findViewById(R.id.tvIQuTingArtistBig);
        mIvIQuTingNextBtnBig = (ImageView) largeCardView.findViewById(R.id.ivIQuTingNextBtnBig);
        mIvIQuTingPreBtnBig = (ImageView) largeCardView.findViewById(R.id.ivIQuTingPreBtnBig);
        mIvIQuTingLikeBtnBig = (ImageView) largeCardView.findViewById(R.id.ivIQuTingLikeBtnBig);
        mTvIQuTingDailySongs = (TextView) largeCardView.findViewById(R.id.tvIQuTingDailySongs);
        mTvIQuTingRankSongs = (TextView) largeCardView.findViewById(R.id.tvIQuTingRankSongs);
        mCircleProgressView = (CircleProgressView) largeCardView.findViewById(R.id.circleProgressView);
        mIvCardIQuTingRefreshBig = (ImageView) largeCardView.findViewById(R.id.ivCardIQuTingRefreshBig);

        mTvIQuTingDailySongs.setOnClickListener(this);
        mTvIQuTingRankSongs.setOnClickListener(this);
        mIvIQuTingPlayPauseBtnBig.setOnClickListener(this);
        mIvIQuTingPreBtnBig.setOnClickListener(this);
        mIvIQuTingNextBtnBig.setOnClickListener(this);
        mIvIQuTingLikeBtnBig.setOnClickListener(this);
        mIvCardIQuTingRefreshBig.setOnClickListener(this);

        RecyclerView rcvCardIQuTingSongList = largeCardView.findViewById(R.id.rcvCardIQuTingSongList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvCardIQuTingSongList.setLayoutManager(layoutManager);
        // 间隔24px
        SimpleRcvDecoration divider = new SimpleRcvDecoration(24,layoutManager );
        if (rcvCardIQuTingSongList.getItemDecorationCount() <= 0) {
            rcvCardIQuTingSongList.addItemDecoration(divider);
        }
        IQuTingSongsAdapter adapter = new IQuTingSongsAdapter(getContext());
        rcvCardIQuTingSongList.setAdapter(adapter);
        rcvCardIQuTingSongList.getItemAnimator().setChangeDuration(0); //防止recyclerView刷新闪屏

        mNormalBigCardViewHolder.setSongsAdapter(adapter);
    }

    @Override
    public boolean hideDefaultTitle() {
        return false;
    }

    private class NormalSmallCardViewHolder {
        NormalSmallCardViewHolder() {
            mIvCover = findViewById(R.id.ivIQuTingCover);
        }

        void updateMediaInfo() {
            GlideHelper.loadLocalAlbumCoverRadius(getContext(), mIvCover, R.drawable.test_cover2, RADIUS);
        }
    }

    private class NormalBigCardViewHolder {
        private View itemView;
        private IQuTingSongsAdapter mIQuTingSongsAdapter;

        NormalBigCardViewHolder(View largeCardView) {
            itemView = largeCardView;
            mIvIQuTingCoverBig = itemView.findViewById(R.id.ivIQuTingCoverBig);
            updateCover();
        }

        public void setSongsAdapter(IQuTingSongsAdapter IQuTingSongsAdapter) {
            mIQuTingSongsAdapter = IQuTingSongsAdapter;
        }

        public void updateSongs(List<BaseSongItemBean> songList) {
            mIQuTingSongsAdapter.setData(songList);
            if(dailySongLists != null && dailySongLists.size() != 0) checkStatusInList();
        }

        public void updateCover() {
            GlideHelper.loadLocalCircleImage(getContext(), mIvIQuTingCoverBig, R.drawable.test_cover2);
        }

        public void updatePlayStatusInList(int position){
            //mIQuTingSongsAdapter.updatePlayStatus(position,isPlaying);
            Log.d(TAG,"updatePlayStatusInList position = " + position);
            if(dailySongLists == null || dailySongLists.size() == 0 || position == -1){
                return;
            }
            if(position == 0){
                mIQuTingSongsAdapter.notifyItemChanged(position);
                mIQuTingSongsAdapter.notifyItemChanged(position + 1);
            }else if(position == dailySongLists.size() - 1){
                mIQuTingSongsAdapter.notifyItemChanged(position);
                mIQuTingSongsAdapter.notifyItemChanged(position - 1);
            }else {
                mIQuTingSongsAdapter.notifyItemChanged(position);
                mIQuTingSongsAdapter.notifyItemChanged(position + 1);
                mIQuTingSongsAdapter.notifyItemChanged(position - 1);
            }
        }

        public void updateAllInStatus(){
            mIQuTingSongsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG,"onAttachedToWindow");
        mLifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG,"onDetachedFromWindow");
        mLifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            Log.d(TAG,"onWindowVisibilityChanged VISIBLE");
            addEventBus();
            addPlayContentListener(this);
            Log.d(TAG_CONTENT,"window visible addContentListener");
            //addContentListener();
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        } else if (visibility == GONE || visibility == INVISIBLE) {
            Log.d(TAG,"onWindowVisibilityChanged INVISIBLE");
//            removeMediaChangeListener();
//            removePlayStateListener();
            removeEventBus();
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        }
    }

    private void removeMediaChangeListener(){
        //FlowPlayControl.getInstance().removeMediaChangeListener(mediaChangeListener);
        //IqutingBindService.getInstance().removeRegistedMediaChangeListener(iqutingMediaChangeListener);
    }

    private void removePlayStateListener(){
        //FlowPlayControl.getInstance().removePlayStateListener(playStateListener);
        //IqutingBindService.getInstance().removeRegistedPlayStateListener(iqutingPlayStateListener);
    }

    private void addEventBus(){
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    private void removeEventBus(){
        EventBus.getDefault().unregister(this);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }
}
