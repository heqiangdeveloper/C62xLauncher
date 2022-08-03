package com.chinatsp.iquting.state;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinatsp.iquting.R;

public class UnLoginState implements IQuTingState {
    public void updateSmallCardState(View view) {
        view.findViewById(R.id.layoutIQuTingNormalSmall).setVisibility(View.GONE);

        ImageView ivCardIQuTingLogo = view.findViewById(R.id.ivCardIQuTingLogo);
        TextView tvCardIQuTingTip = view.findViewById(R.id.tvCardIQuTingLoginTip);
        ImageView ivCardIQuTingButton = view.findViewById(R.id.ivCardIQuTingButton);

        ivCardIQuTingLogo.setImageResource(R.drawable.card_iquting_icon_logo);
        tvCardIQuTingTip.setText(R.string.iquting_unlogin_slogan);
        ivCardIQuTingButton.setImageResource(R.drawable.card_common_left_in_selector);

        ivCardIQuTingLogo.setVisibility(View.VISIBLE);
        tvCardIQuTingTip.setVisibility(View.VISIBLE);
        ivCardIQuTingButton.setVisibility(View.VISIBLE);
    }

    public void updateBigCardState(View view) {
        TextView tvCardIQuTingLoginTipBig = view.findViewById(R.id.tvCardIQuTingLoginTipBig);
        tvCardIQuTingLoginTipBig.setVisibility(View.VISIBLE);
        tvCardIQuTingLoginTipBig.setText(R.string.iquting_unlogin_slogan);

        view.findViewById(R.id.rcvCardIQuTingSongList).setVisibility(View.GONE);
    }

}
