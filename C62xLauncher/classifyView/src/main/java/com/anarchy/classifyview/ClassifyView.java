package com.anarchy.classifyview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anarchy.classifyview.Bean.LocationBean;
import com.anarchy.classifyview.adapter.BaseMainAdapter;
import com.anarchy.classifyview.adapter.BaseSubAdapter;
import com.anarchy.classifyview.adapter.MainRecyclerViewCallBack;
import com.anarchy.classifyview.adapter.SubAdapterReference;
import com.anarchy.classifyview.adapter.SubRecyclerViewCallBack;
import com.anarchy.classifyview.event.ChangeTitleEvent;
import com.anarchy.classifyview.event.JumpToCardEvent;
import com.anarchy.classifyview.event.ReStoreDataEvent;
import com.anarchy.classifyview.simple.BaseSimpleAdapter;
import com.anarchy.classifyview.simple.widget.InsertAbleGridView;
import com.anarchy.classifyview.time.CountTimer;
import com.anarchy.classifyview.util.L;
import com.anarchy.classifyview.util.MyConfigs;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p/>
 * Date: 16/6/1 14:16
 * Author: zhendong.wu@shoufuyou.com
 * <p/>
 */
public class ClassifyView extends FrameLayout {
    /**
     * 不做处理的状态
     */
    public static final int STATE_NONE = 0;
    /**
     * 当前状态为 可移动
     */
    public static final int STATE_MOVE = 1;
    /**
     * 当前状态为 可合并
     */
    public static final int STATE_MERGE = 2;


    private static final int ACTIVE_POINTER_ID_NONE = -1;
    private static final String DESCRIPTION = "Long press";
    private static final String MAIN = "main";
    private static final String SUB = "sub";


    /**
     * 放置主要RecyclerView的容器
     */
    private ViewGroup mMainContainer;
    /**
     * 放置次级RecyclerView的容器
     */
    private ViewGroup mSubContainer;
    /**
     * 被拖动的View
     */
    private View mDragView;

    //添加按钮
    private View addView;
    private RelativeLayout rl;
    private TextView nameTv;
    private InsertAbleGridView iag;

    private View mMainShadowView;
    private View oldPositionView;//添加占位图标
    private View oldPositionViewSub;//sub中添加占位图标
    private RecyclerView mMainRecyclerView;
    private RecyclerView mSubRecyclerView;
    private EditText titleEt;
    public TextView titleTv;
    private RelativeLayout editRl;
    private ImageView topBarIv;

    private int mMainSpanCount;
    private int mSubSpanCount;
    private GestureDetectorCompat mMainGestureDetector;
    private GestureDetectorCompat mSubGestureDetector;

    private RecyclerView.OnItemTouchListener mMainItemTouchListener;
    private RecyclerView.OnItemTouchListener mSubItemTouchListener;

    private MainRecyclerViewCallBack mMainCallBack;
    private SubRecyclerViewCallBack mSubCallBack;

    private float mSubRatio;
    private int mMainActivePointerId = ACTIVE_POINTER_ID_NONE;
    private int mSubActivePointerId = ACTIVE_POINTER_ID_NONE;
    private int mShadowColor;
    private int mAnimationDuration;


    private int mSelectedStartX;
    private int mSelectedStartY;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private float mDx;
    private float mDy;
    private View mSelected;
    private int mSelectedPosition;
    private int mOpenDirPosition = 0;
    private int afterAddPosition = -1;
    /**
     * 触发滑动距离
     */
    private int mEdgeWidth;

    private boolean inMainRegion;
    private boolean inSubRegion;

