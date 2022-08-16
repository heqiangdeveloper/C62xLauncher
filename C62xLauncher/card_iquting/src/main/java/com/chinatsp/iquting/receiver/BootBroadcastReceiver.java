package com.chinatsp.iquting.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chinatsp.iquting.event.BootEvent;

import org.greenrobot.eventbus.EventBus;

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Log.d("bootlog","onReceive BOOT_COMPLETED");
//            FlowPlayControl.getInstance().bindPlayService(context);
//            IqutingBindService.getInstance().bindPlayService(context);//注册爱趣听播放服务
//            IqutingBindService.getInstance().bindContentService(context);//注册爱趣听内容服务
            sendBoot();
        }
    }

    private void sendBoot(){
        try{
            Thread.sleep(1000);
            Log.d("bootlog","sendBoot");
            EventBus.getDefault().post(new BootEvent());
        }catch (Exception e){

        }
    }
}
