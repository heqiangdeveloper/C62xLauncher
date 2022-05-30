package com.common.xui.widget.button.shinebutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.common.xui.R;
import com.common.xui.utils.ResUtils;

/**
 * 图标资源控件
 */
public class PorterShapeImageView extends PorterImageView {

    private Drawable mIconDrawable;
    private Matrix mMatrix;
    private Matrix mDrawMatrix;

    public PorterShapeImageView(Context context) {
        this(context, null);
    }

    public PorterShapeImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PorterShapeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs, defStyleAttr);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PorterShapeImageView, defStyleAttr, 0);
            mIconDrawable = ResUtils.getDrawableAttrRes(getContext(), array, R.styleable.PorterShapeImageView_sb_icon_image);
            array.recycle();
        }
        mMatrix = new Matrix();
    }

    /**
     * 设置图标资源
     *
     * @param drawable
     */
    public void setIconDrawable(Drawable drawable) {
        mIconDrawable = drawable;
        invalidate();
    }

    @Override
    protected void paintMaskCanvas(Canvas maskCanvas, Paint maskPaint, int width, int height) {
        if (mIconDrawable != null) {
            if (mIconDrawable instanceof BitmapDrawable) {
                configureBitmapBounds(getWidth(), getHeight());
                if (mDrawMatrix != null) {
                    int drawableSaveCount = maskCanvas.getSaveCount();
                    maskCanvas.save();
                    maskCanvas.concat(mMatrix);
                    mIconDrawable.draw(maskCanvas);
                    maskCanvas.restoreToCount(drawableSaveCount);
                    return;
                }
            }

            mIconDrawable.setBounds(0, 0, getWidth(), getHeight());
            mIconDrawable.draw(maskCanvas);
        }
    }

    private void configureBitmapBounds(int viewWidth, int viewHeight) {
        mDrawMatrix = null;
        int drawableWidth = mIconDrawable.getIntrinsicWidth();
        int drawableHeight = mIconDrawable.getIntrinsicHeight();
        boolean fits = viewWidth == drawableWidth && viewHeight == drawableHeight;

        if (drawableWidth > 0 && drawableHeight > 0 && !fits) {
            mIconDrawable.setBounds(0, 0, drawableWidth, drawableHeight);
            float widthRatio = (float) viewWidth / (float) drawableWidth;
            float heightRatio = (float) viewHeight / (float) drawableHeight;
            float scale = Math.min(widthRatio, heightRatio);
            float dx = (int) ((viewWidth - drawableWidth * scale) * 0.5f + 0.5f);
            float dy = (int) ((viewHeight - drawableHeight * scale) * 0.5f + 0.5f);

            mMatrix.setScale(scale, scale);
            mMatrix.postTranslate(dx, dy);
        }
    }
}
