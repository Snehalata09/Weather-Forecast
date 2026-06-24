package com.example.WeatherForcast;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class SearchActivity extends AppCompatActivity {

    EditText etCity;
    MaterialButton btnSearchWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etCity = findViewById(R.id.etCity);

        btnSearchWeather =
                findViewById(R.id.btnSearchWeather);

        btnSearchWeather.setOnClickListener(v -> {

            String city =
                    etCity.getText().toString().trim();

            if(city.isEmpty()){

                Toast.makeText(
                        this,
                        "Enter City Name",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            Intent intent =
                    new Intent(
                            SearchActivity.this,
                            MainActivity.class);

            intent.putExtra("city", city);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivity(intent);

            finish();
        });
    }
}