package launcher.base.utils.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

/*
* 第三方应用图标切割成圆角
 */
public class RoundCornerImage {
    public static void crop(Context context, Drawable drawable, ImageView iv){
        RequestBuilder<Drawable> requestBuilder = Glide.with(context)
                .load(drawable)
                //.transition(DrawableTransitionOptions.withCrossFade())
                .apply(new GlideCommonRequestOption());
        //指定弧度
        requestBuilder = requestBuilder.transform(
                new RoundBitmapTransformation(10, true));
        requestBuilder.into(iv);
    }
}
