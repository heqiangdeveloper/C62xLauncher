package com.chinatsp.widgetcards.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.widgetcards.R;
import com.chinatsp.widgetcards.service.CardsTypeManager;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import launcher.base.utils.EasyLog;

public class CardsAdapter extends RecyclerView.Adapter<CardFrameViewHolder> {

    private static final String TAG = "CardsAdapter";
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final List<BaseCardEntity> mCardEntityList = new LinkedList<>();
    private final Set<CardFrameViewHolder> mViewHolders = new HashSet<>();
    private RecyclerView mRecyclerView;


    public CardsAdapter(Context context, RecyclerView recyclerView) {
        mContext = context;
        mRecyclerView = recyclerView;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public CardFrameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseCardEntity cardEntity = CardsTypeManager.getInstance().findByType(viewType);
        ViewGroup layout = (ViewGroup) mLayoutInflater.inflate(R.layout.item_card_frame, parent, false);
        View innerCard = cardEntity.getLayout(layout.getContext());
        innerCard.setTag("InnerCard");
        layout.addView(innerCard,0);
        return new CardFrameViewHolder(layout, mRecyclerView, innerCard);
    }

    @Override
    public int getItemViewType(int position) {
        BaseCardEntity cardEntity = mCardEntityList.get(position);
        return cardEntity.getType();
    }

    public void setCardEntityList(List<BaseCardEntity> cardList) {
        if (cardList != null) {
            mCardEntityList.clear();
            mCardEntityList.addAll(cardList);
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
