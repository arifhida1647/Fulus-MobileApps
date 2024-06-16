package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransferSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_success);

        // Mendapatkan data dari intent
        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        String jumlah = intent.getStringExtra("jumlah");
        String tujuan = intent.getStringExtra("tujuan");
        String currentDate = intent.getStringExtra("date");

        // Menampilkan data di TextView
        TextView paymentDateTimeTextView = findViewById(R.id.payment_date_time);
        TextView recipientNameTextView = findViewById(R.id.recipient_name);
        TextView senderNameTextView = findViewById(R.id.sender_name);
        TextView paymentAmountTextView = findViewById(R.id.payment_amount);

        // Mengisi nilai TextView dengan data yang diterima
        paymentDateTimeTextView.setText(currentDate);
        recipientNameTextView.setText(tujuan);
        senderNameTextView.setText(userName);
        paymentAmountTextView.setText(jumlah);

        // Mendapatkan referensi tombol "Back to Home"
        Button backButton = findViewById(R.id.btn_back_to_home);

        // Memberi fungsi klik pada tombol "Back to Home"
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent untuk kembali ke NewDashboardActivity
                Intent intent = new Intent(TransferSuccessActivity.this, NewDashboardActivity.class);
                // Menambahkan flag agar tidak bisa kembali ke TransferSuccessActivity
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
