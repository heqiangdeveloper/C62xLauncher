package com.chinatsp.widgetcards.home.smallcard;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.widgetcards.R;

import card.base.LauncherCard;
import card.service.ICardStyleChange;

class SmallCardViewHolder extends RecyclerView.ViewHolder {
    private TextView mTvCardName;
    private boolean mHideTitle;
    private View mCardInner;
    private OnExpandCardInCard mOnExpandCardInCard;
    private ImageView mIvCardZoom;

    public SmallCardViewHolder(@NonNull View itemView, View cardInner, OnExpandCardInCard onExpandCardInCard) {
        super(itemView);
        mTvCardName = itemView.findViewById(R.id.tvCardName);
        mIvCardZoom = itemView.findViewById(R.id.ivCardZoom);
        mCardInner = cardInner;
        mOnExpandCardInCard = onExpandCardInCard;
    }

    public void bind(int position, LauncherCard card) {
        mHideTitle = checkNeedHideTitle();
        setTitle(card.getName());
        mIvCardZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expand(card);
            }
        });
    }

    private void expand(LauncherCard card) {
        if (mOnExpandCardInCard != null) {
            mOnExpandCardInCard.onExpand(card);
        }
    }

    private boolean checkNeedHideTitle() {
        boolean hideDefault = false;
        if (mCardInner instanceof ICardStyleChange) {
            hideDefault = ((ICardStyleChange) mCardInner).hideDefaultTitle();
        }
        return hideDefault;
    }

    private void setTitle(String name) {
        mTvCardName.setText(name);
        if (mHideTitle) {
            mTvCardName.setVisibility(View.GONE);
        } else {
            mTvCardName.setVisibility(View.VISIBLE);
        }
    }
}
