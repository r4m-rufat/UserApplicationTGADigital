package com.kivitool.currencyend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    Context context = this;

    TextView test;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        test = findViewById(R.id.test);

        requestQueue = Volley.newRequestQueue(this);

        String url = "https://openexchangerates.org/api/latest.json?app_id=ba4baa2410654310a16d43418bd2f7dd";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject rates = response.getJSONObject("rates");
                            test.setText(rates.getDouble("AED")+"");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();
            }
        });

        requestQueue.add(request);

    }

//    void valutaJsonParse() {
//
//        api = ApiClient.getRetrofit().create(IApi.class);
//
//        Call<CurrencyResponse> call = api.getAllValuta("ba4baa2410654310a16d43418bd2f7dd");
//
//        call.enqueue(new Callback<CurrencyResponse>() {
//            @Override
//            public void onResponse(Call<CurrencyResponse> call, Response<CurrencyResponse> response) {
//
//                if (response.isSuccessful()) {
//
//                    String text = response.body().getBase() + "";
//
//                    test.setText(text);
//
//                    Toast.makeText(context, response.code() + "Menim Errorum", Toast.LENGTH_SHORT).show();
//                }else {
//
//                    test.setText(response.code()+"");
//
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<CurrencyResponse> call, Throwable t) {
//
//                Toast.makeText(context, t.getMessage() + "Senin errorun", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//
//    }

}