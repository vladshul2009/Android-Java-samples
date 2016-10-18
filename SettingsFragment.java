package com.flica.flightcrewdemo.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.flica.flightcrewdemo.R;
import com.flica.flightcrewdemo.activity.MainActivity;
import com.flica.flightcrewdemo.constant.AppConstants;
import com.flica.flightcrewdemo.events.AirportEvent;
import com.flica.flightcrewdemo.events.BackPressedEvent;
import com.flica.flightcrewdemo.events.LoginSettingEvent;
import com.flica.flightcrewdemo.gson.Schedule;
import com.flica.flightcrewdemo.gson.ShadulesMainObj;
import com.flica.flightcrewdemo.gson.TRIP;
import com.flica.flightcrewdemo.utils.DayNightColorController;
import com.flica.flightcrewdemo.utils.Settings;
import com.flica.flightcrewdemo.utils.Utils;
import com.flica.flightcrewdemo.views.SettingsViewPager;
import com.flica.flightcrewdemo.web.Api;
import com.flica.flightcrewdemo.web.RequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;
import com.zenkun.datetimepicker.time.RadialPickerLayout;
import com.zenkun.datetimepicker.time.TimePickerDialog;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by yevgen on 20.09.16.
 */
public class SettingsFragment extends BaseFragment {




    SettingsViewPager id_settings_pager;
    boolean login;

    public void setIdSettingsPager(SettingsViewPager id_settings_pager) {
        this.id_settings_pager = id_settings_pager;
    }


    public static SettingsFragment newInstance(SettingsViewPager id_settings_pager, boolean login){

        SettingsFragment settingsFragment = new SettingsFragment();
        settingsFragment.setIdSettingsPager(id_settings_pager);
        Bundle bundle =new Bundle();
        bundle.putBoolean("login",login);
        settingsFragment.setArguments(bundle);
        return settingsFragment;
    }


    private Settings settings;
    @BindView(R.id.scrollView1)
    ScrollView scrollView1;

//
//    @BindView(R.id.id_pull_refresh)
//    MultiStateToggleButton id_pull_refresh;



    @BindView(R.id.myProgressBarWrapper)
    RelativeLayout myProgressBarWrapper;






    @BindView(R.id.id_pilot_flight_attendant)
    MultiStateToggleButton id_pilot_flight_attendant;

    @BindView(R.id.id_temperature)
    MultiStateToggleButton id_temperature;

    @BindView(R.id.id_show_us_or_canadian)
    MultiStateToggleButton id_show_us_or_canadian;

    @BindView(R.id.id_show_pull_to_refresh)
    MultiStateToggleButton id_show_pull_to_refresh;



    @BindView(R.id.id_activity_airport_txt)
    TextView id_activity_airport_txt;
    @BindView(R.id.id_home_airport_txt)
    TextView id_home_airport_txt;


    @BindView(R.id.id_time_for_report_txt)
    TextView id_time_for_report_txt;





    private int home_activity;
    private static final int home = 0;
    private static final int activity = 1;
    @OnClick(R.id.id_home_airport_txt)
    public void onHomeAirport(View view) {
        home_activity = home;
        id_settings_pager.setCurrentItem(1,false);
    }
    @OnClick(R.id.id_home_airport_img)
    public void onHomeAirportImg(View view) {
        home_activity = home;
        id_settings_pager.setCurrentItem(1,false);
    }



    @OnClick(R.id.id_activity_airport_txt)
    public void onActivityAirport(View view) {
        home_activity = activity;
        id_settings_pager.setCurrentItem(1,false);
    }
    @OnClick(R.id.id_activity_airport_img)
    public void onActivityAirportImg(View view) {
        home_activity = activity;
        id_settings_pager.setCurrentItem(1,false);
    }








