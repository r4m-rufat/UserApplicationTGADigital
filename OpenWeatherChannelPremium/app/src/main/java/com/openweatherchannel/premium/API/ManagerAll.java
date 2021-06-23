package com.openweatherchannel.premium.API;

import com.openweatherchannel.premium.Models.Current.CurrentResult;
import com.openweatherchannel.premium.Models.DaysForWind.JsonMember5DaysResult;
import com.openweatherchannel.premium.Models.Weekly.WeeklyResult;

import retrofit2.Call;

public class ManagerAll extends BaseManager{

    public synchronized static ManagerAll getInstance(){
        return new ManagerAll();
    }

    public Call<CurrentResult> getWeatherCurrentInfo(String country_name, String units, String language, String api_key){
        Call<CurrentResult> currentResultCall = getApiInterface().getCurrentInforamtions(country_name, units, language, api_key);
        return currentResultCall;
    }

    public Call<WeeklyResult> getWeatherWeeklyInformations(String lat, String lon, String daily, String units, String language, String api_key){
        Call<WeeklyResult> weeklyResultCall = getApiInterface().getWeeklyInformations(lat, lon, daily, units, language, api_key);
        return weeklyResultCall;
    }

    public Call<JsonMember5DaysResult> getWeather5DaysInformations(String country_name, String language, String api_key){
        Call<JsonMember5DaysResult> jsonMember5DaysResultCall = getApiInterface().get5DaysInformations(country_name, language, api_key);
        return jsonMember5DaysResultCall;
    }

    public Call<CurrentResult> getWeatherInformationsWithLatLon(String lat, String lon, String units, String language, String api_key){
        Call<CurrentResult> currentResultCall = getApiInterface().getCurrentInforamtionsWithLatLon(lat, lon, units, language, api_key);
        return currentResultCall;
    }

}
