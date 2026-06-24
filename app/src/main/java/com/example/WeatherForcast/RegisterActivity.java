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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.Map;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etEmail,
            etPassword, etConfirmPassword;

    FirebaseAuth auth;
    FirebaseFirestore db;
    MaterialButton btnRegister;
    TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        View mainLayout = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etName = findViewById(R.id.name);
        etEmail = findViewById(R.id.emailaddress);
        etPassword = findViewById(R.id.password);
        etConfirmPassword =
                findViewById(R.id.namcomfirmpass);

        btnRegister =
                findViewById(R.id.register);

        tvLogin =
                findViewById(R.id.clicklog);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Load animations
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);

        // Apply animations
        findViewById(R.id.register_icon).startAnimation(bounce);
        findViewById(R.id.register_title).startAnimation(slideUp);
        findViewById(R.id.name_layout).startAnimation(slideUp);
        findViewById(R.id.email_layout).startAnimation(slideUp);
        findViewById(R.id.password_layout).startAnimation(slideUp);
        findViewById(R.id.confirm_password_layout).startAnimation(slideUp);
        btnRegister.startAnimation(slideUp);
        tvLogin.startAnimation(slideUp);

        btnRegister.setOnClickListener(v -> {

            String name =
                    etName.getText().toString().trim();

            String email =
                    etEmail.getText().toString().trim();

            String password =
                    etPassword.getText().toString().trim();

            String confirmPassword =
                    etConfirmPassword.getText().toString().trim();

            if(name.isEmpty() ||
                    email.isEmpty() ||
                    password.isEmpty() ||
                    confirmPassword.isEmpty()) {

                Toast.makeText(
                        this,
                        "Fill all fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if(!password.equals(confirmPassword)) {

                Toast.makeText(
                        this,
                        "Passwords do not match",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            auth.createUserWithEmailAndPassword(
                    email,
                    password
            ).addOnSuccessListener(authResult -> {

                String uid = auth.getCurrentUser().getUid();
                Map<String, Object> user = new HashMap<>();
                user.put("name", name);
                user.put("email", email);

                db.collection("users").document(uid).set(user)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Registration Successful",
                                    Toast.LENGTH_SHORT
                            ).show();

                            startActivity(
                                    new Intent(
                                            RegisterActivity.this,
                                            MainActivity.class));

                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Error saving user data: " + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        });

            }).addOnFailureListener(e -> {

                Toast.makeText(
                        RegisterActivity.this,
                        e.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

            });

        });

        tvLogin.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            RegisterActivity.this,
                            loginactivity.class);

            startActivity(intent);

            finish();

        });
    }
}