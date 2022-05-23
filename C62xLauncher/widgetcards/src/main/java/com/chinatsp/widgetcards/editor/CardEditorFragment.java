package com.chinatsp.widgetcards.editor;

import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.widgetcards.BaseFragment;
import com.chinatsp.widgetcards.R;

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
    }

    private void initSelectedCards(View rootView) {
        RecyclerView rcvSelectedCards = rootView.findViewById(R.id.rcvCardEditorSelect);
        rcvSelectedCards.setLayoutManager(new LinearLayoutManager(getActivity()));

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
