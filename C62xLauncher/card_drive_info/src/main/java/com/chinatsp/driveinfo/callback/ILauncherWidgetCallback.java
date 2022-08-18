package com.chinatsp.driveinfo.callback;

public interface ILauncherWidgetCallback {
    void onHealthyLevelChanged(String healthLevel);

    void onMaintenanceMileageChanged(int mile);

    void onRankingChanged(int rank);
}
