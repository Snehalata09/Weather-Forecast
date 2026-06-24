package com.example.WeatherForcast;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        View mainLayout = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView logo = findViewById(R.id.weather);
        TextView title = findViewById(R.id.forcast);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in_zoom);
        logo.startAnimation(anim);
        title.startAnimation(anim);

        new android.os.Handler().postDelayed(() -> {

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                startActivity(
                        new Intent(
                                SplashActivity.this,
                                MainActivity.class));

            } else {

                startActivity(
                        new Intent(
                                SplashActivity.this,
                                loginactivity.class));

            }

            finish();

        }, 2000);
    }
}