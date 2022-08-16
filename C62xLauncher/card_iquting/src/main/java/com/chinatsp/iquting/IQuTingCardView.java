package com.chinatsp.iquting;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
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

import com.chinatsp.iquting.event.BootEvent;
import com.chinatsp.iquting.event.ContentConnectEvent;
import com.chinatsp.iquting.event.Event;
import com.chinatsp.iquting.event.PlayConnectEvent;
import com.chinatsp.iquting.songs.IQuTingSong;
import com.chinatsp.iquting.songs.IQuTingSongsAdapter;
import com.chinatsp.iquting.state.NetWorkDisconnectState;
import com.chinatsp.iquting.state.NormalState;
import com.chinatsp.iquting.state.IQuTingState;
import com.chinatsp.iquting.state.UnLoginState;
import com.chinatsp.iquting.utils.ToolUtils;
import com.tencent.wecarflow.contentsdk.ContentListener;
import com.tencent.wecarflow.contentsdk.ContentManager;
import com.tencent.wecarflow.contentsdk.bean.AreaContentResponseBean;
import com.tencent.wecarflow.contentsdk.bean.BaseSongItemBean;
import com.tencent.wecarflow.contentsdk.callback.LoginStatusResult;
import com.tencent.wecarflow.controlsdk.FlowPlayControl;
import com.tencent.wecarflow.controlsdk.MediaChangeListener;
import com.tencent.wecarflow.controlsdk.MediaInfo;
import com.tencent.wecarflow.controlsdk.PlayStateListener;
import com.tencent.wecarflow.controlsdk.QueryCallback;
import com.tencent.wecarflow.controlsdk.data.LaunchConfig;
import com.tencent.wecarflow.controlsdk.data.NavigationInfo;
import com.tencent.wecarflow.controlsdk.data.UserInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import card.service.ICardStyleChange;
import launcher.base.network.NetworkObserver;
import launcher.base.network.NetworkStateReceiver;
import launcher.base.network.NetworkUtils;
import launcher.base.recyclerview.SimpleRcvDecoration;
import launcher.base.utils.glide.GlideHelper;
import launcher.base.utils.recent.RecentAppHelper;
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
    private ImageView mIvIQuTingPlayPauseBtn;
    private TextView mTvCardIQuTingLoginTip;
    private TextView mTvIQuTingMediaName;
    private SimpleProgressView mProgressHorizontalIQuTing;
    private ImageView mIvCover;
    private ImageView mIvIQuTingPreBtn;
    private ImageView mIvIQuTingNextBtn;
    private ImageView mIvIQuTingLike;
    private ImageView mIvCardIQuTingButton;
    private TextView mTvIQuTingPlayPosition;
    private TextView mTvIQuTingPlayDuration;
    private static boolean isPlaying = false;
    private boolean isConnectContent = false;
    private MediaInfo currentMediaInfo;
    private MediaChangeListener mediaChangeListener;
    private PlayStateListener playStateListener;
    private static boolean isHasMediaPlay = false;

    private void init() {
        Log.d(TAG,"init");
        LayoutInflater.from(getContext()).inflate(R.layout.card_iquting, this);
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
        mNormalSmallCardViewHolder = new NormalSmallCardViewHolder();
//        mNormalSmallCardViewHolder.updateMediaInfo();
        mController = new IQuTingController(this);

        mIvIQuTingPlayPauseBtn.setOnClickListener(this);
        mIvIQuTingPreBtn.setOnClickListener(this);
        mIvIQuTingNextBtn.setOnClickListener(this);
        mIvIQuTingLike.setOnClickListener(this);
        mIvCardIQuTingButton.setOnClickListener(this);

        NetworkStateReceiver.getInstance().registerObserver(networkObserver);
        EventBus.getDefault().register(this);
    }

    private NetworkObserver networkObserver = new NetworkObserver() {
        @Override
        public void onNetworkChanged(boolean isConnected) {
            Log.d(TAG, "onNetworkChanged:" + isConnected);
            //有时数据已经打开连上，但是isConnected仍然是false，需要再去主动获取isConnected值
            addPlayListener(IQuTingCardView.this);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event){
        if(event instanceof PlayConnectEvent){
            if(((PlayConnectEvent)event).getType() == PlayConnectEvent.CONNECTED){
                addPlayListener(IQuTingCardView.this);
            }
        }else if(event instanceof ContentConnectEvent){
            if(((ContentConnectEvent)event).getType() == ContentConnectEvent.CONNECTED){
                //addContentListener();
            }
        }else if(event instanceof BootEvent){
            addPlayListener(IQuTingCardView.this);
            //addContentListener();
        }
    }

    private void addPlayListener(View view){
        boolean isConnected = NetworkUtils.isNetworkAvailable(context);
        if (!isConnected) {
            mState = new NetWorkDisconnectState();
            mState.updateViewState(IQuTingCardView.this, mExpand);
        } else {
            mState = new UnLoginState();
            mState.updateViewState(IQuTingCardView.this, mExpand);
            if(FlowPlayControl.getInstance().isServiceConnected()){
                Log.d(TAG,"playService Connect");
                checkLoginStatus(view);//查询用户登录状态
            }else {
                Log.d(TAG,"playService disConnected");
                FlowPlayControl.getInstance().bindPlayService(context);//注册爱趣听播放服务
            }
        }
    }

    private void addContentListener(){
        if(ContentManager.getInstance().isConnected()){
            //mTvCardIQuTingLoginTip.setText("ContentManager onServiceConnected");
            Log.d(TAG_CONTENT,"ContentManager onServiceConnected");
            ContentManager.getInstance().getLoginStatus(new LoginStatusResult() {
                @Override
                public void success(com.tencent.wecarflow.contentsdk.bean.UserInfo userInfo) {
                    if(userInfo != null){
                        mTvCardIQuTingLoginTip.setText("getMusicList");
                        boolean isContentLogin = userInfo.isLogin();
                        Log.d(TAG_CONTENT,"getLoginStatus isContentLogin: " + isContentLogin);
                        getMusicList();//获取音乐榜单
                    }else {
                        Log.d(TAG_CONTENT,"getLoginStatus userInfo is null");
                    }
                }

                @Override
                public void failed(int i) {
                    mTvCardIQuTingLoginTip.setText("getLoginStatus failed");
                    Log.d(TAG_CONTENT,"getLoginStatus failed: " + i);
                }
            });
        }else {
            Log.d(TAG_CONTENT,"ContentManager onService disConnected");
        }
    }

    //查询用户登录状态
    private void checkLoginStatus(View v){
        FlowPlayControl.getInstance().queryLoginStatus(new QueryCallback<UserInfo>() {
            @Override
            public void onError(int i) {
                Log.d(TAG,"checkLoginStatus onError: " + i);
            }

            @Override
            public void onSuccess(UserInfo userInfo) {
                if(userInfo != null){
                    if(userInfo.isLogin()){
                        Log.d(TAG,"checkLoginStatus onSuccess Login");
                        mState = new NormalState();
                        mState.updateViewState(v, mExpand);
                        queryPlayStatus();//查询播放状态
                    }else {
                        Log.d(TAG,"checkLoginStatus onSuccess not Login");
                        mState = new UnLoginState();
                        mState.updateViewState(v, mExpand);
                    }
                }else {
                    Log.d(TAG,"checkLoginStatus onSuccess,userInfo is null");
                }
            }
        });
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

    //监听爱趣听媒体的变化
    private void addIqutingMediaChangeListener(){
        mediaChangeListener = new MediaChangeListener() {
            @Override
            public void onMediaChange(MediaInfo mediaInfo) {
                currentMediaInfo = mediaInfo;
                if(mediaInfo != null){
                    isHasMediaPlay = true;
                    Log.d(TAG,"onMediaChange " + mediaInfo.getMediaName() + "," + mediaInfo.getMediaAuthor() +
                            "," + mediaInfo.getMediaType() + "," + ToolUtils.formatTime(mediaInfo.getDuration()));
                    mTvIQuTingMediaName.setText(mediaInfo.getMediaName() + "-" + mediaInfo.getMediaAuthor());
                    if(!isDestroy((Activity) context) && (mediaInfo.getMediaImage() != null)){
                        GlideHelper.loadUrlAlbumCover(context,mIvCover,mediaInfo.getMediaImage(),5);
                    }

                    mProgressHorizontalIQuTing.updateProgress(0);
                    mTvIQuTingPlayPosition.setText(ToolUtils.formatTime(0));
                    mTvIQuTingPlayDuration.setText(ToolUtils.formatTime(mediaInfo.getDuration()));
                    if(mediaInfo.getMediaType() != null){
                        showFavor(mediaInfo.getMediaType().trim(),mediaInfo.isFavored());
                    }
                }else {
                    Log.d(TAG,"onMediaChange, mediaInfo is null");
                    isHasMediaPlay = false;
                }
            }

            @Override
            public void onMediaChange(MediaInfo mediaInfo, NavigationInfo navigationInfo) {
                Log.d(TAG,"onMediaChange ");
            }

            @Override
            public void onFavorChange(boolean b, String s) {
                Log.d(TAG,"onFavorChange");
            }

            @Override
            public void onModeChange(int i) {
                Log.d(TAG,"onModeChange");
            }

            @Override
            public void onPlayListChange() {
                Log.d(TAG,"onPlayListChange");
            }
        };
        FlowPlayControl.getInstance().addMediaChangeListener(mediaChangeListener);
    }

    private void showFavor(String type,boolean isFavor){
        //sdk目前收藏功能只对音乐有效
        if("song".equals(type)){
            if(isFavor){
                mIvIQuTingLike.setImageResource(R.drawable.card_iquting_icon_like);
                mIvIQuTingLike.setTag("like");
            }else {
                mIvIQuTingLike.setImageResource(R.drawable.card_iquting_icon_unlike);
                mIvIQuTingLike.setTag("unlike");
            }
            mIvIQuTingLike.setVisibility(View.VISIBLE);
        }else {
            mIvIQuTingLike.setVisibility(View.GONE);
        }
    }

    //监听爱趣听播放状态变化
    private void addIqutingPlayStateListener(){
        playStateListener = new PlayStateListener() {
            @Override
            public void onStart() {
                Log.d(TAG,"onStart");
                isPlaying = true;
                mIvIQuTingPlayPauseBtn.setImageResource(R.drawable.card_iquting_icon_play_100);
            }

            @Override
            public void onPause() {
                Log.d(TAG,"onPause");
                isPlaying = false;
                mIvIQuTingPlayPauseBtn.setImageResource(R.drawable.card_iquting_icon_pause_100);
            }

            @Override
            public void onStop() {
                Log.d(TAG,"onStop");
                isPlaying = false;
                mIvIQuTingPlayPauseBtn.setImageResource(R.drawable.card_iquting_icon_pause_100);
            }

            @Override
            public void onProgress(String s, long l, long l1) {//s 类型，l 当前进度， l1总进度
                //播放进度，如果是音乐，新闻，电台类音频，按毫秒为单位，如果是有声书，按字数为单位。
                //Log.d(TAG,"onProgress " + s + "," +l + "," +l1);
                isPlaying = true;
                mTvIQuTingPlayPosition.setText(ToolUtils.formatTime(l / 1000));
                mTvIQuTingPlayDuration.setText(ToolUtils.formatTime(l1 / 1000));
                mProgressHorizontalIQuTing.setMaxValue(l1);
                mProgressHorizontalIQuTing.updateProgress(l);
            }

            @Override
            public void onBufferingStart() {
                Log.d(TAG,"onBufferingStart");
            }

            @Override
            public void onBufferingEnd() {
                Log.d(TAG,"onBufferingEnd");
            }

            @Override
            public void onPlayError(int i, String s) {
                Log.d(TAG,"onPlayError");
            }

            @Override
            public void onAudioSessionId(int i) {
                Log.d(TAG,"onAudioSessionId");
            }
        };
        FlowPlayControl.getInstance().addPlayStateListener(playStateListener);
    }

    //获取音乐榜单
    private void getMusicList(){
        Log.d(TAG_CONTENT,"getMusicList");
        String contentId = "3";
        ContentManager.getInstance().getAreaContentData(new ContentListener<AreaContentResponseBean>() {
            @Override
            public void onContentGot(@Nullable AreaContentResponseBean areaContentResponseBean) {
                Log.d(TAG_CONTENT,"onContentGot");
                mTvCardIQuTingLoginTip.setText("getAreaContentData success");
                List<BaseSongItemBean> songLists = areaContentResponseBean.getSonglist();
                for(BaseSongItemBean bean : songLists){
                    Log.d(TAG_CONTENT,"" + bean.getAlbum_name() + "," + bean.getAlbum_id() +
                            "," + bean.getSinger_name() + "," + bean.getVip());
                }
            }
        },contentId);
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
                if(!aBoolean){
                    //爱趣听没有播放，需要先调起爱趣听播放服务
                    LaunchConfig launchConfig = new LaunchConfig(false,false);
                    FlowPlayControl.getInstance().launchPlayService(context,launchConfig);
                }
                isPlaying = aBoolean;
                if(isPlaying){
                    mIvIQuTingPlayPauseBtn.setImageResource(R.drawable.card_iquting_icon_play_100);
                }else {
                    mIvIQuTingPlayPauseBtn.setImageResource(R.drawable.card_iquting_icon_pause_100);
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
                if(mediaInfo != null){
                    isHasMediaPlay = true;
                    Log.d(TAG,"getCurrentMediaInfo onSuccess " + mediaInfo.getMediaName() + "," + mediaInfo.getMediaAuthor() +
                            "," + mediaInfo.getMediaType() + "," + mediaInfo.getDuration() + "," +mediaInfo.getCurrentDuration());
                    currentMediaInfo = mediaInfo;
                    mTvIQuTingMediaName.setText(mediaInfo.getMediaName() + "-" + mediaInfo.getMediaAuthor());

                    mTvIQuTingPlayPosition.setText(ToolUtils.formatTime(mediaInfo.getCurrentDuration() / 1000));
                    mTvIQuTingPlayDuration.setText(ToolUtils.formatTime(mediaInfo.getDuration()));

                    mProgressHorizontalIQuTing.setMaxValue(mediaInfo.getDuration());
                    mProgressHorizontalIQuTing.updateProgress(mediaInfo.getCurrentDuration() / 1000);
                    if(mediaInfo.getMediaImage() != null){
                        GlideHelper.loadUrlAlbumCover(context,mIvCover,mediaInfo.getMediaImage(),5);
                    }
                    showFavor(mediaInfo.getMediaType().trim(),mediaInfo.isFavored());
                }else{
                    Log.d(TAG,"mediaInfo is null");
                    isHasMediaPlay = false;
                    GlideHelper.loadImageUrlAlbumCover(getContext(), mIvCover, R.drawable.test_cover2, 10);
                    mTvIQuTingMediaName.setText("暗里着迷—刘德华");
                    mProgressHorizontalIQuTing.updateProgress(0);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivIQuTingPlayPauseBtn) {//暂停播放
            Log.d(TAG,"onClick ivIQuTingPlayPauseBtn");
            checkHasMediaPlay();
            Log.d(TAG,"isPlaying: " + isPlaying);
            if(isPlaying){
                FlowPlayControl.getInstance().doPause();
            }else {
                FlowPlayControl.getInstance().doPlay();
            }
        }else if(v.getId() == R.id.ivIQuTingPreBtn){//上一曲
            Log.d(TAG,"onClick ivIQuTingPreBtn");
            checkHasMediaPlay();
//            FlowPlayControl.getInstance().doPre();
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
        }else if(v.getId() == R.id.ivIQuTingNextBtn){//下一曲
            Log.d(TAG,"onClick ivIQuTingNextBtn");
            checkHasMediaPlay();
//            FlowPlayControl.getInstance().doNext();
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
        }else if(v.getId() == R.id.ivIQuTingLike){//收藏
            Log.d(TAG,"onClick ivIQuTingLike");
            checkHasMediaPlay();
            if(currentMediaInfo != null){
                if(((String)mIvIQuTingLike.getTag()).equals("like")){//已收藏
                    FlowPlayControl.getInstance().cancelFavor();
                    mIvIQuTingLike.setImageResource(R.drawable.card_iquting_icon_unlike);
                    mIvIQuTingLike.setTag("unlike");
                }else {//未收藏
                    if(currentMediaInfo.isFavorable()){//当前节目是否可以收藏
                        FlowPlayControl.getInstance().addFavor();
                        mIvIQuTingLike.setImageResource(R.drawable.card_iquting_icon_like);
                        mIvIQuTingLike.setTag("like");
                    }else {
                        Toast.makeText(context,"当前节目不可以收藏",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }else if(v.getId() == R.id.ivCardIQuTingButton){//跳转至爱趣听
            Log.d(TAG,"onClick ivCardIQuTingButton");
            RecentAppHelper.launchApp(context,"com.tencent.wecarflow");
        }
    }

    private void checkHasMediaPlay(){
        Log.d(TAG,"isHasMediaPlay = " + isHasMediaPlay);
        if(!isHasMediaPlay){
            Toast.makeText(context,"当前无媒体在播放，请先在爱趣听中播放音频",Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void expand() {
        mExpand = true;
        if (mLargeCardView == null) {
            mLargeCardView = LayoutInflater.from(getContext()).inflate(R.layout.card_iquting_large, this, false);
            initBigCardView(mLargeCardView);
            mNormalBigCardViewHolder.updateSongs(mController.createTestList());
        }
        addView(mLargeCardView);
        mState.updateViewState(this, mExpand);

        mLargeCardView.setVisibility(VISIBLE);
        mSmallCardView.setVisibility(GONE);

        LayoutParamUtil.setWidth(mLargeWidth, this);
        runExpandAnim();
    }

    private void runExpandAnim() {
        ObjectAnimator.ofFloat(mLargeCardView, "translationX", -500, 0).setDuration(150).start();
        ObjectAnimator.ofFloat(mLargeCardView, "alpha", 0.1f, 1.0f).setDuration(500).start();
    }

    @Override
    public void collapse() {
        mExpand = false;
        mSmallCardView.setVisibility(VISIBLE);
        mLargeCardView.setVisibility(GONE);
        removeView(mLargeCardView);
        LayoutParamUtil.setWidth(mSmallWidth, this);
    }

    private void initBigCardView(View largeCardView) {
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

        mNormalBigCardViewHolder = new NormalBigCardViewHolder(mLargeCardView);
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
            GlideHelper.loadImageUrlAlbumCover(getContext(), mIvCover, R.drawable.test_cover2, 10);
        }
    }

    private class NormalBigCardViewHolder {
        private View itemView;
        private IQuTingSongsAdapter mIQuTingSongsAdapter;
        private ImageView ivIQuTingCoverBig;

        NormalBigCardViewHolder(View largeCardView) {
            itemView = largeCardView;
            ivIQuTingCoverBig = itemView.findViewById(R.id.ivIQuTingCoverBig);
            updateCover();
        }

        public void setSongsAdapter(IQuTingSongsAdapter IQuTingSongsAdapter) {
            mIQuTingSongsAdapter = IQuTingSongsAdapter;
        }

        public void updateSongs(List<IQuTingSong> songList) {
            mIQuTingSongsAdapter.setData(songList);
        }

        public void updateCover() {
            GlideHelper.loadCircleImage(getContext(), ivIQuTingCoverBig, R.drawable.test_cover2);
        }
    }

    private void changeState(IQuTingState newState) {
        mState = newState;
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
            addIqutingMediaChangeListener();//监听爱趣听媒体的变化
            addIqutingPlayStateListener();//监听爱趣听播放状态变化
            addPlayListener(this);
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        } else if (visibility == GONE || visibility == INVISIBLE) {
            Log.d(TAG,"onWindowVisibilityChanged INVISIBLE");
            removeMediaChangeListener();
            removePlayStateListener();
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        }
    }

    private void removeMediaChangeListener(){
        FlowPlayControl.getInstance().removeMediaChangeListener(mediaChangeListener);
    }

    private void removePlayStateListener(){
        FlowPlayControl.getInstance().removePlayStateListener(playStateListener);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }
}
