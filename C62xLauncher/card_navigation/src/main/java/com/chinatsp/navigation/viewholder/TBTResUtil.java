package com.chinatsp.navigation.viewholder;

import com.chinatsp.navigation.R;

public class TBTResUtil {
    public static int getCurrentDirection(int direction) {
        return R.drawable.card_navi_tbt_direct_right;
    }
    public static int getIsland(int exitNumber, boolean clockWise) {
        if (clockWise) {
            return getIslandClockWise(exitNumber);
        } else {
            return getIslandCounterClockWise(exitNumber);
        }
    }

    private static int getIslandCounterClockWise(int exitNumber) {
        int[] drawables = new int[]{
                R.drawable.card_navi_tbt_island_counter_clock_1,
                R.drawable.card_navi_tbt_island_counter_clock_2,
                R.drawable.card_navi_tbt_island_counter_clock_3,
                R.drawable.card_navi_tbt_island_counter_clock_4,
                R.drawable.card_navi_tbt_island_counter_clock_5,
                R.drawable.card_navi_tbt_island_counter_clock_6,
                R.drawable.card_navi_tbt_island_counter_clock_7,
                R.drawable.card_navi_tbt_island_counter_clock_8,
                R.drawable.card_navi_tbt_island_counter_clock_9,
                R.drawable.card_navi_tbt_island_counter_clock_10,
        };
        int index = exitNumber - 1;
        if (index < 0 || index > drawables.length - 1) {
            return drawables[0];
        }
        return drawables[exitNumber];
    }

    private static int getIslandClockWise(int exitNumber) {
        int[] drawables = new int[]{
                R.drawable.card_navi_tbt_island_clock_1,
                R.drawable.card_navi_tbt_island_clock_2,
                R.drawable.card_navi_tbt_island_clock_3,
                R.drawable.card_navi_tbt_island_clock_4,
                R.drawable.card_navi_tbt_island_clock_5,
                R.drawable.card_navi_tbt_island_clock_6,
                R.drawable.card_navi_tbt_island_clock_7,
                R.drawable.card_navi_tbt_island_clock_8,
                R.drawable.card_navi_tbt_island_clock_9,
                R.drawable.card_navi_tbt_island_clock_10,
        };
        int index = exitNumber - 1;
        if (index < 0 || index > drawables.length - 1) {
            return drawables[0];
        }
        return drawables[exitNumber];
    }

}
