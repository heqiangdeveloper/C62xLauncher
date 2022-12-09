package com.chinatsp.widgetcards.home;

import card.base.LauncherCard;
import launcher.base.utils.flowcontrol.DebounceTask;

public class ExpandClickDebounceTask extends DebounceTask {
    private static final int MIN_CLICK_INTERVAL = 600; // ms

    private ExpandClickDebounceTask() {
        super(MIN_CLICK_INTERVAL);
        setTag("ExpandClickDebounceTask");
    }

    private static class Holder {
        private static ExpandClickDebounceTask instance = new ExpandClickDebounceTask();
    }

    public static ExpandClickDebounceTask getInstance() {
        return Holder.instance;
    }
    private LauncherCard mCard;
    private boolean mCardInLeftSide;
    @Override
    public void execute() {
        ExpandStateManager.getInstance().clickExpandButton(mCard, mCardInLeftSide);
    }

    public void emit(LauncherCard card, boolean cardInLeftSide) {
        this.mCard = card;
        this.mCardInLeftSide = cardInLeftSide;
        emit();
    }
}
