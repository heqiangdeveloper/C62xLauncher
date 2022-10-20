package launcher.base.recyclerview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleRcvDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "SimpleRcvDecoration";
    private int mItemSpace;

    private boolean isVertical = true;
    private final Rect mBounds = new Rect();

    private Drawable mDrawable;

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

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (mDrawable != null) {
            if (isVertical) {
                drawVertical(c, parent);
            } else {
                drawHorizontal(c, parent);
            }
        }

    }

    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int left;
        final int right;
        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            final int bottom = mBounds.bottom + Math.round(child.getTranslationY());
            final int top = bottom - mDrawable.getIntrinsicHeight();
            mDrawable.setBounds(left, top, right, bottom);
            mDrawable.draw(canvas);
        }
        canvas.restore();
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int top;
        final int bottom;
        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top,
                    parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = 0;
            bottom = parent.getHeight();
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            parent.getLayoutManager().getDecoratedBoundsWithMargins(child, mBounds);
            final int right = mBounds.right + Math.round(child.getTranslationX());
            final int left = right - mDrawable.getIntrinsicWidth();
            mDrawable.setBounds(left, top, right, bottom);
            mDrawable.draw(canvas);
        }
        canvas.restore();
    }
}
