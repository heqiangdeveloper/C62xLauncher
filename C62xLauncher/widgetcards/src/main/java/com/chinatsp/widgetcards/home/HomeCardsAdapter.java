package com.chinatsp.widgetcards.home;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.widgetcards.R;
import com.chinatsp.widgetcards.manager.CardManager;

import java.util.LinkedList;
import java.util.List;

import card.base.LauncherCard;
import launcher.base.utils.EasyLog;

public class HomeCardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "HomeCardsAdapter";
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private List<LauncherCard> mCardEntityList = new LinkedList<>();
    private final RecyclerView mRecyclerView;
    private final int TYPE_DRAWER = 1000;


    private final boolean mIncludeDrawer = true;


    public HomeCardsAdapter(Context context, RecyclerView recyclerView) {
        mContext = context;
        mRecyclerView = recyclerView;
        mLayoutInflater = LayoutInflater.from(mContext);
    }


    public boolean isIncludeDrawer() {
        return mIncludeDrawer;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mIncludeDrawer) {
            if (viewType == TYPE_DRAWER) {
                return createDrawerHolder(parent);
            } else {
                return createCardFrameHolder(parent, viewType);
            }
        } else {
            return createCardFrameHolder(parent, viewType);
        }
    }

    private CardFrameViewHolder createCardFrameHolder(ViewGroup parent, int viewType) {
        LauncherCard cardEntity = CardManager.getInstance().findByType(viewType);
        ViewGroup layout;
        if (cardEntity.isCanExpand()) {
            layout = (ViewGroup) mLayoutInflater.inflate(R.layout.item_card_frame, parent, false);
        } else {
            layout = (ViewGroup) mLayoutInflater.inflate(R.layout.item_card_frame_locked, parent, false);
        }
        layout.setTag(cardEntity.getName());
        View innerCard = cardEntity.getLayout(layout.getContext());
        layout.addView(innerCard, 0);
        EasyLog.d(TAG, "createCardFrameHolder: " + cardEntity.getName());
        return new CardFrameViewHolder(layout, mRecyclerView, innerCard);
    }

    private RecyclerView.ViewHolder createDrawerHolder(@NonNull ViewGroup parent) {
        ViewGroup drawerLayout = (ViewGroup) mLayoutInflater.inflate(R.layout.item_card_home_drawer, parent, false);
        return new HomeDrawerCardViewHolder(drawerLayout);
    }

    @Override
    public int getItemViewType(int position) {
        if (mIncludeDrawer) {
            if (position > 0) {
                LauncherCard cardEntity = mCardEntityList.get(position - 1);
                return cardEntity.getType();
            } else {
                return TYPE_DRAWER;
            }
        } else {
            LauncherCard cardEntity = mCardEntityList.get(position);
            return cardEntity.getType();
        }
    }

    public void setCardEntityList(List<LauncherCard> cardList) {
        if (cardList != null) {
            mCardEntityList = cardList;
            EasyLog.d(TAG, "setCardEntityList: " + cardList.size());
        }
    }

    @Override
    public int getItemCount() {
        if (mIncludeDrawer) {
            return mCardEntityList.size() + 1;
        } else {
            return mCardEntityList.size();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CardFrameViewHolder) {
            int cardPosition = mIncludeDrawer ? position - 1 : position;
            EasyLog.d(TAG, "onBindViewHolder : " + mCardEntityList.get(cardPosition).getName()+" , position:"+position);
            ((CardFrameViewHolder) holder).bind(cardPosition, mCardEntityList.get(cardPosition));
        }
    }
}
