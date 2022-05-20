package com.chinatsp.widgetcards.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.widgetcards.R;

public class CardFrameViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "CardFrameViewHolder";
    private RecyclerView mRecyclerView;
    private TextView mTvCardName;
    private ImageView mIvCardZoom;
    private View mCardInner;
    private View mCardLargeInner;
    private boolean mExpandState;

    public CardFrameViewHolder(@NonNull View itemView, RecyclerView recyclerView, View cardInner) {
        super(itemView);
        this.mRecyclerView = recyclerView;
        mTvCardName = itemView.findViewById(R.id.tvCardName);
        mIvCardZoom = itemView.findViewById(R.id.ivCardZoom);
        mCardInner = cardInner;
    }

    public void bind(int position, BaseCardEntity cardEntity) {
        mTvCardName.setText(cardEntity.getName());
        if (mOnClickListener == null) {
            mOnClickListener = createListener(cardEntity);
        }
        mIvCardZoom.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener;

    private View.OnClickListener createListener(BaseCardEntity cardEntity) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == mIvCardZoom) {
                    changeExpandState(cardEntity);
                }
            }
        };
    }

    private void changeExpandState(BaseCardEntity cardEntity) {
        boolean changeSuccess = true;
        if (mExpandState) {
            collapse(cardEntity);
            dealCollapseScroll(getAdapterPosition());
        } else {
            changeSuccess = canExpand();
            if (changeSuccess) {
                expand(cardEntity);
                dealExpandScroll(getAdapterPosition());
            }
        }
        if (changeSuccess) {
            mExpandState = !mExpandState;
            cardEntity.setExpandState(mExpandState);
            ExpandStateManager.getInstance().setExpand(mExpandState);
        }
    }

    private boolean scrolledInExpand = false;

    private void dealExpandScroll(int position) {
        // 3个卡片的位置: 0, 605, 1210
        if (itemView.getX() > 700) {
            // 位于第三个位置时, recyclerView需要左移
            Log.d(TAG, "expand x: " + itemView.getX() + ", position:" + position);
            mRecyclerView.scrollToPosition(position);
            LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            if (layoutManager != null) {
                scrolledInExpand = true;
                if (position > 1) {
                    layoutManager.scrollToPositionWithOffset(position - 1, 0);
                }
            }
        }
    }

    private void dealCollapseScroll(int position) {
        if (scrolledInExpand) {
            scrolledInExpand = false;
            LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            if (layoutManager != null) {
                if (position > 1) {
                    layoutManager.scrollToPositionWithOffset(position - 2, 0);
                }
            }
        }
    }

    private void expand(BaseCardEntity cardEntity) {
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.width = (int) getDimension(R.dimen.card_width_large);
        itemView.setBackgroundResource(R.drawable.card_bg_large);
        mIvCardZoom.setImageResource(R.drawable.card_icon_back);
        ViewGroup root = (ViewGroup) itemView;
        root.removeView(mCardInner);
        root.addView(getLargeCardView(cardEntity), 0);
        mTvCardName.setTextColor(getColor(R.color.card_blue_default));
    }

    private void collapse(BaseCardEntity cardEntity) {
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.width = (int) getDimension(R.dimen.card_width);
        itemView.setBackgroundResource(R.drawable.card_bg_small);
        mIvCardZoom.setImageResource(R.drawable.card_common_icon_expand);
        ViewGroup root = (ViewGroup) itemView;
        root.removeView(getLargeCardView(cardEntity));
        root.addView(mCardInner, 0);
        mTvCardName.setTextColor(getColor(R.color.white));
    }

    private View getLargeCardView(BaseCardEntity cardEntity) {
        if (mCardLargeInner == null) {
            mCardLargeInner = cardEntity.getLargeLayout(itemView.getContext());
        }
        return mCardLargeInner;
    }

    /**
     * 当卡片组有任意一个卡片处于expand状态, 就不允许变大.
     *
     * @return 是否允许卡片变大.
     */
    private boolean canExpand() {
        return !ExpandStateManager.getInstance().getExpandState();
    }

    private float getDimension(int dimensionId) {
        Context applicationContext = itemView.getContext().getApplicationContext();
        return applicationContext.getResources().getDimension(dimensionId);
    }
    private int getColor(int colorId) {
        Context applicationContext = itemView.getContext().getApplicationContext();
        return applicationContext.getResources().getColor(colorId);
    }
}