package com.chinatsp.widgetcards.editor;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import launcher.base.component.BaseFragment;

import com.chinatsp.entity.BaseCardEntity;
import com.chinatsp.widgetcards.R;
import com.chinatsp.widgetcards.editor.adapter.CardSelectedAdapter;
import com.chinatsp.widgetcards.editor.adapter.CardUnselectedAdapter;
import com.chinatsp.widgetcards.editor.drag.DragHelper;
import com.chinatsp.widgetcards.editor.drag.EnableDragStrategyImp;
import com.chinatsp.widgetcards.editor.drag.IOnSwipeFinish;
import com.chinatsp.widgetcards.service.CardsTypeManager;

import java.util.List;

import launcher.base.recyclerview.SimpleRcvDecoration;
import launcher.base.utils.EasyLog;
import launcher.base.utils.collection.IndexCheck;
import launcher.base.utils.collection.ListKit;

public class CardEditorFragment extends BaseFragment {

    private static final String TAG = "CardEditorFragment";
    private Button mBtnFinish;
    private Button mBtnCancel;
    private DragHelper mDragHelper;
    private RecyclerView rcvSelectedCards;
    private RecyclerView rcvUnelectedCards;

    @Override
    protected void initViews(View rootView) {
        mBtnFinish = rootView.findViewById(R.id.btnCardEditorOk);
        mBtnCancel = rootView.findViewById(R.id.btnCardEditorCancel);

        mBtnFinish.setOnClickListener(mOnClickListener);
        mBtnCancel.setOnClickListener(mOnClickListener);
        if (mDragHelper == null) {
            mDragHelper = new DragHelper((ViewGroup) rootView, new EnableDragStrategyImp());
        }
        initSelectedCards(rootView);
        initUnelectedCards(rootView);
        mDragHelper.initTouchListener(new IOnSwipeFinish() {
            @Override
            public void onSwipe(int position1, RecyclerView recyclerView1, int position2, RecyclerView recyclerView2) {
                if (recyclerView1 == recyclerView2) {
                    swipeHomeList(position1, position2);
                } else {
                    swipeHomeAndUnselect(recyclerView1, position1, recyclerView2, position2);
                }
            }
        });
    }

    private void swipeHomeAndUnselect(RecyclerView recyclerView1, int position1, RecyclerView recyclerView2, int position2) {
        EasyLog.d(TAG, "swipeHomeAndUnselect:" + position1+" , "+position2);
        List<BaseCardEntity> homeList = CardsTypeManager.getInstance().getHomeList();
        List<BaseCardEntity> unselectCardList = CardsTypeManager.getInstance().getUnselectCardList();
        if (IndexCheck.indexOutOfArray(homeList, position1) || IndexCheck.indexOutOfArray(unselectCardList, position2)) {
            return;
        }
        List<BaseCardEntity> list1, list2;
        if (recyclerView1 == rcvSelectedCards) {
            list1 = homeList;
            list2 = unselectCardList;
        } else {
            list1 = unselectCardList;
            list2 = homeList;
        }
        ListKit.swipeElement(list1, list2, position1, position2);
        rcvSelectedCards.getAdapter().notifyDataSetChanged();
        rcvUnelectedCards.getAdapter().notifyDataSetChanged();
        CardsTypeManager.getInstance().refreshHomeList();
    }

    private void swipeHomeList(int position1, int position2) {
        EasyLog.d(TAG, "swipeHomeList:" + position1+" , "+position2);
        List<BaseCardEntity> homeList = CardsTypeManager.getInstance().getHomeList();
        if (IndexCheck.indexOutOfArray(homeList, position1) || IndexCheck.indexOutOfArray(homeList, position2)) {
            return;
        }
        ListKit.swipeElement(homeList, position1, position2);
        rcvSelectedCards.getAdapter().notifyDataSetChanged();
        CardsTypeManager.getInstance().refreshHomeList();
    }

    private void initSelectedCards(View rootView) {
        rcvSelectedCards = rootView.findViewById(R.id.rcvCardEditorSelect);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rcvSelectedCards.setLayoutManager(layoutManager);
        CardSelectedAdapter cardSelectedAdapter = new CardSelectedAdapter(getActivity());
        cardSelectedAdapter.setData(CardsTypeManager.getInstance().getHomeList());
        rcvSelectedCards.setAdapter(cardSelectedAdapter);
        if (rcvSelectedCards.getItemDecorationCount() <= 0) {
            SimpleRcvDecoration divider = new SimpleRcvDecoration(36, layoutManager);
            rcvSelectedCards.addItemDecoration(divider);
        }
        mDragHelper.setRecyclerView1(rcvSelectedCards);
    }

    private void initUnelectedCards(View rootView) {
        rcvUnelectedCards = rootView.findViewById(R.id.rcvCardEditorUnselect);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rcvUnelectedCards.setLayoutManager(layoutManager);
        CardUnselectedAdapter cardSelectedAdapter = new CardUnselectedAdapter(getActivity());
        cardSelectedAdapter.setData(CardsTypeManager.getInstance().getUnselectCardList());
        rcvUnelectedCards.setAdapter(cardSelectedAdapter);
        if (rcvUnelectedCards.getItemDecorationCount() <= 0) {
            SimpleRcvDecoration divider = new SimpleRcvDecoration(40, layoutManager);
            rcvUnelectedCards.addItemDecoration(divider);
        }
        mDragHelper.setRecyclerView2(rcvUnelectedCards);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cards_editor;
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBtnFinish == v) {
                requireActivity().finish();
            } else if (mBtnCancel == v) {
                requireActivity().finish();
            }
        }
    };
}
