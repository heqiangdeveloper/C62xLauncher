package com.chinatsp.drawer.iquting;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chinatsp.iquting.IQuTingCardView;
import com.chinatsp.iquting.event.ControlEvent;
import com.chinatsp.widgetcards.R;
import com.tencent.wecarflow.contentsdk.bean.BaseSongItemBean;
import com.tencent.wecarflow.controlsdk.FlowPlayControl;

import org.greenrobot.eventbus.EventBus;

import launcher.base.recyclerview.BaseRcvAdapter;
import launcher.base.recyclerview.BaseViewHolder;
import launcher.base.utils.glide.GlideHelper;

class SongsAdapter extends BaseRcvAdapter<BaseSongItemBean> {
    private static final String TAG = "SongsAdapter";
    private final static int MAXSONGS = 2;//最大2首

    private IPlayItemCallback iPlayItemCallback;
    public SongsAdapter(Context context,IPlayItemCallback iPlayItemCallback) {
        super(context);
        this.iPlayItemCallback = iPlayItemCallback;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.drawer_item_iquting_song_item;
    }

    @Override
    protected SongViewHolder createViewHolder(View view) {
        return new SongViewHolder(view);
    }

    public class SongViewHolder extends BaseViewHolder<BaseSongItemBean> {
        private ImageView mIvIqutingDrawerSongItemCover;
        private ImageView mIvIqutingDrawerSongItemPlayOrPause;
        private TextView mTvIqutingDrawerSongItemName;
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvIqutingDrawerSongItemCover = (ImageView) itemView.findViewById(R.id.ivIqutingDrawerSongItemCover);
            mIvIqutingDrawerSongItemPlayOrPause = (ImageView) itemView.findViewById(R.id.ivIqutingDrawerSongItemPlayOrPause);
            mTvIqutingDrawerSongItemName = (TextView) itemView.findViewById(R.id.tvIqutingDrawerSongItemName);
        }

        @Override
        public void bind(int position, BaseSongItemBean baseSongItemBean) {
            super.bind(position, baseSongItemBean);
            String url = baseSongItemBean.getAlbum_pic_300x300();
            String singer = baseSongItemBean.getSinger_name();
            String name = baseSongItemBean.getSong_name();
            long songId = baseSongItemBean.getSong_id();
            Log.d(TAG,"name = " + name + ",id = " + baseSongItemBean.getSong_id() + ",url: " + url);
            if(!TextUtils.isEmpty(url)){
                GlideHelper.loadUrlAlbumCoverRadius(mIvIqutingDrawerSongItemCover.getContext(), mIvIqutingDrawerSongItemCover, url, 10);
            }else {
                GlideHelper.loadLocalAlbumCoverRadius(mIvIqutingDrawerSongItemCover.getContext(),mIvIqutingDrawerSongItemCover, com.chinatsp.iquting.R.drawable.test_cover2,10);
            }
            mTvIqutingDrawerSongItemName.setText(name + "-" + singer);
            mIvIqutingDrawerSongItemPlayOrPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG,"onClick: " + position);
                    //updateSelectItem(0,!DrawerIqutingHolder.isPlaying);
                    //通知item更新状态
                    iPlayItemCallback.onItemClick(position,songId);
                }
            });
            mIvIqutingDrawerSongItemCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlowPlayControl.getInstance().openPlayDetail(mIvIqutingDrawerSongItemCover.getContext());
                }
            });
            if(DrawerIqutingHolder.itemUUIDInDrawer.equals(String.valueOf(baseSongItemBean.getSong_id()))){
                updateSelectItem(0,DrawerIqutingHolder.isPlaying);
            }else {
                mIvIqutingDrawerSongItemPlayOrPause.setImageResource(com.chinatsp.iquting.R.drawable.card_iquting_icon_circle_pause);
            }
        }

        public void updateSelectItem(int position,boolean isPlaying){
            if(isPlaying){
                mIvIqutingDrawerSongItemPlayOrPause.setImageResource(com.chinatsp.iquting.R.drawable.card_iquting_icon_circle_play);
            }else {
                mIvIqutingDrawerSongItemPlayOrPause.setImageResource(com.chinatsp.iquting.R.drawable.card_iquting_icon_circle_pause);
            }
        }
    }

    @Override
    public int getItemCount() {
        if(getData().size() > MAXSONGS){
            return MAXSONGS;
        }
        return super.getItemCount();
    }
}
