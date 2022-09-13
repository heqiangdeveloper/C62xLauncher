package com.chinatsp.widgetcards.home.smallcard;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @param <T> 数据元素类型
 */
class ScrollCardHelper<T> {
    private final int mBigPosition;
    private int mSmallPosition;
    private boolean mSmallCardAtLeftSide;
    private List<T> mList = new LinkedList<>();
    private final int mSize;

    public ScrollCardHelper(int bigPosition, int smallPosition, List<T> list, boolean smallCardAtLeftSide) {
        mBigPosition = bigPosition;
        mSmallPosition = smallPosition;
        mSmallCardAtLeftSide = smallCardAtLeftSide;
        if (list != null) {
            mList.addAll(list);
        }
        mSize = mList.size();
    }

    void scrollLeft() {
        if (mSize == 0 || mSmallPosition >= mSize - 1) {
            return;
        }
        if (mSmallCardAtLeftSide) {
            if (mBigPosition < mSize - 1) {
                mSmallPosition += 2;
            }
        } else {
            if (mSmallPosition < mSize - 1) {
                mSmallPosition++;
            }
        }
    }

    void scrollRight() {
        if (mSize == 0 || mSmallPosition <= 0) {
            return;
        }
        if (mSmallCardAtLeftSide) {
            mSmallPosition--;
        } else {
            if (mBigPosition == 0) {
                return;
            } else {
                mSmallPosition -= 2;
            }
        }
    }

    List<T> getCurrentList() {
        return mList;
    }
}
