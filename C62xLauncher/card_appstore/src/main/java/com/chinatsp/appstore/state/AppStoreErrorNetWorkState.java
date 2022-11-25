package com.chinatsp.appstore.state;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinatsp.appstore.R;

public class AppStoreErrorNetWorkState implements AppStoreState {
    @Override
    public void updateSmallCardState(View view) {
        view.findViewById(R.id.layoutAppStoreNormalSmall).setVisibility(View.GONE);

        TextView tvAppStoreTip = view.findViewById(R.id.tvAppStoreTip);
        ImageView ivAppStoreRefresh = view.findViewById(R.id.ivAppStoreRefresh);
        tvAppStoreTip.setText(R.string.appstore_wifi_disconnect);
        tvAppStoreTip.setVisibility(View.VISIBLE);
        ivAppStoreRefresh.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateBigCardState(View view) {
        TextView tvAppStoreTipBig = view.findViewById(R.id.tvAppStoreTipBig);
        ImageView ivAppStoreRefreshBig = view.findViewById(R.id.ivAppStoreRefreshBig);
        if(tvAppStoreTipBig != null){
            tvAppStoreTipBig.setVisibility(View.VISIBLE);
            tvAppStoreTipBig.setText(R.string.appstore_wifi_disconnect);
        }

        if(view.findViewById(R.id.rcvAppStoreAppsList) != null){
            view.findViewById(R.id.rcvAppStoreAppsList).setVisibility(View.GONE);
        }
        if(ivAppStoreRefreshBig != null) ivAppStoreRefreshBig.setVisibility(View.VISIBLE);
    }
}
