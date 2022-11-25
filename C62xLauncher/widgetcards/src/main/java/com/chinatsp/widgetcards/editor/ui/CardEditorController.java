package com.chinatsp.widgetcards.editor.ui;

import com.chinatsp.widgetcards.manager.CardManager;

import java.util.LinkedList;
import java.util.List;

import card.base.LauncherCard;
import launcher.base.utils.EasyLog;
import launcher.base.utils.collection.IndexCheck;
import launcher.base.utils.collection.ListKit;

public class CardEditorController {
    private final String TAG = "CardEditorController";
    private EditorContract mEditorContract;
    private List<LauncherCard> mHomeList = new LinkedList<>();
    private List<LauncherCard> mUnselectList = new LinkedList<>();

    public CardEditorController(EditorContract editorContract) {
        mEditorContract = editorContract;
        init();
    }

    void init() {
        CardManager cardManager = CardManager.getInstance();
        mHomeList.clear();
        mUnselectList.clear();
        mHomeList.addAll(cardManager.getHomeList());

        List<LauncherCard> unselectCardList = cardManager.getUnselectCardList();
        for (LauncherCard card : unselectCardList) {
            if (card != null && card.getType() != CardManager.CardType.EMPTY) {
                mUnselectList.add(card);
            }
        }
    }

    void swipeHomeItem(int p1, int p2) {
        if (IndexCheck.indexOutOfArray(mHomeList, p1) || IndexCheck.indexOutOfArray(mHomeList, p2)) {
            return;
        }
        ListKit.swipeElement(mHomeList, p1, p2);
        mEditorContract.notifyHomeCardChange();
    }

    void swipeHomeAndUnselect(int positionInHome, int positionInUnselect) {
        EasyLog.d(TAG, "swipeHomeAndUnselect:" + positionInHome + " , " + positionInUnselect);
        if (IndexCheck.indexOutOfArray(mHomeList, positionInHome) || IndexCheck.indexOutOfArray(mUnselectList, positionInUnselect)) {
            return;
        }

        ListKit.swipeElement(mHomeList, mUnselectList, positionInHome, positionInUnselect);
        mEditorContract.notifyTotalCardChange();
    }

    /**
     * 检测卡片列表是否有改动.
     */
    boolean checkChanged() {
        CardManager cardManager = CardManager.getInstance();
        List<LauncherCard> originHomeList = cardManager.getHomeList();
        boolean equalHome = ListKit.equal(mHomeList, originHomeList);
        return !equalHome;
    }
    /**
     * 提交编辑, 使编辑生效.
     */
    void commitEdit(){
        CardManager.getInstance().resetCards(mHomeList, mUnselectList);
    }

    void onDestroy() {
        mEditorContract = null;
    }

    public List<LauncherCard> getHomeList() {
        return mHomeList;
    }

    public List<LauncherCard> getUnselectCardList() {
        return mUnselectList;
    }
}
