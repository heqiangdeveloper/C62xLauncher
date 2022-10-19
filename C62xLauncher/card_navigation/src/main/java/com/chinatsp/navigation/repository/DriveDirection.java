package com.chinatsp.navigation.repository;

import com.chinatsp.navigation.R;

public class DriveDirection {
    private int nameRes;
    private int iconRes;
    private int garDeValue;

    private boolean useRoundIsland;

    public DriveDirection(int nameRes, int iconRes, int garDeValue) {
        this.nameRes = nameRes;
        this.iconRes = iconRes;
        this.garDeValue = garDeValue;
    }

    public int getNameRes() {
        return nameRes;
    }

    public int getIconRes() {
        return iconRes;
    }

    public int getGarDeValue() {
        return garDeValue;
    }

    public boolean isUseRoundIsland() {
        return useRoundIsland;
    }

    @Override
    public String toString() {
        return "DriveDirection{" +
                "nameRes=" + nameRes +
                ", iconRes=" + iconRes +
                ", garDeValue=" + garDeValue +
                '}';
    }

    public static DriveDirection parseFromType(int value) {
        DriveDirection driveDirection = null;
        switch (value) {
            case 1:
                driveDirection = new DriveDirection(R.string.tbt_direct_my_auto, -1, value);
                break;
            case 2:
                driveDirection = new DriveDirection(R.string.tbt_direct_turn_left, R.drawable.tbt_direction_turn_left, value);
                break;
            case 3:
                driveDirection = new DriveDirection(R.string.tbt_direct_turn_right, R.drawable.tbt_direction_turn_right, value);
                break;
            case 4:
                driveDirection = new DriveDirection(R.string.tbt_direct_left_forward, R.drawable.tbt_direct_left_forward, value);
                break;
            case 5:
                driveDirection = new DriveDirection(R.string.tbt_direct_right_forward, R.drawable.tbt_direct_right_forward, value);
                break;
            case 6:
                driveDirection = new DriveDirection(R.string.tbt_direct_left_backward, R.drawable.tbt_direct_left_backward, value);
                break;
            case 7:
                driveDirection = new DriveDirection(R.string.tbt_direct_right_backward, R.drawable.tbt_direct_right_backward, value);
                break;
            case 8:
                driveDirection = new DriveDirection(R.string.tbt_direct_left_u_turn, R.drawable.tbt_direct_left_u_turn, value);
                break;
            case 9:
                driveDirection = new DriveDirection(R.string.tbt_direct_straight_ahead, R.drawable.tbt_direct_straight_ahead, value);
                break;
            case 10:
                driveDirection = new DriveDirection(R.string.tbt_direct_arrive_poi, R.drawable.tbt_direct_arrive_poi, value);
                break;
            case 11:
                driveDirection = new DriveDirection(R.string.tbt_direct_enter_island_on_r, R.drawable.tbt_direct_enter_island_on_r, value);
                break;
            case 12:
                driveDirection = new DriveDirection(R.string.tbt_direct_exit_island_on_r, R.drawable.tbt_direct_straight_island_on_r, value);
                break;
            case 13:
                driveDirection = new DriveDirection(R.string.tbt_direct_arrive_sapa, R.drawable.tbt_direct_arrive_sapa, value);
                break;
            case 14:
                driveDirection = new DriveDirection(R.string.tbt_direct_arrive_toll, R.drawable.tbt_direct_arrive_toll, value);
                break;
            case 15:
                driveDirection = new DriveDirection(R.string.tbt_direct_arrive_end, R.drawable.tbt_direct_arrive_end, value);
                break;
            case 16:
                driveDirection = new DriveDirection(R.string.tbt_direct_enter_tunnel, R.drawable.tbt_direct_enter_tunnel, value);
                break;
            case 17:
                driveDirection = new DriveDirection(R.string.tbt_direct_enter_island_on_l, R.drawable.tbt_direct_enter_island_on_l, value);
                break;
            case 18:
                driveDirection = new DriveDirection(R.string.tbt_direct_exit_island_on_l, R.drawable.tbt_direct_straight_island_on_l, value);
                break;
            case 19:
                driveDirection = new DriveDirection(R.string.tbt_direct_right_u_turn, R.drawable.tbt_direct_right_u_turn, value);
                break;
            case 20:
                driveDirection = new DriveDirection(R.string.tbt_direct_long_straight, R.drawable.tbt_direct_long_straight, value);
                break;
            case 21:
                driveDirection = new DriveDirection(R.string.tbt_direct_turn_left_island_on_r, R.drawable.tbt_direct_turn_left_island_on_r, value);
                break;
            case 22:
                driveDirection = new DriveDirection(R.string.tbt_direct_turn_right_island_on_r, R.drawable.tbt_direct_turn_right_island_on_r, value);
                break;
            case 23:
                driveDirection = new DriveDirection(R.string.tbt_direct_straight_island_on_r, R.drawable.tbt_direct_straight_island_on_r, value);
                break;
            case 24:
                driveDirection = new DriveDirection(R.string.tbt_direct_u_turn_island_on_r, R.drawable.tbt_direct_u_turn_island_on_r, value);
                break;
            case 25:
                driveDirection = new DriveDirection(R.string.tbt_direct_turn_left_island_on_l, R.drawable.tbt_direct_turn_left_island_on_l, value);
                break;
            case 26:
                driveDirection = new DriveDirection(R.string.tbt_direct_turn_right_island_on_l, R.drawable.tbt_direct_turn_right_island_on_l, value);
                break;
            case 27:
                driveDirection = new DriveDirection(R.string.tbt_direct_straight_island_on_l, R.drawable.tbt_direct_straight_island_on_l, value);
                break;
            case 28:
                driveDirection = new DriveDirection(R.string.tbt_direct_u_turn_island_on_l, R.drawable.tbt_direct_u_turn_island_on_l, value);
                break;
            case 65:
                driveDirection = new DriveDirection(R.string.tbt_direct_keep_left, R.drawable.tbt_direct_straight_ahead, value);
                break;
            case 66:
                driveDirection = new DriveDirection(R.string.tbt_direct_keep_right, R.drawable.tbt_direct_straight_ahead, value);
                break;
        }
        if (driveDirection != null) {
            driveDirection.useRoundIsland = (value == 11 || value == 12 || value == 17 || value == 18);
        }
        return driveDirection;
    }
}
