package com.chinatsp.widgetcards.home;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.entity.BaseCardEntity;
import com.chinatsp.widgetcards.R;
import com.chinatsp.widgetcards.editor.CardEditorActivity;

import java.util.function.Consumer;

import card.service.ICardStyleChange;
import launcher.base.routine.ActivityBus;
import launcher.base.utils.EasyLog;
import launcher.base.utils.flowcontrol.StableOnClickListener;

public class CardFrameViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "CardFrameViewHolder";
    private static final int MIN_CLICK_INTERVAL = 400; // ms
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
        EasyLog.d(TAG, "bind position:" + position + ", " + cardEntity.getName());
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                EasyLog.d(TAG, "onLongClick " + cardEntity.getName());
                ActivityBus.newInstance(itemView.getContext())
                        .withClass(CardEditorActivity.class)
                        .go();
                return false;
            }
        });
    }

    private View.OnClickListener mOnClickListener;

    private View.OnClickListener createListener(BaseCardEntity cardEntity) {
        return new StableOnClickListener(MIN_CLICK_INTERVAL, new Consumer<View>() {
            @Override
            public void accept(View view) {
                if (view == mIvCardZoom) {
                    changeExpandState(cardEntity);
                }
            }
        });
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
            EasyLog.d(TAG, "expand x: " + itemView.getX() + ", position:" + position);
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
        itemView.setBackgroundResource(R.drawable.card_bg_large);
        mIvCardZoom.setImageResource(R.drawable.card_icon_back);
        if (mCardInner instanceof ICardStyleChange) {
            ((ICardStyleChange) mCardInner).expand();
        }

        mTvCardName.setTextColor(getColor(R.color.card_blue_lv1));
        runExpandAnimation();
    }

    private void runExpandAnimation() {
        int largeWidth = (int) getDimension(R.dimen.card_width_large);
        int smallWidth = (int) getDimension(R.dimen.card_width);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        ValueAnimator valueAnimator = ObjectAnimator.ofInt(smallWidth, largeWidth).setDuration(150);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                layoutParams.width = value;
                itemView.setLayoutParams(layoutParams);
                EasyLog.d(TAG, "AnimatedValue:" + value);
            }
        });
        valueAnimator.start();
    }

    private void runCollapseAnimation() {
        int largeWidth = (int) getDimension(R.dimen.card_width_large);
        int smallWidth = (int) getDimension(R.dimen.card_width);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        ValueAnimator valueAnimator = ObjectAnimator.ofInt(largeWidth, smallWidth).setDuration(150);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                layoutParams.width = value;
                itemView.setLayoutParams(layoutParams);
                EasyLog.d(TAG, "AnimatedValue:" + value);
            }
        });
        valueAnimator.start();
    }

    private void collapse(BaseCardEntity cardEntity) {
        itemView.setBackgroundResource(R.drawable.card_bg_small);
        mIvCardZoom.setImageResource(R.drawable.card_common_icon_expand);

        if (mCardInner instanceof ICardStyleChange) {
            ((ICardStyleChange) mCardInner).collapse();
        }
        mTvCardName.setTextColor(getColor(R.color.white));
        runCollapseAnimation();
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