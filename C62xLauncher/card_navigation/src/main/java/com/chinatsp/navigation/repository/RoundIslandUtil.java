package com.chinatsp.navigation.repository;

import android.util.Log;

import com.chinatsp.navigation.R;
import com.chinatsp.navigation.repository.DriveDirection;

public class RoundIslandUtil {

    /**
     *
     * @param value 转向图标的值. 对应高德转向图标信息.
     * @return 是否环岛信息
     */
    public static boolean isRoundIsland(int value) {
        // 常量值请参阅文档: 高德-转向图标信息
        return value == 11 || value == 12 || value == 17 || value == 18;
    }

    /**
     * @param value 转向图标的值. 对应高德转向图标信息.
     * @return 环岛是否左侧通行, 即是否顺时针通行
     */
    public static boolean isRoundByClockWise(int value) {
        return value == 17 || value == 18;
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
        return drawables[index];
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
        return drawables[index];
    }

}
