package com.chinatsp.widgetcards;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import androidx.annotation.Nullable;

import com.chinatsp.widgetcards.editor.ui.CardEditorActivity;
import com.chinatsp.widgetcards.home.AppLauncherUtil;

import java.util.List;

import launcher.base.routine.ActivityBus;
import launcher.base.utils.EasyLog;

public class CardIntentService extends IntentService {
    private static final String TAG = "CardIntentService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public CardIntentService(String name) {
        super(name);
    }

    public CardIntentService() {
        this(TAG);
    }

    private static final String OP_KEY = "OP_KEY";
    public static final int OP_VALUE_START_CARD_EDIT = 1;
    public static final int OP_VALUE_START_CARD_APP = 2;

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            int opCodeValue = intent.getIntExtra(OP_KEY, 0);
            dealOperation(opCodeValue, intent);
        }
    }

    private void dealOperation(int opCode, Intent intent) {
        EasyLog.d(TAG, "dealOperation opCode:" + opCode);
        if (opCode == OP_VALUE_START_CARD_EDIT) {
            ActivityBus.newInstance(this)
                    .withClass(CardEditorActivity.class)
                    .goWithNewTaskFlag();
        }
    }

    /**
     * @param opCode 1: 启动卡片编辑模式
     */
    public static void start(Context context, int opCode) {
        Intent intent = new Intent();
        String commandAction = "com.chinatsp.launcher.cardCommandService";
        String opKey = "OP_KEY";
        intent.setPackage("com.chinatsp.launcher");
        intent.setAction(commandAction);
        intent.putExtra(opKey, opCode);
        context.startService(intent);
    }

}
