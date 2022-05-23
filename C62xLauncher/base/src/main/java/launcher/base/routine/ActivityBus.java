package launcher.base.routine;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ActivityBus {
    private Context context;
    private Intent intent;
    public static ActivityBus newInstance(Context context) {
        return ActivityBus.newInstance(context, new Intent());
    }
    public static ActivityBus newInstance(Context context,Intent intent) {
        if (context == null ) {
            throw new IllegalArgumentException("context is null");
        }
        if (intent == null) {
            throw new IllegalArgumentException("intent is null");
        }
        ActivityBus bus = new ActivityBus();
        bus.context = context;
        bus.intent = intent;
        return bus;
    }

    public ActivityBus withAction(String action) {
        intent.setAction(action);
        return this;
    }
    public ActivityBus addCategory(String category) {
        intent.addCategory(category);
        return this;
    }
    public ActivityBus addFlag(int flag) {
        intent.addFlags(flag);
        return this;
    }

    public ActivityBus withClass(Class<?> activityClass) {
        intent.setComponent(new ComponentName(context, activityClass));
        return this;
    }


    public ActivityBus withClass(String className) {
        try {
            Class<?> clz = Class.forName(className);
            intent.setComponent(new ComponentName(context, clz));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return this;
    }

    public ActivityBus addExtra(String key, String value) {
        intent.putExtra(key, value);
        return this;
    }
    public void go() {
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(context,"无法启动, 应用可能未安装",Toast.LENGTH_SHORT).show();
        }
    }
    public void goWithNewTaskFlag() {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(context,"无法启动, 应用可能未安装",Toast.LENGTH_SHORT).show();
        }
    }
}
