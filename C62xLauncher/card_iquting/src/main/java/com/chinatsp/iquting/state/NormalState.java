package com.chinatsp.iquting.state;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.iquting.R;

public class NormalState implements IQuTingState {
    public void updateSmallCardState(View view) {
        view.findViewById(R.id.layoutIQuTingNormalSmall).setVisibility(View.VISIBLE);

        ImageView ivCardIQuTingLogo = view.findViewById(R.id.ivCardIQuTingLogo);
        TextView tvCardIQuTingTip = view.findViewById(R.id.tvCardIQuTingLoginTip);
        ImageView ivCardIQuTingButton = view.findViewById(R.id.ivCardIQuTingButton);

        ivCardIQuTingLogo.setVisibility(View.GONE);
        tvCardIQuTingTip.setVisibility(View.GONE);
        ivCardIQuTingButton.setVisibility(View.GONE);


    }

    public void updateBigCardState(View view) {
        View tvCardIQuTingLoginTipBig = view.findViewById(R.id.tvCardIQuTingLoginTipBig);
        tvCardIQuTingLoginTipBig.setVisibility(View.GONE);

        RecyclerView rcvCardIQuTingSongList = view.findViewById(R.id.rcvCardIQuTingSongList);
        rcvCardIQuTingSongList.setVisibility(View.VISIBLE);
    }

}
