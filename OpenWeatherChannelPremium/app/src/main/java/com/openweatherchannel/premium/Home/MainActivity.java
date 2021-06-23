package com.openweatherchannel.premium.Home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.tianma8023.formatter.SunriseSunsetLabelFormatter;
import com.github.tianma8023.model.Time;
import com.github.tianma8023.ssv.SunriseSunsetView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.openweatherchannel.premium.API.ApiKeys;
import com.openweatherchannel.premium.API.ManagerAll;
import com.openweatherchannel.premium.AdapTers.HourlyAdapter;
import com.openweatherchannel.premium.AdapTers.WeeklyAdapter;
import com.openweatherchannel.premium.AdapTers.WindAdapter;
import com.openweatherchannel.premium.Daily.Daily;
import com.openweatherchannel.premium.DataBase.SpinnerDatabase;
import com.openweatherchannel.premium.DrawerLayoutItems.AboutTheApplication;
import com.openweatherchannel.premium.DrawerLayoutItems.AddNewLocation;
import com.openweatherchannel.premium.DrawerLayoutItems.ContactUs;
import com.openweatherchannel.premium.DrawerLayoutItems.MyLocation;
import com.openweatherchannel.premium.DrawerLayoutItems.Settings;
import com.openweatherchannel.premium.Hourly.Hourly;
import com.openweatherchannel.premium.MenuItem.Help;
import com.openweatherchannel.premium.Models.Current.CurrentResult;
import com.openweatherchannel.premium.Models.DaysForWind.JsonMember5DaysResult;
import com.openweatherchannel.premium.Models.DaysForWind.ListItem;
import com.openweatherchannel.premium.Models.Weekly.DailyItem;
import com.openweatherchannel.premium.Models.Weekly.HourlyItem;
import com.openweatherchannel.premium.Models.Weekly.WeeklyResult;
import com.openweatherchannel.premium.UTILS.PreferenceManager;
import com.openweatherchannel.premium.R;
import com.openweatherchannel.premium.Services.NotificationService;
import com.openweatherchannel.premium.UTILS.SetupLanguagesForAPI;
import com.openweatherchannel.premium.WINDS.Wind;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.openweatherchannel.premium.DrawerLayoutItems.MyLocation.REQUEST_CODE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = "MainActivity";
    private Context context;
    private TextView clock, temprature, weather_stiuation, wind_speed1,
            feels_like, pressure, sunrise, sunset, wind_speed2, txt_km_h, txt_m_sec,
            sunriseView, sunsetView, durationOFDay, daily_forecast, hourly_forecast, wind_forecast,
            week1, week2, week3, week4, week5, week6, week7,
            feels_like_c, pressure_c, sunrise_c, sunset_c, sunriseView_time_c, sunsetView_time_c, duration_of_the_day_c;
    private ImageView weather_stiuation_image, ic_share;
    private SunriseSunsetView sunriseSunsetView;
    private CurrentResult currentResult, current_notificationResult;
    private WeeklyResult weeklyResult, weekly_notificationResult, weekly_widgetResult;
    private JsonMember5DaysResult jsonMember5DaysResult;
    private RecyclerView dailyRecyclerView, hourlyRecyclerView, windRecyclerView;
    String lat, lon, formated_date1, formated_date2;
    private PreferenceManager preferenceManager;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private List<HourlyItem> hourlyItemList;
    private List<DailyItem> dailyItemList;
    private List<ListItem> listItems;
    private Locale myLocale;
    private LineChart lineChart;
    private ApiKeys apiKeys;
    private Menu optionMenu;

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;


    // Database
    SpinnerDatabase spinnerDatabase;

    // variable
    private static String api_key;
    private static String units = "metric";
    private String direction;

    private static String spinnerItem, language, language_api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;

        preferenceManager = new PreferenceManager(context);

        // selected language
        language = preferenceManager.getString("language");
        changeLocale(language);

        SetupLanguagesForAPI setupLanguagesForAPI = new SetupLanguagesForAPI();
        setupLanguagesForAPI.setupLanguage(language, preferenceManager);
        language_api = preferenceManager.getString("language_api");


        spinnerDatabase = new SpinnerDatabase(context);

        // api keys
        apiKeys = new ApiKeys(context);
        api_key = apiKeys.apiKeys();

        setupWidgets();
        setupAdapters();
        setupShare();

        if (preferenceManager.getString("week1_name_for_lineChart") != null) {

            reloadPageWithFast();
            lineChart(lineChart);

        }

        // current location

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

        spinnerColor();
        setupBottomNavigationViewEx();
        showNotification();


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        navigationView.setNavigationItemSelectedListener(this);
        toggle.syncState();


    }


    private void spinnerColor() {

        final ArrayList<String> city_names = new ArrayList<>();
        Cursor cursor = spinnerDatabase.getData();

        while (cursor.moveToNext()) {

            String name = cursor.getString(1);

            city_names.add(name);

        }

        try {

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.layout_for_spinner_color, city_names);

            adapter.setDropDownViewResource(R.layout.layout_dropdown_spinner);
            final Spinner spinner = findViewById(R.id.toolbar_spinner);
            spinner.setAdapter(adapter);

            try {

                spinner.setSelection(preferenceManager.getInteger("spinner_position"));

            } catch (IndexOutOfBoundsException e) {

            }

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selected_city = spinner.getSelectedItem().toString();

                    spinnerItem = selected_city;
                    int spinnerPosition = spinner.getSelectedItemPosition();
                    preferenceManager.putInteger("spinner_position", spinnerPosition);

                    if (String.valueOf(preferenceManager.getInteger("spinner_position")) == null) {

                        Response(city_names.get(0));

                    } else if (String.valueOf(preferenceManager.getInteger("spinner_position")) != null) {

                        int getSpinnerPosition = preferenceManager.getInteger("spinner_position");
                        spinnerItem = city_names.get(getSpinnerPosition);

                        Response(spinnerItem);

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {


                }
            });


        } catch (NullPointerException e) {

        }
    }

    private void changeLocale(String language) {

        myLocale = new Locale(language);
        Locale.setDefault(myLocale);

        Configuration configuration = new Configuration();
        configuration.locale = myLocale;

        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());


    }


    //Todo spinner Adapter
    private void spinnerSetAdapter() {

        final ArrayList<String> city_names = new ArrayList<>();
        Cursor cursor = spinnerDatabase.getData();

        while (cursor.moveToNext()) {

            String name = cursor.getString(1);
            city_names.add(name);

        }

        try {

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.layout_for_spinner_color, city_names);

            adapter.setDropDownViewResource(R.layout.layout_dropdown_spinner);
            final Spinner spinner = findViewById(R.id.toolbar_spinner);
            spinner.setAdapter(adapter);

            try {

                spinner.setSelection(preferenceManager.getInteger("spinner_position") - 1);

            } catch (IndexOutOfBoundsException e) {

            }

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selected_city = spinner.getSelectedItem().toString();

                    spinnerItem = selected_city;
                    int spinnerPosition = spinner.getSelectedItemPosition();
                    preferenceManager.putInteger("spinner_position", spinnerPosition);

                    if (String.valueOf(preferenceManager.getInteger("spinner_position")) == null) {

                        Response(city_names.get(0));

                    } else if (String.valueOf(preferenceManager.getInteger("spinner_position")) != null) {

                        int getSpinnerPosition = preferenceManager.getInteger("spinner_position");
                        spinnerItem = city_names.get(getSpinnerPosition);

                        Response(spinnerItem);

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {


                }
            });


        } catch (NullPointerException e) {

        }
    }

    //Todo Location Database From
    private void deleteLocationFromDatabase(String spinner_position) {

        spinnerDatabase.removeLocationDataListItem(spinner_position);

    }

    private void setupWidgets() {

        clock = findViewById(R.id.txt_clock);
        temprature = findViewById(R.id.current_degree);
        weather_stiuation = findViewById(R.id.weather_stiuation);
        weather_stiuation_image = findViewById(R.id.weather_stiuation_image);
        wind_speed1 = findViewById(R.id.txt_wind_speed);
        wind_speed2 = findViewById(R.id.txt_wind_value);
        feels_like = findViewById(R.id.txt_feelslike);
        sunrise = findViewById(R.id.txt_sunrise);
        pressure = findViewById(R.id.txt_pressure);
        sunset = findViewById(R.id.txt_sunset);
        dailyRecyclerView = findViewById(R.id.daily_recyclerView);
        hourlyRecyclerView = findViewById(R.id.hourly_recyclerView);
        windRecyclerView = findViewById(R.id.wind_recyclerView);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar_for_drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        txt_km_h = findViewById(R.id.txt_km_h);
        txt_m_sec = findViewById(R.id.txt_m_sec);
        ic_share = findViewById(R.id.share_image);
        sunriseSunsetView = findViewById(R.id.ssv);
        sunriseView = findViewById(R.id.sunriseView_time);
        sunsetView = findViewById(R.id.sunsetView_time);
        durationOFDay = findViewById(R.id.durationOfDay_time);
        daily_forecast = findViewById(R.id.txt_daily_forecast);
        hourly_forecast = findViewById(R.id.txt_hourly_forecast);
        wind_forecast = findViewById(R.id.txt_wind_forecast);
        lineChart = findViewById(R.id.lineChart);
        week1 = findViewById(R.id.week1);
        week2 = findViewById(R.id.week2);
        week3 = findViewById(R.id.week3);
        week4 = findViewById(R.id.week4);
        week5 = findViewById(R.id.week5);
        week6 = findViewById(R.id.week6);
        week7 = findViewById(R.id.week7);
        feels_like_c = findViewById(R.id.txt_feelslike_c);
        pressure_c = findViewById(R.id.txt_pressure_c);
        sunrise_c = findViewById(R.id.txt_sunrise_c);
        sunset_c = findViewById(R.id.txt_sunset_c);
        sunriseView_time_c = findViewById(R.id.sunriseView_time_c);
        sunsetView_time_c = findViewById(R.id.sunsetView_time_c);
        duration_of_the_day_c = findViewById(R.id.durationOfDay_time_c);

    }

    private void setupShare() {

        ic_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Open Weather Channel");
                intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.kivitool.openweatherchannel");
                startActivity(Intent.createChooser(intent, "Share Open Weather Channel"));

            }
        });

    }

    private void setupAdapters() {
        dailyRecyclerView.setHasFixedSize(false);
        dailyRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        hourlyRecyclerView.setHasFixedSize(false);
        hourlyRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        windRecyclerView.setHasFixedSize(false);
        windRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    }

    public void startService() {

        Intent serviceIntent = new Intent(context, NotificationService.class);
        serviceIntent.putExtra("input", "notification");
        startService(serviceIntent);
    }

    public void stopService() {

        Intent serviceIntent = new Intent(context, NotificationService.class);
        stopService(serviceIntent);

    }

    private void showNotification() {
        if (preferenceManager.getBoolean("switch_boolean")) {

            startService();

        } else {

            stopService();

        }
    }

    private String toUpperCase(String string) {

        String capital = string.substring(0, 1).toUpperCase() + string.substring(1);

        return capital;

    }

    /**
     * Very important method: When page is reloaded, it will not be slowly
     */

    private void reloadPageWithFast() {

        if (preferenceManager.getString("current_pressure") != null) {


            if (preferenceManager.getBoolean("radio_fahrenheit")) {

                temprature.setText(preferenceManager.getInteger("current_temprature_f") + "°F");
                feels_like.setText(": " + preferenceManager.getInteger("current_temprature_f") + "°F");

            } else {

                temprature.setText(preferenceManager.getInteger("current_temprature_c") + "°C");
                feels_like.setText(": " + preferenceManager.getInteger("current_temprature_c") + "°C");

            }

            weather_stiuation.setText(preferenceManager.getString("current_description") + "");

            /**
             * this cases for wind speed metr/second or km/hour
             * visiblity is for km/hour and metr/second
             */

            if (preferenceManager.getBoolean("radio_km_h")) {

                wind_speed1.setText(preferenceManager.getString("current_wind_km") + " " + getString(R.string.km_h) + " " + preferenceManager.getString("current_wind_direction"));
                wind_speed2.setText(preferenceManager.getString("current_wind_km") + "");

                txt_m_sec.setVisibility(View.INVISIBLE);
                txt_km_h.setVisibility(View.VISIBLE);

            } else {

                wind_speed1.setText(preferenceManager.getString("current_wind_m") + " " + getString(R.string.m_sec) + " " + preferenceManager.getString("current_wind_direction"));
                wind_speed2.setText(preferenceManager.getString("current_wind_m") + "");

                txt_km_h.setVisibility(View.INVISIBLE);
                txt_m_sec.setVisibility(View.VISIBLE);

            }

            clock.setText(preferenceManager.getString("current_date") + "/(" + preferenceManager.getString("current_time_zone") + ")");
            pressure.setText(": " + preferenceManager.getString("current_pressure") + " hPa");
            sunrise.setText(": " + preferenceManager.getString("current_sunrise") + "");
            sunset.setText(": " + preferenceManager.getString("current_sunset") + "");


            if (preferenceManager.getString("current_icon").equals("01d")) {

                weather_stiuation_image.setImageResource(R.drawable.icon_01d);

            } else if (preferenceManager.getString("current_icon").equals("01n")) {

                weather_stiuation_image.setImageResource(R.drawable.icon_01n);

            } else if (preferenceManager.getString("current_icon").equals("02d")) {

                weather_stiuation_image.setImageResource(R.drawable.icon_02d);

            } else if (preferenceManager.getString("current_icon").equals("02n")) {

                weather_stiuation_image.setImageResource(R.drawable.icon_02n);

            } else if (preferenceManager.getString("current_icon").equals("03d")) {

                weather_stiuation_image.setImageResource(R.drawable.icon03d);

            } else if (preferenceManager.getString("current_icon").equals("03n")) {

                weather_stiuation_image.setImageResource(R.drawable.icon03d);

            } else if (preferenceManager.getString("current_icon").equals("04d") || preferenceManager.getString("current_icon").equals("04n")) {

                weather_stiuation_image.setImageResource(R.drawable.icon_04n);

            } else if (preferenceManager.getString("current_icon").equals("09d") || preferenceManager.getString("current_icon").equals("09n")) {

                weather_stiuation_image.setImageResource(R.drawable.icon_9d);

            } else if (preferenceManager.getString("current_icon").equals("10d")) {

                weather_stiuation_image.setImageResource(R.drawable.icon_10d);

            } else if (preferenceManager.getString("current_icon").equals("10n")) {

                weather_stiuation_image.setImageResource(R.drawable.icon_10n);

            } else if (preferenceManager.getString("current_icon").equals("11d")) {

                weather_stiuation_image.setImageResource(R.drawable.icon_11d);

            } else if (preferenceManager.getString("current_icon").equals("11n")) {

                weather_stiuation_image.setImageResource(R.drawable.icon_11n);

            } else if (preferenceManager.getString("current_icon").equals("13d") || preferenceManager.getString("current_icon").equals("13n")) {

                weather_stiuation_image.setImageResource(R.drawable.icon_13n);

            } else if (preferenceManager.getString("current_icon").equals("50d") || preferenceManager.getString("current_icon").equals("50n")) {

                weather_stiuation_image.setImageResource(R.drawable.icon_50d);

            }


            if (preferenceManager.getString("current_icon").equals("01d") || preferenceManager.getString("current_icon").equals("01n")) {

                drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.clean_sky));

            } else if (preferenceManager.getString("current_icon").equals("02d") || preferenceManager.getString("current_icon").equals("02n")) {

                drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.sunny_and_cloudy));

            } else if (preferenceManager.getString("current_icon").equals("03d") || preferenceManager.getString("current_icon").equals("03n") ||
                    preferenceManager.getString("current_icon").equals("04d") || preferenceManager.getString("current_icon").equals("04n")) {

                drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.cloudly));

            } else if (preferenceManager.getString("current_icon").equals("09d") || preferenceManager.getString("current_icon").equals("09n") ||
                    preferenceManager.getString("current_icon").equals("10d") || preferenceManager.getString("current_icon").equals("10n")) {

                drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.rainy));

            } else if (preferenceManager.getString("current_icon").equals("11d") || preferenceManager.getString("current_icon").equals("11n")) {

                drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.thunderstorm));

            } else if (preferenceManager.getString("current_icon").equals("13d") || preferenceManager.getString("current_icon").equals("13n")) {

                drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.snowy));

            } else if (preferenceManager.getString("current_icon").equals("50d") || preferenceManager.getString("current_icon").equals("50n")) {

                drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.mist));

            }

        }

        // week name
        String week1_pref = preferenceManager.getString("week1_name_for_lineChart");
        if (week1_pref.equals("Sat") || week1_pref.equals("Sun") || week1_pref.equals("Ş.") || week1_pref.equals("B.") ||
                week1_pref.equals("cб") || week1_pref.equals("вc") || week1_pref.equals("Sa.") || week1_pref.equals("So.") ||
                week1_pref.equals("Cmt") || week1_pref.equals("Paz")) {

            week1.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            week1.setTypeface(Typeface.DEFAULT_BOLD);

        }

        String week2_pref = preferenceManager.getString("week2_name_for_lineChart");
        if (week2_pref.equals("Sat") || week2_pref.equals("Sun") || week2_pref.equals("Ş.") || week2_pref.equals("B.") ||
                week2_pref.equals("cб") || week2_pref.equals("вc") || week2_pref.equals("Sa.") || week2_pref.equals("So.") ||
                week2_pref.equals("Cmt") || week2_pref.equals("Paz")) {

            week2.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            week2.setTypeface(Typeface.DEFAULT_BOLD);

        }

        String week3_pref = preferenceManager.getString("week3_name_for_lineChart");
        if (week3_pref.equals("Sat") || week3_pref.equals("Sun") || week3_pref.equals("Ş.") || week3_pref.equals("B.") ||
                week3_pref.equals("cб") || week3_pref.equals("вc") || week3_pref.equals("Sa.") || week3_pref.equals("So.") ||
                week3_pref.equals("Cmt") || week3_pref.equals("Paz")) {

            week3.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            week3.setTypeface(Typeface.DEFAULT_BOLD);

        }

        String week4_pref = preferenceManager.getString("week4_name_for_lineChart");
        if (week4_pref.equals("Sat") || week4_pref.equals("Sun") || week4_pref.equals("Ş.") || week4_pref.equals("B.") ||
                week4_pref.equals("cб") || week4_pref.equals("вc") || week4_pref.equals("Sa.") || week4_pref.equals("So.") ||
                week4_pref.equals("Cmt") || week4_pref.equals("Paz")) {

            week4.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            week4.setTypeface(Typeface.DEFAULT_BOLD);

        }

        String week5_pref = preferenceManager.getString("week5_name_for_lineChart");
        if (week5_pref.equals("Sat") || week5_pref.equals("Sun") || week5_pref.equals("Ş.") || week5_pref.equals("B.") ||
                week5_pref.equals("cб") || week5_pref.equals("вc") || week5_pref.equals("Sa.") || week5_pref.equals("So.") ||
                week5_pref.equals("Cmt") || week5_pref.equals("Paz")) {

            week5.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            week5.setTypeface(Typeface.DEFAULT_BOLD);

        }

        String week6_pref = preferenceManager.getString("week6_name_for_lineChart");
        if (week6_pref.equals("Sat") || week6_pref.equals("Sun") || week6_pref.equals("Ş.") || week6_pref.equals("B.") ||
                week6_pref.equals("cб") || week6_pref.equals("вc") || week6_pref.equals("Sa.") || week6_pref.equals("So.") ||
                week6_pref.equals("Cmt") || week6_pref.equals("Paz")) {

            week6.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            week6.setTypeface(Typeface.DEFAULT_BOLD);

        }

        String week7_pref = preferenceManager.getString("week7_name_for_lineChart");
        if (week7_pref.equals("Sat") || week7_pref.equals("Sun") || week7_pref.equals("Ş.") || week7_pref.equals("B.") ||
                week7_pref.equals("cб") || week7_pref.equals("вc") || week7_pref.equals("Sa.") || week7_pref.equals("So.") ||
                week7_pref.equals("Cmt") || week7_pref.equals("Paz")) {

            week7.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            week7.setTypeface(Typeface.DEFAULT_BOLD);

        }


        week1.setText(week1_pref);
        week2.setText(week2_pref);
        week3.setText(week3_pref);
        week4.setText(week4_pref);
        week5.setText(week5_pref);
        week6.setText(week6_pref);
        week7.setText(week7_pref);


        loadHourlyData();
        loadWeeklyData();
        loadWindData();

    }


    private void Response(final String city_name) {


        final Call<CurrentResult> resultCall = ManagerAll.getInstance().getWeatherCurrentInfo(city_name, units, language_api, api_key);
        resultCall.enqueue(new Callback<CurrentResult>() {
            @Override
            public void onResponse(Call<CurrentResult> call, Response<CurrentResult> response) {

                if (response.isSuccessful()) {
                    currentResult = response.body();


                    int cast_temprature = (int) currentResult.getMain().getTemp();

                    /**
                     * change celcius to fahrenheit  ----- °F = (°C × 9/5) + 32
                     */

                    int farenheit_temprature = (cast_temprature * 9 / 5) + 32;
                    if (preferenceManager.getBoolean("radio_fahrenheit")) {

                        temprature.setText(farenheit_temprature + "°F");
                        feels_like.setText(": " + farenheit_temprature + "°F");

                    } else {

                        temprature.setText(cast_temprature + "°C");
                        feels_like.setText(": " + cast_temprature + "°C");

                    }

                    String capitalDescription = toUpperCase(currentResult.getWeather().get(0).getDescription());

                    weather_stiuation.setText(capitalDescription + "");

                    // for direction with degree
                    if (currentResult.getWind().getDeg() > 348.75 ||
                            currentResult.getWind().getDeg() <= 11.25) {

                        direction = getString(R.string.N);

                    } else if (currentResult.getWind().getDeg() > 11.25 &&
                            currentResult.getWind().getDeg() <= 78.75) {

                        direction = getString(R.string.NE);

                    } else if (currentResult.getWind().getDeg() > 78.75 &&
                            currentResult.getWind().getDeg() <= 101.25) {

                        direction = getString(R.string.E);

                    } else if (currentResult.getWind().getDeg() > 101.25 &&
                            currentResult.getWind().getDeg() <= 168.75) {

                        direction = getString(R.string.SE);

                    } else if (currentResult.getWind().getDeg() > 168.75 &&
                            currentResult.getWind().getDeg() <= 191.25) {

                        direction = getString(R.string.S);

                    } else if (currentResult.getWind().getDeg() > 191.25 &&
                            currentResult.getWind().getDeg() <= 258.75) {

                        direction = getString(R.string.SW);

                    } else if (currentResult.getWind().getDeg() > 258.75 &&
                            currentResult.getWind().getDeg() <= 281.25) {

                        direction = getString(R.string.W);

                    } else if (currentResult.getWind().getDeg() > 281.25 &&
                            currentResult.getWind().getDeg() <= 348.75) {

                        direction = getString(R.string.NW);

                    }

                    pressure.setText(": " + currentResult.getMain().getPressure() + " hPa");

                    /**
                     * change wind speed from m/sec to km/h
                     */

                    double wind_speed_value = currentResult.getWind().getSpeed();
                    String formatted_wind_m = String.format("%.1f", wind_speed_value);
                    int km_h = (int) (wind_speed_value * 3.6);
                    String formatted_wind_km = km_h + "";

                    if (preferenceManager.getBoolean("radio_km_h")) {

                        wind_speed1.setText(formatted_wind_km + " " + getString(R.string.km_h) + " " + direction);
                        wind_speed2.setText(formatted_wind_km + "");
                        preferenceManager.putString("current_wind_km", formatted_wind_km);
                        txt_m_sec.setVisibility(View.INVISIBLE);
                        txt_km_h.setVisibility(View.VISIBLE);

                    } else {

                        wind_speed1.setText(formatted_wind_m + " " + getString(R.string.m_sec) + " " + direction);
                        wind_speed2.setText(formatted_wind_m + "");
                        preferenceManager.putString("current_wind_m", formatted_wind_m);
                        txt_km_h.setVisibility(View.INVISIBLE);
                        txt_m_sec.setVisibility(View.VISIBLE);

                    }


                    // current date
                    Calendar calendar = Calendar.getInstance();
                    String current_date = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
                    String time_zone = calendar.getTimeZone().getID();
                    clock.setText(current_date + "/(" + time_zone + ")");

                    /*
                    convert unix time to local time
                     */
                    int int_sunrise = currentResult.getSys().getSunrise();
                    Date date1 = new Date(int_sunrise * 1000L);
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm");
                    simpleDateFormat1.setTimeZone(TimeZone.getTimeZone(calendar.getTimeZone().getID()));
                    formated_date1 = simpleDateFormat1.format(date1);
                    sunrise.setText(": " + formated_date1 + "");

                    int int_sunset = currentResult.getSys().getSunset();
                    Date date2 = new Date(int_sunset * 1000L);
                    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm");
                    simpleDateFormat2.setTimeZone(TimeZone.getTimeZone(calendar.getTimeZone().getID()));
                    formated_date2 = simpleDateFormat2.format(date2);
                    sunset.setText(": " + formated_date2 + "");

                    String stiuation_icon = currentResult.getWeather().get(0).getIcon() + "";


                    // firstly loaded sunrise and sunset time

                    final int view_sunrise_hour_onResponse = Integer.parseInt(formated_date1.substring(0, 2));
                    final int view_sunrise_minute_onResponse = Integer.parseInt(formated_date1.substring(3));
                    final int view_sunset_hour_onResponse = Integer.parseInt(formated_date2.substring(0, 2));
                    final int view_sunset_minute_onResponse = Integer.parseInt(formated_date2.substring(3));

                    sunriseSunsetView.setSunriseTime(new Time(view_sunrise_hour_onResponse, view_sunrise_minute_onResponse));
                    sunriseSunsetView.setSunsetTime(new Time(view_sunset_hour_onResponse, view_sunset_minute_onResponse));

                    sunriseView.setText("" + formated_date1);
                    sunsetView.setText("" + formated_date2);

                    int hour1 = ((view_sunset_hour_onResponse * 60 + view_sunset_minute_onResponse) - (view_sunrise_hour_onResponse * 60 + view_sunrise_minute_onResponse)) / 60;

                    int minute1 = ((view_sunset_hour_onResponse * 60 + view_sunset_minute_onResponse) - (view_sunrise_hour_onResponse * 60 + view_sunrise_minute_onResponse)) - (hour1 * 60);

                    if (hour1 < 0) {
                        hour1 *= -1;
                    }
                    if (minute1 < 0) {
                        minute1 *= -1;
                    }

                    if (minute1 != 0) {
                        durationOFDay.setText(" " + hour1 + " " + getString(R.string.hour) + ", " + minute1 + " " +
                                getString(R.string.minute));
                    } else {
                        durationOFDay.setText(" " + hour1 + " " + getString(R.string.hour));
                    }

                    sunriseSunsetView.setLabelFormatter(new SunriseSunsetLabelFormatter() {
                        @Override
                        public String formatSunriseLabel(@NonNull Time sunrise) {
                            return String.format("%02d:%02d", view_sunrise_hour_onResponse, view_sunrise_minute_onResponse);
                        }

                        @Override
                        public String formatSunsetLabel(@NonNull Time sunset) {
                            return String.format("%02d:%02d", view_sunset_hour_onResponse, view_sunset_minute_onResponse);
                        }
                    });

                    sunriseSunsetView.startAnimate();


                    /**
                     * set weather icon depends on stiuation
                     */

                    if (stiuation_icon.equals("01d")) {

                        weather_stiuation_image.setImageResource(R.drawable.icon_01d);

                    } else if (stiuation_icon.equals("01n")) {

                        weather_stiuation_image.setImageResource(R.drawable.icon_01n);

                    } else if (stiuation_icon.equals("02d")) {

                        weather_stiuation_image.setImageResource(R.drawable.icon_02d);

                    } else if (stiuation_icon.equals("02n")) {

                        weather_stiuation_image.setImageResource(R.drawable.icon_02n);

                    } else if (stiuation_icon.equals("03d")) {

                        weather_stiuation_image.setImageResource(R.drawable.icon03d);

                    } else if (stiuation_icon.equals("03n")) {

                        weather_stiuation_image.setImageResource(R.drawable.icon03d);

                    } else if (stiuation_icon.equals("04d") || stiuation_icon.equals("04n")) {

                        weather_stiuation_image.setImageResource(R.drawable.icon_04n);

                    } else if (stiuation_icon.equals("09d") || stiuation_icon.equals("09n")) {

                        weather_stiuation_image.setImageResource(R.drawable.icon_9d);

                    } else if (stiuation_icon.equals("10d")) {

                        weather_stiuation_image.setImageResource(R.drawable.icon_10d);

                    } else if (stiuation_icon.equals("10n")) {

                        weather_stiuation_image.setImageResource(R.drawable.icon_10n);

                    } else if (stiuation_icon.equals("11d")) {

                        weather_stiuation_image.setImageResource(R.drawable.icon_11d);

                    } else if (stiuation_icon.equals("11n")) {

                        weather_stiuation_image.setImageResource(R.drawable.icon_11n);

                    } else if (stiuation_icon.equals("13d") || stiuation_icon.equals("13n")) {

                        weather_stiuation_image.setImageResource(R.drawable.icon_13n);

                    } else if (stiuation_icon.equals("50d") || stiuation_icon.equals("50n")) {

                        weather_stiuation_image.setImageResource(R.drawable.icon_50d);

                    }

                    if (currentResult.getWeather().get(0).getIcon().equals("01d") || currentResult.getWeather().get(0).getIcon().equals("01n")) {

                        drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.clean_sky));

                    } else if (currentResult.getWeather().get(0).getIcon().equals("02d") || currentResult.getWeather().get(0).getIcon().equals("02n")) {

                        drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.sunny_and_cloudy));

                    } else if (currentResult.getWeather().get(0).getIcon().equals("03d") || currentResult.getWeather().get(0).getIcon().equals("03n") ||
                            currentResult.getWeather().get(0).getIcon().equals("04d") || currentResult.getWeather().get(0).getIcon().equals("04n")) {

                        drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.cloudly));

                    } else if (currentResult.getWeather().get(0).getIcon().equals("09d") || currentResult.getWeather().get(0).getIcon().equals("09n") ||
                            currentResult.getWeather().get(0).getIcon().equals("10d") || currentResult.getWeather().get(0).getIcon().equals("10n")) {

                        drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.rainy));

                    } else if (currentResult.getWeather().get(0).getIcon().equals("11d") || currentResult.getWeather().get(0).getIcon().equals("11n")) {

                        drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.thunderstorm));

                    } else if (currentResult.getWeather().get(0).getIcon().equals("13d") || currentResult.getWeather().get(0).getIcon().equals("13n")) {

                        drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.snowy));

                    } else if (currentResult.getWeather().get(0).getIcon().equals("50d") || currentResult.getWeather().get(0).getIcon().equals("50n")) {

                        drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.mist));

                    }

                    /**
                     * TODO set shared preferensces datas
                     */

                    if (preferenceManager.getBoolean("radio_fahrenheit")) {

                        preferenceManager.putInteger("current_temprature_f", farenheit_temprature);

                    } else {

                        preferenceManager.putInteger("current_temprature_c", cast_temprature);

                    }
                    preferenceManager.putString("current_date", current_date);
                    preferenceManager.putString("current_time_zone", time_zone);
                    preferenceManager.putString("current_description", capitalDescription);
                    String shared_pressure = currentResult.getMain().getPressure() + "";
                    preferenceManager.putString("current_pressure", shared_pressure);
                    preferenceManager.putString("current_wind_direction", direction);
                    preferenceManager.putString("current_sunrise", formated_date1);
                    preferenceManager.putString("current_sunset", formated_date2);
                    preferenceManager.putString("lat", lat);
                    preferenceManager.putString("lon", lon);
                    String shared_icon = currentResult.getWeather().get(0).getIcon();
                    preferenceManager.putString("current_icon", shared_icon);

                    lat = currentResult.getCoord().getLat() + "";
                    lon = currentResult.getCoord().getLon() + "";

                    /*
                    we call this method for lat and lon because we can't get lat and lon with easy way.This is easily way!!!
                     */

                    dailyResponse(lat, lon);
                    hourlyResponse(lat, lon);
                    windResponse(city_name);

                }

            }

            @Override
            public void onFailure(Call<CurrentResult> call, Throwable t) {


                // for sunrise and sunset view
                final int view_sunrise_hour = Integer.parseInt(preferenceManager.getString("current_sunrise").substring(0, 2));
                final int view_sunrise_minute = Integer.parseInt(preferenceManager.getString("current_sunrise").substring(3));
                final int view_sunset_hour = Integer.parseInt(preferenceManager.getString("current_sunset").substring(0, 2));
                final int view_sunset_minute = Integer.parseInt(preferenceManager.getString("current_sunset").substring(3));

                sunriseSunsetView.setSunriseTime(new Time(view_sunrise_hour, view_sunrise_minute));
                sunriseSunsetView.setSunsetTime(new Time(view_sunset_hour, view_sunset_minute));

                sunriseView.setText("" + preferenceManager.getString("current_sunrise"));
                sunsetView.setText("" + preferenceManager.getString("current_sunset"));

                int hour2 = ((view_sunset_hour * 60 + view_sunset_minute) - (view_sunrise_hour * 60 + view_sunrise_minute)) / 60;

                int minute2 = ((view_sunset_hour * 60 + view_sunset_minute) - (view_sunrise_hour * 60 + view_sunrise_minute)) - (hour2 * 60);

                durationOFDay.setText(" " + hour2 + " " + getString(R.string.hour) + ", " + minute2 + " " +
                        getString(R.string.minute));

                sunriseSunsetView.setLabelFormatter(new SunriseSunsetLabelFormatter() {
                    @Override
                    public String formatSunriseLabel(@NonNull Time sunrise) {
                        return String.format("%02d:%02d", view_sunrise_hour, view_sunrise_minute);
                    }

                    @Override
                    public String formatSunsetLabel(@NonNull Time sunset) {
                        return String.format("%02d:%02d", view_sunset_hour, view_sunset_minute);
                    }
                });

                sunriseSunsetView.startAnimate();


                if (preferenceManager.getBoolean("radio_fahrenheit")) {

                    temprature.setText(preferenceManager.getInteger("current_temprature_f") + "°F");
                    feels_like.setText(": " + preferenceManager.getInteger("current_temprature_f") + "°F");

                } else {

                    temprature.setText(preferenceManager.getInteger("current_temprature_c") + "°C");
                    feels_like.setText(": " + preferenceManager.getInteger("current_temprature_c") + "°C");

                }

                weather_stiuation.setText(preferenceManager.getString("current_description") + "");

                /**
                 * this cases for wind speed metr/second or km/hour
                 * visiblity is for km/hour and metr/second
                 */

                if (preferenceManager.getBoolean("radio_km_h")) {

                    wind_speed1.setText(preferenceManager.getString("current_wind_km") + " " + getString(R.string.km_h) + " " + preferenceManager.getString("current_wind_direction"));
                    wind_speed2.setText(preferenceManager.getString("current_wind_km") + "");

                    txt_m_sec.setVisibility(View.INVISIBLE);
                    txt_km_h.setVisibility(View.VISIBLE);

                } else {


                    wind_speed1.setText(preferenceManager.getString("current_wind_m") + " " + getString(R.string.km_h) + " " + preferenceManager.getString("current_wind_direction"));
                    wind_speed2.setText(preferenceManager.getString("current_wind_m") + "");

                    txt_km_h.setVisibility(View.INVISIBLE);
                    txt_m_sec.setVisibility(View.VISIBLE);

                }

                clock.setText(preferenceManager.getString("current_date") + "/(" + preferenceManager.getString("current_time_zone") + ")");
                pressure.setText(": " + preferenceManager.getString("current_pressure") + " hPa");
                sunrise.setText(": " + preferenceManager.getString("current_sunrise") + "");
                sunset.setText(": " + preferenceManager.getString("current_sunset") + "");
                String shared_lat = preferenceManager.getString("lat");
                String shared_lon = preferenceManager.getString("lon");

                preferenceManager.getString("current_icon");

                if (preferenceManager.getString("current_icon").equals("01d")) {

                    weather_stiuation_image.setImageResource(R.drawable.icon_01d);

                } else if (preferenceManager.getString("current_icon").equals("01n")) {

                    weather_stiuation_image.setImageResource(R.drawable.icon_01n);

                } else if (preferenceManager.getString("current_icon").equals("02d")) {

                    weather_stiuation_image.setImageResource(R.drawable.icon_02d);

                } else if (preferenceManager.getString("current_icon").equals("02n")) {

                    weather_stiuation_image.setImageResource(R.drawable.icon_02n);

                } else if (preferenceManager.getString("current_icon").equals("03d")) {

                    weather_stiuation_image.setImageResource(R.drawable.icon03d);

                } else if (preferenceManager.getString("current_icon").equals("03n")) {

                    weather_stiuation_image.setImageResource(R.drawable.icon03d);

                } else if (preferenceManager.getString("current_icon").equals("04d") || preferenceManager.getString("current_icon").equals("04n")) {

                    weather_stiuation_image.setImageResource(R.drawable.icon_04n);

                } else if (preferenceManager.getString("current_icon").equals("09d") || preferenceManager.getString("current_icon").equals("09n")) {

                    weather_stiuation_image.setImageResource(R.drawable.icon_9d);

                } else if (preferenceManager.getString("current_icon").equals("10d")) {

                    weather_stiuation_image.setImageResource(R.drawable.icon_10d);

                } else if (preferenceManager.getString("current_icon").equals("10n")) {

                    weather_stiuation_image.setImageResource(R.drawable.icon_10n);

                } else if (preferenceManager.getString("current_icon").equals("11d")) {

                    weather_stiuation_image.setImageResource(R.drawable.icon_11d);

                } else if (preferenceManager.getString("current_icon").equals("11n")) {

                    weather_stiuation_image.setImageResource(R.drawable.icon_11n);

                } else if (preferenceManager.getString("current_icon").equals("13d") || preferenceManager.getString("current_icon").equals("13n")) {

                    weather_stiuation_image.setImageResource(R.drawable.icon_13n);

                } else if (preferenceManager.getString("current_icon").equals("50d") || preferenceManager.getString("current_icon").equals("50n")) {

                    weather_stiuation_image.setImageResource(R.drawable.icon_50d);

                }


                if (preferenceManager.getString("current_icon").equals("01d") || preferenceManager.getString("current_icon").equals("01n")) {

                    drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.clean_sky));

                } else if (preferenceManager.getString("current_icon").equals("02d") || preferenceManager.getString("current_icon").equals("02n")) {

                    drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.sunny_and_cloudy));

                } else if (preferenceManager.getString("current_icon").equals("03d") || preferenceManager.getString("current_icon").equals("03n") ||
                        preferenceManager.getString("current_icon").equals("04d") || preferenceManager.getString("current_icon").equals("04n")) {

                    drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.cloudly));

                } else if (preferenceManager.getString("current_icon").equals("09d") || preferenceManager.getString("current_icon").equals("09n") ||
                        preferenceManager.getString("current_icon").equals("10d") || preferenceManager.getString("current_icon").equals("10n")) {

                    drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.rainy));

                } else if (preferenceManager.getString("current_icon").equals("11d") || preferenceManager.getString("current_icon").equals("11n")) {

                    drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.thunderstorm));

                } else if (preferenceManager.getString("current_icon").equals("13d") || preferenceManager.getString("current_icon").equals("13n")) {

                    drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.snowy));

                } else if (preferenceManager.getString("current_icon").equals("50d") || preferenceManager.getString("current_icon").equals("50n")) {

                    drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.mist));

                }


                /*
                    we call this method for lat and lon because we can't get lat and lon with easy way.This is easily way!!!
                     */
                dailyResponse(shared_lat, shared_lon);
                hourlyResponse(shared_lat, shared_lon);
                windResponse(city_name);

            }
        });


    }

    /**
     * getting daily informations with helping this method
     */
    private void dailyResponse(String lat, String lon) {


        final Call<WeeklyResult> call = ManagerAll.getInstance().getWeatherWeeklyInformations(lat, lon, "hourly,daily", units, language_api, api_key);
        call.enqueue(new Callback<WeeklyResult>() {
            @Override
            public void onResponse(Call<WeeklyResult> call, Response<WeeklyResult> response) {

                if (response.isSuccessful()) {
                    weeklyResult = response.body();

                    JsonSaveDataWeekly(weeklyResult.getDaily());
                    WeeklyAdapter weeklyAdapter = new WeeklyAdapter(context, weeklyResult.getDaily(), preferenceManager);
                    weeklyAdapter.notifyDataSetChanged();
                    dailyRecyclerView.setAdapter(weeklyAdapter);

                    int int_max = (int) weeklyResult.getDaily().get(0).getTemp().getMax();
                    preferenceManager.putString("first_day_max_temp", (int_max + ""));

                    int int_min = (int) weeklyResult.getDaily().get(0).getTemp().getMin();
                    preferenceManager.putString("first_day_min_temp", (int_min + ""));

                    // this part of code is important for line chart

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE");
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone(calendar.getTimeZone().getID()));

                    int weekNameValue1 = weeklyResult.getDaily().get(1).getDt();
                    Date convertDate1 = new Date(weekNameValue1 * 1000L);
                    String formattedDate1 = simpleDateFormat.format(convertDate1);
                    preferenceManager.putString("week1_name_for_lineChart", formattedDate1);
                    preferenceManager.putInteger("week1_temp_for_lineChart", (int) weeklyResult.getDaily().get(1).getTemp().getDay());

                    int weekNameValue2 = weeklyResult.getDaily().get(2).getDt();
                    Date convertDate2 = new Date(weekNameValue2 * 1000L);
                    String formattedDate2 = simpleDateFormat.format(convertDate2);
                    preferenceManager.putString("week2_name_for_lineChart", formattedDate2);
                    preferenceManager.putInteger("week2_temp_for_lineChart", (int) weeklyResult.getDaily().get(2).getTemp().getDay());

                    int weekNameValue3 = weeklyResult.getDaily().get(3).getDt();
                    Date convertDate3 = new Date(weekNameValue3 * 1000L);
                    String formattedDate3 = simpleDateFormat.format(convertDate3);
                    preferenceManager.putString("week3_name_for_lineChart", formattedDate3);
                    preferenceManager.putInteger("week3_temp_for_lineChart", (int) weeklyResult.getDaily().get(3).getTemp().getDay());


                    int weekNameValue4 = weeklyResult.getDaily().get(4).getDt();
                    Date convertDate4 = new Date(weekNameValue4 * 1000L);
                    String formattedDate4 = simpleDateFormat.format(convertDate4);
                    preferenceManager.putString("week4_name_for_lineChart", formattedDate4);
                    preferenceManager.putInteger("week4_temp_for_lineChart", (int) weeklyResult.getDaily().get(4).getTemp().getDay());


                    int weekNameValue5 = weeklyResult.getDaily().get(5).getDt();
                    Date convertDate5 = new Date(weekNameValue5 * 1000L);
                    String formattedDate5 = simpleDateFormat.format(convertDate5);
                    preferenceManager.putString("week5_name_for_lineChart", formattedDate5);
                    preferenceManager.putInteger("week5_temp_for_lineChart", (int) weeklyResult.getDaily().get(5).getTemp().getDay());


                    int weekNameValue6 = weeklyResult.getDaily().get(6).getDt();
                    Date convertDate6 = new Date(weekNameValue6 * 1000L);
                    String formattedDate6 = simpleDateFormat.format(convertDate6);
                    preferenceManager.putString("week6_name_for_lineChart", formattedDate6);
                    preferenceManager.putInteger("week6_temp_for_lineChart", (int) weeklyResult.getDaily().get(6).getTemp().getDay());


                    int weekNameValue7 = weeklyResult.getDaily().get(7).getDt();
                    Date convertDate7 = new Date(weekNameValue7 * 1000L);
                    String formattedDate7 = simpleDateFormat.format(convertDate7);
                    preferenceManager.putString("week7_name_for_lineChart", formattedDate7);
                    preferenceManager.putInteger("week7_temp_for_lineChart", (int) weeklyResult.getDaily().get(7).getTemp().getDay());


                    // line chart in successfull response

                    lineChart.setDragEnabled(true);
                    lineChart.setScaleEnabled(false);
                    lineChart.setBackground(ContextCompat.getDrawable(context, R.color.white));
                    lineChart.getXAxis().setDrawGridLines(false);
                    lineChart.getXAxis().setEnabled(false);
                    lineChart.getAxisLeft().setDrawGridLines(false);
                    lineChart.getAxisLeft().setEnabled(false);
                    lineChart.getAxisRight().setEnabled(false);
                    lineChart.getAxisRight().setDrawGridLines(false);
                    lineChart.getDescription().setEnabled(false);
                    lineChart.getLegend().setTextColor(ContextCompat.getColor(context, R.color.white));

                    lineChart.setBackground(ContextCompat.getDrawable(context, android.R.color.transparent));


                    ArrayList<Entry> arrayList = new ArrayList<>();
                    arrayList.add(new Entry(0, (int) weeklyResult.getDaily().get(1).getTemp().getDay()));
                    arrayList.add(new Entry(1, (int) weeklyResult.getDaily().get(2).getTemp().getDay()));
                    arrayList.add(new Entry(2, (int) weeklyResult.getDaily().get(3).getTemp().getDay()));
                    arrayList.add(new Entry(3, (int) weeklyResult.getDaily().get(4).getTemp().getDay()));
                    arrayList.add(new Entry(4, (int) weeklyResult.getDaily().get(5).getTemp().getDay()));
                    arrayList.add(new Entry(5, (int) weeklyResult.getDaily().get(6).getTemp().getDay()));
                    arrayList.add(new Entry(6, (int) weeklyResult.getDaily().get(7).getTemp().getDay()));

                    LineDataSet lineDataSet1 = new LineDataSet(arrayList, "°C");


                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(lineDataSet1);

                    LineData data = new LineData(dataSets);
                    lineChart.setData(data);
                    lineChart.getData().setHighlightEnabled(false);
                    lineChart.getData().setValueTextColor(ContextCompat.getColor(context, R.color.white));

                    // set week name in lineChart

                    if (formattedDate1.equals("Sat") || formattedDate1.equals("Sun") || formattedDate1.equals("Ş.") || formattedDate1.equals("B.") ||
                            formattedDate1.equals("cб") || formattedDate1.equals("вc") || formattedDate1.equals("Sa.") || formattedDate1.equals("So.") ||
                            formattedDate1.equals("Cmt") || formattedDate1.equals("Paz")) {

                        week1.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
                        week1.setTypeface(Typeface.DEFAULT_BOLD);

                    }


                    if (formattedDate2.equals("Sat") || formattedDate2.equals("Sun") || formattedDate2.equals("Ş.") || formattedDate2.equals("B.") ||
                            formattedDate2.equals("cб") || formattedDate2.equals("вc") || formattedDate2.equals("Sa.") || formattedDate2.equals("So.") ||
                            formattedDate2.equals("Cmt") || formattedDate2.equals("Paz")) {

                        week2.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
                        week2.setTypeface(Typeface.DEFAULT_BOLD);

                    }


                    if (formattedDate3.equals("Sat") || formattedDate3.equals("Sun") || formattedDate3.equals("Ş.") || formattedDate3.equals("B.") ||
                            formattedDate3.equals("cб") || formattedDate3.equals("вc") || formattedDate3.equals("Sa.") || formattedDate3.equals("So.") ||
                            formattedDate3.equals("Cmt") || formattedDate3.equals("Paz")) {

                        week3.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
                        week3.setTypeface(Typeface.DEFAULT_BOLD);

                    }


                    if (formattedDate4.equals("Sat") || formattedDate4.equals("Sun") || formattedDate4.equals("Ş.") || formattedDate4.equals("B.") ||
                            formattedDate4.equals("cб") || formattedDate4.equals("вc") || formattedDate4.equals("Sa.") || formattedDate4.equals("So.") ||
                            formattedDate4.equals("Cmt") || formattedDate4.equals("Paz")) {

                        week4.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
                        week4.setTypeface(Typeface.DEFAULT_BOLD);

                    }


                    if (formattedDate5.equals("Sat") || formattedDate5.equals("Sun") || formattedDate5.equals("Ş.") || formattedDate5.equals("B.") ||
                            formattedDate5.equals("cб") || formattedDate5.equals("вc") || formattedDate5.equals("Sa.") || formattedDate5.equals("So.") ||
                            formattedDate5.equals("Cmt") || formattedDate5.equals("Paz")) {

                        week5.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
                        week5.setTypeface(Typeface.DEFAULT_BOLD);

                    }


                    if (formattedDate6.equals("Sat") || formattedDate6.equals("Sun") || formattedDate6.equals("Ş.") || formattedDate6.equals("B.") ||
                            formattedDate6.equals("cб") || formattedDate6.equals("вc") || formattedDate6.equals("Sa.") || formattedDate6.equals("So.") ||
                            formattedDate6.equals("Cmt") || formattedDate6.equals("Paz")) {

                        week6.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
                        week6.setTypeface(Typeface.DEFAULT_BOLD);

                    }

                    if (formattedDate7.equals("Sat") || formattedDate7.equals("Sun") || formattedDate7.equals("Ş.") || formattedDate7.equals("B.") ||
                            formattedDate7.equals("cб") || formattedDate7.equals("вc") || formattedDate7.equals("Sa.") || formattedDate7.equals("So.") ||
                            formattedDate7.equals("Cmt") || formattedDate7.equals("Paz")) {

                        week7.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
                        week7.setTypeface(Typeface.DEFAULT_BOLD);

                    }

                    week1.setText(formattedDate1);
                    week2.setText(formattedDate2);
                    week3.setText(formattedDate3);
                    week4.setText(formattedDate4);
                    week5.setText(formattedDate5);
                    week6.setText(formattedDate6);
                    week7.setText(formattedDate7);

                }
            }

            @Override
            public void onFailure(Call<WeeklyResult> call, Throwable t) {

                loadWeeklyData();

            }
        });


    }

    /**
     * getting hourly informations with helping this method
     */
    private void hourlyResponse(String lat, String lon) {


        final Call<WeeklyResult> hourlyCallResult = ManagerAll.getInstance().getWeatherWeeklyInformations(lat, lon, "hourly,daily", units, language_api, api_key);

        hourlyCallResult.enqueue(new Callback<WeeklyResult>() {
            @Override
            public void onResponse(Call<WeeklyResult> call, Response<WeeklyResult> response) {
                if (response.isSuccessful()) {

                    weeklyResult = response.body();
                    JsonSaveDataHourly(weeklyResult.getHourly());

                    HourlyAdapter hourlyAdapter = new HourlyAdapter(context, weeklyResult.getHourly());
                    hourlyAdapter.notifyDataSetChanged();
                    hourlyRecyclerView.setAdapter(hourlyAdapter);
                }
            }

            @Override
            public void onFailure(Call<WeeklyResult> call, Throwable t) {

                loadHourlyData();
                Toast.makeText(context, getString(R.string.check_connection), Toast.LENGTH_SHORT).show();

            }
        });

    }

    /**
     * getting hourly informations with helping this method
     */
    private void windResponse(String city_name) {


        Call<JsonMember5DaysResult> jsonMember5DaysResultCall = ManagerAll.getInstance().getWeather5DaysInformations(city_name, language_api, api_key);

        jsonMember5DaysResultCall.enqueue(new Callback<JsonMember5DaysResult>() {
            @Override
            public void onResponse(Call<JsonMember5DaysResult> call, Response<JsonMember5DaysResult> response) {
                if (response.isSuccessful()) {
                    jsonMember5DaysResult = response.body();
                    JsonSaveDataWind(jsonMember5DaysResult.getList());

                    WindAdapter windAdapter = new WindAdapter(context, jsonMember5DaysResult.getList(), preferenceManager);
                    windAdapter.notifyDataSetChanged();
                    windRecyclerView.setAdapter(windAdapter);

                }
            }

            @Override
            public void onFailure(Call<JsonMember5DaysResult> call, Throwable t) {

                loadWindData();

            }
        });

    }


    /**
     * current location for notification
     */


    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_CODE);

            return;
        }

        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;

                    String lat_for_notification = String.format("%.2f", currentLocation.getLatitude());
                    String lon_for_notification = String.format("%.2f", currentLocation.getLongitude());

                    preferenceManager.putString("local_lat", lat_for_notification);
                    preferenceManager.putString("local_lan", lon_for_notification);

                    currentResponseWithLatLon(lat_for_notification, lon_for_notification);
                    dailyResponseForNotification(lat_for_notification, lon_for_notification);
                    dailyResponseForWidgetFirstTime(lat_for_notification, lon_for_notification);

                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    fetchLastLocation();

                }

                break;

        }
    }

    private void currentResponseWithLatLon(String lat, String lon) {

        final Call<CurrentResult> currentResultCall = ManagerAll.getInstance().getWeatherInformationsWithLatLon(lat, lon, units, language_api, api_key);

        currentResultCall.enqueue(new Callback<CurrentResult>() {
            @Override
            public void onResponse(Call<CurrentResult> call, Response<CurrentResult> response) {
                if (response.isSuccessful()) {

                    current_notificationResult = response.body();

                    int notification_temprature = (int) current_notificationResult.getMain().getTemp();
                    preferenceManager.putInteger("current_temprature_notification", notification_temprature);
                    preferenceManager.putString("current_description_notification", current_notificationResult.getWeather().get(0).getDescription());
                    preferenceManager.putString("city_name_for_notification", current_notificationResult.getName());
                    String shared_icon_notification = current_notificationResult.getWeather().get(0).getIcon();
                    preferenceManager.putString("current_icon_notification", shared_icon_notification);
                    preferenceManager.putInteger("current_notification_wind_speed", (int) current_notificationResult.getWind().getSpeed());

                }
            }

            @Override
            public void onFailure(Call<CurrentResult> call, Throwable t) {

            }
        });

    }

    private void dailyResponseForNotification(String lat, String lon) {


        final Call<WeeklyResult> callNotification = ManagerAll.getInstance().getWeatherWeeklyInformations(lat, lon, "hourly,daily", units, language_api, api_key);
        callNotification.enqueue(new Callback<WeeklyResult>() {
            @Override
            public void onResponse(Call<WeeklyResult> call, Response<WeeklyResult> response) {

                if (response.isSuccessful()) {

                    weekly_notificationResult = response.body();

                    int int_max_temp = (int) weekly_notificationResult.getDaily().get(0).getTemp().getMax();
                    preferenceManager.putString("first_day_max_temp_for_notification", (int_max_temp + ""));

                    int int_min_temp = (int) weekly_notificationResult.getDaily().get(0).getTemp().getMin();
                    preferenceManager.putString("first_day_min_temp_for_notification", (int_min_temp + ""));


                }
            }

            @Override
            public void onFailure(Call<WeeklyResult> call, Throwable t) {


            }
        });


    }

    /**
     * Warning: This method is for uploading items to widget in the first time
     * and in the widgets, we don't have option(RecyclerView and ListView). That's why we write it step-by-step
     *
     * @param lat
     * @param lon
     */

    private void dailyResponseForWidgetFirstTime(String lat, String lon) {

        final Call<WeeklyResult> callForWidget = ManagerAll.getInstance().getWeatherWeeklyInformations(lat, lon, "hourly,daily", units, language_api, api_key);
        callForWidget.enqueue(new Callback<WeeklyResult>() {
            @Override
            public void onResponse(Call<WeeklyResult> call, Response<WeeklyResult> response) {

                if (response.isSuccessful()) {

                    weekly_widgetResult = response.body();
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE");
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone(calendar.getTimeZone().getID()));


                    preferenceManager.putInteger("week_temp_forDailyWidget", (int) weekly_widgetResult.getDaily().get(0).getTemp().getDay());
                    preferenceManager.putString("week_description_forDailyWidget", weekly_widgetResult.getDaily().get(0).getWeather().get(0).getDescription().substring(0, 1).toUpperCase() +
                            weekly_widgetResult.getDaily().get(0).getWeather().get(0).getDescription().substring(1).toLowerCase());
                    preferenceManager.putString("week_icon_forDailyWidget", weekly_widgetResult.getDaily().get(0).getWeather().get(0).getIcon());
                    preferenceManager.putInteger("week_uv_index_forDailyWidget", (int) weekly_widgetResult.getDaily().get(0).getUvi());


                    int weekNameValue1 = weekly_widgetResult.getDaily().get(1).getDt();
                    Date convertDate1 = new Date(weekNameValue1 * 1000L);
                    String formattedDate1 = simpleDateFormat.format(convertDate1);
                    preferenceManager.putString("week_name_for_widget1", formattedDate1);

                    int int_max_temp1 = (int) weekly_widgetResult.getDaily().get(1).getTemp().getMax();
                    preferenceManager.putString("max_temp_for_widget1", (int_max_temp1 + ""));

                    int int_min_temp1 = (int) weekly_widgetResult.getDaily().get(1).getTemp().getMin();
                    preferenceManager.putString("min_temp_for_widget1", (int_min_temp1 + ""));

                    String situation_icon1 = weekly_widgetResult.getDaily().get(1).getWeather().get(0).getIcon();
                    preferenceManager.putString("weather_situation_icon_for_widget1", situation_icon1);

                    int weekNameValue2 = weekly_widgetResult.getDaily().get(2).getDt();
                    Date convertDate2 = new Date(weekNameValue2 * 1000L);
                    String formattedDate2 = simpleDateFormat.format(convertDate2);
                    preferenceManager.putString("week_name_for_widget2", formattedDate2);

                    int int_max_temp2 = (int) weekly_widgetResult.getDaily().get(2).getTemp().getMax();
                    preferenceManager.putString("max_temp_for_widget2", (int_max_temp2 + ""));

                    int int_min_temp2 = (int) weekly_widgetResult.getDaily().get(2).getTemp().getMin();
                    preferenceManager.putString("min_temp_for_widget2", (int_min_temp2 + ""));

                    String situation_icon2 = weekly_widgetResult.getDaily().get(2).getWeather().get(0).getIcon();
                    preferenceManager.putString("weather_situation_icon_for_widget2", situation_icon2);

                    int weekNameValue3 = weekly_widgetResult.getDaily().get(3).getDt();
                    Date convertDate3 = new Date(weekNameValue3 * 1000L);
                    String formattedDate3 = simpleDateFormat.format(convertDate3);
                    preferenceManager.putString("week_name_for_widget3", formattedDate3);

                    int int_max_temp3 = (int) weekly_widgetResult.getDaily().get(3).getTemp().getMax();
                    preferenceManager.putString("max_temp_for_widget3", (int_max_temp3 + ""));

                    int int_min_temp3 = (int) weekly_widgetResult.getDaily().get(3).getTemp().getMin();
                    preferenceManager.putString("min_temp_for_widget3", (int_min_temp3 + ""));

                    String situation_icon3 = weekly_widgetResult.getDaily().get(3).getWeather().get(0).getIcon();
                    preferenceManager.putString("weather_situation_icon_for_widget3", situation_icon3);

                    int weekNameValue4 = weekly_widgetResult.getDaily().get(4).getDt();
                    Date convertDate4 = new Date(weekNameValue4 * 1000L);
                    String formattedDate4 = simpleDateFormat.format(convertDate4);
                    preferenceManager.putString("week_name_for_widget4", formattedDate4);

                    int int_max_temp4 = (int) weekly_widgetResult.getDaily().get(4).getTemp().getMax();
                    preferenceManager.putString("max_temp_for_widget4", (int_max_temp4 + ""));

                    int int_min_temp4 = (int) weekly_widgetResult.getDaily().get(4).getTemp().getMin();
                    preferenceManager.putString("min_temp_for_widget4", (int_min_temp4 + ""));

                    String situation_icon4 = weekly_widgetResult.getDaily().get(4).getWeather().get(0).getIcon();
                    preferenceManager.putString("weather_situation_icon_for_widget4", situation_icon4);

                    int weekNameValue5 = weekly_widgetResult.getDaily().get(5).getDt();
                    Date convertDate5 = new Date(weekNameValue5 * 1000L);
                    String formattedDate5 = simpleDateFormat.format(convertDate5);
                    preferenceManager.putString("week_name_for_widget5", formattedDate5);

                    int int_max_temp5 = (int) weekly_widgetResult.getDaily().get(5).getTemp().getMax();
                    preferenceManager.putString("max_temp_for_widget5", (int_max_temp5 + ""));

                    int int_min_temp5 = (int) weekly_widgetResult.getDaily().get(5).getTemp().getMin();
                    preferenceManager.putString("min_temp_for_widget5", (int_min_temp5 + ""));

                    String situation_icon5 = weekly_widgetResult.getDaily().get(5).getWeather().get(0).getIcon();
                    preferenceManager.putString("weather_situation_icon_for_widget5", situation_icon5);

                }
            }

            @Override
            public void onFailure(Call<WeeklyResult> call, Throwable t) {


            }
        });

    }


    private void setupBottomNavigationViewEx() {

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_viewEx);
        bottomNavigationView.setSelectedItemId(R.id.id_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.id_hourly:

                        startActivity(new Intent(context, Hourly.class));
                        finish();

                        break;

                    case R.id.id_wind:

                        startActivity(new Intent(context, Wind.class));
                        finish();

                        break;

                    case R.id.id_daily:

                        startActivity(new Intent(context, Daily.class));
                        finish();
                        break;
                }

                return false;

            }
        });

    }


    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

            drawerLayout.closeDrawer(GravityCompat.START);

        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.add_new_location:
                startActivity(new Intent(context, AddNewLocation.class));
                break;

            case R.id.about_the_application:
                startActivity(new Intent(context, AboutTheApplication.class));
                break;

            case R.id.my_location:
                startActivity(new Intent(context, MyLocation.class));
                break;

            case R.id.delete_location:

                if (spinnerDatabase.getData().getCount() > 1) {

                    deleteLocationFromDatabase(spinnerItem);

                } else {

                    Toast.makeText(context, getString(R.string.cant_delete_location), Toast.LENGTH_SHORT).show();

                }

                spinnerSetAdapter();

                break;

            case R.id.settings:
                startActivity(new Intent(context, Settings.class));
                break;

            case R.id.contact_us:
                startActivity(new Intent(context, ContactUs.class));
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }


    // <<<<<------------------------------------------------- All Shared Preference set up methods ------------------------------------------------->>>>>

    /**
     * Data saved method
     *
     * @param hourlyItemList
     */

    private void JsonSaveDataHourly(List<HourlyItem> hourlyItemList) {

        SharedPreferences sharedPreferences = getSharedPreferences("hourly_item_list", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonArray = gson.toJson(hourlyItemList);
        editor.putString("task_list", jsonArray);
        editor.apply();

    }

    private void loadHourlyData() {

        SharedPreferences sharedPreferences = getSharedPreferences("hourly_item_list", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task_list", null);
        Type type = new TypeToken<ArrayList<HourlyItem>>() {
        }.getType();

        hourlyItemList = gson.fromJson(json, type);

        HourlyAdapter hourlyAdapter = new HourlyAdapter(context, hourlyItemList);

        hourlyRecyclerView.setAdapter(hourlyAdapter);

        hourlyAdapter.notifyDataSetChanged();

    }


    private void JsonSaveDataWeekly(List<DailyItem> dailyItemList) {

        SharedPreferences sharedPreferences = getSharedPreferences("daily_item_list", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String jsonArray = gson.toJson(dailyItemList);

        editor.putString("task_list", jsonArray);
        editor.apply();

    }

    private void loadWeeklyData() {

        SharedPreferences sharedPreferences1 = getSharedPreferences("daily_item_list", Context.MODE_PRIVATE);
        Gson gson1 = new Gson();
        String json1 = sharedPreferences1.getString("task_list", null);
        Type type1 = new TypeToken<ArrayList<DailyItem>>() {
        }.getType();

        dailyItemList = gson1.fromJson(json1, type1);

        WeeklyAdapter weeklyAdapter = new WeeklyAdapter(context, dailyItemList, preferenceManager);

        dailyRecyclerView.setAdapter(weeklyAdapter);

        weeklyAdapter.notifyDataSetChanged();

    }


    private void JsonSaveDataWind(List<ListItem> listItems) {

        SharedPreferences sharedPreferences2 = getSharedPreferences("list_data_wind", MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();

        Gson gson2 = new Gson();
        String jsonArray2 = gson2.toJson(listItems);

        editor2.putString("task_list", jsonArray2);

        editor2.apply();

    }

    private void loadWindData() {

        SharedPreferences sharedPreferences2 = getSharedPreferences("list_data_wind", MODE_PRIVATE);

        Gson gson2 = new Gson();
        String json2 = sharedPreferences2.getString("task_list", null);

        Type type2 = new TypeToken<ArrayList<ListItem>>() {
        }.getType();

        listItems = gson2.fromJson(json2, type2);

        WindAdapter windAdapter = new WindAdapter(context, listItems, preferenceManager);

        windRecyclerView.setAdapter(windAdapter);

        windAdapter.notifyDataSetChanged();

    }

    private void lineChart(LineChart lineChart) {

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setBackground(ContextCompat.getDrawable(context, R.color.white));
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setEnabled(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setTextColor(ContextCompat.getColor(context, R.color.white));
        lineChart.setBackground(ContextCompat.getDrawable(context, android.R.color.transparent));


        ArrayList<Entry> arrayList = new ArrayList<>();
        arrayList.add(new Entry(0, preferenceManager.getInteger("week1_temp_for_lineChart")));
        arrayList.add(new Entry(1, preferenceManager.getInteger("week2_temp_for_lineChart")));
        arrayList.add(new Entry(2, preferenceManager.getInteger("week3_temp_for_lineChart")));
        arrayList.add(new Entry(3, preferenceManager.getInteger("week4_temp_for_lineChart")));
        arrayList.add(new Entry(4, preferenceManager.getInteger("week5_temp_for_lineChart")));
        arrayList.add(new Entry(5, preferenceManager.getInteger("week6_temp_for_lineChart")));
        arrayList.add(new Entry(6, preferenceManager.getInteger("week7_temp_for_lineChart")));

        LineDataSet lineDataSet1 = new LineDataSet(arrayList, "°C");


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.getData().setHighlightEnabled(false);
        lineChart.getData().setValueTextColor(ContextCompat.getColor(context, R.color.white));


    }

    /**
     * change default items which is used in changing locale place in MainActivity
     */
    private void changeDefaultText() {

        daily_forecast.setText(R.string.daily_forecast);
        hourly_forecast.setText(R.string.hourly_forecast);
        wind_forecast.setText(R.string.wind_forecast);
        txt_m_sec.setText(getString(R.string.m_sec));
        txt_km_h.setText(getString(R.string.km_h));
        feels_like_c.setText(getString(R.string.feels_like));
        pressure_c.setText(getString(R.string.pressure));
        sunrise_c.setText(getString(R.string.sunrise));
        sunset_c.setText(getString(R.string.sunset));
        sunsetView_time_c.setText(getString(R.string.sunset));
        sunriseView_time_c.setText(getString(R.string.sunrise));
        duration_of_the_day_c.setText(getString(R.string.duration_of_the_day));

    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.on_create_option, menu);

        this.optionMenu = menu;

        MenuItem settingItem = optionMenu.findItem(R.id.settingsOption);
        MenuItem helpItem = optionMenu.findItem(R.id.help);

        settingItem.setTitle(getString(R.string.settings));
        helpItem.setTitle(getString(R.string.help));

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.settingsOption:
                startActivity(new Intent(context, Settings.class));
                break;

            case R.id.help:

                Intent helpIntent = new Intent(context, Help.class);
                startActivity(helpIntent);

                break;

        }

        return super.onOptionsItemSelected(item);

    }

    private void setTextToNavigationViewItems() {

        Menu menu = navigationView.getMenu();
        MenuItem addNewLocation = menu.findItem(R.id.add_new_location);
        MenuItem deleteLocation = menu.findItem(R.id.delete_location);
        MenuItem myLocation = menu.findItem(R.id.my_location);
        MenuItem settings = menu.findItem(R.id.settings);
        MenuItem aboutTheApplication = menu.findItem(R.id.about_the_application);
        MenuItem contactUs = menu.findItem(R.id.contact_us);


        addNewLocation.setTitle(getString(R.string.add_new_location));
        deleteLocation.setTitle(getString(R.string.delete_location));
        myLocation.setTitle(getString(R.string.my_location));
        settings.setTitle(getString(R.string.settings));
        aboutTheApplication.setTitle(getString(R.string.about_the_application));
        contactUs.setTitle(getString(R.string.contact_us));

    }

    /**
     * during lifecycle
     */

    @Override
    protected void onDestroy() {
        stopService();
        super.onDestroy();
    }

    @Override
    protected void onStart() {

        changeLocale(language);
        changeDefaultText();
        setTextToNavigationViewItems();
        super.onStart();

    }

    @Override
    protected void onRestart() {

        changeLocale(language);
        changeDefaultText();
        setTextToNavigationViewItems();
        super.onRestart();
    }
}
