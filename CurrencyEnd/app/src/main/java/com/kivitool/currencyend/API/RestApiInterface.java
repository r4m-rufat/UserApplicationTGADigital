package com.kivitool.currencyend.API;

import com.kivitool.currencyend.models.Rates;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestApiInterface {

    @GET("latest.json?")
    Call<Rates> getCurrency(
            @Query("app_id") String api_key
    );
}