    ShadulesMainObj obj;
    public void onSaveAll() {
        if(isValide()){
            Hawk.put("settings",settings);
            Utils.setNightDayMode(getActivity(),settings);
            if(login){
                EventBus.getDefault().post(new LoginSettingEvent());
                EventBus.getDefault().post(new BackPressedEvent());
                getFragmentManager().popBackStack();
            }else {
                if(settings.isChanged()&&Hawk.contains(AppConstants.PREFERENCES_NAME)&&Hawk.contains(AppConstants.PREFERENCES_JSON)){
                    String userName = Hawk.get(AppConstants.PREFERENCES_NAME);
                    myProgressBarWrapper.setVisibility(View.VISIBLE);
                    obj = Hawk.get(AppConstants.PREFERENCES_JSON);
                    if(((MainActivity)getActivity()).checkInternetConnection()){
                    Api.updateJsonFromWether(AppConstants.HOME_ERROR,settings, userName, new Gson().toJson(obj.getSchedules()), null,
                            new RequestListener<String>() {
                                @Override
                                public void onSuccess(String response) {
                                    myProgressBarWrapper.setVisibility(View.GONE);
                                    List<Schedule> wetherSchedules = new Gson().fromJson(response, new TypeToken<ArrayList<Schedule>>(){}.getType());
                                    Hawk.put(AppConstants.PREFERENCES_WHETHER,wetherSchedules);

                                    obj = Utils.updateWetherVSHotel(obj,wetherSchedules);

                                    settings.setChanged(false);

                                    Hawk.put("settings",settings);
                                    Utils.setNightDayMode(getActivity(),settings);


                                    Hawk.put(AppConstants.PREFERENCES_JSON,obj);
                                    BackPressedEvent backPressedEvent = new BackPressedEvent();
                                    backPressedEvent.setChangedSettings(settings.isChanged());
                                    EventBus.getDefault().post(backPressedEvent);
                                    getFragmentManager().popBackStack();

                                }

                                @Override
                                public boolean onFailure() {
                                    myProgressBarWrapper.setVisibility(View.GONE);
                                    ((MainActivity)getActivity()).showErrorMessage(0,getString(R.string.error),getString(R.string.there_is_no_internet_connection));
                                    return super.onFailure();
                                }

                            });
                    }else {
                        ((MainActivity)getActivity()).showErrorMessage(0,getString(R.string.error),getString(R.string.there_is_no_internet_connection));
                        myProgressBarWrapper.setVisibility(View.GONE);
                    }

                }else {
                    BackPressedEvent backPressedEvent = new BackPressedEvent();
                    backPressedEvent.setChangedSettings(settings.isChanged());
                    EventBus.getDefault().post(backPressedEvent);
                    getFragmentManager().popBackStack();
                }
            }

        }
        //startAlert();

    }


//    @OnClick(R.id.id_save)
//    public void onSave(View view) {
//        onSaveAll();
//    }







    @BindView(R.id.id_enable_night_mode_float_btn)
    android.widget.ToggleButton id_enable_night_mode_float_btn;



    @OnClick(R.id.id_mode_start_time_txt)
    public void onStartTime(View view) {
        onCreateDialog(START_MODE_TIME,settings.getStart_hour(),settings.getStart_minute());
    }

    @OnClick(R.id.id_mode_end_time_txt)
    public void onEndTime(View view) {
        onCreateDialog(END_MODE_TIME,settings.getEnd_hour(),settings.getEnd_minute());
    }


    @OnClick(R.id.id_time_for_report_txt)
    public void onTimeForReport(View view) {
       // onCreateDialog(REPORT_TIME, settings.getReportHour(),settings.getReportMinute());
        setTime(settings.getReportHour(),settings.getReportMinute());
    }

    private static final int START_MODE_TIME = 1;
    private static final int END_MODE_TIME = 2;
    private static final int REPORT_TIME = 3;


    @BindView(R.id.id_mode_start_time_txt)
    TextView id_mode_start_time_txt;
    @BindView(R.id.id_mode_end_time_txt)
    TextView id_mode_end_time_txt;