    private VelocityTracker mVelocityTracker;
    private boolean isExistAdd = false;//是否存在添加按钮
    private static final int SUBCONTAINERWIDTH = 800;//文件弹出框的宽度
    private static final int SUBCONTAINERHEIGHT = 600;//文件弹出框的度
    private int SCREENWIDTH = 0;
    private int SCREENHEIGHT = 0;
    private InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    private int position = 0;//点击的桌面主位置

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private boolean isSoftKeyBoardShow = false;
    private boolean isInSubDrag = false;
    private List<String> canUninstallNameLists = new ArrayList<>();
    private boolean isHasDeletedItems = false;//sub中是否有可删除的item
    private CountTimer countTimerView;
    private static final int EDGEWIDTH = 160;//左右边缘间距
    private static final int TOPBARHEIGHT = 30;//topbar的高度
    public ClassifyView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ClassifyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ClassifyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ClassifyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    /**
     * 初始化容器
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;//屏幕宽度
        int height = dm.heightPixels;//屏幕高度
        L.d("screen width = " + width + ",height = " + height);//1920 * 675
        SCREENWIDTH = width;
        SCREENHEIGHT = height;

        preferences = context.getSharedPreferences(MyConfigs.APPPANELSP, Context.MODE_PRIVATE);
        editor = preferences.edit();
        mMainContainer = new FrameLayout(context);
        mSubContainer = new FrameLayout(context);
        mMainContainer.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClassifyView, defStyleAttr, R.style.DefaultStyle);
        mSubRatio = a.getFraction(R.styleable.ClassifyView_SubRatio, 1, 1, 0.7f);
        mMainSpanCount = a.getInt(R.styleable.ClassifyView_MainSpanCount, 3);
        mSubSpanCount = a.getInt(R.styleable.ClassifyView_SubSpanCount, 3);
        mShadowColor = a.getColor(R.styleable.ClassifyView_ShadowColor, 0x83585858);
        mAnimationDuration = a.getInt(R.styleable.ClassifyView_AnimationDuration, 200);
        mEdgeWidth = a.getDimensionPixelSize(R.styleable.ClassifyView_EdgeWidth, 15);
        a.recycle();
        //添加下拉条
        topBarIv = new ImageView(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, TOPBARHEIGHT);
        topBarIv.setPadding(855,11,0,0);//水平居中，topbar图片源尺寸是180 * 8，paddingTop = 30/2 - 8/2 = 11
        topBarIv.setImageResource(R.drawable.ic_top_bar);
        topBarIv.setLayoutParams(params);
        topBarIv.setScaleType(ImageView.ScaleType.MATRIX);
        //topBarIv.setBackgroundColor(Color.WHITE);
        mMainContainer.addView(topBarIv);

        mMainRecyclerView = getMain(context, attrs);
        mMainRecyclerView.setPadding(EDGEWIDTH,0,EDGEWIDTH,0);
        mMainRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);//去掉滑动到顶/底部时的阴影
        mSubRecyclerView = getSub(context, attrs);
        mSubRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
        mMainContainer.addView(mMainRecyclerView);

        mMainShadowView = new View(context);
        mMainShadowView.setBackgroundColor(mShadowColor);
        mMainShadowView.setVisibility(View.GONE);

        //添加占位图标
        oldPositionView = new View(context);
        oldPositionView.setBackgroundResource(R.drawable.location_bg);
        oldPositionView.setVisibility(View.GONE);
        //sub中添加占位图标
        oldPositionViewSub = new View(context);
        oldPositionViewSub.setBackgroundResource(R.drawable.location_bg);
        oldPositionViewSub.setVisibility(View.GONE);
        mMainShadowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHideSubAnim != null && mHideSubAnim.isRunning()) return;

                //判断sub中有无显示删除按钮的item,有，说明处于删除状态
                //之前RecyclerView显示正在显示的item的删除按钮，未显示的item未处理
                //出现sub中存在可删除的item，但是未显示出来，导致点击sub外时，没有先清除删除按钮就直接退出了，先按这样做
                //如果出现上面的情况，需要根据MyConfigs.SHOWDELETE判断
//                RelativeLayout relativeLayout;
//                InsertAbleGridView insertAbleGridView;
//                int num;
//                for(num = 0; num < mSubRecyclerView.getChildCount(); num++){
//                    relativeLayout = (RelativeLayout) mSubRecyclerView.getChildAt(num);
//                    insertAbleGridView = (InsertAbleGridView) relativeLayout.getChildAt(0);
//                    if(insertAbleGridView.getChildCount() == 1){//非文件夹
//                        ImageView iv = (ImageView) relativeLayout.getChildAt(2);
//                        if(iv.getVisibility() == View.VISIBLE){
//                            isHasDeletedItems = true;
//                            break;
//                        }
//                    }
//                }
//
//                if(num >= mSubRecyclerView.getChildCount()){
//                    isHasDeletedItems = false;
//                }

                boolean isInDeletedMode = preferences.getBoolean(MyConfigs.SHOWDELETE,false);

                //重置delete标签
                editor.putBoolean(MyConfigs.SHOWDELETE,false);
                editor.commit();
                List<LocationBean> subDatas = mSubCallBack.getSubData();
                LocationBean locationBean;
                int num;
                A:for(num = 0; num < subDatas.size(); num++){
                    locationBean = subDatas.get(num);
                    if(locationBean != null){
                        if(locationBean.getCanuninstalled() == 1){
                            isHasDeletedItems = true;
                            break A;
                        }
                    }
                }
                if(num >= subDatas.size()){
                    isHasDeletedItems = false;
                }

                L.d("mMainShadowView OnClick isInDeletedMode: " + isInDeletedMode + ",isHasDeletedItems: " + isHasDeletedItems);
                //如果处于删除模式 且 有可删除的items
                if(isInDeletedMode && isHasDeletedItems){
                    for(int i = 0; i < mSubRecyclerView.getChildCount(); i++){
                        relativeLayout = (RelativeLayout) mSubRecyclerView.getChildAt(i);
                        insertAbleGridView = (InsertAbleGridView) relativeLayout.getChildAt(0);
                        if(insertAbleGridView.getChildCount() == 1){//非文件夹
                            ImageView iv = (ImageView) relativeLayout.getChildAt(2);
                            iv.setVisibility(View.GONE);
                        }
                    }
                }else {
                    hideSubContainer();
                    //刷新桌面中sub的显示,实际发现mSubRecyclerView.getChildCount()数目会变少，不采用mSubRecyclerView计算
                    //mSubCallBack.removeItem(mSubRecyclerView.getChildCount() - 1);
                    mMainCallBack = (MainRecyclerViewCallBack) mMainRecyclerView.getAdapter();
                    for(int i = 0; i < mMainCallBack.total(); i++){
                        List list = mMainCallBack.explodeItem(i,  null);
                        if(list != null){
                            list.removeAll(Collections.singleton(null));//清除掉null对象
                        }
                    }
                }
            }
        });
        mMainShadowView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mMainContainer.addView(mMainShadowView);
        oldPositionView.setLayoutParams(new LayoutParams(120,120));
        mMainContainer.addView(oldPositionView);
        //mSubRecyclerView.setLayoutParams(new LayoutParams(600,600));
        //编辑框高editHeight = 50，距上边15，mSubRecyclerView与编辑框间隔5
        mSubRecyclerView.setPadding(0,80,0,0);

        int editHeight = 50;
        FrameLayout.LayoutParams titleSize =new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        FrameLayout.LayoutParams subRecyclerViewSize =new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        //由于给的背景图片四周有阴影，因此需要设置recyclerView的margin
        subRecyclerViewSize.setMargins(50,10,50,50);

        mSubContainer.addView(mSubRecyclerView,subRecyclerViewSize);
        oldPositionViewSub.setLayoutParams(new LayoutParams(120,120));
        mSubContainer.addView(oldPositionViewSub);
        //设置文件夹名称
        editRl = new RelativeLayout(context);
        RelativeLayout.LayoutParams linearParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,editHeight
        );
        linearParams.setMargins(80,45,80,0);
        editRl.setBackgroundResource(R.drawable.input_bg);
        titleEt = new EditText(context);
        RelativeLayout.LayoutParams titleEtParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        titleEt.setSingleLine(true);
        //使用反射来设置光标样式
        try {
            @SuppressLint("SoonBlockedPrivateApi")
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(titleEt, R.drawable.input_cursor_line);
        } catch (Exception e) {
            L.d("set titleEt cursor exception: " + e);
        }
        titleEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)}); //即限定最大输入字符数为12个
        titleEt.setText("");
        titleEtParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        titleEtParams.addRule(RelativeLayout.CENTER_VERTICAL);
        titleEt.setBackground(null);
        titleEt.setGravity(Gravity.CENTER_HORIZONTAL);
        titleEt.setTextSize(28);
        titleEt.setPadding(0,0,0,0);
        titleEt.setTextColor(Color.WHITE);

        ImageView clearIv = new ImageView(context);
        RelativeLayout.LayoutParams clearIvParams = new RelativeLayout.LayoutParams(
                editHeight,editHeight
        );
        clearIv.setBackgroundResource(R.drawable.input_close);
        clearIvParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        clearIvParams.addRule(RelativeLayout.CENTER_VERTICAL);
        clearIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                titleEt.setText("");
            }
        });

        //标题文本
        titleTv = new TextView(context);
        RelativeLayout.LayoutParams titleTvParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,editHeight
        );
        titleTv.setText("");
        titleTv.setGravity(Gravity.CENTER_HORIZONTAL);
        titleTv.setTextSize(28);
        titleTv.setPadding(0,50,0,0);
        titleTv.setTextColor(Color.WHITE);

        //editRl.addView(titleTv,titleTvParams);
        editRl.addView(titleEt,titleEtParams);
        titleEt.setSelection(titleEt.getText().toString().trim().length());
        editRl.addView(clearIv,clearIvParams);
        titleTv.setVisibility(View.GONE);
        editRl.setVisibility(View.GONE);
        mSubContainer.addView(editRl,linearParams);
        mSubContainer.addView(titleTv,titleSize);
//        mSubContainer.setBackgroundColor(Color.parseColor("#252C3D"));
        mSubContainer.setBackgroundResource(R.drawable.folder_app_bg);

        addViewInLayout(mMainContainer, 0, mMainContainer.getLayoutParams());
        mDragView = new View(context);
        mDragView.setVisibility(GONE);
        addViewInLayout(mDragView, -1, generateDefaultLayoutParams());
        setUpTouchListener(context);
        setTopBarIvTouchListener(context);//设置topbar下拉事件
        setMainRecyclerViewScrollListener();//设置MainRecyclerView滑动事件,以决定topbar显示与否
        //初始化CountTimer，设置倒计时为10s。
        countTimerView = new CountTimer(10000L,1000L,getMainRecyclerView(),editor);
    }

    protected
    @NonNull
    RecyclerView getMain(Context context, AttributeSet parentAttrs) {
        RecyclerView recyclerView = new RecyclerView(context);
        RecyclerView.LayoutParams params= new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //顶部距离topbar 30
        params.setMargins(0,TOPBARHEIGHT,0,0);
        recyclerView.setLayoutParams(params);
        recyclerView.setLayoutManager(new GridLayoutManager(context, mMainSpanCount));
        recyclerView.setItemAnimator(new ClassifyItemAnimator());
        return recyclerView;
    }


    protected
    @NonNull
    RecyclerView getSub(Context context, AttributeSet parentAttrs) {
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new GridLayoutManager(context, mSubSpanCount));
        recyclerView.setItemAnimator(new ClassifyItemAnimator());
        return recyclerView;
    }


    public RecyclerView getMainRecyclerView() {
        return mMainRecyclerView;
    }

    public RecyclerView getSubRecyclerView() {
        return mSubRecyclerView;
    }


    private View findChildView(RecyclerView recyclerView, MotionEvent event) {
        // first check elevated views, if none, then call RV
        final float x = event.getX();
        final float y = event.getY();
        return recyclerView.findChildViewUnder(x, y);
    }

    /**
     * 设置adapter
     *
     * @param mainAdapter
     * @param subAdapter
     */
    public void setAdapter(BaseMainAdapter mainAdapter, BaseSubAdapter subAdapter) {
        mMainRecyclerView.setAdapter(mainAdapter);
        mMainRecyclerView.addOnItemTouchListener(mMainItemTouchListener);
        mMainCallBack = mainAdapter;
        mSubRecyclerView.setAdapter(subAdapter);
        //RecycleView设置行间距
        //mSubRecyclerView.addItemDecoration(new SubRecyclerViewDecoration(0,0));
        mSubRecyclerView.addOnItemTouchListener(mSubItemTouchListener);
        mSubCallBack = subAdapter;
        mMainRecyclerView.setOnDragListener(new MainDragListener());
        mSubRecyclerView.setOnDragListener(new SubDragListener());
    }

    /**
     * @param baseSimpleAdapter
     */
    public void setAdapter(BaseSimpleAdapter baseSimpleAdapter) {
        setAdapter(baseSimpleAdapter.getMainAdapter(), baseSimpleAdapter.getSubAdapter());
    }

    /*
     * 重新设置sub的显示位置，解决重命名文件时，键盘弹出将SubContainer顶上去，收回时，SubContainer没有下来居中显示
     */
    public void setSoftKeyBoardStatus(boolean isShow){
        isSoftKeyBoardShow = isShow;
        if(isShow){
            isInSubDrag = false;
        }
        Log.d("hideKeyBoard","isSoftKeyBoardShow = " + isSoftKeyBoardShow + ",isInSubDrag = " + isInSubDrag);
        if(mSubContainer != null && !isInSubDrag){
            FrameLayout.LayoutParams params =  (FrameLayout.LayoutParams)mSubContainer.getLayoutParams();
            if(params != null){
                params.gravity = isShow ? Gravity.CENTER_HORIZONTAL : Gravity.CENTER;
                mSubContainer.setLayoutParams(params);
            }
        }
    }

    public RecyclerView.LayoutManager getMainLayoutManager() {
        return mMainRecyclerView.getLayoutManager();
    }

    public RecyclerView.LayoutManager getSubLayoutManager() {
        return mSubRecyclerView.getLayoutManager();
    }

