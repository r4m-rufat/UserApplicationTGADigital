package com.openweatherchannel.premium.InitialActivities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.openweatherchannel.premium.DataBase.SpinnerDatabase;
import com.openweatherchannel.premium.Home.MainActivity;
import com.openweatherchannel.premium.R;
import com.openweatherchannel.premium.UTILS.CheckInternetConnection;
import com.openweatherchannel.premium.UTILS.PreferenceManager;

import java.util.ArrayList;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class SearchActivity extends AppCompatActivity {

    private ImageView search;
    CheckInternetConnection connection;
    EditText location;
    String city_name, language_abbreviation;
    Context context;
    SpinnerDatabase spinnerDatabase;
    Spinner languagesSpinner;
    PreferenceManager preferenceManager;

    private static Locale myLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        context = this;
        connection = new CheckInternetConnection(context);
        spinnerDatabase = new SpinnerDatabase(context);

        location = findViewById(R.id.edit_city_name);
        search = findViewById(R.id.search_location);
        languagesSpinner = findViewById(R.id.languagesSpinner);
        preferenceManager = new PreferenceManager(context);

        // languages spinner
        setupSpinnerLanguages();


        if (spinnerDatabase.checkingNull()) {

            startActivity(new Intent(context, MainActivity.class));
            finish();

        } else {

            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (connection.isNetworkAvailableAndConnected()){

                        city_name = location.getText().toString();

                        if (!city_name.isEmpty()) {

                            spinnerDatabase.InsertColumn(toUpperCase(city_name));

                            startActivity(new Intent(context, MainActivity.class));
                            finish();

                        } else {

                            Toasty.error(context, "You must write a location !", Toasty.LENGTH_SHORT).show();

                        }

                    }else{

                        Toasty.error(context, "Check internet connection !", Toasty.LENGTH_SHORT).show();

                    }

                }
            });

        }


    }

    private String toUpperCase(String city_name) {

        String capital = city_name.substring(0, 1).toUpperCase() + city_name.substring(1).toLowerCase();

        return capital;

    }

    private void setupSpinnerLanguages(){

        ArrayList<String> languagesList = new ArrayList<>();
        languagesList.add("English");
        languagesList.add("Azerbaijani");
        languagesList.add("Chinese");
        languagesList.add("German");
        languagesList.add("Italian");
        languagesList.add("Russian");
        languagesList.add("Turkish");
        languagesList.add("Vietnamese");

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

                preferenceManager.putString("langauge", "en");
                language_abbreviation = "en";
                changeLocale(language_abbreviation);

            }
        });

    }

    private void changeLocale(String language){

        myLocale = new Locale(language);
        Locale.setDefault(myLocale);

        Configuration configuration = new Configuration();
        configuration.locale = myLocale;

        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());



    }



//    public class LoadCities extends SimpleAsyncTask<List<String>> {
//
//        @Override
//        protected List<String> doInBackgroundSimple() {
//            citylists = new ArrayList<>();
//            try {
//                StringBuilder builder = new StringBuilder();
//                InputStream is = getResources().openRawResource(R.raw.citylist);
//                GZIPInputStream gzipInputStream = new GZIPInputStream(is);
//
//                InputStreamReader reader = new InputStreamReader(gzipInputStream);
//                BufferedReader in = new BufferedReader(reader);
//                String readed;
//                while ((readed = in.readLine()) != null)
//                    builder.append(readed);
//                citylists = new Gson().fromJson(builder.toString(), new TypeToken<List<String>>() {
//                }.getType());
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return citylists;
//        }
//
//        @Override
//        protected void onSuccess(final List<String> listCity) {
//            super.onSuccess(listCity);
//
//            searchBar.setEnabled(true);
//            searchBar.addTextChangeListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                    List<String> suggest = new ArrayList<>();
//                    for (String search : listCity) {
//                        if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
//                            suggest.add(search);
//                    }
//                    searchBar.setLastSuggestions(suggest);
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void afterTextChanged(final Editable editable) {
//                    buttonsearch.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            getWeatherInformation(editable.toString());
//                            searchBar.setLastSuggestions(listCity);
//                        }
//                    });
//
//                }
//            });

}