package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    TextView textViewHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefer", MODE_PRIVATE);

        // Mengecek apakah terdapat username dalam SharedPreferences
        if (sharedPreferences.contains("username")) {
            // Jika ada, langsung arahkan ke DashboardActivity
            startActivity(new Intent(MainActivity.this, NewDashboardActivity.class));
            // Selesaikan MainActivity agar tidak bisa kembali dengan mengakhiri aktivitas
            finish();
        } else {
            // Jika tidak ada, arahkan ke LoginActivity
            startActivity(new Intent(MainActivity.this, IntroActivity.class));
            // Selesaikan MainActivity agar tidak bisa kembali dengan mengakhiri aktivitas
            finish();
        }
    }
}
