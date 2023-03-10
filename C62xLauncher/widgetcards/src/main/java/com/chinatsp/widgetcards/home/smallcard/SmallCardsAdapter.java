package com.chinatsp.widgetcards.home.smallcard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.widgetcards.R;
import com.chinatsp.widgetcards.home.CardFrameViewHolder;
import com.chinatsp.widgetcards.manager.CardManager;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import card.base.LauncherCard;
import launcher.base.utils.EasyLog;

public class SmallCardsAdapter extends RecyclerView.Adapter<SmallCardViewHolder> {

    private static final String TAG = "SmallCardsAdapter";
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private List<LauncherCard> mCardEntityList = new LinkedList<>();
    private final Set<CardFrameViewHolder> mViewHolders = new HashSet<>();
    private RecyclerView mRecyclerView;
    private OnExpandCardInCard mOnExpandCardInCard;


    public SmallCardsAdapter(Context context, RecyclerView recyclerView, OnExpandCardInCard onExpandCardInCard) {
        mContext = context;
        mRecyclerView = recyclerView;
        mOnExpandCardInCard = onExpandCardInCard;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public SmallCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LauncherCard cardEntity = CardManager.getInstance().findByType(viewType);
        ViewGroup layout;
        if (cardEntity.isCanExpand()) {
            layout = (ViewGroup) mLayoutInflater.inflate(R.layout.item_card_frame, parent, false);
        } else {
            layout = (ViewGroup) mLayoutInflater.inflate(R.layout.item_card_frame_locked, parent, false);
        }
        View innerCard = cardEntity.getLayout(layout.getContext());
        innerCard.setTag("InnerCard");
        layout.addView(innerCard,0);
        EasyLog.d(TAG, "onCreateViewHolder: "+viewType);
        return new SmallCardViewHolder(layout, innerCard, mOnExpandCardInCard);
    }

    @Override
    public int getItemViewType(int position) {
        LauncherCard cardEntity = mCardEntityList.get(position);
        return cardEntity.getType();
    }

    public void setCardEntityList(List<LauncherCard> cardList) {
        if (cardList != null) {
            mCardEntityList = cardList;
            EasyLog.d(TAG, "setCardEntityList: "+cardList.size());
        }
    }

    @Override
    public int getItemCount() {
        return mCardEntityList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull SmallCardViewHolder holder, int position) {
        holder.bind(position, mCardEntityList.get(position));
    }

    public LauncherCard getItem(int position) {
        if (position >= 0 && position < getItemCount()) {
            return mCardEntityList.get(position);
        }
        return null;
    }

    public void clear() {
        mCardEntityList.clear();
        notifyDataSetChanged();
    }
}