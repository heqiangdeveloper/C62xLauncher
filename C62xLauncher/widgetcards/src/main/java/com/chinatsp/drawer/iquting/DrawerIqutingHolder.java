package com.chinatsp.drawer.iquting;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.drawer.DrawerEntity;
import com.chinatsp.drawer.bean.RecentAppsBean;
import com.chinatsp.widgetcards.R;

import java.util.List;

import launcher.base.recyclerview.BaseViewHolder;
import launcher.base.recyclerview.SimpleRcvDecoration;

public class DrawerIqutingHolder extends BaseViewHolder<DrawerEntity> {
    private RecyclerView rcvDrawerIqutingLogin;
    private SongsAdapter mSongsAdapter;
    private View tvDrawerIqutingLogin;
    private Context mContext;

    private IqutingController mController;
    public DrawerIqutingHolder(@NonNull View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        rcvDrawerIqutingLogin = itemView.findViewById(R.id.rcvDrawerIqutingLogin);
        initSongsRcv();
        tvDrawerIqutingLogin = itemView.findViewById(R.id.tvDrawerIqutingLogin);
        mController = new IqutingController(this);
        mController.requestSongs();
    }

    /**
     * todo: 显示数据.
     * @param songItemList
     */
    void showSongs(List<SongItem> songItemList) {
        tvDrawerIqutingLogin.setVisibility(View.GONE);
        rcvDrawerIqutingLogin.setVisibility(View.VISIBLE);
        mSongsAdapter.setData(songItemList);
    }

    void showLoginTip() {
        tvDrawerIqutingLogin.setVisibility(View.VISIBLE);
        rcvDrawerIqutingLogin.setVisibility(View.GONE);
    }

    void showNetworkError() {

    }

    void showDataError() {

    }

    private void initSongsRcv() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvDrawerIqutingLogin.setLayoutManager(layoutManager);
        SimpleRcvDecoration divider = new SimpleRcvDecoration(23, layoutManager);
        rcvDrawerIqutingLogin.addItemDecoration(divider);
        mSongsAdapter = new SongsAdapter(mContext);
        rcvDrawerIqutingLogin.setAdapter(mSongsAdapter);
    }
}
