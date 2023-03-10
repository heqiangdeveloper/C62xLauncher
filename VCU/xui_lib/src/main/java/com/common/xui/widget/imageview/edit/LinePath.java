package com.common.xui.widget.imageview.edit;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * 画笔路线
 */
class LinePath {
    private final Paint mDrawPaint;
    private final Path mDrawPath;

    LinePath(final Path drawPath, final Paint drawPaints) {
        mDrawPaint = new Paint(drawPaints);
        mDrawPath = new Path(drawPath);
    }

    Paint getDrawPaint() {
        return mDrawPaint;
    }

    Path getDrawPath() {
        return mDrawPath;
    }
}