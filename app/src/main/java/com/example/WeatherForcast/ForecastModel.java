package com.example.WeatherForcast;

public class ForecastModel {

    String day;
    String temp;
    String condition;

    public ForecastModel(String day, String temp, String condition) {
        this.day = day;
        this.temp = temp;
        this.condition = condition;
    }

    public String getDay() {
        return day;
    }

    public String getTemp() {
        return temp;
    }

    public String getCondition() {
        return condition;
    }
}