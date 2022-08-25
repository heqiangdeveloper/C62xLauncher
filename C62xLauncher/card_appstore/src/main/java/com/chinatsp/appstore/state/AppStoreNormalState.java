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

        ImageView ivCardIQuTingLogo = view.findViewById(R.id.ivAppStoreLogo);
        TextView tvCardIQuTingTip = view.findViewById(R.id.tvAppStoreTip);
        ImageView ivCardIQuTingButton = view.findViewById(R.id.ivAppStoreButton);

        ivCardIQuTingLogo.setVisibility(View.GONE);
        tvCardIQuTingTip.setVisibility(View.GONE);
        ivCardIQuTingButton.setVisibility(View.GONE);


    }

    public void updateBigCardState(View view) {
        View tvCardIQuTingLoginTipBig = view.findViewById(R.id.tvAppStoreTipBig);
        tvCardIQuTingLoginTipBig.setVisibility(View.GONE);

        RecyclerView rcvCardIQuTingSongList = view.findViewById(R.id.rcvAppStoreAppsList);
        rcvCardIQuTingSongList.setVisibility(View.VISIBLE);
    }

}
