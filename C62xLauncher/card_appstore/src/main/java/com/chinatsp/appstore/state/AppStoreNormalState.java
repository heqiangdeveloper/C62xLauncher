package com.chinatsp.appstore.state;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.appstore.R;

public class AppStoreNormalState implements AppStoreState {
    public void updateSmallCardState(View view) {
        view.findViewById(R.id.layoutAppStoreNormalSmall).setVisibility(View.VISIBLE);

        ImageView ivAppStoreLogo = view.findViewById(R.id.ivAppStoreLogo);
        TextView tvAppStoreTip = view.findViewById(R.id.tvAppStoreTip);
        ImageView ivAppStoreRefresh = view.findViewById(R.id.ivAppStoreRefresh);

        ivAppStoreLogo.setVisibility(View.GONE);
        tvAppStoreTip.setVisibility(View.GONE);
        ivAppStoreRefresh.setVisibility(View.GONE);
    }

    public void updateBigCardState(View view) {
        View tvCardIQuTingLoginTipBig = view.findViewById(R.id.tvAppStoreTipBig);
        ImageView ivAppStoreRefreshBig = view.findViewById(R.id.ivAppStoreRefreshBig);
        tvCardIQuTingLoginTipBig.setVisibility(View.GONE);

        RecyclerView rcvCardIQuTingSongList = view.findViewById(R.id.rcvAppStoreAppsList);
        rcvCardIQuTingSongList.setVisibility(View.VISIBLE);
        if(ivAppStoreRefreshBig != null) ivAppStoreRefreshBig.setVisibility(View.GONE);
    }

}
