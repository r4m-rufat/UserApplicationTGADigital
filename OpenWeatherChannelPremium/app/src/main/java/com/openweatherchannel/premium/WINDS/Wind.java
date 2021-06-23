package com.openweatherchannel.premium.WINDS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
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
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.openweatherchannel.premium.API.ApiKeys;
import com.openweatherchannel.premium.API.ManagerAll;
import com.openweatherchannel.premium.AdapTers.ActivityWindAdapter;
import com.openweatherchannel.premium.Daily.Daily;
import com.openweatherchannel.premium.DataBase.SpinnerDatabase;
import com.openweatherchannel.premium.DrawerLayoutItems.AboutTheApplication;
import com.openweatherchannel.premium.DrawerLayoutItems.AddNewLocation;
import com.openweatherchannel.premium.DrawerLayoutItems.ContactUs;
import com.openweatherchannel.premium.DrawerLayoutItems.MyLocation;
import com.openweatherchannel.premium.DrawerLayoutItems.Settings;
import com.openweatherchannel.premium.Home.MainActivity;
import com.openweatherchannel.premium.Hourly.Hourly;
import com.openweatherchannel.premium.MenuItem.Help;
import com.openweatherchannel.premium.Models.Current.CurrentResult;
import com.openweatherchannel.premium.Models.DaysForWind.JsonMember5DaysResult;
import com.openweatherchannel.premium.Models.DaysForWind.ListItem;
import com.openweatherchannel.premium.UTILS.PreferenceManager;
import com.openweatherchannel.premium.R;
import com.openweatherchannel.premium.UTILS.SetupLanguagesForAPI;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Wind extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private JsonMember5DaysResult jsonMember5DaysResult;
    private RecyclerView wind_recyclerView;
    PreferenceManager preferenceManager;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    CurrentResult currentResult;
    public static final String units = "metric";
    private List<ListItem> listItems;
    private String spinnerItem;
    private ImageView ic_share;
    private TextView wind_forecast;
    private Locale myLocale;
    private ApiKeys apiKeys;

    // Database
    SpinnerDatabase spinnerDatabase;

    // varaibles
    private String api_key, language, language_api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wind);
        context = this;


        preferenceManager = new PreferenceManager(context);

        // selected language
        language = preferenceManager.getString("language");
        SetupLanguagesForAPI setupLanguagesForAPI = new SetupLanguagesForAPI();
        setupLanguagesForAPI.setupLanguage(language, preferenceManager);
        language_api = preferenceManager.getString("language_api");


        spinnerDatabase = new SpinnerDatabase(context);

        // api keys
        apiKeys = new ApiKeys(context);
        api_key = apiKeys.apiKeys();

        setupWidgets();
        setupShare();
        setupBottomNavigationViewEx();

        if (preferenceManager.getString("for_reload") != null) {

            loadWindData();

        }

        setBackground();

        spinnerColor();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


    }

    private void windResponse(String city_name) {

        Call<JsonMember5DaysResult> jsonMember5DaysResultCall = ManagerAll.getInstance().getWeather5DaysInformations(city_name, language_api, api_key);

        jsonMember5DaysResultCall.enqueue(new Callback<JsonMember5DaysResult>() {
            @Override
            public void onResponse(Call<JsonMember5DaysResult> call, Response<JsonMember5DaysResult> response) {
                if (response.isSuccessful()) {
                    jsonMember5DaysResult = response.body();
                    JsonSaveDataWind(jsonMember5DaysResult.getList());

                    // for reload page, need a default object
                    preferenceManager.putString("for_reload_wind_page", jsonMember5DaysResult.getList().get(0).getDtTxt());

                    ActivityWindAdapter activityWindAdapter = new ActivityWindAdapter(context, jsonMember5DaysResult.getList(), preferenceManager);
                    activityWindAdapter.notifyDataSetChanged();
                    wind_recyclerView.setAdapter(activityWindAdapter);
                }
            }

            @Override
            public void onFailure(Call<JsonMember5DaysResult> call, Throwable t) {

                loadWindData();

            }
        });
    }

    private void currentData(String city_name) {

        Call<CurrentResult> call = ManagerAll.getInstance().getWeatherCurrentInfo(city_name, units, language_api, api_key);
        call.enqueue(new Callback<CurrentResult>() {
            @Override
            public void onResponse(Call<CurrentResult> call, Response<CurrentResult> response) {
                if (response.isSuccessful()) {

                    currentResult = response.body();

                    preferenceManager.putString("weather_icon", currentResult.getWeather().get(0).getIcon());

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

                }
            }

            @Override
            public void onFailure(Call<CurrentResult> call, Throwable t) {

                if (preferenceManager.getString("weather_icon").equals("01d") || preferenceManager.getString("weather_icon").equals("01n")) {

                    drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.clean_sky));

                } else if (preferenceManager.getString("weather_icon").equals("02d") || preferenceManager.getString("weather_icon").equals("02n")) {

                    drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.sunny_and_cloudy));

                } else if (preferenceManager.getString("weather_icon").equals("03d") || preferenceManager.getString("weather_icon").equals("03n") ||
                        preferenceManager.getString("weather_icon").equals("04d") || preferenceManager.getString("weather_icon").equals("04n")) {

                    drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.cloudly));

                } else if (preferenceManager.getString("weather_icon").equals("09d") || preferenceManager.getString("weather_icon").equals("09n") ||
                        preferenceManager.getString("weather_icon").equals("10d") || preferenceManager.getString("weather_icon").equals("10n")) {

                    drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.rainy));

                } else if (preferenceManager.getString("weather_icon").equals("11d") || preferenceManager.getString("weather_icon").equals("11n")) {

                    drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.thunderstorm));

                } else if (preferenceManager.getString("weather_icon").equals("13d") || preferenceManager.getString("weather_icon").equals("13n")) {

                    drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.snowy));

                } else if (preferenceManager.getString("weather_icon").equals("50d") || preferenceManager.getString("weather_icon").equals("50n")) {

                    drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.mist));

                }

            }
        });
    }

    private void spinnerColor() {
        final ArrayList<String> city_names = new ArrayList<>();

        Cursor cursor = spinnerDatabase.getData();

        while (cursor.moveToNext()) {

            String name = cursor.getString(1);
            city_names.add(name);
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.layout_for_spinner_color, city_names);

        adapter.setDropDownViewResource(R.layout.layout_dropdown_spinner);
        final Spinner spinner = findViewById(R.id.toolbar_spinner);
        spinner.setAdapter(adapter);

        try {

            spinner.setSelection(preferenceManager.getInteger("spinner_position"));

        } catch (NullPointerException e) {

        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected_city = spinner.getSelectedItem().toString();

                spinnerItem = selected_city;
                int spinnerPosition = spinner.getSelectedItemPosition();
                preferenceManager.putInteger("spinner_position", spinnerPosition);

                if (String.valueOf(preferenceManager.getInteger("spinner_position")) == null) {

                    windResponse(city_names.get(0));
                    currentData(city_names.get(0));

                } else if (String.valueOf(preferenceManager.getInteger("spinner_position")) != null) {

                    int getSpinnerPosition = preferenceManager.getInteger("spinner_position");
                    spinnerItem = city_names.get(getSpinnerPosition);

                    windResponse(spinnerItem);
                    currentData(spinnerItem);

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                windResponse(city_names.get(0));
                currentData(city_names.get(0));

                spinnerItem = city_names.get(0);

            }
        });
    }

    private void deleteLocationFromDatabase(String spinner_position) {

        spinnerDatabase.removeLocationDataListItem(spinner_position);

    }

    private void setSpinnerAdapterAfterDelete() {
        ArrayList<String> city_names = new ArrayList<>();
        Cursor cursor = spinnerDatabase.getData();

        while (cursor.moveToNext()) {

            String name = cursor.getString(1);
            city_names.add(name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.layout_for_spinner_color, city_names);

        adapter.setDropDownViewResource(R.layout.layout_dropdown_spinner);
        final Spinner spinner = findViewById(R.id.toolbar_spinner);
        spinner.setAdapter(adapter);

    }

    private void setupWidgets() {

        //widgets
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar_for_drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        ic_share = findViewById(R.id.share_image);
        wind_forecast = findViewById(R.id.txt_wind_forecast);

        wind_recyclerView = findViewById(R.id.recycler_view_forActivityWind);
        wind_recyclerView.setHasFixedSize(false);
        wind_recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

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


    private void setupBottomNavigationViewEx() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_viewEx);
        bottomNavigationView.clearAnimation();
        bottomNavigationView.setSelectedItemId(R.id.id_wind);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.id_home:
                        context.startActivity(new Intent(context, MainActivity.class));
                        finish();
                        break;

                    case R.id.id_hourly:
                        context.startActivity(new Intent(context, Hourly.class));
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.add_new_location:

                Intent intent = new Intent(context, AddNewLocation.class);
                startActivity(intent);


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
                setSpinnerAdapterAfterDelete();
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

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

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

        ActivityWindAdapter activityWindAdapter = new ActivityWindAdapter(context, listItems, preferenceManager);

        wind_recyclerView.setAdapter(activityWindAdapter);

        activityWindAdapter.notifyDataSetChanged();


    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.on_create_option, menu);

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


    private void setBackground() {


        if (preferenceManager.getString("weather_icon") != null) {

            if (preferenceManager.getString("weather_icon").equals("01d") || preferenceManager.getString("weather_icon").equals("01n")) {

                drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.clean_sky));

            } else if (preferenceManager.getString("weather_icon").equals("02d") || preferenceManager.getString("weather_icon").equals("02n")) {

                drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.sunny_and_cloudy));

            } else if (preferenceManager.getString("weather_icon").equals("03d") || preferenceManager.getString("weather_icon").equals("03n") ||
                    preferenceManager.getString("weather_icon").equals("04d") || preferenceManager.getString("weather_icon").equals("04n")) {

                drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.cloudly));

            } else if (preferenceManager.getString("weather_icon").equals("09d") || preferenceManager.getString("weather_icon").equals("09n") ||
                    preferenceManager.getString("weather_icon").equals("10d") || preferenceManager.getString("weather_icon").equals("10n")) {

                drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.rainy));

            } else if (preferenceManager.getString("weather_icon").equals("11d") || preferenceManager.getString("weather_icon").equals("11n")) {

                drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.thunderstorm));

            } else if (preferenceManager.getString("weather_icon").equals("13d") || preferenceManager.getString("weather_icon").equals("13n")) {

                drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.snowy));

            } else if (preferenceManager.getString("weather_icon").equals("50d") || preferenceManager.getString("weather_icon").equals("50n")) {

                drawerLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.mist));

            }

        }

    }

    @Override
    protected void onStart() {

        changeLocale(language);

        wind_forecast.setText(getString(R.string.wind_forecast));

        super.onStart();
    }

    @Override
    protected void onRestart() {

        changeLocale(language);

        wind_forecast.setText(getString(R.string.wind_forecast));

        super.onRestart();
    }

    private void changeLocale(String language) {

        myLocale = new Locale(language);
        Locale.setDefault(myLocale);

        Configuration configuration = new Configuration();
        configuration.locale = myLocale;

        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

    }

}