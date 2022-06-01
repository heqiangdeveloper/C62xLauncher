package launcher.base.utils.view;

import android.view.View;
import android.widget.TextView;

public class LayoutParamUtil {
    public static void setWidth(int width, View... views) {
        for (View view : views) {
            view.getLayoutParams().width = width;
        }
    }
    public static void setHeight(int height, View... views) {
        for (View view : views) {
            view.getLayoutParams().height = height;
        }
    }
}
