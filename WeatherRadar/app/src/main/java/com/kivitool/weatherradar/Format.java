package com.kivitool.weatherradar;

public class Format {

    //Method for capitalising each word in string
    public static String stringCapitalise(String str) {
        String[] strArray = str.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }
        str = builder.toString();
        return str;
    }

    //Method for formatting date
    public static String formatDate(String date){

        //Get time, month and date substrings
        String time = date.substring(11, 13);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);

        //Change time to friendly format
        int timeInt = Integer.parseInt(time);

        if (timeInt == 0) {
            timeInt += 12;
            time = (timeInt + "am");
        } else if (timeInt < 12) {
            time = (timeInt + "am");
        } else if (timeInt == 12) {
            time = (timeInt + "pm");
        } else {
            timeInt -= 12;
            time = (timeInt + "pm");
        }

        //Combine for rearranged date UK format

        return time + " " + day + "/" + month;
    }

    public static int celsiusToFahrenheit(int celsius){
        return Math.round(9 * (celsius/5) + 32);
    }

    public static int windMph(int wind){
        return (int)Math.round(wind * 2.23694);
    }

    //Method for rounding precipitation volumes to 3 decimal places
    public static double roundVolume(double volume){

        long factor = (long)Math.pow(10, 3);
        volume *= factor;
        long temp = Math.round(volume);
        return (double) temp/factor;

    }

    public static String formatWind(int windDeg){

        String windDirection = "";

        //Get string direction from wind degrees via 8 point compass
        if(windDeg < 23){
            windDirection = "N";
        } else if(windDeg < 68){
            windDirection = "NE";
        } else if(windDeg < 113){
            windDirection = "E";
        } else if(windDeg < 158){
            windDirection = "SE";
        } else if(windDeg < 203){
            windDirection = "S";
        } else if(windDeg < 248){
            windDirection = "SW";
        } else if(windDeg < 293){
            windDirection = "W";
        } else if(windDeg < 338){
            windDirection = "NW";
        } else if(windDeg > 338){
            windDirection = "N";
        }
        return windDirection;
    }

}
