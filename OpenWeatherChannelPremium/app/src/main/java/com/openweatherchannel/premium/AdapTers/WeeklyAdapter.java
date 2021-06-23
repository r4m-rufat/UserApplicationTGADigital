package com.openweatherchannel.premium.AdapTers;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.openweatherchannel.premium.Models.Weekly.DailyItem;
import com.openweatherchannel.premium.UTILS.PreferenceManager;
import com.openweatherchannel.premium.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class WeeklyAdapter extends RecyclerView.Adapter<WeeklyAdapter.ViewHolder> {

    Context context;
    List<DailyItem> dailyItemList;
    PreferenceManager preferenceManager;

    public WeeklyAdapter(Context context, List<DailyItem> dailyItemList, PreferenceManager preferenceManager) {
        this.context = context;
        this.dailyItemList = dailyItemList;
        this.preferenceManager = preferenceManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_daily_forecast_for_recycler_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        changeLocale(preferenceManager.getString("language"));

        int weekNameValue =dailyItemList.get(position).getDt();

        Calendar calendar = Calendar.getInstance();
        Date convertDate = new Date(weekNameValue * 1000L);
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("EEE");
        dateFormat1.setTimeZone(TimeZone.getTimeZone(calendar.getTimeZone().getID()));
        dateFormat2.setTimeZone(TimeZone.getTimeZone(calendar.getTimeZone().getID()));
        String formattedDate1 = dateFormat1.format(convertDate);
        String formattedDate2 = dateFormat2.format(convertDate);


        if (formattedDate2.equals("Sat") || formattedDate2.equals("Sun") || formattedDate2.equals("B.") || formattedDate2.equals("Ş.")
                || formattedDate2.equals("cб") || formattedDate2.equals("вc") || formattedDate2.equals("Sa.") || formattedDate2.equals("So.")
                || formattedDate2.equals("Cmt") || formattedDate2.equals("Paz")) {
            holder.daily_date.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            holder.daily_date.setTypeface(Typeface.DEFAULT_BOLD);
        }
        holder.week_name.setText(formattedDate1 + "");
        holder.daily_date.setText("   " + formattedDate2 + "   ");

        holder.humidity.setText(dailyItemList.get(position).getHumidity() + "%");



        String stiuation_icon = dailyItemList.get(position).getWeather().get(0).getIcon()  + "";

        /**
         * set weather icon depends on stiuation
         */

        if (stiuation_icon.equals("01d")){
            holder.weekly_icon.setImageResource(R.drawable.icon_01d);
        }else if (stiuation_icon.equals("01n")){
            holder.weekly_icon.setImageResource(R.drawable.icon_01n);
        }else if (stiuation_icon.equals("02d")){
            holder.weekly_icon.setImageResource(R.drawable.icon_02d);
        }else if (stiuation_icon.equals("02n")){
            holder.weekly_icon.setImageResource(R.drawable.icon_02n);
        }else if (stiuation_icon.equals("03d")){
            holder.weekly_icon.setImageResource(R.drawable.icon03d);
        }else if (stiuation_icon.equals("03n")){
            holder.weekly_icon.setImageResource(R.drawable.icon03d);
        }else if (stiuation_icon.equals("04d") || stiuation_icon.equals("04n")){
            holder.weekly_icon.setImageResource(R.drawable.icon_04n);
        }else if (stiuation_icon.equals("09d") || stiuation_icon.equals("09n")){
            holder.weekly_icon.setImageResource(R.drawable.icon_9d);
        }else if (stiuation_icon.equals("10d")){
            holder.weekly_icon.setImageResource(R.drawable.icon_10d);
        }else if (stiuation_icon.equals("10n")){
            holder.weekly_icon.setImageResource(R.drawable.icon_10n);
        }else if (stiuation_icon.equals("11d")){
            holder.weekly_icon.setImageResource(R.drawable.icon_11d);
        }else if (stiuation_icon.equals("11n")){
            holder.weekly_icon.setImageResource(R.drawable.icon_11n);
        }else if (stiuation_icon.equals("13d") || stiuation_icon.equals("13n")){
            holder.weekly_icon.setImageResource(R.drawable.icon_13n);
        }else if (stiuation_icon.equals("50d") || stiuation_icon.equals("50n")){
            holder.weekly_icon.setImageResource(R.drawable.icon_50d);
        }

    }

    @Override
    public int getItemCount() {
        return dailyItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView week_name, daily_date, humidity;
        ImageView weekly_icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            week_name = itemView.findViewById(R.id.txt_week_forDaily);
            daily_date = itemView.findViewById(R.id.txt_monthly_date_time);
            humidity = itemView.findViewById(R.id.txt_humidity_for_daily);
            weekly_icon = itemView.findViewById(R.id.stiuation_image_for_daily);
        }
    }

    private void changeLocale(String language) {

        Locale myLocale = new Locale(language);
        Locale.setDefault(myLocale);

        Configuration configuration = new Configuration();
        configuration.locale = myLocale;

        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());


    }
}
