package com.kivitool.currencyend.API;


import com.kivitool.currencyend.models.CurrencyResponse;
import com.kivitool.currencyend.models.Rates;

import retrofit2.Call;

public class ManagerAll extends BaseManager {

    public synchronized static ManagerAll getInstance(){
        return new ManagerAll();
    }

    public Call<Rates> getCurrentInforamtions(String api_key){

        Call<Rates> currencyResponseCall = getApiInterface().getCurrency(api_key);
        return currencyResponseCall;

    }



}
