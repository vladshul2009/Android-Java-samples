package com.flica.flightcrewdemo.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.flica.flightcrewdemo.R;
import com.flica.flightcrewdemo.constant.AppConstants;
import com.flica.flightcrewdemo.gson.Schedule;
import com.flica.flightcrewdemo.gson.ShadulesMainObj;
import com.flica.flightcrewdemo.gson.TRIP;
import com.flica.flightcrewdemo.utils.Settings;
import com.flica.flightcrewdemo.utils.Utils;
import com.flica.flightcrewdemo.web.Api;
import com.flica.flightcrewdemo.web.RequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class UploadService extends Service{
//    String postId;
    NotificationManager notificationManager;
    String userName;
    public UploadService() {

    }


    public Settings getSettings(){
        if(Hawk.contains("settings")){
            return Hawk.get("settings");
        }
        return new Settings();
    }


    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
       // Log.d("startServices", "onStartCommand");
        //Toast.makeText(getBaseContext(), "Time is up!!!!.",Toast.LENGTH_LONG).show();
//        Vibrator vibrator = (Vibrator) getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);
//        vibrator.vibrate(2000);


        //bildNotificationLoad(getString(R.string.weather_updating));

        if(intent!=null&&intent.hasExtra("userName")&&intent.getStringExtra("userName")!=null){
        userName = intent.getStringExtra("userName");}
        uploadWeather(userName);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    ShadulesMainObj obj;
    private void uploadWeather(final String userName){
        if(Hawk.contains(AppConstants.PREFERENCES_JSON)){
            obj = Hawk.get(AppConstants.PREFERENCES_JSON);
            if(obj.getSchedules()!=null&&obj.getSchedules().size()>0){
             Api.updateJsonFromWether(AppConstants.HOME_ERROR,getSettings(), userName, new Gson().toJson(obj.getSchedules()), null,
                                    new RequestListener<String>() {
                                        @Override
                                        public void onSuccess(String response) {
                                            if(response!=null){
                                            List<Schedule> wetherSchedules = new Gson().fromJson(response, new TypeToken<ArrayList<Schedule>>(){}.getType());
                                            if(Hawk.contains(AppConstants.PREFERENCES_JSON)){
                                                obj = Utils.updateWetherVSHotel(obj,wetherSchedules);
                                                Hawk.put(AppConstants.PREFERENCES_JSON,obj);
                                                EventBus.getDefault().post(obj);

                                               // bildNotificationLoad(getString(R.string.weather_updated));
                                               // stopSelf();
                                            }}
                            }});
    }}}







    public void bildNotificationLoad(String title) {
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.airplane);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        if(title.contains(getString(R.string.weather_updating))) {
            builder.setContentIntent(null).setColor(Color.parseColor("#4cc1d2"))
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(getNotificationIcon())
                    .setContentTitle(title)
                    .setProgress(0, 0, true);
        } else {
            builder.setContentIntent(null)
                    .setColor(Color.parseColor("#4cc1d2"))
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(getNotificationIcon())
                    .setContentTitle(title);
        }


        Notification notification = builder.build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        builder.setAutoCancel(true);
        notificationManager.notify(1, notification);
    }






    private int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ?   R.drawable.airplane :R.drawable.airplane;
    }
//


//
//    public static String getMimeType(String url) {
//        String type = null;
//        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
//        if (extension != null) {
//            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//        }
//        return type;
//    }

//
//

//
//    public void uploadVideo(String s){
//        RetroCallback<JobResponse> retroCallback = new RetroCallback<JobResponse>(this);
//        String auth = Hawk.get("cookie_name", "")+"="+ Hawk.get("cookie","");
//        retroCallback.setRequestId(HttpConstants.ApiResponseCodes.UPLOAD_VIDEO);
//        //     TypedFile typedFile = new TypedFile("multipart/form-data", new File(s));
//        if (s==null) {
//            Toast.makeText(this, "Sorry, Problem retrieving video.", Toast.LENGTH_SHORT).show();
//            Crashlytics.log("Problem retreiving video, s is null");
//            Crashlytics.logException(new NullPointerException("Problem retreiving video, s is null"));
//            return;
//        }
//        String mimeType = getMimeType(s);
//        if (mimeType == null) {
//            Toast.makeText(this, "Sorry, Problem retrieving video.", Toast.LENGTH_SHORT).show();
//            Crashlytics.log("Problem retreiving video, mimeType is null s: " + s);
//            Crashlytics.logException(new NullPointerException("Problem retreiving video, mimeType is null s: " + s));
//            return;
//        }
//        TypedFile typedFile = new TypedFile(mimeType, new File(s));
////        Map<String, String> params = new HashMap<String, String>();
////        params.put("postid",  postId);
//        mAppApi.uploadVideo(auth, "video" + Utils.getRandomString() + ".mp4", typedFile, retroCallback);
//    }
//
//    @Override
//    public void success(Object model, int requestId) {
//        bildNotificationLoad(getString(R.string.video_upload));
//        stopSelf();
//    }
//
//    @Override
//    public void failure(int requestId, int errorCode, String message, RetrofitError error) {
//        bildNotificationLoad(getString(R.string.something_went_wrong));
//        stopSelf();
//    }
}
