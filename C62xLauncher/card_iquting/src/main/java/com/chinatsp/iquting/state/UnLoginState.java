package com.chinatsp.iquting.state;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinatsp.iquting.R;

public class UnLoginState implements State{
    @Override
    public void updateViewState(View view) {
        view.findViewById(R.id.layoutIQuTingNormalSmall).setVisibility(View.GONE);

        ImageView ivCardIQuTingLogo = view.findViewById(R.id.ivCardIQuTingLogo);
        TextView tvCardIQuTingTip = view.findViewById(R.id.tvCardIQuTingTip);
        ImageView ivCardIQuTingButton = view.findViewById(R.id.ivCardIQuTingButton);

        ivCardIQuTingLogo.setImageResource(R.drawable.card_iquting_icon_logo);
        tvCardIQuTingTip.setText(R.string.iquting_unlogin_slogan);
        ivCardIQuTingButton.setImageResource(R.drawable.card_common_left_in_selector);

        ivCardIQuTingLogo.setVisibility(View.VISIBLE);
        tvCardIQuTingTip.setVisibility(View.VISIBLE);
        ivCardIQuTingButton.setVisibility(View.VISIBLE);
    }
}
