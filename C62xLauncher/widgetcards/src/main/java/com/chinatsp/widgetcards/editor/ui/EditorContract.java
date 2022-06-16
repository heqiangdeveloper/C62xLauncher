package com.chinatsp.widgetcards.editor.ui;

public interface EditorContract {
    /**
     * 通知编辑页内的首页卡片变化
     */
    void notifyHomeCardChange();

    /**
     * 通知编辑页内的首页和未选卡片变化
     */
    void notifyTotalCardChange();


}
