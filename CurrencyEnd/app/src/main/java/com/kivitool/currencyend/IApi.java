package com.kivitool.currencyend;

import com.kivitool.currencyend.models.CurrencyResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IApi {

    @GET("latest.json?")
    Call<CurrencyResponse> getAllValuta(

            @Query("app_id") String app_id


    );

}
