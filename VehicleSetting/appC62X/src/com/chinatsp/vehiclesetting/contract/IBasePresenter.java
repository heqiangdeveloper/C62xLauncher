package com.chinatsp.vehiclesetting.contract;

public interface IBasePresenter<T extends IBaseView> {
    /**
     * 绑定界面，初始化后绑定
     * @param view
     */
    void attachView(T view);

    /**
     * 解绑界面，界面销毁后解绑
     */
    void detachView();

    /**
     * 判断当前界面是否绑定
     * @return
     */
    boolean isViewAttached();
}
