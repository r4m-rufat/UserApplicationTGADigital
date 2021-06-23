package com.openweatherchannel.premium.API;

public class BaseManager {

    protected RestApiInterface getApiInterface(){
        RestApiClient restApiClient = new RestApiClient(BaseUrl.BASE_URL);
        return restApiClient.getRestApiInterface();
    }

}
