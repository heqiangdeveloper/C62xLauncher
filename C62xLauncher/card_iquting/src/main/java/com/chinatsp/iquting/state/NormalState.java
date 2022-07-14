package com.chinatsp.iquting.state;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chinatsp.iquting.R;

public class NormalState implements State{
    @Override
    public void updateViewState(View view) {
        view.findViewById(R.id.layoutIQuTingNormalSmall).setVisibility(View.VISIBLE);

        ImageView ivCardIQuTingLogo = view.findViewById(R.id.ivCardIQuTingLogo);
        TextView tvCardIQuTingTip = view.findViewById(R.id.tvCardIQuTingTip);
        ImageView ivCardIQuTingButton = view.findViewById(R.id.ivCardIQuTingButton);

        ivCardIQuTingLogo.setVisibility(View.GONE);
        tvCardIQuTingTip.setVisibility(View.GONE);
        ivCardIQuTingButton.setVisibility(View.GONE);


    }
}
