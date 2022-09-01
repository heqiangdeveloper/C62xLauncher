package com.chinatsp.appstore.state;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinatsp.appstore.R;

public class AppStoreErrorNetWorkState implements AppStoreState {
    @Override
    public void updateSmallCardState(View view) {
        view.findViewById(R.id.layoutAppStoreNormalSmall).setVisibility(View.GONE);

        ImageView ivCardIQuTingLogo = view.findViewById(R.id.ivAppStoreLogo);
        TextView tvCardIQuTingTip = view.findViewById(R.id.tvAppStoreTip);
        ImageView ivCardIQuTingButton = view.findViewById(R.id.ivAppStoreButton);

        ivCardIQuTingLogo.setImageResource(R.drawable.card_icon_wifi_disconnect);
        tvCardIQuTingTip.setText(R.string.iquting_disconnect_tip);
        ivCardIQuTingButton.setImageResource(R.drawable.card_icon_left_in_normal);

        ivCardIQuTingLogo.setVisibility(View.VISIBLE);
        tvCardIQuTingTip.setVisibility(View.VISIBLE);
        ivCardIQuTingButton.setVisibility(View.VISIBLE);

    }

    @Override
    public void updateBigCardState(View view) {
        TextView tvCardIQuTingLoginTipBig = view.findViewById(R.id.tvAppStoreTipBig);
        if(tvCardIQuTingLoginTipBig != null){
            tvCardIQuTingLoginTipBig.setVisibility(View.VISIBLE);
            tvCardIQuTingLoginTipBig.setText(R.string.iquting_disconnect_tip);
        }

        if(view.findViewById(R.id.rcvAppStoreAppsList) != null){
            view.findViewById(R.id.rcvAppStoreAppsList).setVisibility(View.GONE);
        }
    }
}
