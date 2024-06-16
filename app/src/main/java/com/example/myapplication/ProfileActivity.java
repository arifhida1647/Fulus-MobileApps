package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {
    TextView tv_item_name, tv_item_username, tv_item_email, tv_item_no, tv_item_country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Inisialisasi view
        tv_item_name = findViewById(R.id.tv_item_name);
        tv_item_username = findViewById(R.id.tv_item_username);
        tv_item_email = findViewById(R.id.tv_item_email);
        tv_item_no = findViewById(R.id.tv_item_no);
        tv_item_country = findViewById(R.id.tv_item_country);

        // Set listener untuk tombol delete
        findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefer", MODE_PRIVATE);
                String username = sharedPreferences.getString("username", "");

                // Ambil data email dari textView
                String email = tv_item_email.getText().toString();

                // Kirim data ke endpoint delete
                sendDataToDelete(username, email);
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefer", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        getDataProfile(username);

        findViewById(R.id.imageView3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Refresh MainActivity
                finish();
                startActivity(new Intent(ProfileActivity.this, SettingActivity.class));
            }
        });
    }
    private void getDataProfile(String username) {
        // Buat URL untuk request
        String url = Constants.BASE_URL + "/cekProfile?userName=" + username;

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
                            String country = response.getString("country");
                            String noTlp = response.getString("noTlp");
                            String nama = response.getString("nama");
                            String email = response.getString("email");

                            // Buat objek UserProfile
                            UserProfile userProfile = new UserProfile(country, noTlp, username, nama, email);
                            tv_item_name.setText(userProfile.getNama());
                            tv_item_username.setText(username);
                            tv_item_email.setText(userProfile.getEmail());
                            tv_item_no.setText(userProfile.getNoTlp());
                            tv_item_country.setText(userProfile.getCountry());
                            // Tampilkan data profil
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ProfileActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfileActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Tambahkan request ke queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
    private void sendDataToDelete(String username, String email) {
        // Create URL for the request
        String url = Constants.BASE_URL + "/delete";

        // Create JSON object with required data
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("userName", username);
            requestData.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create JSON request using Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            if ("Delete successful".equals(message)) {
                                // Clear SharedPreferences
                                SharedPreferences.Editor editor = getSharedPreferences("MyPrefer", MODE_PRIVATE).edit();
                                editor.clear();
                                editor.apply();

                                // Navigate to MainActivity
                                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Handle unexpected response
                                Toast.makeText(ProfileActivity.this, "Unexpected response: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ProfileActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Navigate to MainActivity
                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
        );

        // Add request to queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}