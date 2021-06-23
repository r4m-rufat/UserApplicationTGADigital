package com.openweatherchannel.premium.API;


import android.content.Context;

import java.util.Random;

public class ApiKeys {

    Context context;

    public ApiKeys(Context context) {
        this.context = context;
    }

    public String apiKeys(){

        String[] api_keys = {"6cf4abd840cde11a5b028168ae8e7c02","3677ae352bfc58f74b56a52510595166"};


        return api_keys[new Random().nextInt(api_keys.length)];

    }

}
