package launcher.base.utils.flowcontrol;

import android.view.View;

import java.util.function.Consumer;

/**
 * 防抖 click listener
 */
public class StableOnClickListener implements View.OnClickListener {
    private static final long CLICK_COOL_DOWN_TIME = 500;
    private static final int CLICK_EVENT_CACHE_KEY = -545632215;
    private final Consumer<View> clickConsumer;
    private final long coolDownTime;

    public StableOnClickListener(Consumer<View> clickConsumer) {
        this(CLICK_COOL_DOWN_TIME, clickConsumer);
    }

    public StableOnClickListener(long coolDownTime, Consumer<View> clickConsumer) {
        this.coolDownTime = coolDownTime;
        this.clickConsumer = clickConsumer;
    }

    @Override
    public void onClick(View v) {
        Object lastObj = v.getTag(CLICK_EVENT_CACHE_KEY);
        if (lastObj instanceof Long) {
            long lastClickStamp = (long) lastObj;
            if (System.currentTimeMillis() - lastClickStamp < coolDownTime) return;
        }
        long clickTimeStamp = System.currentTimeMillis();
        v.setTag(CLICK_EVENT_CACHE_KEY, clickTimeStamp);
        if (clickConsumer != null) clickConsumer.accept(v);
    }
    
}
