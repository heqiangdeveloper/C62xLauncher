package com.chinatsp.widgetcards.home.smallcard;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.widgetcards.R;
import com.chinatsp.widgetcards.home.AppLauncherUtil;
import com.chinatsp.widgetcards.home.CardFrameViewHolder;
import com.chinatsp.widgetcards.home.ExpandStateManager;
import com.chinatsp.widgetcards.home.smallcard2.HomeCardRcvManager;
import com.chinatsp.widgetcards.home.smallcard2.SmallCardRcvManager;
import com.chinatsp.widgetcards.manager.CardNameRes;

import card.base.LauncherCard;
import card.service.ICardStyleChange;
import launcher.base.utils.EasyLog;

public class SmallCardViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "SmallCardViewHolder";
    private TextView mTvCardName;
    private boolean mHideTitle;
    private View mCardInner;
    private OnExpandCardInCard mOnExpandCardInCard;
    private ImageView mIvCardZoom;
    private View ivCardTopSpace;


    public SmallCardViewHolder(@NonNull View itemView, View cardInner, OnExpandCardInCard onExpandCardInCard) {
        super(itemView);
        mTvCardName = itemView.findViewById(R.id.tvCardName);
        mIvCardZoom = itemView.findViewById(R.id.ivCardZoom);
        ivCardTopSpace = itemView.findViewById(R.id.ivCardTopSpace);

        mCardInner = cardInner;
        mOnExpandCardInCard = onExpandCardInCard;
    }

    public void bind(int position, LauncherCard card) {
        EasyLog.d("SmallCardViewHolder", "bind card:" + card.getName());
        mHideTitle = checkNeedHideTitle();
        setTitle(card.getType());
        resetExpandIcon(card);
        if (mOnClickListener == null) {
            mOnClickListener = createListener(card);
        }
        ivCardTopSpace.setOnClickListener(mOnClickListener);
        mTvCardName.setOnClickListener(mOnClickListener);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLauncherUtil.start(v.getContext(), card.getType());
            }
        });
    }
    private View.OnClickListener mOnClickListener;

    private static long mLastValidClickExpandTime;
    private static long mLastClickExpandTime;
    private static final int MIN_CLICK_INTERVAL = 600; // ms

    private View.OnClickListener createListener(LauncherCard cardEntity) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long now = System.currentTimeMillis();
                long diffToValid = now - mLastValidClickExpandTime;
                long diffToInvalid = now - mLastClickExpandTime;
                mLastClickExpandTime = now;
                if (diffToValid < MIN_CLICK_INTERVAL && diffToInvalid < MIN_CLICK_INTERVAL) {
                    EasyLog.w(TAG, "click expand or collapse view fail: touch too fast."
                            + cardEntity.getName() + " , diff:" + diffToValid + " , diffToInvalid:" + diffToInvalid);
                    return;
                }
                mLastValidClickExpandTime = now;
                EasyLog.d(TAG, "click expand or collapse view :" + cardEntity.getName()
                        + " , diff:" + diffToValid + " , diffToInvalid:" + diffToInvalid);
                if (!cardEntity.isCanExpand()) {
                    return;
                }
                LauncherCard anchorSmallCard = ExpandStateManager.getInstance().getAnchorSmallCard();
                if (anchorSmallCard == null) {
                    return;
                }
                CardFrameViewHolder viewHoldByCard = HomeCardRcvManager.getInstance().findViewHoldByCard(anchorSmallCard);
                ExpandStateManager.getInstance().clickExpandButton(cardEntity, viewHoldByCard.isCardInLeftSide());
            }
        };
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

    private void resetExpandIcon(LauncherCard cardEntity) {
        if (cardEntity.isCanExpand()) {
            mIvCardZoom.setVisibility(View.VISIBLE);
        } else {
            mIvCardZoom.setVisibility(View.GONE);
        }
    }

    private void setTitle(int type) {
        mTvCardName.setText(CardNameRes.getStringRes(type));
        if (mHideTitle) {
            mTvCardName.setVisibility(View.GONE);
        } else {
            mTvCardName.setVisibility(View.VISIBLE);
        }
    }
}