    /**
     * 初始化 触摸事件监听
     *
     * @param context
     */
    private void setUpTouchListener(final Context context) {
        mMainGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                View pressedView = findChildView(mMainRecyclerView, e);
                if (pressedView == null) {
                    //点击的非RecyclerView的空白区域
                    Log.d("MyAppInfoAdapter","pressedView is null");
                    editor.putBoolean(MyConfigs.MAINSHOWDELETE,false);
                    editor.commit();
                    mMainRecyclerView.getAdapter().notifyDataSetChanged();
                    return false;
                }
                position = mMainRecyclerView.getChildAdapterPosition(pressedView);
                List list = mMainCallBack.explodeItem(position, pressedView);
                editor.putBoolean(MyConfigs.SHOWDELETE,false);
                editor.putBoolean(MyConfigs.MAINSHOWDELETE,false);
                editor.commit();
                if(isInDeleteMode && isCountTimer){
                    Log.d("CountTimer","onSingleTapConfirmed cancel count");
                    countTimerView.cancel();
                    isCountTimer = false;
                }
                if (list == null || list.size() < 2) {
                    Log.d("MyAppInfoAdapter","click app position: " + position);
                    mMainCallBack.onItemClick(position, pressedView);
                    return true;
                } else {
                    //隐藏非文件夹的删除按钮
                    for(int i = 0; i < mMainRecyclerView.getChildCount(); i++){
                        relativeLayout = (RelativeLayout) mMainRecyclerView.getChildAt(i);
                        insertAbleGridView = (InsertAbleGridView) relativeLayout.getChildAt(0);
                        if(insertAbleGridView.getChildCount() == 1){//非文件夹
                            ImageView iv = (ImageView) relativeLayout.getChildAt(2);
                            iv.setVisibility(View.GONE);
                        }
                    }

                    //如果没有添加按钮，则新增一个添加按钮,防止添加按钮不是在最末尾，先清除，再新增
                    isExistAdd = false;
                    A:for(int i = 0; i < list.size(); i++){
                        if(list.get(i) == null){
                            list.remove(i);
                            break A;
                        }
                    }
                    list.add(null);

                    mSubCallBack.initData(position, list);
                    titleTv.setVisibility(View.VISIBLE);
                    editRl.setVisibility(View.GONE);
                    RecyclerView.ViewHolder target = mMainRecyclerView.findViewHolderForAdapterPosition(position);
                    RelativeLayout relativeLayout = (RelativeLayout)target.itemView;
                    TextView nameTv = (TextView) relativeLayout.getChildAt(3);
                    titleTv.setText(nameTv.getText().toString());
                    if (ViewCompat.isAttachedToWindow(mSubContainer)) {
                        //取消之前进行的动画
                        if (mShowSubAnim != null && mShowSubAnim.isRunning()) {
                            mShowSubAnim.cancel();
                        }
                        //确保次级窗口在屏幕外
                        resetSubContainerPlace();
                        showSubContainer(position);
                    } else {
                        removeView(mSubContainer);//如果有已存在的mSubContainer，先移除
                        final int height = (int) (getHeight() * mSubRatio);
                        LayoutParams params = new LayoutParams(SUBCONTAINERWIDTH, SUBCONTAINERHEIGHT);//设置高度，屏幕尺寸是1920*720
                        params.gravity = Gravity.CENTER;
                        mSubContainer.setLayoutParams(params);
                        addView(mSubContainer);
                        ViewCompat.postOnAnimation(mSubContainer, new Runnable() {
                            @Override
                            public void run() {
                                mSubContainer.setTranslationY(height);
                                showSubContainer(position);
                            }
                        });
                    }
                    return true;
                }

            }

            @Override
            public void onLongPress(MotionEvent e) {
                L.d("onLongPress");
                View pressedView = findChildView(mMainRecyclerView, e);
                if (pressedView == null) return;
                //L.d("Main recycler view on long press: x: %1$s + y: %2$s", e.getX(), e.getY());
                position = mMainRecyclerView.getChildAdapterPosition(pressedView);

                int pointerId = MotionEventCompat.getPointerId(e, 0);
                if (pointerId == mMainActivePointerId) {
                    if (mMainCallBack.canDragOnLongPress(position, pressedView)) {
                        mSelectedPosition = position;
                        mSelectedStartX = pressedView.getLeft();
                        mSelectedStartY = pressedView.getTop();
                        mDx = mDy = 0f;
                        int index = MotionEventCompat.findPointerIndex(e, mMainActivePointerId);
                        mInitialTouchX = MotionEventCompat.getX(e, index);
                        mInitialTouchY = MotionEventCompat.getY(e, index);
                        //L.d("handle event on long press:X: %1$s , Y: %2$s ", mInitialTouchX, mInitialTouchY);

                        inMainRegion = true;
                        mSelected = pressedView;
                        pressedView.startDrag(ClipData.newPlainText(DESCRIPTION, MAIN),
                                new ClassifyDragShadowBuilder(pressedView), mSelected, 0);
                    }
                }
            }

