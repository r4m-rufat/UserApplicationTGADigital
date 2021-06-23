package com.openweatherchannel.premium.InitialActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.openweatherchannel.premium.R;
import com.openweatherchannel.premium.UTILS.PreferenceManager;

import java.util.Locale;

public class SplashActivity extends Activity {

    private Context context;
    private TextView owc_text;
    private Animation fade_in_text;
    private PreferenceManager preferenceManager;
    private Locale myLocale;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = this;
        preferenceManager = new PreferenceManager(context);

        owc_text = findViewById(R.id.owc_text);

        fade_in_text = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.splash_text_anim);
        owc_text.startAnimation(fade_in_text);

        if(preferenceManager.getString("language") == null){
            preferenceManager.putString("language", "en");
            preferenceManager.putString("language_whole", "English");
            language = "en";
            Toast.makeText(context, "girildi" + preferenceManager.getString("language_whole"), Toast.LENGTH_SHORT).show();
            changeLocale(language);
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(SplashActivity.this, SearchActivity.class);
                context.startActivity(intent);
                finish();

            }
        }, 2500);


    }


    private void changeLocale(String language){

        myLocale = new Locale(language);
        Locale.setDefault(myLocale);

        Configuration configuration = new Configuration();
        configuration.locale = myLocale;

        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

    }


}