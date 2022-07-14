package com.chinatsp.widgetcards.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.widgetcards.R;
import com.chinatsp.widgetcards.manager.CardManager;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import card.base.LauncherCard;
import launcher.base.utils.EasyLog;

public class HomeCardsAdapter extends RecyclerView.Adapter<CardFrameViewHolder> {

    private static final String TAG = "CardsAdapter";
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private List<LauncherCard> mCardEntityList = new LinkedList<>();
    private final Set<CardFrameViewHolder> mViewHolders = new HashSet<>();
    private RecyclerView mRecyclerView;


    public HomeCardsAdapter(Context context, RecyclerView recyclerView) {
        mContext = context;
        mRecyclerView = recyclerView;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public CardFrameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
        return new CardFrameViewHolder(layout, mRecyclerView, innerCard);
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
    public void onBindViewHolder(@NonNull CardFrameViewHolder holder, int position) {
        holder.bind(position, mCardEntityList.get(position));
    }

}
