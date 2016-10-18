package com.flica.flightcrewdemo.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.flica.flightcrewdemo.constant.AppConstants;
import com.flica.flightcrewdemo.utils.Settings;
import com.flica.flightcrewdemo.utils.Utils;
import com.orhanobut.hawk.Hawk;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by yevgen on 30.09.16.
 */

public class BootStartReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context ctx,Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Utils.setAlarm(ctx);
            Utils.setNightDayMode(ctx,getSettings());
        }
    }

    public Settings getSettings(){
        if(Hawk.contains("settings")){
            return Hawk.get("settings");
        }
        return null;
    }

}
