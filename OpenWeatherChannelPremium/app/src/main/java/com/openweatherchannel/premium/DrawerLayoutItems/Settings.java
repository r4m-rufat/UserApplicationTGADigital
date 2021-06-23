package com.openweatherchannel.premium.DrawerLayoutItems;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.openweatherchannel.premium.Home.MainActivity;
import com.openweatherchannel.premium.UTILS.PreferenceManager;
import com.openweatherchannel.premium.R;
import com.openweatherchannel.premium.Services.NotificationService;

import java.util.ArrayList;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class Settings extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private Switch notification_switch;
    private ImageView backarrow;
    private Dialog dialog;
    RadioButton celcius, fahrenheit, m_sec, km_h;
    RelativeLayout rel_temprature_settings, about_application, wind_settings;
    private Button confirm, cancel, confirm_wind, cancel_wind;
    private TextView temperature_settings, celcius_and_fahrenheit, wind_speed,
            m_km_h, required, languages, settings;
    private Spinner languagesSpinner;
    private String language, language_abbreviation;
    private Locale myLocale;

    Context context;


    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = this;
        preferenceManager = new PreferenceManager(context);
        
        language = preferenceManager.getString("language");
        changeLocale(language);


        setupWidgets();

        rel_temprature_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomTempratureDialog();
            }
        });

        wind_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomWindDialog();
            }
        });

        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });

        notification_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

                    if (isChecked){
                        preferenceManager.putBoolean("switch_boolean", true);
                        notification_switch.setChecked(true);
                        startService();

                    }else{
                        preferenceManager.putBoolean("switch_boolean", false);
                        notification_switch.setChecked(false);
                        stopService();
                    }

                }else{

                    Toasty.error(context, "Your phone version is smaller than Android 8. Notification is only work in Andriod 8 and higher !",Toasty.LENGTH_LONG).show();
                    notification_switch.setChecked(false);

                }

            }
        });

        notification_switch.setChecked(preferenceManager.getBoolean("switch_boolean"));

        changeLanguageSpinner();
    }

    public void startService(){

        Intent serviceIntent = new Intent(context, NotificationService.class);
        startService(serviceIntent);

    }

    public void stopService(){

        Intent serviceIntent = new Intent(context, NotificationService.class);
        stopService(serviceIntent);

    }

    private void setupWidgets(){

        notification_switch = findViewById(R.id.notification_switch);
        backarrow = findViewById(R.id.backArrowForSettings);
        rel_temprature_settings = findViewById(R.id.rel_time_units_settings);
        about_application = findViewById(R.id.rel_about_application);
        wind_settings = findViewById(R.id.rel_wind_units);
        temperature_settings = findViewById(R.id.temprature_settings);
        celcius_and_fahrenheit = findViewById(R.id.temprature_choice);
        wind_speed = findViewById(R.id.wind_speed_settings);
        m_km_h = findViewById(R.id.wind_speed_choice);
        required = findViewById(R.id.required);
        languages = findViewById(R.id.languages);
        settings = findViewById(R.id.txt_settings);
        languagesSpinner = findViewById(R.id.languagesSpinner);

    }

    private void mCustomTempratureDialog(){

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_unit_dialog);
        dialog.setTitle("Temprature unit choices");
        celcius = dialog.findViewById(R.id.radio_celcius);
        fahrenheit = dialog.findViewById(R.id.radio_fahrenheit);
        confirm = dialog.findViewById(R.id.confirm);
        cancel = dialog.findViewById(R.id.cancel);

        confirm.setText(getString(R.string.confirm));
        cancel.setText(getString(R.string.cancel));
        celcius.setText(getString(R.string.celsius));
        fahrenheit.setText(getString(R.string.fahrenheit));

        preferenceManager.putBoolean("radio_celcius", true);
        celcius.setChecked(preferenceManager.getBoolean("radio_celcius"));

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceManager.putBoolean("radio_celcius", celcius.isChecked());
                preferenceManager.putBoolean("radio_fahrenheit", fahrenheit.isChecked());
                dialog.dismiss();
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        celcius.setChecked(preferenceManager.getBoolean("radio_celcius"));
        fahrenheit.setChecked(preferenceManager.getBoolean("radio_fahrenheit"));

        dialog.show();


    }

    private void mCustomWindDialog(){

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_wind_dilog);
        dialog.setTitle("Wind speed choices");
        m_sec = dialog.findViewById(R.id.radio_m_sec);
        km_h = dialog.findViewById(R.id.radio_km_h);
        confirm_wind = dialog.findViewById(R.id.confirm_wind);
        cancel_wind = dialog.findViewById(R.id.cancel_wind);

        m_sec.setText(getString(R.string.m_sec));
        km_h.setText(getString(R.string.km_h));
        confirm_wind.setText(getString(R.string.confirm));
        cancel_wind.setText(getString(R.string.cancel));

        preferenceManager.putBoolean("radio_m_sec", true);
        m_sec.setChecked(preferenceManager.getBoolean("radio_m_sec"));

        confirm_wind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceManager.putBoolean("radio_m_sec", m_sec.isChecked());
                preferenceManager.putBoolean("radio_km_h", km_h.isChecked());
                dialog.dismiss();
            }
        });


        cancel_wind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        m_sec.setChecked(preferenceManager.getBoolean("radio_m_sec"));
        km_h.setChecked(preferenceManager.getBoolean("radio_km_h"));

        dialog.show();


    }

    private void changeLanguageSpinner(){

        ArrayList<String> languagesList = new ArrayList<>();
        languagesList.add("English");
        languagesList.add("Azerbaijani");
        languagesList.add("Chinese");
        languagesList.add("German");
        languagesList.add("Italian");
        languagesList.add("Russian");
        languagesList.add("Turkish");
        languagesList.add("Vietnamese");



        if (preferenceManager.getString("language_whole").equals("Azerbaijan")){

            languagesList.clear();
            languagesList.add("Azerbaijani");
            languagesList.add("Chinese");
            languagesList.add("English");
            languagesList.add("German");
            languagesList.add("Italian");
            languagesList.add("Russian");
            languagesList.add("Turkish");
            languagesList.add("Vietnamese");

        }else if(preferenceManager.getString("language_whole").equals("English")){

            languagesList.clear();
            languagesList.add("English");
            languagesList.add("Azerbaijani");
            languagesList.add("Chinese");
            languagesList.add("German");
            languagesList.add("Italian");
            languagesList.add("Russian");
            languagesList.add("Turkish");
            languagesList.add("Vietnamese");

        }else if(preferenceManager.getString("language_whole").equals("German")){

            languagesList.clear();
            languagesList.add("German");
            languagesList.add("Azerbaijani");
            languagesList.add("Chinese");
            languagesList.add("English");
            languagesList.add("Italian");
            languagesList.add("Russian");
            languagesList.add("Turkish");
            languagesList.add("Vietnamese");

        }else if(preferenceManager.getString("language_whole").equals("Russian")){

            languagesList.clear();
            languagesList.add("Russian");
            languagesList.add("Azerbaijani");
            languagesList.add("Chinese");
            languagesList.add("English");
            languagesList.add("German");
            languagesList.add("Italian");
            languagesList.add("Turkish");
            languagesList.add("Vietnamese");

        }else if(preferenceManager.getString("language_whole").equals("Turkish")){

            languagesList.clear();
            languagesList.add("Turkish");
            languagesList.add("English");
            languagesList.add("Azerbaijani");
            languagesList.add("Chinese");
            languagesList.add("German");
            languagesList.add("Italian");
            languagesList.add("Russian");
            languagesList.add("Vietnamese");

        }else if(preferenceManager.getString("language_whole").equals("Vietnamese")){

            languagesList.clear();
            languagesList.add("Vietnamese");
            languagesList.add("Azerbaijani");
            languagesList.add("Chinese");
            languagesList.add("English");
            languagesList.add("German");
            languagesList.add("Italian");
            languagesList.add("Russian");
            languagesList.add("Turkish");

        }else if(preferenceManager.getString("language_whole").equals("Chinese")){

            languagesList.clear();
            languagesList.add("Chinese");
            languagesList.add("Azerbaijani");
            languagesList.add("English");
            languagesList.add("German");
            languagesList.add("Italian");
            languagesList.add("Russian");
            languagesList.add("Turkish");
            languagesList.add("Vietnamese");

        }else if(preferenceManager.getString("language_whole").equals("Italian")){

            languagesList.clear();
            languagesList.add("Italian");
            languagesList.add("Azerbaijani");
            languagesList.add("Chinese");
            languagesList.add("English");
            languagesList.add("German");
            languagesList.add("Russian");
            languagesList.add("Turkish");
            languagesList.add("Vietnamese");

        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item ,languagesList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languagesSpinner.setAdapter(spinnerAdapter);

        languagesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selected_language = languagesSpinner.getSelectedItem().toString();
                preferenceManager.putString("language_whole", selected_language);

                if (selected_language == "Azerbaijani"){

                    preferenceManager.putString("language", "az");
                    language_abbreviation = "az";

                }else if (selected_language == "Russian"){

                    preferenceManager.putString("language", "ru");
                    language_abbreviation = "ru";

                }else if (selected_language == "German"){

                    preferenceManager.putString("language", "de");
                    language_abbreviation = "de";

                }else if (selected_language == "English"){

                    preferenceManager.putString("language", "en");
                    language_abbreviation = "en";

                }else if (selected_language == "Turkish"){

                    preferenceManager.putString("language", "tr");
                    language_abbreviation = "tr";

                }else if (selected_language == "Chinese"){

                    preferenceManager.putString("language", "zh");
                    language_abbreviation = "zh";

                }else if (selected_language == "Italian"){

                    preferenceManager.putString("language", "it");
                    language_abbreviation = "it";

                }else if (selected_language == "Vietnamese"){

                    preferenceManager.putString("language", "vi");
                    language_abbreviation = "vi";

                }

                changeLocale(language_abbreviation);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();

    }

    @Override
    protected void onStart() {

        language = preferenceManager.getString("language");
        changeLocale(language);

        temperature_settings.setText(getString(R.string.temprature_settings));
        celcius_and_fahrenheit.setText(getString(R.string.celcius_and_fahrenheit));
        wind_speed.setText(getString(R.string.wind_speed));
        m_km_h.setText(getString(R.string.m_km_h));
        notification_switch.setText(getString(R.string.notification));
        required.setText(getString(R.string.required_android_level));
        languages.setText(getString(R.string.languages));
        settings.setText(getString(R.string.settings));

        super.onStart();
    }

    private void changeLocale(String language){

        myLocale = new Locale(language);
        Locale.setDefault(myLocale);

        Configuration configuration = new Configuration();
        configuration.locale = myLocale;

        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());


    }

}