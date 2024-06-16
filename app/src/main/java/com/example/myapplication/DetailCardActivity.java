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

public class DetailCardActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    TextView tv_item_name, tv_item_acc, tv_item_amount;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_card);
        sharedPreferences = getSharedPreferences("MyPrefer", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        tv_item_name = findViewById(R.id.tv_item_name);
        tv_item_acc = findViewById(R.id.tv_item_acc);
        tv_item_amount = findViewById(R.id.tv_item_amount);


        getDataProfile(username);
        getDataBalance(username);
        findViewById(R.id.imageView3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Refresh MainActivity
                finish();
                startActivity(new Intent(DetailCardActivity.this, NewDashboardActivity.class));
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
                            tv_item_acc.setText(userProfile.getEmail());
                            // Tampilkan data profil
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DetailCardActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DetailCardActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Tambahkan request ke queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
    private void getDataBalance(String username) {
        // Buat URL untuk request
        String url = Constants.BASE_URL+ "/cekSaldo?userName=" + username;

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
                            tv_item_amount.setText("$ " + userBalance.getBalance());
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
}