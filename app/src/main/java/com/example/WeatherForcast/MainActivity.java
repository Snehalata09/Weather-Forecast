package com.example.WeatherForcast;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView tvCity;
    TextView tvDate;
    TextView tvTemp;
    TextView tvCondition;
    TextView tvHumidity;
    TextView tvWind;
    TextView tvPressure;
    TextView tvFeelsLike;

    ImageView imgWeather;

    RecyclerView recyclerForecast;
    List<ForecastModel> forecastList;
    ForecastAdapter forecastAdapter;

    ImageButton btnLogout;
    ImageButton btnSearchAction;
    EditText etCitySearch;
    SwipeRefreshLayout swipeRefreshLayout;

    String currentCity = "Pune";
    ObjectAnimator weatherIconAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        imgWeather = findViewById(R.id.imgWeather);

        tvCity = findViewById(R.id.tvCity1);
        tvDate = findViewById(R.id.tvDate1);
        tvTemp = findViewById(R.id.tvTemp1);
        tvCondition = findViewById(R.id.tvCondition1);
        tvHumidity = findViewById(R.id.tvHumidity1);
        tvWind = findViewById(R.id.tvWind1);
        tvPressure = findViewById(R.id.tvPressure1);
        tvFeelsLike = findViewById(R.id.tvFeelsLike1);

        btnLogout = findViewById(R.id.btnLogout);
        btnSearchAction = findViewById(R.id.btnSearchAction);
        etCitySearch = findViewById(R.id.etCitySearch);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // Customize SwipeRefreshLayout style
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.button_start),
                getResources().getColor(R.color.button_end)
        );

        recyclerForecast = findViewById(R.id.recyclerForecast);
        forecastList = new ArrayList<>();
        forecastAdapter = new ForecastAdapter(forecastList);

        // Set horizontal layout manager for forecast
        recyclerForecast.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerForecast.setAdapter(forecastAdapter);

        String currentDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        tvDate.setText(currentDate);

        handleIntent(getIntent());

        // Setup search submit listeners
        etCitySearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        btnSearchAction.setOnClickListener(v -> performSearch());

        // Setup SwipeRefreshLayout listener
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadWeather(currentCity);
            loadForecast(currentCity);
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, loginactivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Start premium floating icon animation
        startWeatherIconAnimation();
    }

    private void startWeatherIconAnimation() {
        if (weatherIconAnimator != null) {
            weatherIconAnimator.cancel();
        }
        weatherIconAnimator = ObjectAnimator.ofFloat(imgWeather, "translationY", 0f, -15f, 0f);
        weatherIconAnimator.setDuration(3000);
        weatherIconAnimator.setRepeatCount(ValueAnimator.INFINITE);
        weatherIconAnimator.setRepeatMode(ValueAnimator.REVERSE);
        weatherIconAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        weatherIconAnimator.start();
    }

    private void performSearch() {
        String query = etCitySearch.getText().toString().trim();
        if (!query.isEmpty()) {
            currentCity = query;
            loadWeather(query);
            loadForecast(query);
            etCitySearch.clearFocus();

            // Hide keyboard
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(etCitySearch.getWindowToken(), 0);
            }
        } else {
            Toast.makeText(this, "Enter City Name", Toast.LENGTH_SHORT).show();
        }
    }

    private void animateLayoutsOnLoad() {
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        // Staggered animations for a more dynamic feel
        final View[] views = {
            findViewById(R.id.layoutCurrentWeather),
            findViewById(R.id.layoutGridDetails),
            findViewById(R.id.tvForecastTitle),
            findViewById(R.id.recyclerForecast)
        };

        for (int i = 0; i < views.length; i++) {
            final View v = views[i];
            if (v == null) continue;
            v.setVisibility(View.INVISIBLE);
            final int delay = i * 150;
            new Handler().postDelayed(() -> {
                v.setVisibility(View.VISIBLE);
                v.startAnimation(slideUp);
            }, delay);
        }

        // Apply bounce to icon
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        imgWeather.startAnimation(bounce);
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String city = intent.getStringExtra("city");
        if (city == null || city.isEmpty()) {
            city = "Pune";
        }
        currentCity = city;
        loadWeather(city);
        loadForecast(city);
    }

    private void loadWeather(String city) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q="
                + city
                + "&appid=a0cd82984c51ce7dd070c9c4d9d674d9&units=metric";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONObject main = response.getJSONObject("main");
                        JSONObject wind = response.getJSONObject("wind");
                        JSONArray weather = response.getJSONArray("weather");
                        JSONObject weatherObject = weather.getJSONObject(0);

                        String cityName = response.getString("name");
                        double temp = main.getDouble("temp");
                        int humidity = main.getInt("humidity");
                        int pressure = main.getInt("pressure");
                        double feelsLike = main.optDouble("feels_like", temp);
                        double windSpeed = wind.getDouble("speed");
                        String condition = weatherObject.getString("main");

                        if (condition.equalsIgnoreCase("Clear")) {
                            imgWeather.setImageResource(R.drawable.sun);
                        } else if (condition.equalsIgnoreCase("Clouds")) {
                            imgWeather.setImageResource(R.drawable.ic_sun);
                        } else if (condition.equalsIgnoreCase("Rain")) {
                            imgWeather.setImageResource(R.drawable.heavyrain);
                        } else if (condition.equalsIgnoreCase("Thunderstorm")) {
                            imgWeather.setImageResource(R.drawable.thunderstorm);
                        } else {
                            imgWeather.setImageResource(R.drawable.ic_sun);
                        }

                        tvCity.setText(cityName);
                        tvTemp.setText(String.format(Locale.getDefault(), "%.1f°C", temp));
                        tvCondition.setText(condition);
                        tvHumidity.setText(humidity + "%");
                        tvWind.setText(windSpeed + " m/s");
                        tvPressure.setText(pressure + " hPa");
                        tvFeelsLike.setText(String.format(Locale.getDefault(), "%.1f°C", feelsLike));

                        // Run exit/entry animations when details are loaded
                        animateLayoutsOnLoad();

                    } catch (Exception e) {
                        tvCondition.setText("Data Error");
                    } finally {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                },
                error -> {
                    tvCondition.setText("Weather API Error");
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

        queue.add(request);
    }

    private void loadForecast(String city) {
        String url = "https://api.openweathermap.org/data/2.5/forecast?q="
                + city
                + "&appid=a0cd82984c51ce7dd070c9c4d9d674d9&units=metric";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        forecastList.clear();
                        JSONArray list = response.getJSONArray("list");

                        for (int i = 0; i < list.length(); i += 8) {
                            JSONObject item = list.getJSONObject(i);
                            JSONObject main = item.getJSONObject("main");
                            JSONArray weather = item.getJSONArray("weather");
                            JSONObject weatherObj = weather.getJSONObject(0);

                            double temp = main.getDouble("temp");
                            String condition = weatherObj.getString("main");
                            String date = item.getString("dt_txt");

                            forecastList.add(new ForecastModel(
                                    date,
                                    String.format(Locale.getDefault(), "%.0f°C", temp),
                                    condition));
                        }

                        forecastAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                },
                error -> {
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

        queue.add(request);
    }
}