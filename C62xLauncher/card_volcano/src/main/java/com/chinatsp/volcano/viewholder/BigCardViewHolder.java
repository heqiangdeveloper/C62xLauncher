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
import com.chinatsp.volcano.videos.VolcanoVideo;
import com.chinatsp.volcano.videos.VolcanoVideoAdapter;

import java.util.List;

import launcher.base.utils.EasyLog;
import launcher.base.utils.selector.OnViewSelected;
import launcher.base.utils.selector.StatefulViewHolder;
import launcher.base.utils.selector.ViewStateSelector;

public class BigCardViewHolder extends VolcanoViewHolder{
    private static final int ITEM_COUNT_MAX = 10;
    private final String TAG = "BigCardViewHolder";
    private ImageView ivCardVolcanoLogin;
    private TextView tvCardVolcanoLogin;
    private RecyclerView rcvCardVolcanoVideoList;
    private VolcanoVideoAdapter mVolcanoVideoAdapter;
    private ViewStateSelector mTypeViewSelector;
    private ImageView ivCardVolcanoLogoXigua;
    private ImageView ivCardVolcanoLogoDouyin;
    private ImageView ivCardVolcanoLogoToutiao;
    private TextView tvCardVolcanoNetworkErr;
    private ImageView ivCardVolcanoNetworkErr;
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
        tvCardVolcanoNetworkErr = rootView.findViewById(R.id.tvCardVolcanoNetworkErr);
        ivCardVolcanoNetworkErr = rootView.findViewById(R.id.ivCardVolcanoNetworkErr);
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
    public void updateList(VideoListData videoListData) {
        EasyLog.d(TAG, "updateList "+videoListData);
        List<VolcanoVideo> list = videoListData.getList();
        if (list != null) {
            EasyLog.d(TAG, "updateList size: "+list.size());

        }
        List<VolcanoVideo> list2 = list;
        if (list != null && !list.isEmpty()) {
            if (list.size() > ITEM_COUNT_MAX) {
                list2 = list.subList(0, ITEM_COUNT_MAX);
            }
        }
        mVolcanoVideoAdapter.setData(list2);
        hideLoadingView();
    }

    @Override
    public void init() {
        if (mInitialed) {
            return;
        }
        mTypeViewSelector.setCurrent(0);
        mInitialed = true;
    }

    @Override
    public void onChangeSource(String source) {

    }

    @Override
    public void showLoadingView() {
        rcvCardVolcanoVideoList.setVisibility(View.INVISIBLE);
        viewLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingView() {
        rcvCardVolcanoVideoList.setVisibility(View.VISIBLE);
        viewLoading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showNetworkError() {
        rcvCardVolcanoVideoList.setVisibility(View.INVISIBLE);
        setSourceTabsVisible(false);
        viewLoading.setVisibility(View.INVISIBLE);
        ivCardVolcanoNetworkErr.setImageResource(R.drawable.card_icon_wifi_disconnect);
        ivCardVolcanoNetworkErr.setVisibility(View.VISIBLE);
        tvCardVolcanoNetworkErr.setText(R.string.card_network_err);
        tvCardVolcanoNetworkErr.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNetworkError() {
        setSourceTabsVisible(true);
        ivCardVolcanoNetworkErr.setVisibility(View.INVISIBLE);
        tvCardVolcanoNetworkErr.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showDataError() {
        rcvCardVolcanoVideoList.setVisibility(View.INVISIBLE);
        setSourceTabsVisible(false);
        viewLoading.setVisibility(View.INVISIBLE);
        ivCardVolcanoNetworkErr.setImageResource(R.drawable.card_icon_date_error);
        ivCardVolcanoNetworkErr.setVisibility(View.VISIBLE);
        tvCardVolcanoNetworkErr.setText(R.string.card_data_err);
        tvCardVolcanoNetworkErr.setVisibility(View.VISIBLE);
    }

    private void setSourceTabsVisible(boolean show) {
        int visible = show ? View.VISIBLE : View.INVISIBLE;
        ivCardVolcanoLogoToutiao.setVisibility(visible);
        ivCardVolcanoLogoDouyin.setVisibility(visible);
        ivCardVolcanoLogoXigua.setVisibility(visible);
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
