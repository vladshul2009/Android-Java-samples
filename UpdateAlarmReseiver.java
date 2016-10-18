package com.flica.flightcrewdemo.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.flica.flightcrewdemo.constant.AppConstants;
import com.flica.flightcrewdemo.services.UploadService;
import com.flica.flightcrewdemo.utils.DayNightColorController;
import com.flica.flightcrewdemo.utils.PreferencesManager;
import com.flica.flightcrewdemo.utils.Settings;
import com.flica.flightcrewdemo.utils.Utils;
import com.orhanobut.hawk.Hawk;

/**
 * Created by yevgen on 28.09.16.
 */

public class UpdateAlarmReseiver extends BroadcastReceiver{
    public static final String ALARM_ACTION = "ALARM_ACTION";
    public static final String START_NIGHT_MODE_ACTION = "START_NIGHT_MODE_ACTION";
    public static final String START_DAY_MODE_ACTION = "START_DAY_MODE_ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ALARM_ACTION)){
            if(Hawk.contains(AppConstants.PREFERENCES_NAME)){
                String name  = Hawk.get(AppConstants.PREFERENCES_NAME, null);
                if(name!=null&&!name.equals("")){
                    Intent i = new Intent(context, UploadService.class);
                    i.putExtra("userName", name);
                    context.startService(i);
                    Utils.setAlarm(context);
                }
            }
        }else if(intent.getAction().equals(START_NIGHT_MODE_ACTION)){
            DayNightColorController.getInstance().refreshNightMode(true);
            Utils.startDayMode(context,false,getSettings());
        }else if(intent.getAction().equals(START_DAY_MODE_ACTION)){
            DayNightColorController.getInstance().refreshNightMode(false);
            Utils.startNightMode(context,false,getSettings());
        }

    }


    public Settings getSettings(){
        if(Hawk.contains("settings")){
            return Hawk.get("settings");
        }
        return new Settings();
    }

}
