package com.example.WeatherForcast;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    List<ForecastModel> forecastList;

    public ForecastAdapter(List<ForecastModel> forecastList) {
        this.forecastList = forecastList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_forecastmodel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ForecastModel model = forecastList.get(position);

        // Format date string from "yyyy-MM-dd HH:mm:ss" to user-friendly "EEE, h a"
        String rawDate = model.getDay();
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, h a", Locale.getDefault());
            Date dateObj = parser.parse(rawDate);
            if (dateObj != null) {
                holder.tvDay.setText(formatter.format(dateObj));
            } else {
                holder.tvDay.setText(rawDate);
            }
        } catch (Exception e) {
            holder.tvDay.setText(rawDate);
        }

        holder.tvTemp.setText(model.getTemp());
        holder.tvCondition.setText(model.getCondition());

        // Set icon based on weather condition
        String condition = model.getCondition();
        if (condition.equalsIgnoreCase("Clear")) {
            holder.imgForecastIcon.setImageResource(R.drawable.sun);
        } else if (condition.equalsIgnoreCase("Clouds")) {
            holder.imgForecastIcon.setImageResource(R.drawable.ic_sun);
        } else if (condition.equalsIgnoreCase("Rain")) {
            holder.imgForecastIcon.setImageResource(R.drawable.heavyrain);
        } else if (condition.equalsIgnoreCase("Thunderstorm")) {
            holder.imgForecastIcon.setImageResource(R.drawable.thunderstorm);
        } else {
            holder.imgForecastIcon.setImageResource(R.drawable.ic_sun);
        }
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDay;
        TextView tvTemp;
        TextView tvCondition;
        ImageView imgForecastIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvTemp = itemView.findViewById(R.id.tvForecastTemp);
            tvCondition = itemView.findViewById(R.id.tvForecastCondition);
            imgForecastIcon = itemView.findViewById(R.id.imgForecastIcon);
        }
    }
}