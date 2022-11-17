package launcher.base.utils.glide;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class GlideHelper {
    private static final String TAG = "GlideHelper";
    private static final int COVER_SIZE = 196;
    public static void loadLocalAlbumCoverRadius(Context context, ImageView imageView,
                                              int res,int radius) {
        RequestBuilder<Drawable> requestBuilder = Glide.with(context)
                .load(res)
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(COVER_SIZE, COVER_SIZE)
                .apply(new GlideCommonRequestOption());
        if (radius > 0) {
            requestBuilder = requestBuilder.transform(
                    new RoundBitmapTransformation(radius, radius, radius, radius));
        }
        requestBuilder.into(imageView);
    }

    public static void loadUrlAlbumCoverRadius(Context context, ImageView imageView,
                                         String res,int radius) {
        //如果已经destroy就不往下进行
        if(isDestroy((Activity) context)){
            Log.d(TAG,"Activity is destroyed");
            return;
        }
        if(TextUtils.isEmpty(res)){
            Log.d(TAG,"url is empty");
            return;
        }
        RequestBuilder<Drawable> requestBuilder = Glide.with(context)
                .load(res)
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(COVER_SIZE, COVER_SIZE)
                .apply(new GlideCommonRequestOption());
        if (radius > 0) {
            requestBuilder = requestBuilder.transform(
                    new RoundBitmapTransformation(radius, radius, radius, radius));
        }
        requestBuilder.into(imageView);
    }

    public static void loadLocalCircleImage(Context context, ImageView imageView, int res) {
        RequestBuilder<Drawable> requestBuilder = Glide.with(context)
                .load(res)
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(COVER_SIZE, COVER_SIZE)
                .circleCrop()
                .apply(new GlideCommonRequestOption());
        requestBuilder.into(imageView);
    }


    public static void loadUrlImage(Context context, ImageView imageView, String res) {
        //如果已经destroy就不往下进行
        if(isDestroy((Activity) context)){
            Log.d(TAG,"Activity is destroyed");
            return;
        }
        if(TextUtils.isEmpty(res)){
            Log.d(TAG,"url is empty");
            return;
        }
        RequestBuilder<Drawable> requestBuilder = Glide.with(context)
                .load(res)
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(COVER_SIZE, COVER_SIZE)
                .circleCrop()
                .apply(new GlideCommonRequestOption());
        requestBuilder.into(imageView);
    }
    public static void loadUrlImage(Context context, ImageView imageView, String res, int width, int height, int radius) {
        //如果已经destroy就不往下进行
        if(isDestroy((Activity) context)){
            Log.d(TAG,"Activity is destroyed");
            return;
        }
        if(TextUtils.isEmpty(res)){
            Log.d(TAG,"url is empty");
            return;
        }
        RequestBuilder<Drawable> requestBuilder = Glide.with(context)
                .load(res)
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(width, height)
                .apply(new GlideCommonRequestOption());
        if (radius > 0) {
            requestBuilder = requestBuilder.transform(
                    new RoundBitmapTransformation(radius, true));
        }
        requestBuilder.into(imageView);
    }

    /**
     * 判断Activity是否Destroy
     * @param mActivity
     * @return
     */
    public static boolean isDestroy(Activity mActivity) {
        if (mActivity== null || mActivity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed())) {
            return true;
        } else {
            return false;
        }
    }
}
