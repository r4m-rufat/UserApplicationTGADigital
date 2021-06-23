package com.openweatherchannel.premium.UTILS;

public class SetupLanguagesForAPI {

    public void setupLanguage(String language, PreferenceManager preferenceManager){

        if (language == "az"){

            preferenceManager.putString("language_api", "az");

        }else if (language == "ru"){

            preferenceManager.putString("language_api", "ru");

        }else if (language == "de"){

            preferenceManager.putString("language_api", "de");

        }else if (language == "en"){

            preferenceManager.putString("language_api", "en");

        }else if (language == "tr"){

            preferenceManager.putString("language_api", "tr");

        }else if (language == "zh"){

            preferenceManager.putString("language_api", "zh_tw");

        }else if (language == "it"){

            preferenceManager.putString("language_api", "it");


        }else if (language == "vi"){

            preferenceManager.putString("language_api", "vi");

        }

    }

}
