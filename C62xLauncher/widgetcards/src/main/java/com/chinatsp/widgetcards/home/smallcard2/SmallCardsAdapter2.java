package com.chinatsp.widgetcards.home.smallcard2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.widgetcards.R;
import com.chinatsp.widgetcards.home.CardFrameViewHolder;
import com.chinatsp.widgetcards.home.HomeDrawerCardViewHolder;
import com.chinatsp.widgetcards.home.smallcard.SmallCardViewHolder;
import com.chinatsp.widgetcards.home.smallcard.SmallCardsAdapter;
import com.chinatsp.widgetcards.manager.CardManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import card.base.LauncherCard;
import launcher.base.utils.EasyLog;

public class SmallCardsAdapter2 extends RecyclerView.Adapter<SmallCardViewHolder> {

    private static final String TAG = "SmallCardsAdapter2";
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;

    public List<LauncherCard> getCardEntityList() {
        return mCardEntityList;
    }

    private List<LauncherCard> mCardEntityList = new LinkedList<>();
    private final RecyclerView mRecyclerView;
    private final int TYPE_DRAWER = 1000;


    private Map<LauncherCard, RecyclerView.ViewHolder> mViewHolderMap = new HashMap<>();


    public SmallCardsAdapter2(Context context, RecyclerView recyclerView) {
        mContext = context;
        mRecyclerView = recyclerView;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public SmallCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LauncherCard cardEntity = CardManager.getInstance().findByType(viewType);
        ViewGroup layout;
        if (viewType == CardManager.CardType.PHONE) {
            layout = (ViewGroup) mLayoutInflater.inflate(R.layout.item_card_frame_phone_temp, parent, false);
        } else {
            layout = (ViewGroup) mLayoutInflater.inflate(R.layout.item_card_frame, parent, false);
        }
        View innerCard = cardEntity.getLayout(layout.getContext());
        innerCard.setTag("InnerCard");
        layout.addView(innerCard,0);
        EasyLog.d(TAG, "onCreateViewHolder: "+viewType);
        return new SmallCardViewHolder(layout, innerCard, null);
    }

    @Override
    public void onBindViewHolder(@NonNull SmallCardViewHolder holder, int position) {
        holder.bind(position, mCardEntityList.get(position));
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
            // 蓝牙卡片: 添加在第1个child之后. 因为顶部有两个TAB按钮,  不能被卡片顶部ivCardTopSpace覆盖
            layout.addView(innerCard, 1);
        } else {
            // 普通卡片: 添加在第0个child之后
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
        LauncherCard cardEntity = mCardEntityList.get(position);
        return cardEntity.getType();
    }

    public void setCardEntityList(List<LauncherCard> cardList) {
        if (cardList != null) {
            mCardEntityList = cardList;
            EasyLog.d(TAG, "setCardEntityList: " + cardList.size());
        }
    }

    @Override
    public int getItemCount() {
        return mCardEntityList.size();
    }

    public RecyclerView.ViewHolder find(LauncherCard card) {
        if (card == null) {
            return null;
        }
        return mViewHolderMap.get(card);
    }
}