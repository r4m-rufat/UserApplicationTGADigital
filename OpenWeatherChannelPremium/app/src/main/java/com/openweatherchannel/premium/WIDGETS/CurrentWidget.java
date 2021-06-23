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
import com.openweatherchannel.premium.Models.Current.CurrentResult;
import com.openweatherchannel.premium.UTILS.PreferenceManager;
import com.openweatherchannel.premium.R;
import com.openweatherchannel.premium.InitialActivities.SplashActivity;
import com.openweatherchannel.premium.UTILS.SetupLanguagesForAPI;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Implementation of App Widget functionality.
 */
public class CurrentWidget extends AppWidgetProvider {


    private static final String TAG = "CurrentWidget";

    static CurrentResult currentResult;
    static boolean work = true;
    private RemoteViews remoteViews;
    private PreferenceManager preferenceManager;
    private String api_key, language, language_api;
    private Locale myLocale;
    private ApiKeys apiKeys;


    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId) {


        Log.d(TAG, "updateAppWidget: bu da isleyir");

        preferenceManager = new PreferenceManager(context);

        // selected language
        language = preferenceManager.getString("language");
        myLocale = new Locale(language);

        SetupLanguagesForAPI setupLanguagesForAPI = new SetupLanguagesForAPI();
        setupLanguagesForAPI.setupLanguage(language, preferenceManager);
        language_api = preferenceManager.getString("language_api");
        Locale.setDefault(myLocale);

        Configuration configuration = new Configuration();
        configuration.locale = myLocale;

        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.current_widget);


        Calendar calendar = Calendar.getInstance();
        String current_calendar = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        remoteViews.setTextViewText(R.id.txt_calendar_forWidget, current_calendar);

        // api keys
        apiKeys = new ApiKeys(context);
        api_key = apiKeys.apiKeys();

        // response
        final Call<CurrentResult> resultCall = ManagerAll.getInstance().getWeatherInformationsWithLatLon(
                preferenceManager.getString("local_lat"),
                preferenceManager.getString("local_lan"),
                "metric",
                language_api,
                api_key
        );

        resultCall.enqueue(new Callback<CurrentResult>() {
            @Override
            public void onResponse(Call<CurrentResult> call, Response<CurrentResult> response) {
                if (response.isSuccessful()){
                    currentResult = response.body();

                    int temprature = (int) currentResult.getMain().getTemp();
                    preferenceManager.putInteger("widget_temprature", temprature);

                    int wind_speed = (int) currentResult.getWind().getSpeed();
                    preferenceManager.putInteger("widget_wind_speed", wind_speed);

                    String description = currentResult.getWeather().get(0).getDescription() + "";
                    String change_description = description.substring(0, 1).toUpperCase() + description.substring(1).toLowerCase() + "";
                    preferenceManager.putString("widget_description", change_description);

                    String city_name = currentResult.getName();
                    preferenceManager.putString("widget_city_name", city_name);

                    preferenceManager.putString("widget_current_icon", currentResult.getWeather().get(0).getIcon() + "");


                    Log.d(TAG, "onResponse: Response successfully respond");

                }
            }

            @Override
            public void onFailure(Call<CurrentResult> call, Throwable t) {

                Log.d(TAG, "onFailure: Response isn't responding");

            }
        });

        if (work){

            remoteViews.setTextViewText(R.id.weather_valueForCurrentWidget, preferenceManager.getInteger("current_temprature_notification")+"°C");
            remoteViews.setTextViewText(R.id.txt_weather_stiuation, preferenceManager.getString("current_description_notification").substring(0,1).toUpperCase() +
                    preferenceManager.getString("current_description_notification").substring(1).toLowerCase());
            remoteViews.setTextViewText(R.id.txt_wind_speed, context.getString(R.string.wind_speed) + ": " + preferenceManager.getInteger("current_notification_wind_speed") + " " + context.getString(R.string.m_sec));
            remoteViews.setTextViewText(R.id.txtLocalPlace, preferenceManager.getString("city_name_for_notification"));

        }else {

            remoteViews.setTextViewText(R.id.weather_valueForCurrentWidget, preferenceManager.getInteger("widget_temprature")+"°C");
            remoteViews.setTextViewText(R.id.txt_weather_stiuation, preferenceManager.getString("widget_description"));
            remoteViews.setTextViewText(R.id.txt_wind_speed, context.getString(R.string.wind_speed) + ": " + preferenceManager.getInteger("widget_wind_speed") + " " + R.string.m_sec);
            remoteViews.setTextViewText(R.id.txtLocalPlace, preferenceManager.getString("widget_city_name"));
        }

        if (work){

            if (preferenceManager.getString("current_icon_notification").equals("01d")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_01d);

            } else if (preferenceManager.getString("current_icon_notification").equals("01n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_01n);

            } else if (preferenceManager.getString("current_icon_notification").equals("02d")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_02d);

            } else if (preferenceManager.getString("current_icon_notification").equals("02n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_02n);

            } else if (preferenceManager.getString("current_icon_notification").equals("03d")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon03d);

            } else if (preferenceManager.getString("current_icon_notification").equals("03n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon03d);

            } else if (preferenceManager.getString("current_icon_notification").equals("04d") || preferenceManager.getString("current_icon_notification").equals("04n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_04n);

            } else if (preferenceManager.getString("current_icon_notification").equals("09d") || preferenceManager.getString("current_icon_notification").equals("09n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_9d);

            } else if (preferenceManager.getString("current_icon_notification").equals("10d")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_10d);

            } else if (preferenceManager.getString("current_icon_notification").equals("10n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_10n);

            } else if (preferenceManager.getString("current_icon_notification").equals("11d")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_11d);

            } else if (preferenceManager.getString("current_icon_notification").equals("11n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_11n);

            } else if (preferenceManager.getString("current_icon_notification").equals("13d") || preferenceManager.getString("current_icon_notification").equals("13n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_13n);

            } else if (preferenceManager.getString("current_icon_notification").equals("50d") || preferenceManager.getString("current_icon_notification").equals("50n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_50d);

            }

        }else {

            if (preferenceManager.getString("widget_current_icon").equals("01d")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_01d);

            } else if (preferenceManager.getString("widget_current_icon").equals("01n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_01n);

            } else if (preferenceManager.getString("widget_current_icon").equals("02d")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_02d);

            } else if (preferenceManager.getString("widget_current_icon").equals("02n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_02n);

            } else if (preferenceManager.getString("widget_current_icon").equals("03d")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon03d);

            } else if (preferenceManager.getString("widget_current_icon").equals("03n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon03d);

            } else if (preferenceManager.getString("widget_current_icon").equals("04d") || preferenceManager.getString("widget_current_icon").equals("04n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_04n);

            } else if (preferenceManager.getString("widget_current_icon").equals("09d") || preferenceManager.getString("widget_current_icon").equals("09n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_9d);

            } else if (preferenceManager.getString("widget_current_icon").equals("10d")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_10d);

            } else if (preferenceManager.getString("widget_current_icon").equals("10n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_10n);

            } else if (preferenceManager.getString("widget_current_icon").equals("11d")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_11d);

            } else if (preferenceManager.getString("widget_current_icon").equals("11n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_11n);

            } else if (preferenceManager.getString("widget_current_icon").equals("13d") || preferenceManager.getString("widget_current_icon").equals("13n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_13n);

            } else if (preferenceManager.getString("widget_current_icon").equals("50d") || preferenceManager.getString("widget_current_icon").equals("50n")) {

                remoteViews.setImageViewResource(R.id.weather_situationForCurrentWidget, R.drawable.icon_50d);

            }

        }

        work = false;


        Intent homeIntent = new Intent(context, SplashActivity.class);
        homeIntent.setData(Uri.parse(homeIntent.toUri(Intent.URI_INTENT_SCHEME)));
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent homePendingIntent = PendingIntent.getActivity(context, 0, homeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.forPendingIntent, homePendingIntent);

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

