package com.chinatsp.appstore;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.appstore.bean.AppInfo;
import com.chinatsp.appstore.adapter.AppStoreAppsAdapter;
import com.chinatsp.appstore.bean.AppStoreBean;
import com.chinatsp.appstore.bean.MaterialBean;
import com.chinatsp.appstore.configs.AppStoreConfigs;
import com.chinatsp.appstore.event.RefreshUIEvent;
import com.chinatsp.appstore.request.AdsBody;
import com.chinatsp.appstore.request.TokenBody;
import com.chinatsp.appstore.request.TokenResponse;
import com.chinatsp.appstore.state.AppStoreDataErrorState;
import com.chinatsp.appstore.state.AppStoreErrorNetWorkState;
import com.chinatsp.appstore.state.AppStoreNormalState;
import com.chinatsp.appstore.state.AppStoreState;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import card.service.ICardStyleChange;
import launcher.base.network.NetworkObserver;
import launcher.base.network.NetworkStateReceiver;
import launcher.base.network.NetworkUtils;
import launcher.base.utils.EasyLog;
import launcher.base.utils.glide.GlideHelper;
import launcher.base.utils.property.PropertyUtils;
import launcher.base.utils.recent.RecentAppHelper;
import launcher.base.utils.view.LayoutParamUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AppStoreCardView extends ConstraintLayout implements ICardStyleChange, LifecycleOwner, View.OnClickListener{

    public AppStoreCardView(@NonNull Context context) {
        super(context);
        init();
    }

    public AppStoreCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppStoreCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public AppStoreCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private static final String TAG = "AppStoreCardView";
    private static final String APPSTOREPKG = "com.huawei.appmarket.car.landscape";
    private View mLargeCardView;
    private View mSmallCardView;
    private NormalSmallCardViewHolder mNormalSmallCardViewHolder;
    private NormalBigCardViewHolder mNormalBigCardViewHolder;
    private ImageView mIvAppStoreRefresh;
    private ImageView mIvAppStoreRefreshBig;
    private ImageView mIvAppIconTop;
    private ImageView mIvAppIconBottom;
    private TextView mTvAppNameTop;
    private TextView mTvAppDescTop;
    private TextView mTvAppNameBottom;
    private TextView mTvAppDescBottom;
    private AppStoreState mState;
    private boolean mExpand = false;
    private List<AppInfo> infos;
    private int mSmallWidth;
    private int mLargeWidth;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private ObjectAnimator mRefreshAnimator;
    private ObjectAnimator mRefreshBigAnimator;
    private final int MIN_LOADING_ANIM_TIME = 1000;

    private void init() {
        Log.d(TAG, "init");
        LayoutInflater.from(getContext()).inflate(R.layout.card_appstore, this);
        sp = getContext().getSharedPreferences(AppStoreConfigs.APPSTORESP,Context.MODE_PRIVATE);
        editor = sp.edit();
        mLargeWidth = (int) getResources().getDimension(R.dimen.card_width_large);
        mSmallCardView = findViewById(R.id.layoutSmallCardView);
        mIvAppStoreRefresh = (ImageView) findViewById(R.id.ivAppStoreRefresh);
        mIvAppIconTop = (ImageView) findViewById(R.id.ivAppIconTop);
        mIvAppIconBottom = (ImageView) findViewById(R.id.ivAppIconBottom);
        mTvAppNameTop = (TextView) findViewById(R.id.tvAppNameTop);
        mTvAppDescTop = (TextView) findViewById(R.id.tvAppDescTop);
        mTvAppNameBottom = (TextView) findViewById(R.id.tvAppNameBottom);
        mTvAppDescBottom = (TextView) findViewById(R.id.tvAppDescBottom);

        mSmallWidth = (int) getResources().getDimension(R.dimen.card_width);
        mLargeWidth = (int) getResources().getDimension(R.dimen.card_width_large);
        mIvAppStoreRefresh.setOnClickListener(this);
        mIvAppIconTop.setOnClickListener(this);
        mIvAppIconBottom.setOnClickListener(this);
        //点击空白处跳转至应用商城
//        setOnClickListener(this);

        NetworkStateReceiver.getInstance().registerObserver(networkObserver);
        loadData();
    }

    private NetworkObserver networkObserver = new NetworkObserver() {
        @Override
        public void onNetworkChanged(boolean isConnected) {
            Log.d(TAG, "onNetworkChanged:" + isConnected);
            loadData();
        }
    };

    private void loadData(){
        boolean isConnected = NetworkUtils.isNetworkAvailable(getContext());
        if (!isConnected) {
            mState = new AppStoreErrorNetWorkState();
            mState.updateViewState(AppStoreCardView.this, mExpand);
        } else {
            mState = new AppStoreNormalState();
            mState.updateViewState(AppStoreCardView.this,mExpand);

            if(infos == null || infos.size() == 0){
                getData();
            }else {
                refreshUI();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ivAppIconTop){
            //如果该应用已经安装就打开它，否则跳转到下载详情
            String pkgName = infos.get(0).getPackageName();
            boolean isPkgInstalled = PropertyUtils.checkPkgInstalled(v.getContext(),pkgName);
            if(isPkgInstalled){
                RecentAppHelper.launchApp(v.getContext(), pkgName);
            }else {
                AppStoreJump.jumpAppMarket(pkgName, v.getContext());
            }
        }else if(v.getId() == R.id.ivAppIconBottom){
            //如果该应用已经安装就打开它，否则跳转到下载详情
            String pkgName = infos.get(1).getPackageName();
            boolean isPkgInstalled = PropertyUtils.checkPkgInstalled(v.getContext(),pkgName);
            if(isPkgInstalled){
                RecentAppHelper.launchApp(v.getContext(), pkgName);
            }else {
                AppStoreJump.jumpAppMarket(pkgName, v.getContext());
            }
        }else if(v.getId() == R.id.ivAppStoreRefresh){
            Log.d(TAG,"onClick ivAppStoreRefresh");
            boolean isConnected = NetworkUtils.isNetworkAvailable(getContext());
            if (isConnected) {
                getData();
            }
            showRefreshAnimation();
        }else if(v.getId() == R.id.ivAppStoreRefreshBig){
            Log.d(TAG,"onClick ivAppStoreRefreshBig");
            boolean isConnected = NetworkUtils.isNetworkAvailable(getContext());
            if (isConnected) {
                getData();
            }
            showRefreshBigAnimation();
        }else {
            RecentAppHelper.launchApp(getContext(),APPSTOREPKG);
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
        ObjectAnimator animator = ObjectAnimator.ofFloat(mIvAppStoreRefresh, "rotation", 0f, 360f).setDuration(MIN_LOADING_ANIM_TIME);
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
        ObjectAnimator animator = ObjectAnimator.ofFloat(mIvAppStoreRefreshBig, "rotation", 0f, 360f).setDuration(MIN_LOADING_ANIM_TIME);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(1);
        return animator;
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }

    @Override
    public void expand() {
        mExpand = true;
        if (mLargeCardView == null) {
            mLargeCardView = LayoutInflater.from(getContext()).inflate(R.layout.card_appstore_large, this, false);
            initBigCardView(mLargeCardView);
        }

        mNormalBigCardViewHolder.updateApps(infos);//更新数据
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

        loadData();
        mSmallCardView.setVisibility(VISIBLE);
        mLargeCardView.setVisibility(GONE);
        removeView(mLargeCardView);
        LayoutParamUtil.setWidth(mSmallWidth, this);
    }

    private void getData(){
        //1.获取token
        String token = sp.getString(AppStoreConfigs.TOKEN,"");
        long getTokenTime = sp.getLong(AppStoreConfigs.GETTOKENTIME,0L);
        Log.d(TAG,"token = " + token + ",getTokenTime = " + getTokenTime);
        //如果token不存在或者过期了，需要重新去获取
        if(TextUtils.isEmpty(token) || (Math.abs(System.currentTimeMillis() - getTokenTime) >= AppStoreConfigs.EXPIRE_TIME)){
            getToken();
        }else {
            getAds();
        }
    }

    private void getToken(){
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
        String jsonBody = new Gson().toJson(new TokenBody(
                AppStoreConfigs.GRANT_TYPE,AppStoreConfigs.CLIENT_ID,AppStoreConfigs.CLIENT_SECRET));
        RequestBody body = RequestBody.create(mediaType, jsonBody);
        Request request = new Request.Builder()
                .url(AppStoreConfigs.TOKEN_URL)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG,"getToken onFailure: " + e);
                //显示获取数据失败页面
                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mState = new AppStoreDataErrorState();
                        mState.updateViewState(AppStoreCardView.this, mExpand);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(TAG,"getToken onResponse: " + response.code());
                if(response.code() == 200){
                    String res = response.body().string();
                    Log.d(TAG,"onResponse: " + res);
                    TokenResponse tokenResponse = new Gson().fromJson(res,TokenResponse.class);
                    String token = tokenResponse.getAccess_token();
                    editor.putString(AppStoreConfigs.TOKEN,token);
                    editor.putLong(AppStoreConfigs.GETTOKENTIME,System.currentTimeMillis());
                    editor.commit();
                    getAds();
                }else {
                    //显示获取数据失败页面
                    ((Activity)getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mState = new AppStoreDataErrorState();
                            mState.updateViewState(AppStoreCardView.this, mExpand);
                        }
                    });
                }
            }
        });
    }

    private void getAds(){
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
        String jsonBody = new Gson().toJson(getAdsBody());
        Log.d(TAG,"getAds jsonBody: " + jsonBody);
        RequestBody body = RequestBody.create(mediaType, jsonBody);
        String token = sp.getString(AppStoreConfigs.TOKEN,"");
        Log.d(TAG,"getAds token: " + token);
        Request request = new Request.Builder()
                .url(AppStoreConfigs.ADS_URL)
                .addHeader("client_id",AppStoreConfigs.CLIENT_ID)
                .addHeader("Authorization","Bearer" + " " + token)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG,"getAds onFailure: " + e);
                //显示获取数据失败页面
                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mState = new AppStoreDataErrorState();
                        mState.updateViewState(AppStoreCardView.this, mExpand);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(TAG,"getAds onResponse: " + response.code());
                if(response.code() == 200){
                    String res = response.body().string();
                    Log.d(TAG,"onResponse: " + res);
                    AppStoreBean appStoreBean = new Gson().fromJson(res,AppStoreBean.class);
                    if(appStoreBean.getRtnCode() == 0){//成功
                        List<MaterialBean> materialBeanList = appStoreBean.getAdInfos();
                        infos = new ArrayList<>();
                        for(MaterialBean materialBean : materialBeanList){
                            infos.add(materialBean.getMaterial().getAppInfo());
                        }

                        //通知更新UI
                        refreshUI();
                    }
                }else {
                    //显示获取数据失败页面
                    ((Activity)getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mState = new AppStoreDataErrorState();
                            mState.updateViewState(AppStoreCardView.this, mExpand);
                        }
                    });
                }
            }
        });
    }

    private void refreshUI(){
        ((Activity)getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!mExpand) {
                    GlideHelper.loadUrlAlbumCoverRadius(getContext(),mIvAppIconTop,infos.get(0).getIcon(),0);
                    String desTop = infos.get(0).getDescription();
                    if(desTop.length() > 10){
                        desTop = desTop.substring(0,10) + "...";
                    }
                    mTvAppNameTop.setText(infos.get(0).getAppName());
                    mTvAppDescTop.setText(desTop);

                    GlideHelper.loadUrlAlbumCoverRadius(getContext(),mIvAppIconBottom,infos.get(1).getIcon(),0);
                    String desBottom = infos.get(1).getDescription();
                    if(desBottom.length() > 10){
                        desBottom = desBottom.substring(0,10) + "...";
                    }
                    mTvAppNameBottom.setText(infos.get(1).getAppName());
                    mTvAppDescBottom.setText(desBottom);
                }else {
                    mNormalBigCardViewHolder.updateApps(infos);//更新数据
                }

                mState = new AppStoreNormalState();
                mState.updateViewState(AppStoreCardView.this, mExpand);
            }
        });
    }

    private AdsBody getAdsBody(){
        /*
        *   {"adSlot":{"adCount":6,"sceneId":"2001","slotId":"6Cc22KKwYrfvHfi246aIFM8aBrwISc5c"},"apiVersion":"1.0",
        *    "appSign":"launcher","deviceInfo":{"androidApiLevel":"28","country":"CN","deviceId":"2416b0e9eb9e7ee8",
        *    "deviceModel":"C62X-F06","deviceType":"8","locale":"zh_CN","os":"1"},"mediaInfo":{"mediaPkgName":"com.chinatsp.launcher",
        *    "mediaVersion":"1.0.2"},"networkInfo":{"carrier":"0","connectType":"0"},"pkgName":"com.chinatsp.launcher",
        *    "requestId":"I1e8acc4R29cH89HH86oF07lEn2TZ8Hc"}
         */
        String requestId = getCharAndNumr(32);
        String deviceId = getAndroidId(getContext());
        Log.d(TAG,"deviceId：" + deviceId);
        AdsBody.AdSlot adSlot = new AdsBody.AdSlot(AppStoreConfigs.ADCOUNT,AppStoreConfigs.SCENEID,AppStoreConfigs.SLOTID);
        AdsBody.DeviceInfo deviceInfo = new AdsBody.DeviceInfo("28","CN","C62X-F06",
                "zh_CN","1",deviceId,"8");
        AdsBody.MediaInfo mediaInfo = new AdsBody.MediaInfo("com.chinatsp.launcher","1.0.2");
        AdsBody.NetworkInfo networkInfo = new AdsBody.NetworkInfo("0","0");
        return new AdsBody(requestId,"1.0","launcher","com.chinatsp.launcher",adSlot,
                deviceInfo,mediaInfo,networkInfo);
    }

    /**
     * java生成随机数字和字母组合
     * @param length 生成随机数的长度
     * @return
     */
    public String getCharAndNumr(int length) {
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            // 输出字母还是数字
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            // 字符串
            if ("char".equalsIgnoreCase(charOrNum)) {
                // 取得大写字母还是小写字母
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (choice + random.nextInt(26));
            } else if ("num".equalsIgnoreCase(charOrNum)) { // 数字
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    public String getAndroidId(Context context){
        String androidId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        if(TextUtils.isEmpty(androidId)){
            androidId = Settings.System.getString(context.getContentResolver(), "TUID");
        }
        return androidId;
    }

    private void initBigCardView(View largeCardView) {
        mNormalBigCardViewHolder = new NormalBigCardViewHolder(mLargeCardView);
        mIvAppStoreRefreshBig = largeCardView.findViewById(R.id.ivAppStoreRefreshBig);
        mIvAppStoreRefreshBig.setOnClickListener(this);
        RecyclerView rcvCardIQuTingSongList = largeCardView.findViewById(R.id.rcvAppStoreAppsList);
        //rcvCardIQuTingSongList.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3){
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rcvCardIQuTingSongList.setLayoutManager(layoutManager);
        // 间隔24px
//        SimpleRcvDecoration divider = new SimpleRcvDecoration(24,layoutManager );
//        if (rcvCardIQuTingSongList.getItemDecorationCount() <= 0) {
//            rcvCardIQuTingSongList.addItemDecoration(divider);
//        }
        AppStoreAppsAdapter adapter = new AppStoreAppsAdapter(getContext());
        rcvCardIQuTingSongList.setAdapter(adapter);
        rcvCardIQuTingSongList.getItemAnimator().setChangeDuration(0); //防止recyclerView刷新闪屏

        //点击RecyclerView空白区域，跳转至应用商城
//        rcvCardIQuTingSongList.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(v.getId() != 0){
//                    RecentAppHelper.launchApp(getContext(),APPSTOREPKG);
//                }
//                return false;
//            }
//        });
        mNormalBigCardViewHolder.setAppsAdapter(adapter);
    }

    @Override
    public boolean hideDefaultTitle() {
        return false;
    }

    private class NormalSmallCardViewHolder {
        NormalSmallCardViewHolder() {

        }

        void updateMediaInfo() {

        }
    }

    private class NormalBigCardViewHolder {
        private AppStoreAppsAdapter mAppStoreAppsAdapter;
        NormalBigCardViewHolder(View largeCardView) {
            updateCover();
        }

        public void setAppsAdapter(AppStoreAppsAdapter adapter) {
            mAppStoreAppsAdapter = adapter;
        }

        public void updateApps(List<AppInfo> infos) {
            mAppStoreAppsAdapter.setData(infos);
        }

        public void updateCover() {
            //GlideHelper.loadLocalCircleImage(getContext(), mIvIQuTingCoverBig, R.drawable.test_cover2);
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
    }
}
