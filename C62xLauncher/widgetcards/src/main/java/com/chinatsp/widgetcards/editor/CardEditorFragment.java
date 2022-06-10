package com.chinatsp.widgetcards.editor;

import android.view.DragEvent;
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
import com.chinatsp.widgetcards.editor.drag.IOnSwipeFinish;
import com.chinatsp.widgetcards.service.CardsTypeManager;

import java.util.List;

import launcher.base.recyclerview.SimpleRcvDecoration;
import launcher.base.utils.collection.ListKit;

public class CardEditorFragment extends BaseFragment {

    private Button mBtnFinish;
    private Button mBtnCancel;
    private DragHelper mDragHelper;
    private RecyclerView rcvSelectedCards;
    @Override
    protected void initViews(View rootView) {
        mBtnFinish = rootView.findViewById(R.id.btnCardEditorOk);
        mBtnCancel = rootView.findViewById(R.id.btnCardEditorCancel);

        mBtnFinish.setOnClickListener(mOnClickListener);
        mBtnCancel.setOnClickListener(mOnClickListener);
        if (mDragHelper == null) {
            mDragHelper = new DragHelper((ViewGroup) rootView);
        }
        initSelectedCards(rootView);
        initUnelectedCards(rootView);
        mDragHelper.initTouchListener(new IOnSwipeFinish() {
            @Override
            public void onSwipeHome(int position1, int position2) {
                List<BaseCardEntity> homeList = CardsTypeManager.getInstance().getHomeList();
                ListKit.swipeElement(homeList, position1, position2);
                rcvSelectedCards.getAdapter().notifyDataSetChanged();
                CardsTypeManager.getInstance().refreshHomeList();
            }
        });
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
        RecyclerView rcvUnelectedCards = rootView.findViewById(R.id.rcvCardEditorUnselect);
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