    @BindView(R.id.id_home_airport_container)
    LinearLayout id_home_airport_container;

    @BindView(R.id.id_activity_airport_container)
    LinearLayout id_activity_airport_container;


    TimePickerDialog timePicker;
    public void onCreateDialog(final int ufter,int hour,int minute) {
            timePicker = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                    switch (ufter){
                        case START_MODE_TIME:
                            id_mode_start_time_txt.setText(Utils.convertTimeToString("KK:mm",hourOfDay,minute));
                            settings.setStartSet(true,hourOfDay,minute);
                            break;
                        case END_MODE_TIME:
                            id_mode_end_time_txt.setText(Utils.convertTimeToString("KK:mm",hourOfDay,minute));
                           // id_mode_end_time_txt.setText(addZero("" + hourOfDay) + ":" + addZero("" + minute));
                            settings.setEndSet(true,hourOfDay,minute);
                            break;
                        case REPORT_TIME:
                            id_time_for_report_txt.setText(Utils.convertTimeToStringWithOut("HH:mm",hourOfDay,minute));
                            //id_time_for_report_txt.setText(addZero("" + hourOfDay) + ":" + addZero("" + minute));
                            settings.setReportSet(true,hourOfDay,minute);
                            break;
                    }


                }
            }, hour, minute, true);
            timePicker.show(getFragmentManager(),"");
    }





    private String addZero(String value){
        if(value.length()<=1){
            return "0"+value;
        }
        return value;
    }







    public void setTime(int hour,int minute) {
        new android.app.TimePickerDialog(getActivity(), t,hour,minute, true).show();
    }


    // установка обработчика выбора времени
    android.app.TimePickerDialog.OnTimeSetListener t=new android.app.TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            id_time_for_report_txt.setText(Utils.convertTimeToStringWithOut("HH:mm",hourOfDay,minute));
            //id_time_for_report_txt.setText(addZero("" + hourOfDay) + ":" + addZero("" + minute));
            settings.setReportSet(true,hourOfDay,minute);
        }


    };




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if(Hawk.contains("settings")){
            settings = Hawk.get("settings");
        }else {
            settings= new Settings();
        }
        ButterKnife.bind(this, view);
        return view;
    }


    private boolean nm_chaced = false;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        login = getArguments().getBoolean("login");


        initViews(id_pilot_flight_attendant,R.array.pilot_flight_attendant);
        initViews(id_show_us_or_canadian,R.array.us_canadian);
        initViews(id_temperature,R.array.fahrenheit_celsius);
        initViews(id_show_pull_to_refresh,R.array.pull_to_refresh);


        if(!settings.getHomeAiroport().equals("")){
            id_home_airport_txt.setText(settings.getHomeAiroport());
        }else {
            id_home_airport_txt.setText(getString(R.string.tap_to_set));
        }

        if(!settings.getActivityAeroport().equals("")){
            id_activity_airport_txt.setText(settings.getActivityAeroport());
        }else {
            id_activity_airport_txt.setText(getString(R.string.tap_to_set));
        }


        if(settings.isStart_set()){
            id_mode_start_time_txt.setText(Utils.convertTimeToString("KK:mm",settings.getStart_hour(),settings.getStart_minute()));
        }else {
            id_mode_start_time_txt.setText(getString(R.string.tap_to_set));
        }

        if(settings.isEnd_set()){
            id_mode_end_time_txt.setText(Utils.convertTimeToString("KK:mm",settings.getEnd_hour(),settings.getEnd_minute()));
        }else {
            id_mode_end_time_txt.setText(getString(R.string.tap_to_set));
        }


        if(settings.isReport_set()){
            id_time_for_report_txt.setText(Utils.convertTimeToStringWithOut("HH:mm",settings.getReportHour(),settings.getReportMinute()));
        }else {
            id_time_for_report_txt.setText(getString(R.string.tap_to_set));
        }





        setNM(settings.isNightModeEnable());
        id_enable_night_mode_float_btn.setChecked(settings.isNightModeEnable());
        id_enable_night_mode_float_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                settings.setNightModeEnable(isChecked);
                setNM(isChecked);
            }
        });

    }