            @Override
            public void onShowPress(MotionEvent e) {
                super.onShowPress(e);
                L.d("onShowPress");
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                /*
                *  热区：mMainRecyclerView左右两边160的区域
                *  mMainRecyclerView滑动至顶部后，空白区域下滑超过200px，跳转至卡片页
                 */
                if(mMainRecyclerView!= null && !mMainRecyclerView.canScrollVertically(-1)){//mMainRecyclerView已经到达顶部
                    //topBarIv.setVisibility(View.VISIBLE);
                    if(isNeedJumpCard(e1,e2)){
                        EventBus.getDefault().post(new JumpToCardEvent());
                    }
                }else {
                    //topBarIv.setVisibility(View.GONE);
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //L.d("onFling");
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
        mMainItemTouchListener = new RecyclerView.OnItemTouchListener() {

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                mMainGestureDetector.onTouchEvent(e);
                int action = MotionEventCompat.getActionMasked(e);

                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        //L.d("onInterceptTouchEvent ACTION_MOVE");
                        break;
                    case MotionEvent.ACTION_DOWN:
                        //L.d("onInterceptTouchEvent ACTION_DOWN");
                        mMainActivePointerId = MotionEventCompat.getPointerId(e, 0);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mMainActivePointerId = ACTIVE_POINTER_ID_NONE;
                        inMergeState = false;
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                mMainGestureDetector.onTouchEvent(e);
                int action = MotionEventCompat.getActionMasked(e);
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        L.d("onTouchEvent ACTION_DOWN");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        L.d("onTouchEvent ACTION_DOWN");
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mMainActivePointerId = ACTIVE_POINTER_ID_NONE;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        int pointerIndex = MotionEventCompat.getActionIndex(e);
                        int pointerId = MotionEventCompat.getPointerId(e, pointerIndex);
                        if (pointerId == mSubActivePointerId) {
                            int newPointerId = pointerIndex == 0 ? 1 : 0;
                            mMainActivePointerId = MotionEventCompat.getPointerId(e, newPointerId);
                        }
                        break;
                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        };
        mSubGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                L.d("Sub recycler view onDown: x: %1$s + y: %2$s", e.getX(), e.getY());
                if(editRl.getVisibility() == View.VISIBLE){//文件夹编辑模式下
                    isInSubDrag = true;
                    //隐藏键盘
                    if(isSoftKeyBoardShow){
                        imm.hideSoftInputFromWindow(titleEt.getWindowToken(), 0); //强制隐藏键盘
                    }
                    Log.d("keyboardtest","onDown");
                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                View pressedView = findChildView(mSubRecyclerView, e);
                if (pressedView == null) return false;
                int position = mSubRecyclerView.getChildAdapterPosition(pressedView);
                mSubCallBack.onItemClick(position, pressedView);
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View pressedView = findChildView(mSubRecyclerView, e);
                if (pressedView == null) return;
                L.d("Sub recycler view on long press: x: %1$s + y: %2$s", e.getX(), e.getY());
                int position = mSubRecyclerView.getChildAdapterPosition(pressedView);
                int pointerId = MotionEventCompat.getPointerId(e, 0);
                if (pointerId == mSubActivePointerId) {
                    if (mSubCallBack.canDragOnLongPress(position, pressedView)) {
                        mSelectedPosition = position;
                        mSelectedStartX = pressedView.getLeft();
                        mSelectedStartY = pressedView.getTop();
                        mDx = mDy = 0f;
                        int index = MotionEventCompat.findPointerIndex(e, mSubActivePointerId);
                        mInitialTouchX = MotionEventCompat.getX(e, index);
                        mInitialTouchY = MotionEventCompat.getY(e, index);
                        inSubRegion = true;
                        mSelected = pressedView;
                        pressedView.startDrag(ClipData.newPlainText(
                                DESCRIPTION, SUB),
                                getShadowBuilder(pressedView), mSelected, 0);
                    }
                }
            }

            //onFling():手指在触摸屏上迅速移动，并松开的动作(滑动的比onScroll快)
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(editRl.getVisibility() == View.VISIBLE){//文件夹编辑模式下
                    Log.d("keyboardtest","onFling");
                    isInSubDrag = false;
                    if(mSubContainer != null){
                        FrameLayout.LayoutParams params =  (FrameLayout.LayoutParams)mSubContainer.getLayoutParams();
                        params.gravity = Gravity.CENTER;
                        mSubContainer.setLayoutParams(params);
                    }
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }

            //onScroll():手指在屏幕上滑动
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if(editRl.getVisibility() == View.VISIBLE){//文件夹编辑模式下
                    Log.d("keyboardtest","onScroll");
                    isInSubDrag = false;
                    if(mSubContainer != null){
                        FrameLayout.LayoutParams params =  (FrameLayout.LayoutParams)mSubContainer.getLayoutParams();
                        params.gravity = Gravity.CENTER;
                        mSubContainer.setLayoutParams(params);
                    }
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            //onSingleTapUp() 手指离开view那一瞬间执行
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if(editRl.getVisibility() == View.VISIBLE){//文件夹编辑模式下
                    Log.d("keyboardtest","onSingleTapUp");
                    isInSubDrag = false;
                    if(mSubContainer != null){
                        FrameLayout.LayoutParams params =  (FrameLayout.LayoutParams)mSubContainer.getLayoutParams();
                        params.gravity = Gravity.CENTER;
                        mSubContainer.setLayoutParams(params);
                    }
                }
                return super.onSingleTapUp(e);
            }
        });
        mSubItemTouchListener = new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                mSubGestureDetector.onTouchEvent(e);
                int action = MotionEventCompat.getActionMasked(e);
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mSubActivePointerId = MotionEventCompat.getPointerId(e, 0);
                        mInitialTouchX = e.getX();
                        mInitialTouchY = e.getY();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mSubActivePointerId = ACTIVE_POINTER_ID_NONE;
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                mSubGestureDetector.onTouchEvent(e);
                int action = MotionEventCompat.getActionMasked(e);
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mSubActivePointerId = ACTIVE_POINTER_ID_NONE;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        int pointerIndex = MotionEventCompat.getActionIndex(e);
                        int pointerId = MotionEventCompat.getPointerId(e, pointerIndex);
                        if (pointerId == mSubActivePointerId) {
                            int newPointerId = pointerIndex == 0 ? 1 : 0;
                            mSubActivePointerId = MotionEventCompat.getPointerId(e, newPointerId);
                        }
                        break;

                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        };
    }

    private void setTopBarIvTouchListener(final Context context){
        GestureDetectorCompat gestureDetectorCompat = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener(){
            //此事件是移动视口的过程中
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            //此事件是用户在动作结束时抬起手指(这onFling就是所谓的一次动作的原因)
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(isNeedJumpMain(e1,e2)){
                    EventBus.getDefault().post(new JumpToCardEvent());
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
        topBarIv.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetectorCompat.onTouchEvent(motionEvent);
                return false;
            }
        });
        topBarIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new JumpToCardEvent());
            }
        });
    }

    private void setMainRecyclerViewScrollListener(){
        mMainRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!recyclerView.canScrollVertically(-1)){//滑动到顶部
                    topBarIv.setVisibility(View.VISIBLE);
                }else {
                    topBarIv.setVisibility(View.GONE);
                }
            }
        });
    }

    /*
     *  热区：mMainRecyclerView左右两边宽度160的区域
     */
    private boolean isNeedJumpCard(MotionEvent e1, MotionEvent e2) {
        float moveY = e2.getY() - e1.getY();
        if(e1.getX() > EDGEWIDTH && e1.getX() < (SCREENWIDTH - EDGEWIDTH)){
            return false;
        }
        if(e2.getX() > EDGEWIDTH && e2.getX() < (SCREENWIDTH - EDGEWIDTH)){
            return false;
        }
        if(moveY < 0){
            return false;
        }
        //向下移动350时
        if(moveY >= 350){
            Log.d("onscroll", "isNeedJumpCard true");
            return true;
        }else {
            return false;
        }
    }

    /*
     *  热区：topBarIv的区域  1920 * 30
     */
    private boolean isNeedJumpMain(MotionEvent e1, MotionEvent e2) {
        float moveY = e2.getY() - e1.getY();
        if(e1.getY() < 30  && moveY >= 5){
            return true;
        }else {
            return false;
        }
    }

    private void resetSubContainerPlace() {
        int height = mSubContainer.getHeight();
        mSubContainer.setTranslationY(height);
    }

    private AnimatorSet mShowSubAnim;
    private AnimatorSet mHideSubAnim;

    /**
     * 显示次级窗口
     */
    public void showSubContainer(int subParentIndex) {
        if (mShowSubAnim != null && mShowSubAnim.isRunning()) return;
        mSubContainer.setVisibility(VISIBLE);
        mShowSubAnim = new AnimatorSet();
        ObjectAnimator subAnim = ObjectAnimator.ofFloat(mSubContainer, "translationY", 0);
        ObjectAnimator shadowAnim = ObjectAnimator.ofFloat(mMainShadowView, "alpha", 0f, 1f);
        mShowSubAnim.setDuration(mAnimationDuration);
        mShowSubAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        editor.putInt(MyConfigs.PARENTINDEX,subParentIndex);
        editor.commit();
        mShowSubAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //如果是重命名，显示编辑框和键盘
                if(editRl.getVisibility() == View.VISIBLE){
                    mOpenDirPosition = mSelectedPosition;
                    RecyclerView.ViewHolder target = mMainRecyclerView.findViewHolderForAdapterPosition(mOpenDirPosition);
                    RelativeLayout relativeLayout = (RelativeLayout)target.itemView;
                    TextView nameTv = (TextView) relativeLayout.getChildAt(3);
                    titleEt.setText(nameTv.getText().toString());
                    titleEt.setFocusable(true);
                    titleEt.setFocusableInTouchMode(true);
                    titleEt.requestFocus();
                    titleEt.setSelection(titleEt.getText().toString().length());
                    imm.showSoftInput(titleEt,0);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                //文件弹出窗外部的区域，点击此区域，文件弹出窗消失
                mMainShadowView.setVisibility(VISIBLE);
            }
        });
        mShowSubAnim.play(subAnim).with(shadowAnim);
        mShowSubAnim.start();
    }

    /**
     * 隐藏次级窗口
     */
    public void hideSubContainer() {
        if (mHideSubAnim != null && mHideSubAnim.isRunning()) return;
        int height = mSubContainer.getHeight();
        mHideSubAnim = new AnimatorSet();
        ObjectAnimator subAnim = ObjectAnimator.ofFloat(mSubContainer, "translationY", height);
        ObjectAnimator shadowAnim = ObjectAnimator.ofFloat(mMainShadowView, "alpha", 1f, 0f);
        mHideSubAnim.setDuration(mAnimationDuration);
        mHideSubAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        mHideSubAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                mMainShadowView.setVisibility(GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                editor.putInt(MyConfigs.PARENTINDEX,-1);
                editor.commit();
                mMainShadowView.setVisibility(GONE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                if(editRl.getVisibility() == View.VISIBLE){
                    imm.hideSoftInputFromWindow(titleEt.getWindowToken(), 0); //强制隐藏键盘
                    //这个时候mSelectedPosition是sub中选择拖拽的view位置，不是这个文件在桌面上的位置
                    afterAddPosition = preferences.getInt(MyConfigs.PARENTINDEX,-1);
                    if(afterAddPosition >= 0){
                        mOpenDirPosition = afterAddPosition;
                    }
//                    RecyclerView.ViewHolder target = mMainRecyclerView.findViewHolderForAdapterPosition(mOpenDirPosition);
//                    if(target == null){
//                        L.d("target is null");
//                    }else {
//                        if(!TextUtils.isEmpty(titleEt.getText().toString().trim())){
//                            RelativeLayout relativeLayout = (RelativeLayout)target.itemView;
//                            TextView nameTv = (TextView) relativeLayout.getChildAt(3);
//                            nameTv.setText(titleEt.getText().toString().trim());
//                        }
//                    }
                    if(!TextUtils.isEmpty(titleEt.getText().toString().trim())){
                        //通知adapter更新title
                        EventBus.getDefault().post(new ChangeTitleEvent(mOpenDirPosition,titleEt.getText().toString().trim()));
                    }
                }
                //隐藏弹出的文件夹框
                mSubContainer.setVisibility(GONE);
                //隐藏文件夹弹窗后，清除掉之前添加的添加按钮
                //mSubCallBack.removeItem(mSubRecyclerView.getChildCount() - 1);
                mMainShadowView.setVisibility(VISIBLE);
            }
        });
        mHideSubAnim.playTogether(subAnim, shadowAnim);
        mHideSubAnim.start();

    }

    public boolean isSubContainerShow(){
        if(mMainShadowView != null){
            return mMainShadowView.getVisibility() == View.VISIBLE ? true : false;
        }else {
            return false;
        }
    }

    private boolean mergeSuccess = false;

    long startTime = 0;
    float lastX = 0f;
    float lastY = 0f;
    Dialog dialog = null;
    boolean isInDeleteMode = true;
    RecyclerView recyclerView;
    RelativeLayout relativeLayout;
    InsertAbleGridView insertAbleGridView;
    boolean isDialogShow = false;//文件夹编辑框是否显示
    boolean isCountTimer = false;
    class MainDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            if (mSelected == null) return false;
            int action = event.getAction();
            int width = mSelected.getWidth();
            int height = mSelected.getHeight();
            float x = event.getX();
            float y = event.getY();
            float centerX = x - width / 2;
            float centerY = y - height / 2;

            if(isCountTimer){
                Log.d("CountTimer","MainDragListener cancel count");
                countTimerView.cancel();
                isCountTimer = false;
            }
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    if (inMainRegion) {
                        //rl.setBackgroundColor(Color.GREEN);
                        oldPositionView.setX(mSelected.getX() + mSelected.getWidth()/2 - 60);//图片大小是120*120
                        oldPositionView.setY(mSelected.getY() + mSelected.getHeight()/2 - 60 + TOPBARHEIGHT);
                        oldPositionView.setVisibility(View.VISIBLE);

                        L.d("ACTION_DRAG_STARTED");
                        obtainVelocityTracker();
                        restoreDragView();
                        mDragView.setBackgroundDrawable(getDragDrawable(mSelected));
                        mDragView.setVisibility(VISIBLE);
                        mMainCallBack.setDragPosition(mSelectedPosition);
                        mDragView.setX(mInitialTouchX - width / 2);
                        mDragView.setY(mInitialTouchY - height / 2);
                        mDragView.bringToFront();
                        mElevationHelper.floatView(mMainRecyclerView, mDragView);

                        recyclerView = (RecyclerView) v;
                        final List list = mMainCallBack.explodeItem(mSelectedPosition, mDragView);
                        if (list == null || list.size() < 2) {//非文件夹
                            for(int i = 0; i < recyclerView.getChildCount(); i++){
                                relativeLayout = (RelativeLayout) recyclerView.getChildAt(i);
                                insertAbleGridView = (InsertAbleGridView) relativeLayout.getChildAt(0);
                                if(insertAbleGridView.getChildCount() == 1){//非文件夹
                                    ImageView iv = (ImageView) relativeLayout.getChildAt(2);
                                    iv.setVisibility((int)iv.getTag() == 1 ? View.VISIBLE : View.GONE);
                                }
                            }
                        } else {//文件夹
                            for(int i = 0; i < recyclerView.getChildCount(); i++){
                                relativeLayout = (RelativeLayout) recyclerView.getChildAt(i);
                                insertAbleGridView = (InsertAbleGridView) relativeLayout.getChildAt(0);
                                if(insertAbleGridView.getChildCount() == 1){//非文件夹
                                    ImageView iv = (ImageView) relativeLayout.getChildAt(2);
                                    iv.setVisibility(View.GONE);
                                }
                            }
                            dialog = new Dialog(getContext(),R.style.mydialog);
                            dialog.setContentView(R.layout.file_edit_item);
                            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                            //L.d("getX " + e.getX(0) + "," + e.getX() + "," + e.getRawX() + ",");

                            LinearLayout rootLl = (LinearLayout) dialog.getWindow().findViewById(R.id.root_ll);
                            if((mSelectedPosition + 1) % mMainSpanCount == 0){
                                params.x = (int) (mInitialTouchX - width / 2 - 300 / 2) - 50;//300是params.width
                                rootLl.setBackgroundResource(R.drawable.edit_folder_bg_left);
                            }else{
                                params.x = (int) (mInitialTouchX + 20) + 20;
                                rootLl.setBackgroundResource(R.drawable.edit_folder_bg_right);
                            }
                            params.y = (int) (mInitialTouchY);
                            params.width = 300;
                            params.height = 200;
                            dialog.getWindow().setGravity(Gravity.LEFT | Gravity.TOP);
                            dialog.getWindow().setAttributes(params);
                            TextView renameTv = (TextView) dialog.getWindow().findViewById(R.id.rename_tv);
                            TextView editTv = (TextView) dialog.getWindow().findViewById(R.id.edit_tv);
                            renameTv.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    editor.putBoolean(MyConfigs.MAINSHOWDELETE,false);
                                    editor.putBoolean(MyConfigs.SHOWDELETE,false);
                                    editor.commit();
                                    //如果没有添加按钮，则新增一个添加按钮
                                    A:for(int i = 0; i < list.size(); i++){
                                        if(list.get(i) == null){
                                            list.remove(i);
                                            break A;
                                        }
                                    }
                                    list.add(null);
                                    mSubCallBack.initData(mSelectedPosition, list);

                                    removeView(mSubContainer);//如果有已存在的mSubContainer，先移除
                                    final int height = (int) (getHeight() * mSubRatio);
                                    LayoutParams params = new LayoutParams(SUBCONTAINERWIDTH, SUBCONTAINERHEIGHT);//设置高度，屏幕尺寸是1920*720
                                    params.gravity = Gravity.CENTER_HORIZONTAL;//防止键盘将输入框顶到外面去了，指定为CENTER_HORIZONTAL
                                    mSubContainer.setLayoutParams(params);
                                    addView(mSubContainer);
                                    editRl.setVisibility(View.VISIBLE);
                                    titleTv.setVisibility(View.GONE);
                                    titleEt.setText("");

                                    ViewCompat.postOnAnimation(mSubContainer, new Runnable() {
                                        @Override
                                        public void run() {
                                            mSubContainer.setTranslationY(height);
                                            showSubContainer(mSelectedPosition);
                                        }
                                    });
                                }
                            });
                            editTv.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    for(int i = 0; i < mMainRecyclerView.getChildCount(); i++){
                                        relativeLayout = (RelativeLayout) recyclerView.getChildAt(i);
                                        insertAbleGridView = (InsertAbleGridView) relativeLayout.getChildAt(0);
                                        if(insertAbleGridView.getChildCount() == 1){//非文件夹
                                            ImageView iv = (ImageView) relativeLayout.getChildAt(2);
                                            iv.setVisibility((int)iv.getTag() == 1 ? View.VISIBLE : View.GONE);
                                        }
                                    }
                                    Log.d("CountTimer","editTv start count");
                                    isCountTimer = true;
                                    countTimerView.start();
                                }
                            });
                            dialog.show();
                        }

                        lastX = x;
                        lastY = y;
                    }
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    //L.d("ACTION_DRAG_LOCATION x = " + x + ",y = " + y);
//                    L.d("ACTION_DRAG_LOCATION lastX = " + lastX + ",lastY = " + lastY);
                    if(Math.abs(x - lastX) >= 5 || Math.abs(y - lastY) > 5){
                        if(dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        recyclerView = (RecyclerView) v;
                        for(int i = 0; i < recyclerView.getChildCount(); i++){
                            relativeLayout = (RelativeLayout) recyclerView.getChildAt(i);
                            insertAbleGridView = (InsertAbleGridView) relativeLayout.getChildAt(0);
                            if(insertAbleGridView.getChildCount() == 1){//非文件夹
                                ImageView iv = (ImageView) relativeLayout.getChildAt(2);
                                iv.setVisibility(View.GONE);
                            }
                        }
                    }
                    if (mVelocityTracker != null){
                        mVelocityTracker.addMovement(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                                MotionEvent.ACTION_MOVE, x, y, 0));
                    }
                    mDragView.setX(centerX);
                    mDragView.setY(centerY);
                    mDx = x - mInitialTouchX;
                    mDy = y - mInitialTouchY;
                    moveIfNecessary(mSelected);
                    removeCallbacks(mScrollRunnable);
                    mScrollRunnable.run();
                    invalidate();
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    L.d("ACTION_DRAG_ENDED");
                    //因为sub中长按后，最后也会走到这里，所以需要判断是否是在sub中操作的
                    boolean isSubShow = isSubContainerShow();
                    if(dialog == null){
                        isDialogShow = false;
                    }else {
                        isDialogShow = dialog.isShowing();
                    }
                    Log.d("CountTimer","isSubShow: " + isSubShow + ",isDialogShow: " + isDialogShow);
                    //sub未展开，且dialog未显示
                    if(!isSubShow && !isDialogShow){
                        if(isInDeleteMode){
                            Log.d("CountTimer","ACTION_DRAG_ENDED start count");
                            isCountTimer = true;
                            countTimerView.start();
                        }else {
                            isCountTimer = false;
                        }
                    }else {
                        isCountTimer = false;
                    }
