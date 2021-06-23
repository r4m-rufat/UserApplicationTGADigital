package com.openweatherchannel.premium.DrawerLayoutItems;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.openweatherchannel.premium.Home.MainActivity;
import com.openweatherchannel.premium.UTILS.PreferenceManager;
import com.openweatherchannel.premium.R;

import java.util.Locale;

public class ContactUs extends AppCompatActivity {

    ImageView backFromContactUs;
    private TextView openweathermap, txt_contact_us, contact_us;
    private Locale myLocale;
    private PreferenceManager preferenceManager;
    private String language;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        preferenceManager = new PreferenceManager(ContactUs.this);
        language = preferenceManager.getString("language");
        changeLocale(language);

        backFromContactUs = findViewById(R.id.backArrowForContact);
        openweathermap = findViewById(R.id.txt_contact_openweathermap);
        txt_contact_us = findViewById(R.id.txt_contact_us);
        contact_us = findViewById(R.id.txt_contact);

        backFromContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContactUs.this, MainActivity.class));
                finish();
            }
        });

    }

    @Override
    protected void onStart() {

        openweathermap.setText(getString(R.string.openweathermap));
        txt_contact_us.setText(getString(R.string.txt_contact_us));
        contact_us.setText(getString(R.string.contact_us));

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