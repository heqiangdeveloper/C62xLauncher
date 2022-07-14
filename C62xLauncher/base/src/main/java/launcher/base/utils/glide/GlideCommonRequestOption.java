package launcher.base.utils.glide;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;

public class GlideCommonRequestOption extends RequestOptions {
    @NonNull
    @Override
    public RequestOptions skipMemoryCache(boolean skip) {
        return super.skipMemoryCache(true);
    }

    @NonNull
    @Override
    public RequestOptions format(@NonNull DecodeFormat format) {
        return super.format(DecodeFormat.PREFER_RGB_565);
    }

    @NonNull
    @Override
    public RequestOptions encodeQuality(int quality) {
        return super.encodeQuality(50);
    }

    @NonNull
    @Override
    public RequestOptions encodeFormat(@NonNull Bitmap.CompressFormat format) {
        return super.encodeFormat(Bitmap.CompressFormat.WEBP);
    }
}
