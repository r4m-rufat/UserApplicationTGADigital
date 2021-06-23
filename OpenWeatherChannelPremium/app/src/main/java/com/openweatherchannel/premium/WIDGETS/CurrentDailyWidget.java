package com.openweatherchannel.premium.WIDGETS;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.openweatherchannel.premium.API.ApiKeys;
import com.openweatherchannel.premium.API.ManagerAll;
import com.openweatherchannel.premium.Models.Weekly.WeeklyResult;
import com.openweatherchannel.premium.UTILS.PreferenceManager;
import com.openweatherchannel.premium.R;
import com.openweatherchannel.premium.InitialActivities.SplashActivity;
import com.openweatherchannel.premium.UTILS.SetupLanguagesForAPI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Implementation of App Widget functionality.
 */

public class CurrentDailyWidget extends AppWidgetProvider {

    private static final String TAG = "CurrentDailyWidget";

    private RemoteViews remoteViews;
    private PreferenceManager preferenceManager;
    private String api_key, language, language_api;
    static WeeklyResult weekly_widgetResult;
    private Locale myLocale;
    private ApiKeys apiKeys;

    private boolean workDaily = true;


    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId) {

        preferenceManager = new PreferenceManager(context);

        // selected language
        language = preferenceManager.getString("language");
        myLocale = new Locale(language);
        Locale.setDefault(myLocale);

        SetupLanguagesForAPI setupLanguagesForAPI = new SetupLanguagesForAPI();
        setupLanguagesForAPI.setupLanguage(language, preferenceManager);
        language_api = preferenceManager.getString("language_api");

        Configuration configuration = new Configuration();
        configuration.locale = myLocale;

        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());



        remoteViews = new RemoteViews(context.getPackageName(), R.layout.current_daily_widget);

        Calendar calendar1 = Calendar.getInstance();
        String daily_calendar_constant = DateFormat.getDateInstance(DateFormat.FULL).format(calendar1.getTime());
        remoteViews.setTextViewText(R.id.txt_calendar_forDailyWidget, daily_calendar_constant);


        // api keys
        apiKeys = new ApiKeys(context);
        api_key = apiKeys.apiKeys();

        /**
         * when widget is called items which get from the api response upload preference manager(internal storage)
         */

        final Call<WeeklyResult> call = ManagerAll.getInstance().getWeatherWeeklyInformations(preferenceManager.getString("local_lat"),
                preferenceManager.getString("local_lan"), "hourly,daily", "metric", language_api, api_key);

        call.enqueue(new Callback<WeeklyResult>() {
            @Override
            public void onResponse(Call<WeeklyResult> call, Response<WeeklyResult> response) {

                if (response.isSuccessful()) {

                    weekly_widgetResult = response.body();

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE");
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone(calendar.getTimeZone().getID()));


                    preferenceManager.putInteger("updated_week_temp_forDailyWidget", (int) weekly_widgetResult.getDaily().get(0).getTemp().getDay());
                    preferenceManager.putString("updated_week_description_forDailyWidget", weekly_widgetResult.getDaily().get(0).getWeather().get(0).getDescription().substring(0,1).toUpperCase() +
                            weekly_widgetResult.getDaily().get(0).getWeather().get(0).getDescription().substring(1).toLowerCase());
                    preferenceManager.putString("updated_week_icon_forDailyWidget", weekly_widgetResult.getDaily().get(0).getWeather().get(0).getIcon());
                    preferenceManager.putInteger("updated_week_max_temp_forDailyWidget", (int) weekly_widgetResult.getDaily().get(0).getTemp().getMax());
                    preferenceManager.putInteger("updated_week_min_temp_forDailyWidget", (int) weekly_widgetResult.getDaily().get(0).getTemp().getMin());
                    preferenceManager.putInteger("updated_week_uv_index_forDailyWidget", (int) weekly_widgetResult.getDaily().get(0).getUvi());


                    int weekNameValue1 = weekly_widgetResult.getDaily().get(1).getDt();
                    Date convertDate1 = new Date(weekNameValue1 * 1000L);
                    String formattedDate1 = simpleDateFormat.format(convertDate1);
                    preferenceManager.putString("updated_week_name_for_widget1", formattedDate1);

                    int int_max_temp1 = (int) weekly_widgetResult.getDaily().get(1).getTemp().getMax();
                    preferenceManager.putString("updated_max_temp_for_widget1", (int_max_temp1 + ""));

                    int int_min_temp1 = (int) weekly_widgetResult.getDaily().get(1).getTemp().getMin();
                    preferenceManager.putString("updated_min_temp_for_widget1", (int_min_temp1 + ""));

                    String situation_icon1 = weekly_widgetResult.getDaily().get(1).getWeather().get(0).getIcon();
                    preferenceManager.putString("updated_weather_situation_icon_for_widget1", situation_icon1);

                    int weekNameValue2 = weekly_widgetResult.getDaily().get(2).getDt();
                    Date convertDate2 = new Date(weekNameValue2 * 1000L);
                    String formattedDate2 = simpleDateFormat.format(convertDate2);
                    preferenceManager.putString("updated_week_name_for_widget2", formattedDate2);

                    int int_max_temp2 = (int) weekly_widgetResult.getDaily().get(2).getTemp().getMax();
                    preferenceManager.putString("updated_max_temp_for_widget2", (int_max_temp2 + ""));

                    int int_min_temp2 = (int) weekly_widgetResult.getDaily().get(2).getTemp().getMin();
                    preferenceManager.putString("updated_min_temp_for_widget2", (int_min_temp2 + ""));

                    String situation_icon2 = weekly_widgetResult.getDaily().get(2).getWeather().get(0).getIcon();
                    preferenceManager.putString("updated_weather_situation_icon_for_widget2", situation_icon2);

                    int weekNameValue3 = weekly_widgetResult.getDaily().get(3).getDt();
                    Date convertDate3 = new Date(weekNameValue3 * 1000L);
                    String formattedDate3 = simpleDateFormat.format(convertDate3);
                    preferenceManager.putString("updated_week_name_for_widget3", formattedDate3);

                    int int_max_temp3 = (int) weekly_widgetResult.getDaily().get(3).getTemp().getMax();
                    preferenceManager.putString("updated_max_temp_for_widget3", (int_max_temp3 + ""));

                    int int_min_temp3 = (int) weekly_widgetResult.getDaily().get(3).getTemp().getMin();
                    preferenceManager.putString("updated_min_temp_for_widget3", (int_min_temp3 + ""));

                    String situation_icon3 = weekly_widgetResult.getDaily().get(3).getWeather().get(0).getIcon();
                    preferenceManager.putString("updated_weather_situation_icon_for_widget3", situation_icon3);

                    int weekNameValue4 = weekly_widgetResult.getDaily().get(4).getDt();
                    Date convertDate4 = new Date(weekNameValue4 * 1000L);
                    String formattedDate4 = simpleDateFormat.format(convertDate4);
                    preferenceManager.putString("updated_week_name_for_widget4", formattedDate4);

                    int int_max_temp4 = (int) weekly_widgetResult.getDaily().get(4).getTemp().getMax();
                    preferenceManager.putString("updated_max_temp_for_widget4", (int_max_temp4 + ""));

                    int int_min_temp4 = (int) weekly_widgetResult.getDaily().get(4).getTemp().getMin();
                    preferenceManager.putString("updated_min_temp_for_widget4", (int_min_temp4 + ""));

                    String situation_icon4 = weekly_widgetResult.getDaily().get(4).getWeather().get(0).getIcon();
                    preferenceManager.putString("updated_weather_situation_icon_for_widget4", situation_icon4);

                    int weekNameValue5 = weekly_widgetResult.getDaily().get(5).getDt();
                    Date convertDate5 = new Date(weekNameValue5 * 1000L);
                    String formattedDate5 = simpleDateFormat.format(convertDate5);
                    preferenceManager.putString("updated_week_name_for_widget5", formattedDate5);

                    int int_max_temp5 = (int) weekly_widgetResult.getDaily().get(5).getTemp().getMax();
                    preferenceManager.putString("updated_max_temp_for_widget5", (int_max_temp5 + ""));

                    int int_min_temp5 = (int) weekly_widgetResult.getDaily().get(5).getTemp().getMin();
                    preferenceManager.putString("updated_min_temp_for_widget5", (int_min_temp5 + ""));

                    String situation_icon5 = weekly_widgetResult.getDaily().get(5).getWeather().get(0).getIcon();
                    preferenceManager.putString("updated_weather_situation_icon_for_widget5", situation_icon5);

                }
            }

            @Override
            public void onFailure(Call<WeeklyResult> call, Throwable t) {

                Log.d(TAG, "onFailure: Updated response is not work");

            }
        });



        if(workDaily){

            remoteViews.setTextViewText(R.id.txtLocalPlaceForDailyWidget, preferenceManager.getString("city_name_for_notification"));
            remoteViews.setTextViewText(R.id.weather_valueForDailyWidget, preferenceManager.getInteger("week_temp_forDailyWidget") + "°");
            remoteViews.setTextViewText(R.id.max_weather_valueForDailyWidget, "↑" + preferenceManager.getString("first_day_max_temp_for_notification") + "°");
            remoteViews.setTextViewText(R.id.min_weather_valueForDailyWidget, "↓" + preferenceManager.getString("first_day_min_temp_for_notification") + "°");
            remoteViews.setTextViewText(R.id.txt_weather_stiuationDailyWidget, preferenceManager.getString("week_description_forDailyWidget"));
            remoteViews.setTextViewText(R.id.txt_uv_indexDailyWidget, context.getString(R.string.uv_index) + ": " + (int) preferenceManager.getInteger("week_uv_index_forDailyWidget"));
            if (preferenceManager.getString("week_icon_forDailyWidget").equals("01d")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_01d);

            } else if (preferenceManager.getString("week_icon_forDailyWidget").equals("01n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_01n);

            } else if (preferenceManager.getString("week_icon_forDailyWidget").equals("02d")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_02d);

            } else if (preferenceManager.getString("week_icon_forDailyWidget").equals("02n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_02n);

            } else if (preferenceManager.getString("week_icon_forDailyWidget").equals("03d")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon03d);

            } else if (preferenceManager.getString("week_icon_forDailyWidget").equals("03n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon03d);

            } else if (preferenceManager.getString("week_icon_forDailyWidget").equals("04d") || preferenceManager.getString("week_icon_forDailyWidget").equals("04n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_04n);

            } else if (preferenceManager.getString("week_icon_forDailyWidget").equals("09d") || preferenceManager.getString("week_icon_forDailyWidget").equals("09n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_9d);

            } else if (preferenceManager.getString("week_icon_forDailyWidget").equals("10d")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_10d);

            } else if (preferenceManager.getString("week_icon_forDailyWidget").equals("10n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_10n);

            } else if (preferenceManager.getString("week_icon_forDailyWidget").equals("11d")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_11d);

            } else if (preferenceManager.getString("week_icon_forDailyWidget").equals("11n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_11n);

            } else if (preferenceManager.getString("week_icon_forDailyWidget").equals("13d") || preferenceManager.getString("week_icon_forDailyWidget").equals("13n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_13n);

            } else if (preferenceManager.getString("week_icon_forDailyWidget").equals("50d") || preferenceManager.getString("week_icon_forDailyWidget").equals("50n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_50d);

            }

            /**
             * Items of 5 days of week is below
             *
             */

            // ------------------------------------- first day -------------------------------------

            remoteViews.setTextViewText(R.id.txt_week_name_forDailyWidget1, preferenceManager.getString("week_name_for_widget1"));
            remoteViews.setTextViewText(R.id.txt_weather_value_DetailDailyWidget1, preferenceManager.getString("max_temp_for_widget1") + "°/" +
                    preferenceManager.getString("min_temp_for_widget1") + "°");

            if (preferenceManager.getString("weather_situation_icon_for_widget1").equals("01d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_01d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget1").equals("01n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_01n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget1").equals("02d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_02d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget1").equals("02n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_02n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget1").equals("03d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon03d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget1").equals("03n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon03d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget1").equals("04d") || preferenceManager.getString("weather_situation_icon_for_widget1").equals("04n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_04n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget1").equals("09d") || preferenceManager.getString("weather_situation_icon_for_widget1").equals("09n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_9d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget1").equals("10d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_10d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget1").equals("10n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_10n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget1").equals("11d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_11d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget1").equals("11n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_11n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget1").equals("13d") || preferenceManager.getString("weather_situation_icon_for_widget1").equals("13n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_13n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget1").equals("50d") || preferenceManager.getString("weather_situation_icon_for_widget1").equals("50n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_50d);

            }


            // ------------------------------------- second day -------------------------------------

            remoteViews.setTextViewText(R.id.txt_week_name_forDailyWidget2, preferenceManager.getString("week_name_for_widget2"));
            remoteViews.setTextViewText(R.id.txt_weather_value_DetailDailyWidget2, preferenceManager.getString("max_temp_for_widget2") + "°/" +
                    preferenceManager.getString("min_temp_for_widget2") + "°");

            if (preferenceManager.getString("weather_situation_icon_for_widget2").equals("01d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon_01d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget2").equals("01n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon_01n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget2").equals("02d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon_02d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget2").equals("02n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon_02n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget2").equals("03d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon03d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget2").equals("03n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon03d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget2").equals("04d") || preferenceManager.getString("weather_situation_icon_for_widget2").equals("04n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_04n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget2").equals("09d") || preferenceManager.getString("weather_situation_icon_for_widget2").equals("09n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon_9d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget2").equals("10d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon_10d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget2").equals("10n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon_10n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget2").equals("11d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon_11d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget2").equals("11n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon_11n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget2").equals("13d") || preferenceManager.getString("weather_situation_icon_for_widget2").equals("13n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_13n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget2").equals("50d") || preferenceManager.getString("weather_situation_icon_for_widget2").equals("50n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon_50d);

            }


            // ------------------------------------- third day -------------------------------------

            remoteViews.setTextViewText(R.id.txt_week_name_forDailyWidget3, preferenceManager.getString("week_name_for_widget3"));
            remoteViews.setTextViewText(R.id.txt_weather_value_DetailDailyWidget3, preferenceManager.getString("max_temp_for_widget3") + "°/" +
                    preferenceManager.getString("min_temp_for_widget3") + "°");

            if (preferenceManager.getString("weather_situation_icon_for_widget3").equals("01d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_01d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget3").equals("01n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_01n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget3").equals("02d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_02d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget3").equals("02n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_02n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget3").equals("03d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon03d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget3").equals("03n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon03d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget3").equals("04d") || preferenceManager.getString("weather_situation_icon_for_widget3").equals("04n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_04n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget3").equals("09d") || preferenceManager.getString("weather_situation_icon_for_widget3").equals("09n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_9d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget3").equals("10d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_10d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget3").equals("10n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_10n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget3").equals("11d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_11d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget3").equals("11n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_11n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget3").equals("13d") || preferenceManager.getString("weather_situation_icon_for_widget3").equals("13n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_13n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget3").equals("50d") || preferenceManager.getString("weather_situation_icon_for_widget3").equals("50n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_50d);

            }


            // ------------------------------------- fourth day -------------------------------------

            remoteViews.setTextViewText(R.id.txt_week_name_forDailyWidget4, preferenceManager.getString("week_name_for_widget4"));
            remoteViews.setTextViewText(R.id.txt_weather_value_DetailDailyWidget4, preferenceManager.getString("max_temp_for_widget4") + "°/" +
                    preferenceManager.getString("min_temp_for_widget4") + "°");

            if (preferenceManager.getString("weather_situation_icon_for_widget4").equals("01d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_01d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget4").equals("01n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_01n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget4").equals("02d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_02d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget4").equals("02n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_02n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget4").equals("03d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon03d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget4").equals("03n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon03d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget4").equals("04d") || preferenceManager.getString("weather_situation_icon_for_widget4").equals("04n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_04n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget4").equals("09d") || preferenceManager.getString("weather_situation_icon_for_widget4").equals("09n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_9d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget4").equals("10d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_10d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget4").equals("10n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_10n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget4").equals("11d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_11d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget4").equals("11n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_11n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget4").equals("13d") || preferenceManager.getString("weather_situation_icon_for_widget4").equals("13n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_13n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget4").equals("50d") || preferenceManager.getString("weather_situation_icon_for_widget4").equals("50n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_50d);

            }


            // ------------------------------------- fifth day -------------------------------------

            remoteViews.setTextViewText(R.id.txt_week_name_forDailyWidget5, preferenceManager.getString("week_name_for_widget5"));
            remoteViews.setTextViewText(R.id.txt_weather_value_DetailDailyWidget5, preferenceManager.getString("max_temp_for_widget5") + "°/" +
                    preferenceManager.getString("min_temp_for_widget5") + "°");

            if (preferenceManager.getString("weather_situation_icon_for_widget5").equals("01d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_01d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget5").equals("01n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_01n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget5").equals("02d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_02d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget5").equals("02n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_02n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget5").equals("03d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon03d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget5").equals("03n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon03d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget5").equals("04d") || preferenceManager.getString("weather_situation_icon_for_widget5").equals("04n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_04n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget5").equals("09d") || preferenceManager.getString("weather_situation_icon_for_widget5").equals("09n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_9d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget5").equals("10d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_10d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget5").equals("10n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_10n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget5").equals("11d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_11d);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget5").equals("11n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_11n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget5").equals("13d") || preferenceManager.getString("weather_situation_icon_for_widget5").equals("13n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_13n);

            } else if (preferenceManager.getString("weather_situation_icon_for_widget5").equals("50d") || preferenceManager.getString("weather_situation_icon_for_widget5").equals("50n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_50d);

            }


        }else {

            // ------------------------------------ set updated today informations ------------------------------------

            remoteViews.setTextViewText(R.id.txtLocalPlaceForDailyWidget, preferenceManager.getString("city_name_for_notification"));
            remoteViews.setTextViewText(R.id.weather_valueForDailyWidget, preferenceManager.getInteger("updated_week_temp_forDailyWidget") + "°");
            remoteViews.setTextViewText(R.id.max_weather_valueForDailyWidget, "↑" + preferenceManager.getInteger("updated_week_max_temp_forDailyWidget") + "°");
            remoteViews.setTextViewText(R.id.min_weather_valueForDailyWidget, "↓" + preferenceManager.getInteger("updated_week_min_temp_forDailyWidget") + "°");
            remoteViews.setTextViewText(R.id.txt_weather_stiuationDailyWidget, preferenceManager.getString("updated_week_description_forDailyWidget"));
            remoteViews.setTextViewText(R.id.txt_uv_indexDailyWidget, context.getString(R.string.uv_index) + ": " + (int) preferenceManager.getInteger("updated_week_uv_index_forDailyWidget"));


            if (preferenceManager.getString("updated_week_icon_forDailyWidget").equals("01d")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_01d);

            } else if (preferenceManager.getString("updated_week_icon_forDailyWidget").equals("01n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_01n);

            } else if (preferenceManager.getString("updated_week_icon_forDailyWidget").equals("02d")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_02d);

            } else if (preferenceManager.getString("updated_week_icon_forDailyWidget").equals("02n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_02n);

            } else if (preferenceManager.getString("updated_week_icon_forDailyWidget").equals("03d")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon03d);

            } else if (preferenceManager.getString("updated_week_icon_forDailyWidget").equals("03n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon03d);

            } else if (preferenceManager.getString("updated_week_icon_forDailyWidget").equals("04d") || preferenceManager.getString("updated_week_icon_forDailyWidget").equals("04n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_04n);

            } else if (preferenceManager.getString("updated_week_icon_forDailyWidget").equals("09d") || preferenceManager.getString("updated_week_icon_forDailyWidget").equals("09n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_9d);

            } else if (preferenceManager.getString("updated_week_icon_forDailyWidget").equals("10d")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_10d);

            } else if (preferenceManager.getString("updated_week_icon_forDailyWidget").equals("10n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_10n);

            } else if (preferenceManager.getString("updated_week_icon_forDailyWidget").equals("11d")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_11d);

            } else if (preferenceManager.getString("updated_week_icon_forDailyWidget").equals("11n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_11n);

            } else if (preferenceManager.getString("updated_week_icon_forDailyWidget").equals("13d") || preferenceManager.getString("updated_week_icon_forDailyWidget").equals("13n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_13n);

            } else if (preferenceManager.getString("updated_week_icon_forDailyWidget").equals("50d") || preferenceManager.getString("updated_week_icon_forDailyWidget").equals("50n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForDailyWidget, R.drawable.icon_50d);

            }

            /**
             * Items of 5 days of week is below
             * and this items is updated after is called widget for the first time
             */

            // ------------------------------------- first day -------------------------------------

            remoteViews.setTextViewText(R.id.txt_week_name_forDailyWidget1, preferenceManager.getString("updated_week_name_for_widget1"));
            remoteViews.setTextViewText(R.id.txt_weather_value_DetailDailyWidget1, preferenceManager.getString("updated_max_temp_for_widget1") + "°/" +
                    preferenceManager.getString("updated_min_temp_for_widget1") + "°");

            if (preferenceManager.getString("updated_weather_situation_icon_for_widget1").equals("01d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_01d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget1").equals("01n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_01n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget1").equals("02d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_02d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget1").equals("02n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_02n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget1").equals("03d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon03d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget1").equals("03n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon03d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget1").equals("04d") || preferenceManager.getString("updated_weather_situation_icon_for_widget1").equals("04n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_04n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget1").equals("09d") || preferenceManager.getString("updated_weather_situation_icon_for_widget1").equals("09n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_9d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget1").equals("10d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_10d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget1").equals("10n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_10n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget1").equals("11d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_11d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget1").equals("11n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_11n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget1").equals("13d") || preferenceManager.getString("updated_weather_situation_icon_for_widget1").equals("13n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_13n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget1").equals("50d") || preferenceManager.getString("updated_weather_situation_icon_for_widget1").equals("50n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_50d);

            }


            // ------------------------------------- second day -------------------------------------

            remoteViews.setTextViewText(R.id.txt_week_name_forDailyWidget2, preferenceManager.getString("updated_week_name_for_widget2"));
            remoteViews.setTextViewText(R.id.txt_weather_value_DetailDailyWidget2, preferenceManager.getString("updated_max_temp_for_widget2") + "°/" +
                    preferenceManager.getString("updated_min_temp_for_widget2") + "°");

            if (preferenceManager.getString("updated_weather_situation_icon_for_widget2").equals("01d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon_01d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget2").equals("01n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon_01n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget2").equals("02d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon_02d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget2").equals("02n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon_02n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget2").equals("03d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon03d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget2").equals("03n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon03d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget2").equals("04d") || preferenceManager.getString("updated_weather_situation_icon_for_widget2").equals("04n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_04n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget2").equals("09d") || preferenceManager.getString("updated_weather_situation_icon_for_widget2").equals("09n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon_9d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget2").equals("10d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon_10d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget2").equals("10n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon_10n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget2").equals("11d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon_11d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget2").equals("11n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon_11n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget2").equals("13d") || preferenceManager.getString("updated_weather_situation_icon_for_widget2").equals("13n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget1, R.drawable.icon_13n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget2").equals("50d") || preferenceManager.getString("updated_weather_situation_icon_for_widget2").equals("50n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget2, R.drawable.icon_50d);

            }


            // ------------------------------------- third day -------------------------------------

            remoteViews.setTextViewText(R.id.txt_week_name_forDailyWidget3, preferenceManager.getString("updated_week_name_for_widget3"));
            remoteViews.setTextViewText(R.id.txt_weather_value_DetailDailyWidget3, preferenceManager.getString("updated_max_temp_for_widget3") + "°/" +
                    preferenceManager.getString("updated_min_temp_for_widget3") + "°");

            if (preferenceManager.getString("updated_weather_situation_icon_for_widget3").equals("01d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_01d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget3").equals("01n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_01n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget3").equals("02d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_02d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget3").equals("02n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_02n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget3").equals("03d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon03d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget3").equals("03n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon03d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget3").equals("04d") || preferenceManager.getString("updated_weather_situation_icon_for_widget3").equals("04n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_04n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget3").equals("09d") || preferenceManager.getString("updated_weather_situation_icon_for_widget3").equals("09n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_9d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget3").equals("10d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_10d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget3").equals("10n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_10n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget3").equals("11d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_11d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget3").equals("11n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_11n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget3").equals("13d") || preferenceManager.getString("updated_weather_situation_icon_for_widget3").equals("13n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_13n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget3").equals("50d") || preferenceManager.getString("updated_weather_situation_icon_for_widget3").equals("50n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget3, R.drawable.icon_50d);

            }


            // ------------------------------------- fourth day -------------------------------------

            remoteViews.setTextViewText(R.id.txt_week_name_forDailyWidget4, preferenceManager.getString("updated_week_name_for_widget4"));
            remoteViews.setTextViewText(R.id.txt_weather_value_DetailDailyWidget4, preferenceManager.getString("updated_max_temp_for_widget4") + "°/" +
                    preferenceManager.getString("updated_min_temp_for_widget4") + "°");

            if (preferenceManager.getString("updated_weather_situation_icon_for_widget4").equals("01d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_01d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget4").equals("01n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_01n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget4").equals("02d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_02d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget4").equals("02n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_02n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget4").equals("03d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon03d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget4").equals("03n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon03d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget4").equals("04d") || preferenceManager.getString("updated_weather_situation_icon_for_widget4").equals("04n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_04n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget4").equals("09d") || preferenceManager.getString("updated_weather_situation_icon_for_widget4").equals("09n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_9d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget4").equals("10d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_10d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget4").equals("10n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_10n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget4").equals("11d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_11d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget4").equals("11n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_11n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget4").equals("13d") || preferenceManager.getString("updated_weather_situation_icon_for_widget4").equals("13n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_13n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget4").equals("50d") || preferenceManager.getString("updated_weather_situation_icon_for_widget4").equals("50n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget4, R.drawable.icon_50d);

            }


            // ------------------------------------- fifth day -------------------------------------

            remoteViews.setTextViewText(R.id.txt_week_name_forDailyWidget5, preferenceManager.getString("updated_week_name_for_widget5"));
            remoteViews.setTextViewText(R.id.txt_weather_value_DetailDailyWidget5, preferenceManager.getString("updated_max_temp_for_widget5") + "°/" +
                    preferenceManager.getString("updated_min_temp_for_widget5") + "°");

            if (preferenceManager.getString("updated_weather_situation_icon_for_widget5").equals("01d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_01d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget5").equals("01n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_01n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget5").equals("02d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_02d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget5").equals("02n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_02n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget5").equals("03d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon03d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget5").equals("03n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon03d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget5").equals("04d") || preferenceManager.getString("weather_situation_icon_for_widget5").equals("04n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_04n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget5").equals("09d") || preferenceManager.getString("weather_situation_icon_for_widget5").equals("09n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_9d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget5").equals("10d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_10d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget5").equals("10n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_10n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget5").equals("11d")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_11d);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget5").equals("11n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_11n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget5").equals("13d") || preferenceManager.getString("updated_weather_situation_icon_for_widget5").equals("13n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_13n);

            } else if (preferenceManager.getString("updated_weather_situation_icon_for_widget5").equals("50d") || preferenceManager.getString("updated_weather_situation_icon_for_widget5").equals("50n")) {

                remoteViews.setImageViewResource(R.id.stiuation_image_for_dailyWidget5, R.drawable.icon_50d);

            }

        }


        workDaily = false;


        Intent homeIntent = new Intent(context, SplashActivity.class);
        homeIntent.setData(Uri.parse(homeIntent.toUri(Intent.URI_INTENT_SCHEME)));
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent homePendingIntent = PendingIntent.getActivity(context, 0, homeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.forPendingIntentDaily, homePendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        language = preferenceManager.getString("language");
        myLocale = new Locale(language);
        Locale.setDefault(myLocale);

        Configuration configuration = new Configuration();
        configuration.locale = myLocale;

        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {


    }

}