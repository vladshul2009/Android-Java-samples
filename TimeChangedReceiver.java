package com.flica.flightcrewdemo.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.flica.flightcrewdemo.utils.Settings;
import com.flica.flightcrewdemo.utils.Utils;
import com.orhanobut.hawk.Hawk;

/**
 * Created by yevgen on 13.10.16.
 */

public class TimeChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
            Utils.setAlarm(context);
            Utils.setNightDayMode(context,getSettings());
    }

    public Settings getSettings(){
        if(Hawk.contains("settings")){
            return Hawk.get("settings");
        }
        return new Settings();
    }
}