package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TopUpSuccesActivity extends AppCompatActivity {

    private TextView paymentAmountTextView;
    private TextView usernameTextView;
    private TextView paymentMethodTextView;
    private TextView dateTimeTextView; // Tambahkan TextView untuk menampilkan tanggal dan waktu

    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_succes);

        // Inisialisasi views
        paymentAmountTextView = findViewById(R.id.payment_amount);
        usernameTextView = findViewById(R.id.username);
        paymentMethodTextView = findViewById(R.id.payment_method);
        dateTimeTextView = findViewById(R.id.payment_date_time); // Inisialisasi TextView untuk menampilkan tanggal dan waktu
        backButton = findViewById(R.id.btn_back_to_home);

        // Mendapatkan data dari intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String amount = extras.getString("amount");
            String username = extras.getString("username");
            String paymentMethod = extras.getString("paymentMethod");
            String currentDate = extras.getString("date");

            // Set nilai ke TextViews
            paymentAmountTextView.setText(String.valueOf(amount));
            usernameTextView.setText(username);
            paymentMethodTextView.setText(paymentMethod);
            dateTimeTextView.setText(currentDate);
        }

        // Tombol untuk kembali ke NewDashboardActivity
        backButton.setOnClickListener(v -> {
            // Membuat intent untuk NewDashboardActivity
            Intent intent = new Intent(TopUpSuccesActivity.this, NewDashboardActivity.class);
            // Menjalankan intent
            startActivity(intent);
            // Menutup TopUpSuccessActivity agar tidak tersimpan di dalam stack activity
            finish();
        });
    }
}
