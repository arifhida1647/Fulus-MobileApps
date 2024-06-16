package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Date;

public class TopUpActivity extends AppCompatActivity {

    private static final String TAG = "TopUpActivity";
    private static final String URL = Constants.BASE_URL + "/topUp";
    SharedPreferences sharedPreferences;

    private EditText topupAmount;
    private Button btnTopup;
    private RequestQueue requestQueue;
    private RadioGroup paymentMethods;
    private ProgressBar loadingBar;
    private View blurView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        topupAmount = findViewById(R.id.topup_amount);
        btnTopup = findViewById(R.id.btn_topup);
        requestQueue = Volley.newRequestQueue(this);
        paymentMethods = findViewById(R.id.payment_methods);
        loadingBar = findViewById(R.id.loadingBar);
        blurView = findViewById(R.id.blurView);

        btnTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTopUpRequest();
            }
        });

        findViewById(R.id.imageView3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Refresh MainActivity
                finish();
                startActivity(new Intent(TopUpActivity.this, NewDashboardActivity.class));
            }
        });
    }

    private void sendTopUpRequest() {
        String amountStr = topupAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(TopUpActivity.this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(TopUpActivity.this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        sharedPreferences = getSharedPreferences("MyPrefer", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        // Dapatkan ID radio button yang dipilih
        int selectedRadioButtonId = paymentMethods.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        String paymentMethod = selectedRadioButton.getTag().toString();

        if (username.isEmpty()) {
            Toast.makeText(TopUpActivity.this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userName", username);
            jsonObject.put("jumlah", amount);
            jsonObject.put("paymentMethod", paymentMethod); // Sertakan nilai radio button yang dipilih
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception: " + e.getMessage());
        }

        // Show loading animation
        blurView.setVisibility(View.VISIBLE);
        loadingBar.setVisibility(View.VISIBLE);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        blurView.setVisibility(View.GONE);
                        loadingBar.setVisibility(View.GONE);

                        try {
                            String message = response.getString("message");
                            Toast.makeText(TopUpActivity.this, message, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Response: " + message);

                            // Get current date
                            String currentDate = java.text.DateFormat.getDateTimeInstance().format(new Date());

                            // Format amount to remove decimal part if it's a whole number
                            DecimalFormat decimalFormat = new DecimalFormat("#");
                            String formattedAmount = decimalFormat.format(amount);

                            // Jika top-up berhasil, pindah ke TopUpSuccessActivity
                            Intent intent = new Intent(TopUpActivity.this, TopUpSuccesActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("amount", "$ " + formattedAmount); // Use formatted amount
                            intent.putExtra("paymentMethod", paymentMethod); // Sertakan nilai radio button yang dipilih
                            intent.putExtra("date", currentDate);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        blurView.setVisibility(View.GONE);
                        loadingBar.setVisibility(View.GONE);

                        String errorMessage = "Top Up Failed";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            errorMessage = new String(error.networkResponse.data);
                        }
                        Toast.makeText(TopUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Request Failure: " + error.toString());
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }
}
