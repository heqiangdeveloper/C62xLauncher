package com.chinatsp.widgetcards.editor.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import card.base.LauncherCard;
import card.views.dialog.CustomDialog;
import card.views.dialog.DialogMaster;
import launcher.base.component.BaseFragment;

import com.chinatsp.widgetcards.R;
import com.chinatsp.widgetcards.editor.adapter.CardSelectedAdapter;
import com.chinatsp.widgetcards.editor.adapter.CardUnselectedAdapter;
import com.chinatsp.widgetcards.editor.drag.DragHelper;
import com.chinatsp.widgetcards.editor.drag.EnableDragStrategyImp;
import com.chinatsp.widgetcards.editor.drag.IOnSwipeFinish;
import com.chinatsp.widgetcards.manager.CardManager;

import java.util.List;
import java.util.Objects;

import launcher.base.recyclerview.SimpleRcvDecoration;
import launcher.base.utils.EasyLog;
import launcher.base.utils.collection.IndexCheck;
import launcher.base.utils.collection.ListKit;
import launcher.base.utils.view.B561Toast;

public class CardEditorFragment extends BaseFragment implements EditorContract{

    private static final String TAG = "CardEditorFragment";
    private CardEditorController mController;
    private Button mBtnFinish;
    private Button mBtnCancel;
    private DragHelper mDragHelper;
    private RecyclerView rcvSelectedCards;
    private RecyclerView rcvUnelectedCards;

    @Override
    protected void initViews(@NonNull View rootView) {
        mController = new CardEditorController(this);
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
                    mController.swipeHomeItem(position1, position2);
                } else {
                    swipeHomeAndUnselect(recyclerView1, position1, recyclerView2, position2);
                }
            }
        });
    }

    private void swipeHomeAndUnselect(RecyclerView recyclerView1, int position1, RecyclerView recyclerView2, int position2) {
        EasyLog.d(TAG, "swipeHomeAndUnselect:" + position1+" , "+position2);
        int positionInHome = position1, positionInUnselect = position2;
        if (recyclerView1 == rcvUnelectedCards) {
            positionInHome = position2;
            positionInUnselect = position1;
        }
        mController.swipeHomeAndUnselect(positionInHome, positionInUnselect);
    }

    private void swipeHomeList(int position1, int position2) {
        EasyLog.d(TAG, "swipeHomeList:" + position1+" , "+position2);
        List<LauncherCard> homeList = CardManager.getInstance().getHomeList();
        if (IndexCheck.indexOutOfArray(homeList, position1) || IndexCheck.indexOutOfArray(homeList, position2)) {
            return;
        }
        ListKit.swipeElement(homeList, position1, position2);
    }

    private void initSelectedCards(@NonNull View rootView) {
        rcvSelectedCards = rootView.findViewById(R.id.rcvCardEditorSelect);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rcvSelectedCards.setLayoutManager(layoutManager);
        CardSelectedAdapter cardSelectedAdapter = new CardSelectedAdapter(getActivity());
        cardSelectedAdapter.setData(mController.getHomeList());
        rcvSelectedCards.setAdapter(cardSelectedAdapter);
        if (rcvSelectedCards.getItemDecorationCount() <= 0) {
            SimpleRcvDecoration divider = new SimpleRcvDecoration(36, layoutManager);
            rcvSelectedCards.addItemDecoration(divider);
        }
        mDragHelper.setRecyclerView1(rcvSelectedCards);
    }

    private void initUnelectedCards(@NonNull View rootView) {
        rcvUnelectedCards = rootView.findViewById(R.id.rcvCardEditorUnselect);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rcvUnelectedCards.setLayoutManager(layoutManager);
        CardUnselectedAdapter cardSelectedAdapter = new CardUnselectedAdapter(getActivity());
        cardSelectedAdapter.setData(mController.getUnselectCardList());
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
                okPage();
            } else if (mBtnCancel == v) {
                cancelPage();
            }
        }
    };

    private void okPage() {
        if (mController.checkChanged()) {
            mController.commitEdit();
        }
        Context applicationContext = getContext().getApplicationContext();
        B561Toast.showShort(applicationContext,R.string.card_edit_msg_ok);
        finishActivity();
    }

    private void cancelPage() {
        if (!mController.checkChanged()) {
            finishActivity();
            return;
        }
        DialogMaster dialogMaster = DialogMaster.create(getActivity(), new DialogMaster.OnPressOk() {
                    @Override
                    public void onPress(View v) {
                        okPage();
                    }
                }, new DialogMaster.OnPressCancel() {
                    @Override
                    public void onPress(View v) {
                        finishActivity();
                    }
                }, 740, 488
        );
        CustomDialog editDialog = dialogMaster.getDialog();
        editDialog.setBtnOkText(R.string.card_edit_btn_save);
        editDialog.setBtnCancelText(R.string.card_edit_btn_cancel);
        editDialog.setTitleIcon(R.drawable.card_icon_wifi_warning);
        editDialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void notifyHomeCardChange() {
        Objects.requireNonNull(rcvSelectedCards.getAdapter()).notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void notifyTotalCardChange() {
        Objects.requireNonNull(rcvSelectedCards.getAdapter()).notifyDataSetChanged();
        Objects.requireNonNull(rcvUnelectedCards.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mController.onDestroy();
    }
}