//                    Log.d("MyAppFragment","main drag ACTION_DRAG_ENDED ReStoreDataEvent");
//                    EventBus.getDefault().post(new ReStoreDataEvent());//通知存储数据
                    if (mergeSuccess) {
                        mergeSuccess = false;
                        break;
                    }
                    if (inMainRegion) {
                        doRecoverAnimation();
                    }
                    releaseVelocityTracker();
                    if(inMainRegion){
                        //更新桌面的所有图标，防止有的应用图标显示了文件夹边框
                        Log.d("onNotifyAll","onNotifyAll..");
                        mMainCallBack.onNotifyAll();
                    }
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    L.d("ACTION_DRAG_EXITED");
                    break;
                case DragEvent.ACTION_DROP:
                    L.d("x = " + x + ",lastX = " + lastX);
                    if(Math.abs(x - lastX) >= 5 || Math.abs(y - lastY) > 5){
                        isInDeleteMode = false;
                    }else {
                        isInDeleteMode = true;
                    }
                    if(dialog == null){
                        isDialogShow = false;
                    }else {
                        isDialogShow = dialog.isShowing();
                    }
                    Log.d("CountTimer","isInDeleteMode: " + isInDeleteMode + ",isDialogShow: " + isDialogShow);
                    //存储在SP中，在MyAppInfoAdapter中刷新时再判断是否显示删除按钮
                    SharedPreferences sp = getContext().getSharedPreferences(MyConfigs.APPPANELSP,Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(MyConfigs.MAINSHOWDELETE,isInDeleteMode && !isDialogShow ? true : false);
                    editor.commit();
                    L.d("ACTION_DROP");
                    if (inMergeState) {
                        inMergeState = false;
                        if (mLastMergeStartPosition == -1) break;
                        ChangeInfo changeInfo = mMainCallBack.onPrepareMerge(mMainRecyclerView, mSelectedPosition, mLastMergeStartPosition);
                        RecyclerView.ViewHolder target = mMainRecyclerView.findViewHolderForAdapterPosition(mLastMergeStartPosition);
                        if (target == null || changeInfo == null || target.itemView == mSelected) {
                            mergeSuccess = false;
                            break;
                        }
                        float scaleX = ((float) changeInfo.itemWidth) / ((float) (mSelected.getWidth() - changeInfo.paddingLeft - changeInfo.paddingRight - 2 * changeInfo.outlinePadding));
                        float scaleY = ((float) changeInfo.itemHeight) / ((float) (mSelected.getHeight() - changeInfo.paddingTop - changeInfo.paddingBottom - 2 * changeInfo.outlinePadding));
                        int targetX = (int) (target.itemView.getLeft() + changeInfo.left + changeInfo.paddingLeft - (changeInfo.paddingLeft + changeInfo.outlinePadding) * scaleX);
                        int targetY = (int) (target.itemView.getTop() + changeInfo.top + changeInfo.paddingTop - (changeInfo.paddingTop + changeInfo.outlinePadding) * scaleY);
                        mDragView.setPivotX(0);
                        mDragView.setPivotY(0);
                        L.d("targetX:%1$s,targetY:%2$s,scaleX:%3$s,scaleY:%4$s", targetX, targetY, scaleX, scaleY);
                        //不做图标缩放
                        //mDragView.animate().x(targetX).y(targetY).scaleX(scaleX).scaleY(scaleY).setListener(mMergeAnimListener).setDuration(mAnimationDuration).start();
                        mDragView.animate().x(targetX).y(targetY).scaleX(1f).scaleY(1f).setListener(mMergeAnimListener).setDuration(mAnimationDuration).start();
                        mergeSuccess = true;
                    }else {
                        mMainCallBack.onMergeCancel(mMainRecyclerView, mSelectedPosition, mLastMergeStartPosition);
                        Log.d("MyAppFragment","main drag ACTION_DROP ReStoreDataEvent");
                        EventBus.getDefault().post(new ReStoreDataEvent());//通知存储数据
                    }
                    break;
            }
            return true;
        }
    }

    public void startCountTimer(){
        countTimerView.start();
        isCountTimer = true;
    }

    public void cancelCountTimer(){
        countTimerView.cancel();
        isCountTimer = false;
    }

    private int mLastMergeStartPositionAtAnimationStart = 0;
    private AnimatorListenerAdapter mMergeAnimListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationStart(Animator animation) {
            L.d("onAnimationStart mLastMergeStartPosition = " + mLastMergeStartPosition);
            mLastMergeStartPositionAtAnimationStart = mLastMergeStartPosition;
            mMainCallBack.onStartMergeAnimation(mMainRecyclerView, mSelectedPosition, mLastMergeStartPosition, mAnimationDuration);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            L.d("onAnimationEnd mLastMergeStartPosition = " + mLastMergeStartPositionAtAnimationStart);
            //mMainCallBack.onMerged(mMainRecyclerView, mSelectedPosition, mLastMergeStartPosition);
            mMainCallBack.onMerged(mMainRecyclerView, mSelectedPosition, mLastMergeStartPositionAtAnimationStart);
            restoreToInitial();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            L.d("onAnimationCancel mLastMergeStartPosition = " + mLastMergeStartPosition);
            mMainCallBack.onMerged(mMainRecyclerView, mSelectedPosition, mLastMergeStartPosition);
            restoreToInitial();
        }
    };

    protected Drawable getDragDrawable(View view) {
        return new DragDrawable(view);
    }

    class SubDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            if (mSelected == null) return false;
            int action = event.getAction();
            int width = mSelected.getWidth();
            int height = mSelected.getHeight();
            float x = event.getX();
            float y = event.getY();
            float centerX = x - width / 2;