private void setNM(boolean nm){
    if(nm){
        id_mode_start_time_txt.setClickable(true);
        id_mode_start_time_txt.setTextColor(ContextCompat.getColor(getActivity(),R.color.color_scheme_day_2));
        id_mode_end_time_txt.setClickable(true);
        id_mode_end_time_txt.setTextColor(ContextCompat.getColor(getActivity(),R.color.color_scheme_day_2));
    }else {
        id_mode_start_time_txt.setClickable(false);
        id_mode_end_time_txt.setClickable(false);
        id_mode_start_time_txt.setTextColor(ContextCompat.getColor(getActivity(),R.color.gray));
        id_mode_end_time_txt.setTextColor(ContextCompat.getColor(getActivity(),R.color.gray));
    }
}

    public void initViews(final MultiStateToggleButton view,final int array) {

        view.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int position) {
                switchMyOption(view,array,position);
                switch (view.getId()){
                    case R.id.id_pilot_flight_attendant:
                        settings.setPilotOrFlightAttendant(position);
                        break;
                    case R.id.id_show_us_or_canadian:
                        settings.setChanged(true);
                        settings.setUSOrCanadian(position);
                        break;
                    case R.id.id_temperature:
                        settings.setFhrenheitCelsius(position);
                        break;
                    case R.id.id_show_pull_to_refresh:
                        settings.setPullToRefresh(position);
                        break;
                }


            }
        });


        switch (view.getId()){
            case R.id.id_pilot_flight_attendant:
                switchMyOption(view,array,settings.getIsPilotOrFlightAttendant());
                break;
            case R.id.id_show_us_or_canadian:
                switchMyOption(view,array,settings.getIsUSOrCanadian());
                break;
            case R.id.id_temperature:
                switchMyOption(view,array,settings.getIsFhrenheitCelsius());
                break;
            case R.id.id_show_pull_to_refresh:
                switchMyOption(view,array,settings.getIsPullToRefresh());
                break;
            default:
                switchMyOption(view,array,0);
                break;
        }

    }

    public void switchMyOption(MultiStateToggleButton view,int array, int x) {
        view.setElements(array, x);
    }




    public void onEvent(AirportEvent event){
        if(event.getPosition()!=0){
        if(home_activity==home){
            settings.setChanged(true);
            settings.setHomeAiroport(event.getText());
            id_home_airport_txt.setText(event.getText());
        }else {
            id_activity_airport_txt.setText(event.getText());
            settings.setActivityAeroport(event.getText());
        }}else {
            settings.setChanged(true);
            if(home_activity==home){
                id_home_airport_txt.setText(getString(R.string.tap_to_set));
                settings.setHomeAiroport("");
            }else {
                id_activity_airport_txt.setText(getString(R.string.tap_to_set));
                settings.setActivityAeroport("");
            }
        }
    }


    Handler handler = new Handler();
    private boolean isValide(){

        if(settings.getHomeAiroport().equals("")){
            Utils.scrollToView(getActivity(),scrollView1,id_home_airport_container);
            handler.postDelayed(new Runnable() {
                public void run() {
                    startAnimation(id_home_airport_container);
                }
            }, 500);
            return false;
        }
        if(settings.getActivityAeroport().equals("")){
            Utils.scrollToView(getActivity(),scrollView1,id_home_airport_container);
            handler.postDelayed(new Runnable() {
                public void run() {
                    startAnimation(id_activity_airport_container);
                }
            }, 500);
            return false;
        }
        return true;
    }


    private void startAnimation(LinearLayout linearLayout) {
        Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        linearLayout.startAnimation(shake);

    }



}
