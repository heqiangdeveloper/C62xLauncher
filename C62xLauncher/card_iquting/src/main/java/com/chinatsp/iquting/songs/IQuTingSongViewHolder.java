package com.chinatsp.iquting.songs;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chinatsp.iquting.IQuTingCardView;
import com.chinatsp.iquting.R;
import com.chinatsp.iquting.event.ControlEvent;
import com.tencent.wecarflow.contentsdk.ContentManager;
import com.tencent.wecarflow.contentsdk.bean.BaseSongItemBean;
import com.tencent.wecarflow.contentsdk.callback.MediaPlayResult;

import org.greenrobot.eventbus.EventBus;

import launcher.base.recyclerview.BaseViewHolder;
import launcher.base.utils.glide.GlideHelper;

public class IQuTingSongViewHolder extends BaseViewHolder<BaseSongItemBean> {
    private static final String TAG = "IQuTingSongViewHolder";
    private ImageView mSongCover;
    private TextView mTvIqutingSongItemName;
    private ImageView mIvIqutingSongVip;
    private ImageView mIvIqutingSongItemPlayBtn;
    public IQuTingSongViewHolder(@NonNull View itemView) {
        super(itemView);
        mSongCover = itemView.findViewById(R.id.ivIqutingSongItemCover);
        mTvIqutingSongItemName = (TextView) itemView.findViewById(R.id.tvIqutingSongItemName);
        mIvIqutingSongVip = (ImageView) itemView.findViewById(R.id.ivIqutingSongVip);
        mIvIqutingSongItemPlayBtn = (ImageView) itemView.findViewById(R.id.ivIqutingSongItemPlayBtn);
    }

    @Override
    public void bind(int position, BaseSongItemBean baseSongItemBean) {
        super.bind(position, baseSongItemBean);
        String url = baseSongItemBean.getItemImageUrl();
        String singer = baseSongItemBean.getSinger_name();
        String name = baseSongItemBean.getSong_name();
        boolean isVip = baseSongItemBean.getVip() == 1 ? true : false;
        if(!TextUtils.isEmpty(url)){
            GlideHelper.loadUrlAlbumCoverRadius(mSongCover.getContext(), mSongCover, url, 10);
        }
        mTvIqutingSongItemName.setText(name + "-" + singer);
        mIvIqutingSongVip.setVisibility(isVip ? View.VISIBLE : View.GONE);
        mIvIqutingSongItemPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick: " + position);
                updateSelectItem(0,!IQuTingCardView.isPlaying);
                EventBus.getDefault().post(new ControlEvent(position,String.valueOf(baseSongItemBean.getSong_id())));
            }
        });
        if(IQuTingCardView.itemUUID.equals(String.valueOf(baseSongItemBean.getSong_id()))){
            updateSelectItem(0,IQuTingCardView.isPlaying);
        }else {
            mIvIqutingSongItemPlayBtn.setImageResource(R.drawable.card_iquting_icon_circle_pause);
        }
    }

    public void updateSelectItem(int position,boolean isPlaying){
        if(isPlaying){
            mIvIqutingSongItemPlayBtn.setImageResource(R.drawable.card_iquting_icon_circle_play);
        }else {
            mIvIqutingSongItemPlayBtn.setImageResource(R.drawable.card_iquting_icon_circle_pause);
        }
    }
}
