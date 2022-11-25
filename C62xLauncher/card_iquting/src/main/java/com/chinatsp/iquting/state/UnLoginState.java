package com.chinatsp.iquting.state;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.chinatsp.iquting.R;

public class UnLoginState implements IQuTingState {
    public void updateSmallCardState(View view) {
        view.findViewById(R.id.layoutIQuTingNormalSmall).setVisibility(View.GONE);
        view.findViewById(R.id.layoutIQuTingErrorNet).setVisibility(View.GONE);
        view.findViewById(R.id.layoutIQuTingLogin).setVisibility(View.VISIBLE);

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
        TextView tvIQuTingDailySongs = view.findViewById(R.id.tvIQuTingDailySongs);
        TextView tvIQuTingRankSongs = view.findViewById(R.id.tvIQuTingRankSongs);
        ImageView ivCardIQuTingRefreshBig = view.findViewById(R.id.ivCardIQuTingRefreshBig);
//        ConstraintLayout cl = view.findViewById(R.id.rootLayout);
//        if(cl != null) cl.setVisibility(View.GONE);
        View widgetView = view.findViewById(R.id.layoutIQuTingPlayWidget);
        if(widgetView != null) widgetView.setVisibility(View.VISIBLE);
        if(tvCardIQuTingLoginTipBig != null){
            tvCardIQuTingLoginTipBig.setVisibility(View.VISIBLE);
            tvCardIQuTingLoginTipBig.setText(R.string.iquting_unlogin_slogan);
        }

        if(tvIQuTingDailySongs != null) tvIQuTingDailySongs.setVisibility(View.GONE);
        if(tvIQuTingRankSongs != null) tvIQuTingRankSongs.setVisibility(View.GONE);

        if(view.findViewById(R.id.rcvCardIQuTingSongList) != null){
            view.findViewById(R.id.rcvCardIQuTingSongList).setVisibility(View.GONE);
        }
        if(ivCardIQuTingRefreshBig != null) ivCardIQuTingRefreshBig.setVisibility(View.GONE);
    }

}
