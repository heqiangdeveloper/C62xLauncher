package com.chinatsp.iquting.state;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.chinatsp.iquting.R;

public class NetWorkDisconnectState implements IQuTingState {
    @Override
    public void updateSmallCardState(View view) {
        view.findViewById(R.id.layoutIQuTingNormalSmall).setVisibility(View.GONE);

        ImageView ivCardIQuTingLogo = view.findViewById(R.id.ivCardIQuTingLogo);
        TextView tvCardIQuTingTip = view.findViewById(R.id.tvCardIQuTingLoginTip);
        ImageView ivCardIQuTingButton = view.findViewById(R.id.ivCardIQuTingButton);

        ivCardIQuTingLogo.setImageResource(R.drawable.card_icon_wifi_disconnect);
        tvCardIQuTingTip.setText(R.string.iquting_disconnect_tip);
        ivCardIQuTingButton.setImageResource(R.drawable.card_common_left_in_selector);

        ivCardIQuTingLogo.setVisibility(View.VISIBLE);
        tvCardIQuTingTip.setVisibility(View.VISIBLE);
        ivCardIQuTingButton.setVisibility(View.VISIBLE);

    }

    @Override
    public void updateBigCardState(View view) {
        TextView tvCardIQuTingLoginTipBig = view.findViewById(R.id.tvCardIQuTingLoginTipBig);
        TextView tvIQuTingDailySongs = view.findViewById(R.id.tvIQuTingDailySongs);
        TextView tvIQuTingRankSongs = view.findViewById(R.id.tvIQuTingRankSongs);
        if(tvCardIQuTingLoginTipBig != null){
            tvCardIQuTingLoginTipBig.setVisibility(View.VISIBLE);
            tvCardIQuTingLoginTipBig.setText(R.string.iquting_disconnect_tip);
        }
        if(tvIQuTingDailySongs != null) tvIQuTingDailySongs.setVisibility(View.GONE);
        if(tvIQuTingRankSongs != null) tvIQuTingRankSongs.setVisibility(View.GONE);

        if(view.findViewById(R.id.rcvCardIQuTingSongList) != null){
            view.findViewById(R.id.rcvCardIQuTingSongList).setVisibility(View.GONE);
        }
    }
}
