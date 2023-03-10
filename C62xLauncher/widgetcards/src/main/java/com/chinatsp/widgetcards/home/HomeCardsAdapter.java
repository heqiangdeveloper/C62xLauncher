package com.chinatsp.widgetcards.home;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.drawer.iquting.DrawerIqutingHolder;
import com.chinatsp.widgetcards.R;
import com.chinatsp.widgetcards.manager.CardManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import card.base.LauncherCard;
import launcher.base.utils.EasyLog;
import launcher.base.utils.collection.IndexCheck;
import launcher.base.utils.collection.ListKit;

public class HomeCardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "HomeCardsAdapter";
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private List<LauncherCard> mCardEntityList = new LinkedList<>();
    private final RecyclerView mRecyclerView;
    private final int TYPE_DRAWER = 1000;


    private final boolean mIncludeDrawer = true;
    private Map<LauncherCard, RecyclerView.ViewHolder> mViewHolderMap = new HashMap<>();


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
        if (viewType == CardManager.CardType.PHONE) {
            layout = (ViewGroup) mLayoutInflater.inflate(R.layout.item_card_frame_phone_temp, parent, false);
        } else {
            layout = (ViewGroup) mLayoutInflater.inflate(R.layout.item_card_frame, parent, false);
        }
        View innerCard = cardEntity.getLayout(layout.getContext());
        if (viewType == CardManager.CardType.I_QU_TING || viewType == CardManager.CardType.VOLCANO) {
            // ????????????1???child??????. ?????????????????????TAB??????,  ?????????????????????ivCardTopSpace??????
            layout.addView(innerCard, 1);
        } else {
            // ????????????: ????????????0???child??????
            layout.addView(innerCard, 0);
        }
//        layout.addView(innerCard, 0);
        EasyLog.d(TAG, "createCardFrameHolder: " + cardEntity.getName());
        CardFrameViewHolder viewHolder = new CardFrameViewHolder(layout, mRecyclerView, innerCard);
        mViewHolderMap.put(cardEntity, viewHolder);
        return viewHolder;
    }

    private RecyclerView.ViewHolder createDrawerHolder(@NonNull ViewGroup parent) {
        EasyLog.d(TAG, "createDrawerHolder");
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
            EasyLog.d(TAG, "onBindViewHolder : " + mCardEntityList.get(cardPosition).getName() + " , position:" + position);
            ((CardFrameViewHolder) holder).bind(position, mCardEntityList.get(cardPosition));
        } else if (holder instanceof HomeDrawerCardViewHolder) {
            ((HomeDrawerCardViewHolder) holder).bind(position);
        }
    }

    public RecyclerView.ViewHolder find(LauncherCard card) {
        if (card == null) {
            return null;
        }
        return mViewHolderMap.get(card);
    }

    public RecyclerView.ViewHolder findViewHolderByPosition(int position) {
        if (position <= 0 || position >= getItemCount()) {
            return null;
        }
        int realCardPosition = position;
        if (isIncludeDrawer()) {
            realCardPosition = position - 1;
        }
        return find(mCardEntityList.get(realCardPosition));
    }

    public int getPositionByCard(LauncherCard card) {
        int position = CardManager.getInstance().getHomeList().indexOf(card);
        if (isIncludeDrawer()) {
            position++;
        }
        return position;
    }

    public LauncherCard getCardByPosition(int position) {
        int cardPos = position;
        if (isIncludeDrawer()) {
            cardPos = position - 1;
        }
        if (!IndexCheck.indexOutOfArray(mCardEntityList, cardPos)) {
            return mCardEntityList.get(cardPos);
        }
        return null;
    }
}