//            float centerX = x + width / 2;
            float centerY = y - height / 2;
            float marginTop = getHeight() - mSubContainer.getHeight();
            float marginLeft = mSubContainer.getWidth();
            //获取添加按钮
            rl = (RelativeLayout)mSubRecyclerView.getChildAt(mSubRecyclerView.getChildCount() - 1);
            nameTv = (TextView) rl.getChildAt(3);
            if(nameTv.getText().equals("添加")){
                addView = mSubRecyclerView.getChildAt(mSubRecyclerView.getChildCount() - 1);
            }

            //添加按钮不可拖动
//            if(mSelectedPosition == mSubRecyclerView.getChildCount() - 1){
//                L.d("Long press addView");
//                return true;
//            }
            //如果是添加按钮，不往下执行
            if(mSelected == addView){
                return true;
            }
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    if (inSubRegion) {
                        L.d("Sub ACTION_DRAG_STARTED");
                        L.d("x： " + mDragView.getX() + ",y: " + mDragView.getY());

                        oldPositionViewSub.setX(mSelected.getX() + mSelected.getWidth()/2 - 60 + 50);//图片大小是120*120
                        oldPositionViewSub.setY(mSelected.getY() + mSelected.getHeight()/2 - 60 + 10);
                        oldPositionViewSub.setVisibility(View.VISIBLE);

                        obtainVelocityTracker();
                        restoreDragView();
                        mDragView.setBackgroundDrawable(getDragDrawable(mSelected));
                        mDragView.setVisibility(VISIBLE);
                        mSubCallBack.setDragPosition(mSelectedPosition);
                        mDragView.setX(mInitialTouchX - width / 2 + (SCREENWIDTH - SUBCONTAINERWIDTH) / 2);
                        if(isSoftKeyBoardShow){
                            mDragView.setY(mInitialTouchY - height / 2 - (SCREENHEIGHT - SUBCONTAINERHEIGHT) / 2);
                        }
                        else if(mSubContainer.getTop() < marginTop / 2){
                            mDragView.setY(mInitialTouchY - height / 2 );
                        }else {
                            mDragView.setY(mInitialTouchY - height / 2 - marginTop / 2);
                        }
//                        mDragView.setX(mSelected.getX() + 20);
//                        mDragView.setY(mSelected.getY() + 20);
                        mDragView.bringToFront();
                        mElevationHelper.floatView(mSubRecyclerView, mDragView);
                        if(null != addView) addView.setVisibility(View.GONE);

                        //判断sub中是否有可删除的
                        isHasDeletedItems = false;
                        List<LocationBean> subDatas = mSubCallBack.getSubData();
                        LocationBean locationBean;
                        if(canUninstallNameLists != null) canUninstallNameLists.clear();
                        for(int i = 0; i < subDatas.size(); i++){
                            locationBean = subDatas.get(i);
                            if(locationBean != null){
                                if(locationBean.getCanuninstalled() == 1){
                                    canUninstallNameLists.add(locationBean.getName());
                                }
                            }
                        }

                        if(canUninstallNameLists != null && canUninstallNameLists.size() != 0){
                            isHasDeletedItems = true;
                        }else {
                            isHasDeletedItems = false;
                        }

                        L.d("Sub ACTION_DRAG_STARTED isHasDeletedItems: " + isHasDeletedItems);
                        if(isHasDeletedItems){
                            //getChildCount得到的是当前正在显示的item的个数,所以这里只能显示正在显示的item的删除按钮
                            for(int i = 0; i < mSubRecyclerView.getChildCount(); i++){
                                relativeLayout = (RelativeLayout) mSubRecyclerView.getChildAt(i);
                                insertAbleGridView = (InsertAbleGridView) relativeLayout.getChildAt(0);
                                ImageView iv = (ImageView) relativeLayout.getChildAt(2);
                                TextView nameTv = (TextView) relativeLayout.getChildAt(3);

                                String text = nameTv.getText().toString().trim();
                                L.d("text is: " + text);
                                if(canUninstallNameLists != null && canUninstallNameLists.contains(text)){
                                    iv.setVisibility(View.VISIBLE);
                                }else{
                                    iv.setVisibility(View.GONE);
                                }
                            }
                        }

                        lastX = x;
                        lastY = y;
                    }
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    if(null != addView) addView.setVisibility(View.GONE);
                    //L.d("Sub ACTION_DRAG_LOCATION");
                    //L.d("x： " + mDragView.getX() + ",y: " + mDragView.getY());
                    //sub中拖动时，隐藏所有的删除按钮
                    if(Math.abs(x - lastX) >= 5 || Math.abs(y - lastY) > 5){
                        for(int i = 0; i < mSubRecyclerView.getChildCount(); i++){
                            relativeLayout = (RelativeLayout) mSubRecyclerView.getChildAt(i);
                            insertAbleGridView = (InsertAbleGridView) relativeLayout.getChildAt(0);
                            ImageView iv = (ImageView) relativeLayout.getChildAt(2);
                            iv.setVisibility(View.GONE);
                        }
                    }
                    mVelocityTracker.addMovement(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                            MotionEvent.ACTION_MOVE, x, y, 0));
