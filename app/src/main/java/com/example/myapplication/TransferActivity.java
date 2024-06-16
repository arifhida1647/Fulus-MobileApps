package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import java.util.Date;

public class TransferActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String username;

    TextView textView26;
    EditText editTextNumber, editTextText2;
    Button button;
    View blurView;
    ProgressBar loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        sharedPreferences = getSharedPreferences("MyPrefer", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        textView26 = findViewById(R.id.textView26);
        editTextNumber = findViewById(R.id.editTextNumber);
        editTextText2 = findViewById(R.id.editTextText2);
        button = findViewById(R.id.button);
        blurView = findViewById(R.id.blurView);
        loadingBar = findViewById(R.id.loadingBar);

        getDataBalance(username);
        findViewById(R.id.imageView3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Refresh MainActivity
                finish();
                startActivity(new Intent(TransferActivity.this, NewDashboardActivity.class));
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jumlah = editTextNumber.getText().toString().trim();
                String tujuan = editTextText2.getText().toString().trim();

                if (!jumlah.isEmpty() && !tujuan.isEmpty()) {
                    blurView.setVisibility(View.VISIBLE);
                    loadingBar.setVisibility(View.VISIBLE);
                    button.setEnabled(false);
                    sendDataToServer(username, jumlah, tujuan);
                } else {
                    Toast.makeText(TransferActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getDataBalance(String username) {
        // Buat URL untuk request
        String url = Constants.BASE_URL + "/cekSaldo?userName=" + username;

        // Buat request JSON menggunakan Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Ambil data profil dari response
                            Integer balance = Integer.valueOf(response.getString("message"));

                            // Buat objek UserProfile
                            UserBalance userBalance = new UserBalance(balance);
                            textView26.setText("$ " + userBalance.getBalance());
                            // Tampilkan data profil
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );

        // Tambahkan request ke queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void sendDataToServer(String userName, String jumlah, String tujuan) {
        // URL endpoint
        String url = Constants.BASE_URL + "/transfer";

        // Create JSON object to send data
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userName", userName);
            jsonObject.put("jumlah", jumlah);
            jsonObject.put("tujuan", tujuan);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // RequestQueue for managing network requests
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Request a JSON response from the provided URL
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Hide blur and loading animation
                            blurView.setVisibility(View.GONE);
                            loadingBar.setVisibility(View.GONE);
                            button.setEnabled(true);

                            // Handle response
                            String message = response.getString("message");
                            Toast.makeText(TransferActivity.this, message, Toast.LENGTH_SHORT).show();

                            // Get current date
                            String currentDate = java.text.DateFormat.getDateTimeInstance().format(new Date());

                            // Create intent to start TransferSuccessActivity
                            Intent intent = new Intent(TransferActivity.this, TransferSuccessActivity.class);

                            // Pass data to TransferSuccessActivity
                            intent.putExtra("userName", userName);
                            intent.putExtra("jumlah", "$ " + jumlah);
                            intent.putExtra("tujuan", tujuan);
                            intent.putExtra("date", currentDate);

                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(TransferActivity.this, "Failed to parse response", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Hide blur and loading animation
                blurView.setVisibility(View.GONE);
                loadingBar.setVisibility(View.GONE);
                button.setEnabled(true);

                // Handle error
                Toast.makeText(TransferActivity.this, "Failed to send data", Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }

}
