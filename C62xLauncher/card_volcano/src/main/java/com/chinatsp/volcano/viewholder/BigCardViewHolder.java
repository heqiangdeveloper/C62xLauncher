package com.chinatsp.volcano.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.volcano.R;
import com.chinatsp.volcano.VolcanoCardView;
import com.chinatsp.volcano.api.response.VideoListData;
import com.chinatsp.volcano.repository.VolcanoRepository;
import com.chinatsp.volcano.videos.VolcanoVideoAdapter;

import launcher.base.utils.EasyLog;
import launcher.base.utils.selector.OnViewSelected;
import launcher.base.utils.selector.StatefulViewHolder;
import launcher.base.utils.selector.ViewStateSelector;

public class BigCardViewHolder extends VolcanoViewHolder{
    private final String TAG = "BigCardViewHolder";
    private ImageView ivCardVolcanoLogin;
    private TextView tvCardVolcanoLogin;
    private RecyclerView rcvCardVolcanoVideoList;
    private VolcanoVideoAdapter mVolcanoVideoAdapter;
    private ViewStateSelector mTypeViewSelector;
    private ImageView ivCardVolcanoLogoXigua;
    private ImageView ivCardVolcanoLogoDouyin;
    private ImageView ivCardVolcanoLogoToutiao;
    private View viewLoading;

    private boolean mInitialed;

    private VolcanoCardView mCardView;
    public BigCardViewHolder(View rootView, VolcanoCardView cardView) {
        super(rootView);
        ivCardVolcanoLogin = rootView.findViewById(R.id.ivCardVolcanoLogin);
        tvCardVolcanoLogin = rootView.findViewById(R.id.tvCardVolcanoLogin);
        rcvCardVolcanoVideoList = rootView.findViewById(R.id.rcvCardVolcanoVideoList);
        ivCardVolcanoLogoXigua = rootView.findViewById(R.id.ivCardVolcanoLogoXigua);
        ivCardVolcanoLogoDouyin = rootView.findViewById(R.id.ivCardVolcanoLogoDouyin);
        ivCardVolcanoLogoToutiao = rootView.findViewById(R.id.ivCardVolcanoLogoToutiao);
        viewLoading = rootView.findViewById(R.id.viewLoading);
        mCardView = cardView;
        initBigCardView(rootView);
        initTypeSelector();
    }

    private void initTypeSelector() {
        mTypeViewSelector = ViewStateSelector.create(new OnViewSelected() {
            @Override
            public void onViewSelected(StatefulViewHolder statefulViewHolder) {
                EasyLog.i(TAG, "onViewSelected : "+statefulViewHolder.getTag());
                requestData(statefulViewHolder.getTag());
            }
        },
                StatefulViewHolder.create(ivCardVolcanoLogoToutiao, VolcanoRepository.SOURCE_TOUTIAO),
                StatefulViewHolder.create(ivCardVolcanoLogoDouyin, VolcanoRepository.SOURCE_DOUYIN),
                StatefulViewHolder.create(ivCardVolcanoLogoXigua, VolcanoRepository.SOURCE_XIGUA)
                );
    }
    public void requestData(String source) {
        if (mCardView != null) {
            mCardView.switchSource(source);
        }
    }

    @Override
    public void showNormal() {
        ivCardVolcanoLogin.setVisibility(View.GONE);
        tvCardVolcanoLogin.setVisibility(View.GONE);
        rcvCardVolcanoVideoList.setVisibility(View.VISIBLE);
    }

    @Override
    public void showDisconnect() {

    }

    @Override
    public void showLogin() {

    }

    @Override
    public void updateList(VideoListData videoListData) {
        EasyLog.d(TAG, "updateList "+videoListData.getList().size());
        mVolcanoVideoAdapter.setData(videoListData.getList());
        hideLoadingView();
    }

    @Override
    public void init() {
        if (mInitialed) {
            return;
        }
        mTypeViewSelector.selectFirst();
        mInitialed = true;
    }

    @Override
    public void onChangeSource(String source) {

    }

    @Override
    public void showLoadingView() {
        EasyLog.i(TAG, "showLoadingView");
        rcvCardVolcanoVideoList.setVisibility(View.GONE);
        viewLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingView() {
        EasyLog.i(TAG, "hideLoadingView");
        rcvCardVolcanoVideoList.setVisibility(View.VISIBLE);
        viewLoading.setVisibility(View.GONE);
    }

    private void initBigCardView(View largeCardView) {
        rcvCardVolcanoVideoList = largeCardView.findViewById(R.id.rcvCardVolcanoVideoList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(largeCardView.getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvCardVolcanoVideoList.setLayoutManager(layoutManager);
        mVolcanoVideoAdapter = new VolcanoVideoAdapter(largeCardView.getContext());
        rcvCardVolcanoVideoList.setAdapter(mVolcanoVideoAdapter);
    }

}
