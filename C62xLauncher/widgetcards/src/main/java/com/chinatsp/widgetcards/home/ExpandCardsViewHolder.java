package com.chinatsp.widgetcards.home;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.chinatsp.widgetcards.R;
import com.chinatsp.widgetcards.manager.CardEntityFactory;

import card.base.LauncherCard;
import launcher.base.recyclerview.BaseEntity;

public class ExpandCardsViewHolder {
    private FrameLayout mRootView;
    private View mBigCardView;
    private View mSmallCardsView;
    private LayoutInflater mLayoutInflater;

    public ExpandCardsViewHolder(ViewGroup parent, LauncherCard bigCard, LauncherCard smallCard, boolean bigCardInLeft) {
        mLayoutInflater = LayoutInflater.from(parent.getContext());

        mRootView = new FrameLayout(parent.getContext());
        mRootView.setBackgroundColor(Color.parseColor("#556699"));
        mRootView.addView(createCard(smallCard, mRootView));
        parent.addView(mRootView);
    }

    private View createCard(LauncherCard card, ViewGroup parent) {
        ViewGroup layout;
        if (card.isCanExpand()) {
            layout = (ViewGroup) mLayoutInflater.inflate(R.layout.item_card_frame, parent, false);
        } else {
            layout = (ViewGroup) mLayoutInflater.inflate(R.layout.item_card_frame_locked, parent, false);
        }
        View innerCard = card.getLayout(layout.getContext());
        innerCard.setTag("InnerCard");
        layout.addView(innerCard,0);
        return layout;
    }

    public View getRootView() {
        return mRootView;
    }

    public void release(ViewGroup parent) {
        parent.removeView(mRootView);
        mRootView = null;
        mBigCardView = null;
        mSmallCardsView = null;
    }
}
