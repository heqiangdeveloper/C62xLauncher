package com.chinatsp.widgetcards.editor.drag;

/**
 * 位于RecyclerView中的ItemView变化接口.
 * 注意 ItemView 本身永远在列表中,  而随着手指移动的View只是它的一个副本.
 */
public interface IDragItemView {
    /**
     * 当itemView被拖拽时, 它会被置位一个空位
     */
    void becomeEmpty();


    /**
     * 当itemView被恢复后, 它需要从空位恢复成到正常状态
     */
    void restore();
}
