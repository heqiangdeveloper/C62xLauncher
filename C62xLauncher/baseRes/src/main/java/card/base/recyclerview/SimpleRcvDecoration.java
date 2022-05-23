package card.base.recyclerview;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleRcvDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "SimpleRcvDecoration";
    private int mItemSpace;

    private boolean isVertical = true;

    public SimpleRcvDecoration(int itemSpace, RecyclerView.LayoutManager layoutManager) {
        mItemSpace = itemSpace / 2;
        if (layoutManager instanceof LinearLayoutManager) {
            isVertical = ((LinearLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.VERTICAL;
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int childAdapterPosition = parent.getChildAdapterPosition(view);
        if (parent.getAdapter() != null) {
            int count = parent.getAdapter().getItemCount();
            dealTopAndBottom(outRect, childAdapterPosition, count);
            dealLeftAndRight(outRect, childAdapterPosition, count);
        }

    }


    private void dealTopAndBottom(Rect outRect, int childAdapterPosition, int total) {
        if (isVertical) {
            outRect.top = 0;
            if (childAdapterPosition == total - 1) {
                outRect.bottom = 0;
            } else {
                outRect.bottom = mItemSpace;
            }
        }
    }

    private void dealLeftAndRight(Rect outRect, int childAdapterPosition, int total) {
        if (!isVertical) {
            if (childAdapterPosition == 0) {
                outRect.left = 0;
                outRect.right = mItemSpace;
            } else if (childAdapterPosition == total - 1) {
                outRect.left = mItemSpace;
                outRect.right = 0;
            } else {
                outRect.left = mItemSpace;
                outRect.right = mItemSpace;
            }
        }
    }

}
