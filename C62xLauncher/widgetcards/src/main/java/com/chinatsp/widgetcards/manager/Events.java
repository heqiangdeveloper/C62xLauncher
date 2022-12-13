package com.chinatsp.widgetcards.manager;

import android.view.View;

import card.base.LauncherCard;

/**
 * BusEvent 事件类.
 */
public class Events {
    public static class SwipeEvent {
        public LauncherCard mBigLauncherCard;
        public boolean mBigInLeftSide;
    }
    public static Events.SwipeEvent createSwipeEvent(LauncherCard cardEntity, View view) {
        Events.SwipeEvent swipeEvent = new Events.SwipeEvent();
        swipeEvent.mBigLauncherCard = cardEntity;
        swipeEvent.mBigInLeftSide = isCardInLeftSide(view);
        return swipeEvent;
    }

    private static boolean isCardInLeftSide(View view) {
        return view.getX() < 600;
    }
}
