package com.chinatsp.appstore.state;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinatsp.appstore.R;

public class AppStoreDataErrorState implements AppStoreState {
    @Override
    public void updateSmallCardState(View view) {
        view.findViewById(R.id.layoutAppStoreNormalSmall).setVisibility(View.GONE);

        TextView tvCardIQuTingTip = view.findViewById(R.id.tvAppStoreTip);
        tvCardIQuTingTip.setText(R.string.appstore_get_data_error);
    }

    @Override
    public void updateBigCardState(View view) {
        TextView tvAppStoreTipBig = view.findViewById(R.id.tvAppStoreTipBig);
        ImageView ivAppStoreRefreshBig = view.findViewById(R.id.ivAppStoreRefreshBig);
        if(tvAppStoreTipBig != null){
            tvAppStoreTipBig.setVisibility(View.VISIBLE);
            tvAppStoreTipBig.setText(R.string.appstore_get_data_error);
        }

        if(view.findViewById(R.id.rcvAppStoreAppsList) != null){
            view.findViewById(R.id.rcvAppStoreAppsList).setVisibility(View.GONE);
        }
        if(ivAppStoreRefreshBig != null) ivAppStoreRefreshBig.setVisibility(View.VISIBLE);
    }
}
