package com.openweatherchannel.premium.DrawerLayoutItems;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.openweatherchannel.premium.DataBase.SpinnerDatabase;
import com.openweatherchannel.premium.Home.MainActivity;
import com.openweatherchannel.premium.UTILS.PreferenceManager;
import com.openweatherchannel.premium.R;

import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class AddNewLocation extends AppCompatActivity {

    ImageView back_arrow, search_location;
    TextView txt_add_new_location, write_city;
    EditText add_new_location;
    public static String new_location;
    private Context context;
    private Locale myLocale;
    SpinnerDatabase spinnerDatabase;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_location);
        setUpWidgets();
        context = this;
        preferenceManager = new PreferenceManager(context);
        spinnerDatabase = new SpinnerDatabase(context);

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        });

        search_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new_location = toUpperCase(add_new_location.getText().toString().trim());
                if (!new_location.isEmpty()){

                    if (spinnerDatabase.checkIsRowExists(new_location)){

                        Intent intent = new Intent(AddNewLocation.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }else{

                        spinnerDatabase.InsertColumn(new_location);
                        Intent intent = new Intent(AddNewLocation.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }

                }else if (new_location.isEmpty()){

                    Toasty.error(context, getString(R.string.must_location), Toast.LENGTH_SHORT).show();

                }

            }
        });


    }

    private void setUpWidgets(){

        back_arrow = findViewById(R.id.backArrow);
        add_new_location = findViewById(R.id.add_new_location);
        search_location = findViewById(R.id.search_location);
        txt_add_new_location = findViewById(R.id.txt_add_new_location);
        write_city = findViewById(R.id.search);

    }

    private String toUpperCase(String city_name){

        String capital = city_name.substring(0, 1).toUpperCase() + city_name.substring(1).toLowerCase();

        return capital;

    }

    @Override
    protected void onStart() {

        changeLocale(preferenceManager.getString("language"));

        changeDefaultText();

        super.onStart();
    }

    private void changeDefaultText(){

        add_new_location.setHint(getString(R.string.only_city_name));
        txt_add_new_location.setText(getString(R.string.add_new_location));
        write_city.setText(getString(R.string.write_location));

    }


    private void changeLocale(String language){

        myLocale = new Locale(language);
        Locale.setDefault(myLocale);

        Configuration configuration = new Configuration();
        configuration.locale = myLocale;

        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());


    }

}