package com.example.WeatherForcast;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.button.MaterialButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class loginactivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    MaterialButton btnLogin;
    TextView tvRegister;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loginactivity);

        View mainLayout = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);

        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        auth = FirebaseAuth.getInstance();

        // Load animations
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);

        // Apply animations
        findViewById(R.id.login_icon).startAnimation(bounce);
        findViewById(R.id.login_title).startAnimation(slideUp);
        findViewById(R.id.email_layout).startAnimation(slideUp);
        findViewById(R.id.password_layout).startAnimation(slideUp);
        btnLogin.startAnimation(slideUp);
        tvRegister.startAnimation(slideUp);

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(loginactivity.this, RegisterActivity.class));
        });

        btnLogin.setOnClickListener(v -> {

            String email =
                    etEmail.getText().toString().trim();

            String password =
                    etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                        loginactivity.this,
                        "Please enter email and password",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            auth.signInWithEmailAndPassword(
                    email,
                    password
            ).addOnSuccessListener(authResult -> {

                Toast.makeText(
                        loginactivity.this,
                        "Login Successful",
                        Toast.LENGTH_SHORT
                ).show();

                startActivity(
                        new Intent(
                                loginactivity.this,
                                MainActivity.class));

                finish();

            }).addOnFailureListener(e -> {

                Toast.makeText(
                        loginactivity.this,
                        e.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

            });

        });
    }
}