package com.chinatsp.widgetcards.editor;

import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import launcher.base.component.BaseFragment;
import com.chinatsp.widgetcards.R;
import com.chinatsp.widgetcards.editor.adapter.CardSelectedAdapter;
import com.chinatsp.widgetcards.editor.adapter.CardUnselectedAdapter;
import com.chinatsp.widgetcards.service.CardsTypeManager;

import launcher.base.recyclerview.SimpleRcvDecoration;

public class CardEditorFragment extends BaseFragment {

    private Button mBtnFinish;
    private Button mBtnCancel;
    @Override
    protected void initViews(View rootView) {
        mBtnFinish = rootView.findViewById(R.id.btnCardEditorOk);
        mBtnCancel = rootView.findViewById(R.id.btnCardEditorCancel);

        mBtnFinish.setOnClickListener(mOnClickListener);
        mBtnCancel.setOnClickListener(mOnClickListener);

        initSelectedCards(rootView);
        initUnelectedCards(rootView);
    }

    private void initSelectedCards(View rootView) {
        RecyclerView rcvSelectedCards = rootView.findViewById(R.id.rcvCardEditorSelect);
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
    }

    private void initUnelectedCards(View rootView) {
        RecyclerView rcvSelectedCards = rootView.findViewById(R.id.rcvCardEditorUnselect);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rcvSelectedCards.setLayoutManager(layoutManager);
        CardUnselectedAdapter cardSelectedAdapter = new CardUnselectedAdapter(getActivity());
        cardSelectedAdapter.setData(CardsTypeManager.getInstance().getUnselectCardList());
        rcvSelectedCards.setAdapter(cardSelectedAdapter);
        if (rcvSelectedCards.getItemDecorationCount() <= 0) {
            SimpleRcvDecoration divider = new SimpleRcvDecoration(40, layoutManager);
            rcvSelectedCards.addItemDecoration(divider);
        }
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