//                    mDragView.setX(centerX);
//                    mDragView.setY(centerY + marginTop);
                    mDragView.setX(centerX + + (SCREENWIDTH - SUBCONTAINERWIDTH) / 2);

                    if(isSoftKeyBoardShow){
                        mDragView.setY(centerY - (SCREENHEIGHT - SUBCONTAINERHEIGHT) / 2);
                    }
                    else if(mSubContainer.getTop() < marginTop / 2){
                        mDragView.setY(centerY);
                    }else {
                        mDragView.setY(centerY - marginTop / 2);
                    }
                    mDx = x - mInitialTouchX;
                    mDy = y - mInitialTouchY;
                    moveIfNecessary(mSelected);
                    removeCallbacks(mScrollRunnable);
                    mScrollRunnable.run();
                    invalidate();
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    L.d("sub ACTION_DRAG_ENDED");
                    Log.d("MyAppFragment","sub drag ACTION_DRAG_ENDED ReStoreDataEvent");
                    EventBus.getDefault().post(new ReStoreDataEvent());//通知存储数据
                    if (inSubRegion) {
                        doRecoverAnimation();
                    }
                    releaseVelocityTracker();
                    break;
                case DragEvent.ACTION_DRAG_EXITED://拖拽到main
                    if (mSubCallBack.canDragOut(mSelectedPosition)) {
                        inSubRegion = false;
                        inMainRegion = true;
                        hideSubContainer();
                        //重新刷新sub及main，,实际发现mSubRecyclerView.getChildCount()数目会变少，不采用mSubRecyclerView计算
                        //mSubCallBack.removeItem(mSubRecyclerView.getChildCount() - 1);
                        mMainCallBack = (MainRecyclerViewCallBack) mMainRecyclerView.getAdapter();

                        int newIndex = preferences.getInt(MyConfigs.PARENTINDEX,-1);
                        Log.d("dragtest","position = " + position + ",newIndex = " + newIndex);
                        if(newIndex >= 0){
                            position = newIndex;
                        }
                        List list = mMainCallBack.explodeItem(position, null);
                        A:for(int i = list.size() - 1; i >= 0; i--){
                            if(list.get(i) == null){
                                list.remove(i);
                                break A;
                            }
                        }
                        mSubCallBack.initData(position,list);
                        Log.d("dragtest","selectedPosition = " + mSelectedPosition +",list.size() = " + list.size());
                        if(mSelectedPosition >= list.size()){
                            mSelectedPosition = list.size() - 1;
                        }

                        mSelectedPosition = mMainCallBack.onLeaveSubRegion(mSelectedPosition, new SubAdapterReference(mSubCallBack));
                        L.d("mSelectedPosition = " + mSelectedPosition);
                        mMainCallBack.setDragPosition(mSelectedPosition);
                        mSubCallBack.setDragPosition(-1);
                    }
                    break;
                case DragEvent.ACTION_DROP:
                    L.d("sub DragEvent.ACTION_DROP");
                    if(Math.abs(x - lastX) >= 5 || Math.abs(y - lastY) > 5){
                        isInDeleteMode = false;
                    }else {
                        isInDeleteMode = true;
                    }
                    //存储在SP中，在MyAppInfoAdapter中刷新时再判断是否显示删除按钮
                    SharedPreferences sp = getContext().getSharedPreferences(MyConfigs.APPPANELSP,Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    L.d("isHasDeletedItems: " + isHasDeletedItems + ",isInDeleteMode: " + isInDeleteMode);
                    editor.putBoolean(MyConfigs.SHOWDELETE,isInDeleteMode && isHasDeletedItems ? true : false);
                    editor.commit();
                    break;
            }
            return true;
        }
    }

    /**
     * 做恢复到之前状态的动画
     */
    private void doRecoverAnimation() {
        L.d("doRecoverAnimation");
        Animator recoverAnimator = null;
        if (inSubRegion) {
            RecyclerView.ViewHolder holder = mSubRecyclerView.findViewHolderForAdapterPosition(mSelectedPosition);
            if (holder == null) {
                PropertyValuesHolder yOffset = PropertyValuesHolder.ofFloat("y", getHeight() + mSelected.getHeight());
                recoverAnimator = ObjectAnimator.ofPropertyValuesHolder(mDragView, yOffset);
            } else {
                PropertyValuesHolder xOffset = PropertyValuesHolder.ofFloat("x", mSubContainer.getLeft() + holder.itemView.getLeft());
                PropertyValuesHolder yOffset = PropertyValuesHolder.ofFloat("y", mSubContainer.getTop() + holder.itemView.getTop());
                recoverAnimator = ObjectAnimator.ofPropertyValuesHolder(mDragView, xOffset, yOffset);
            }
        }

        if (inMainRegion) {
            RecyclerView.ViewHolder holder = mMainRecyclerView.findViewHolderForAdapterPosition(mSelectedPosition);
            if (holder == null) {
                PropertyValuesHolder yOffset = PropertyValuesHolder.ofFloat("y", getHeight() + mSelected.getHeight());
                recoverAnimator = ObjectAnimator.ofPropertyValuesHolder(mDragView, yOffset);
            } else {
                PropertyValuesHolder xOffset = PropertyValuesHolder.ofFloat("x", holder.itemView.getLeft());
                PropertyValuesHolder yOffset = PropertyValuesHolder.ofFloat("y", holder.itemView.getTop());
                recoverAnimator = ObjectAnimator.ofPropertyValuesHolder(mDragView, xOffset, yOffset);
            }
        }
        if (recoverAnimator == null) return;
        recoverAnimator.setDuration(mAnimationDuration);
        recoverAnimator.setInterpolator(sDragScrollInterpolator);
        recoverAnimator.addListener(mRecoverAnimatorListener);
        recoverAnimator.start();
    }

    private AnimatorListenerAdapter mRecoverAnimatorListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            restoreToInitial();
            //显示添加按钮
            if(addView != null) addView.setVisibility(View.VISIBLE);
            //隐藏占位图片
            if(oldPositionView != null) oldPositionView.setVisibility(View.GONE);
            if(oldPositionViewSub != null) oldPositionViewSub.setVisibility(View.GONE);

            //重置sub的位置
            if(editRl.getVisibility() == View.VISIBLE){//文件夹编辑模式下
                isInSubDrag = false;
                if(mSubContainer != null){
                    FrameLayout.LayoutParams params =  (FrameLayout.LayoutParams)mSubContainer.getLayoutParams();
                    if(params != null){
                        params.gravity = Gravity.CENTER;
                        mSubContainer.setLayoutParams(params);
                    }
                }
            }
        }
    };

    private void restoreToInitial() {

        if (inSubRegion) {
            restoreDragView();
            mSubCallBack.setDragPosition(-1);
            inSubRegion = false;
        }
        if (inMainRegion) {
            restoreDragView();
            mMainCallBack.setDragPosition(-1);
            inMainRegion = false;
        }
        //隐藏占位图片
        if(oldPositionView != null) oldPositionView.setVisibility(View.GONE);
        if(oldPositionViewSub != null) oldPositionViewSub.setVisibility(View.GONE);
    }

    private void restoreDragView() {
        mDragView.setVisibility(GONE);
        mDragView.setScaleX(1f);
        mDragView.setScaleY(1f);
        mDragView.setTranslationX(0f);
        mDragView.setTranslationY(0f);
    }

    /**
     * If user drags the view to the edge, trigger a scroll if necessary.
     */
    private boolean scrollIfNecessary() {
        RecyclerView recyclerView = null;
        if (inMainRegion) {
            recyclerView = mMainRecyclerView;
        }
        if (inSubRegion) {
            recyclerView = mSubRecyclerView;
        }
        if (recyclerView == null) return false;
        final long now = System.currentTimeMillis();
        final long scrollDuration = mDragScrollStartTimeInMs
                == Long.MIN_VALUE ? 0 : now - mDragScrollStartTimeInMs;
        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();

        int scrollX = 0;
        int scrollY = 0;
        if (lm.canScrollHorizontally()) {
            int curX = (int) (mInitialTouchX + mDx - mSelected.getWidth() / 2);
            final int leftDiff = curX - mEdgeWidth - recyclerView.getPaddingLeft();
            if (mDx < 0 && leftDiff < 0) {
                scrollX = leftDiff;
            } else if (mDx > 0) {
                final int rightDiff =
                        curX + mSelected.getWidth() + mEdgeWidth - (recyclerView.getWidth() - recyclerView.getPaddingRight());
                if (rightDiff > 0) {
                    scrollX = rightDiff;
                }
            }
        }
        if (lm.canScrollVertically()) {
            int curY = (int) (mInitialTouchY + mDy - mSelected.getHeight() / 2);
            final int topDiff = curY - mEdgeWidth - recyclerView.getPaddingTop();
            if (mDy < 0 && topDiff < 0) {
                scrollY = topDiff;
            } else if (mDy > 0) {
                final int bottomDiff = curY + mSelected.getHeight() + mEdgeWidth -
                        (recyclerView.getHeight() - recyclerView.getPaddingBottom());
                if (bottomDiff > 0) {
                    scrollY = bottomDiff;
                }
            }
        }
        if (scrollX != 0) {
            scrollX = interpolateOutOfBoundsScroll(recyclerView,
                    mSelected.getWidth(), scrollX,
                    recyclerView.getWidth(), scrollDuration);
        }
        if (scrollY != 0) {
            scrollY = interpolateOutOfBoundsScroll(recyclerView,
                    mSelected.getHeight(), scrollY,
                    recyclerView.getHeight(), scrollDuration);
        }
        if (scrollX != 0 || scrollY != 0) {
            if (mDragScrollStartTimeInMs == Long.MIN_VALUE) {
                mDragScrollStartTimeInMs = now;
            }
            recyclerView.scrollBy(scrollX, scrollY);
            return true;
        }
        mDragScrollStartTimeInMs = Long.MIN_VALUE;
        return false;
    }


    private int interpolateOutOfBoundsScroll(RecyclerView recyclerView,
                                             int viewSize, int viewSizeOutOfBounds,
                                             int totalSize, long msSinceStartScroll) {
        final int maxScroll = getMaxDragScroll(recyclerView);
        final int absOutOfBounds = Math.abs(viewSizeOutOfBounds);
        final int direction = (int) Math.signum(viewSizeOutOfBounds);
        // might be negative if other direction
        float outOfBoundsRatio = Math.min(1f, 1f * absOutOfBounds / viewSize);
        final int cappedScroll = (int) (direction * maxScroll *
                sDragViewScrollCapInterpolator.getInterpolation(outOfBoundsRatio));
        final float timeRatio;
        if (msSinceStartScroll > DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS) {
            timeRatio = 1f;
        } else {
            timeRatio = (float) msSinceStartScroll / DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS;
        }
        final int value = (int) (cappedScroll * sDragScrollInterpolator
                .getInterpolation(timeRatio));
        if (value == 0) {
            return viewSizeOutOfBounds > 0 ? 1 : -1;
        }
        return value;
    }

    private static final long DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS = 2000;
    private static final Interpolator sDragViewScrollCapInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };
    private static final Interpolator sDragScrollInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            return t * t * t * t * t;
        }
    };
    private int mCachedMaxScrollSpeed = -1;

    private int getMaxDragScroll(RecyclerView recyclerView) {
        if (mCachedMaxScrollSpeed == -1) {
            mCachedMaxScrollSpeed = recyclerView.getResources().getDimensionPixelSize(
                    R.dimen.item_touch_helper_max_drag_scroll_per_frame);
        }
        return mCachedMaxScrollSpeed;
    }

    /**
     * When user started to drag scroll. Reset when we don't scroll
     */
    private long mDragScrollStartTimeInMs;


    /**
     * When user drags a view to the edge, we start scrolling the LayoutManager as long as View
     * is partially out of bounds.
     */
    private final Runnable mScrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (mSelected != null && scrollIfNecessary()) {
                if (mSelected != null) { //it might be lost during scrolling
                    moveIfNecessary(mSelected);
                }
                removeCallbacks(mScrollRunnable);
                ViewCompat.postOnAnimation(ClassifyView.this, this);
            }
        }
    };
    private boolean inMergeState = false;
    private int mLastMergeStartPosition = -1;

    private void moveIfNecessary(View view) {
        final int x = (int) (mSelectedStartX + mDx);
        final int y = (int) (mSelectedStartY + mDy);
        //如果移动范围在自身范围内
        if (Math.abs(y - view.getTop()) < view.getHeight() * 0.5f
                && Math.abs(x - view.getLeft())
                < view.getWidth() * 0.5f) {
            return;
        }
        List<View> swapTargets = findSwapTargets(view);
        if (swapTargets.size() == 0) return;
        View target = chooseTarget(view, swapTargets, x, y);
        if (target == null) return;
        if (inSubRegion) {//次级目录下 没有merge形式
            int targetPosition = mSubRecyclerView.getChildAdapterPosition(target);
            int state = mSubCallBack.getCurrentState(mSelected, target, x, y, mVelocityTracker, mSelectedPosition,
                    targetPosition);
            if (state == STATE_MOVE) {
                if (mSubCallBack.onMove(mSelectedPosition, targetPosition)) {
                    mSelectedPosition = targetPosition;
                    RecyclerView.ViewHolder viewHolder = mSubRecyclerView.findViewHolderForAdapterPosition(mSelectedPosition);
                    if (viewHolder != null) mSelected = viewHolder.itemView;
                    mSubCallBack.setDragPosition(targetPosition);
                    //mSubCallBack.moved(mSelectedPosition, targetPosition);
                }
            }
        }
        if (inMainRegion) {//在主层级下 有merge状况 以及次级目录拖动到主层级的状况
            int targetPosition = mMainRecyclerView.getChildAdapterPosition(target);
            if(targetPosition != mLastMergeStartPosition) inMergeState = false;
            int state = mMainCallBack.getCurrentState(mSelected, target, x, y, mVelocityTracker, mSelectedPosition,
                    targetPosition);
            boolean mergeState = state == STATE_MERGE;
            if (mergeState ^ inMergeState) {
                if (mergeState) {
                    if (mMainCallBack.onMergeStart(mMainRecyclerView, mSelectedPosition, targetPosition)) {
                        inMergeState = true;
                        mLastMergeStartPosition = targetPosition;
                        //A. by heqiang 2022-5-13
                        Log.d("heqq","L940 mLastMergeStartPosition = " + mLastMergeStartPosition);
                    }
                } else {
                    if (mLastMergeStartPosition != -1 && inMergeState) {
                        mMainCallBack.onMergeCancel(mMainRecyclerView, mSelectedPosition, mLastMergeStartPosition);
                        mLastMergeStartPosition = -1;
                        inMergeState = false;
                    }
                }
            }
            if (state == STATE_MOVE) {
                if (inMergeState && mLastMergeStartPosition != -1) {
                    //makeSure trigger mergeCancel
                    mMainCallBack.onMergeCancel(mMainRecyclerView, mSelectedPosition, mLastMergeStartPosition);
                    mLastMergeStartPosition = -1;
                    //A. by heqiang 2022-5-13
                    Log.d("heqq","L956 mLastMergeStartPosition = -1");
                    inMergeState = false;
                }
                if (mMainCallBack.onMove(mSelectedPosition, targetPosition)) {
                    mSelectedPosition = targetPosition;
                    RecyclerView.ViewHolder viewHolder = mMainRecyclerView.findViewHolderForAdapterPosition(mSelectedPosition);
                    if (viewHolder != null) mSelected = viewHolder.itemView;
                    mMainCallBack.setDragPosition(targetPosition);
                    //mMainCallBack.moved(mSelectedPosition, targetPosition);
                }
            }
        }
    }

    private List<View> mSwapTargets;

    /**
     * 找到当前移动View 有覆盖的view
     *
     * @return
     */
    private List<View> findSwapTargets(View view) {
        if (mSwapTargets == null) {
            mSwapTargets = new ArrayList<>();
        } else {
            mSwapTargets.clear();
        }
        int left = Math.round(mSelectedStartX + mDx);
        int top = Math.round(mSelectedStartY + mDy);
        int right = left + view.getWidth();
        int bottom = top + view.getHeight();
        RecyclerView.LayoutManager lm = null;
        RecyclerView recyclerView = null;
        if (inMainRegion) {
            lm = getMainLayoutManager();
            recyclerView = mMainRecyclerView;
        }
        if (inSubRegion) {
            lm = getSubLayoutManager();
            recyclerView = mSubRecyclerView;
        }
        if (lm == null || recyclerView == null) return mSwapTargets;
        int childCount = lm.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = lm.getChildAt(i);
            if (child == view) {
                //本身
                continue;
            }
            if (child.getBottom() < top || child.getTop() > bottom || child.getLeft() > right || child.getRight() < left) {
                continue;//没有覆盖到
            }
            int targetPosition = recyclerView.getChildAdapterPosition(child);
            //检验目标位置是否能移动
            if (inMainRegion) {
                if (!mMainCallBack.canDropOVer(mSelectedPosition, targetPosition)) continue;
            }
            if (inSubRegion) {
                if (!mSubCallBack.canDropOver(mSelectedPosition, targetPosition)) continue;
            }
            mSwapTargets.add(child);
        }
        return mSwapTargets;
    }

    private void obtainVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
        }
        mVelocityTracker = VelocityTracker.obtain();
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * 从候选项中找到最有优势的目标
     *
     * @param selected
     * @param swapTargets
     * @param curX
     * @param curY
     * @return
     */
    protected View chooseTarget(View selected, List<View> swapTargets, int curX, int curY) {
        int right = curX + selected.getWidth();
        int bottom = curY + selected.getHeight();
        View winner = null;
        int winnerScore = Integer.MAX_VALUE;
        final int dx = curX - selected.getLeft();
        final int dy = curY - selected.getTop();
        final int targetsSize = swapTargets.size();
        for (int i = 0; i < targetsSize; i++) {
            final View target = swapTargets.get(i);
            final int score = Math.abs(target.getLeft() - curX) + Math.abs(target.getTop() - curY)
                    + Math.abs(target.getBottom() - bottom) + Math.abs(target.getRight() - right);
            if (score < winnerScore) {
                winnerScore = score;
                winner = target;
            }
//            if (dx > 0) {
//                int diff = target.getRight() - right;
//                if (diff < 0) {
//                    final int score = Math.abs(diff);
//                    if (score > winnerScore) {
//                        winnerScore = score;
//                        winner = target;
//                    }
//                }
//            }
//            if (dx < 0) {
//                int diff = target.getLeft() - curX;
//                if (diff > 0) {
//                    final int score = Math.abs(diff);
//                    if (score > winnerScore) {
//                        winnerScore = score;
//                        winner = target;
//                    }
//                }
//            }
//            if (dy < 0) {
//                int diff = target.getTop() - curY;
//                if (diff > 0) {
//                    final int score = Math.abs(diff);
//                    if (score > winnerScore) {
//                        winnerScore = score;
//                        winner = target;
//                    }
//                }
//            }
//
//            if (dy > 0) {
//                int diff = target.getBottom() - bottom;
//                if (diff < 0) {
//                    final int score = Math.abs(diff);
//                    if (score > winnerScore) {
//                        winnerScore = score;
//                        winner = target;
//                    }
//                }
//            }
        }
        return winner;
    }

    /**
     * 获取DragShadowBuilder 用于渲染 被拖动的view
     * 默认使用 {@link ClassifyDragShadowBuilder} 实现 自定义时请重写该方法
     *
     * @param view 被拖动item 的 root view
     * @return
     */
    protected DragShadowBuilder getShadowBuilder(View view) {
        return new ClassifyDragShadowBuilder(view);
    }

    private ElevationHelper mElevationHelper = new ElevationHelper();

    static class ElevationHelper {


        public void floatView(RecyclerView recyclerView, View dragView) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                float maxElevation = findMaxElevation(recyclerView) + 1f;
                dragView.setElevation(maxElevation);
            } else {
                Drawable drawable = dragView.getBackground();
                if (drawable instanceof DragDrawable) {
                    DragDrawable dragDrawable = (DragDrawable) drawable;
                    dragDrawable.showShadow();
                    dragView.setLayerType(View.LAYER_TYPE_SOFTWARE, dragDrawable.getPaint());
                }
            }
        }

        private float findMaxElevation(RecyclerView recyclerView) {
            final int childCount = recyclerView.getChildCount();
            float max = 0;
            for (int i = 0; i < childCount; i++) {
                final View child = recyclerView.getChildAt(i);

                final float elevation = ViewCompat.getElevation(child);
                if (elevation > max) {
                    max = elevation;
                }
            }
            return max;
        }
    }


}
