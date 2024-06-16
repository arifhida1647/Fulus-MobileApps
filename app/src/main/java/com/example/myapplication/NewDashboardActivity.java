package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewDashboardActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    TextView tv_display_name;
    TextView textView10;
    RecyclerView recyclerView;
    TransactionAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_new_dashboard);
        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefer", MODE_PRIVATE);

        tv_display_name = findViewById(R.id.tv_display_name);
        textView10 = findViewById(R.id.textView10);
        recyclerView = findViewById(R.id.TransactionView);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        username = sharedPreferences.getString("username", "");

        // Mendapatkan data profil dari server
        getDataProfile(username);
        getDataBalance(username);
        getDataHistory(username);

        findViewById(R.id.btnBank).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewDashboardActivity.this, DetailCardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);  // Menonaktifkan animasi transisi
            }
        });
        findViewById(R.id.buttonHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewDashboardActivity.this, HistoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);  // Menonaktifkan animasi transisi
            }
        });
        findViewById(R.id.textView17).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewDashboardActivity.this, HistoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);  // Menonaktifkan animasi transisi
            }
        });
        findViewById(R.id.buttonSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewDashboardActivity.this, SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);  // Menonaktifkan animasi transisi
            }
        });
        findViewById(R.id.imageView7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewDashboardActivity.this, TopUpActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.imageView5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewDashboardActivity.this, TransferActivity.class);
                startActivity(intent);
            }
        });

        // Setup SwipeRefreshLayout untuk melakukan refresh saat menggulir ke bawah
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Memperbarui data saat melakukan refresh
                getDataProfile(username);
                getDataBalance(username);
                getDataHistory(username);
                // Menghentikan indikator refresh setelah selesai
                swipeRefreshLayout.setRefreshing(false);
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
                            tv_display_name.setText(userProfile.getNama());
                            // Tampilkan data profil
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(NewDashboardActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(NewDashboardActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Tambahkan request ke queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
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
                            textView10 = findViewById(R.id.textView10);
                            textView10.setText("$ " + userBalance.getBalance());
                            // Tampilkan data profil
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(NewDashboardActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(NewDashboardActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Tambahkan request ke queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
    private void getDataHistory(String username) {
        String url = Constants.BASE_URL + "/cekHistory?userName=" + username;

                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            List<Transaction> transactions = new ArrayList<>();

                            for (int i = 0; i < response.length() && i < 4; i++) {
                                JSONObject transactionObject = response.getJSONObject(i);
                                String category = transactionObject.optString("kategori", ""); // Use optString to get optional values
                                String amountString = transactionObject.optString("jumlah", "0");
                                String amount = "$ " + amountString;
                                String date = transactionObject.optString("date", ""); // Extract date

                                transactions.add(new Transaction(category, amount, username, date));
                            }

                            RecyclerView recyclerView = findViewById(R.id.TransactionView);
                            recyclerView.setLayoutManager(new LinearLayoutManager(NewDashboardActivity.this));
                            TransactionAdapter adapter = new TransactionAdapter(NewDashboardActivity.this, transactions);
                            // Set click listener on adapter to handle item click
                            adapter.setOnItemClickListener(new TransactionAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(Transaction transaction) {
                                    // Create intent to start TopUpSuccessActivity
                                    if (transaction.getCategory().equals("Cash Out")){
                                        Intent intent = new Intent(NewDashboardActivity.this, TransferSuccessActivity.class);
                                        // Put extra data
                                        intent.putExtra("tujuan", "*****");
                                        intent.putExtra("date", transaction.getDate());
                                        intent.putExtra("jumlah", transaction.getAmount());
                                        intent.putExtra("userName", username); // Assuming payment method is fixed
                                        // Start activity
                                        startActivity(intent);
                                    } else if (transaction.getCategory().equals("Cash In")) {
                                        Intent intent = new Intent(NewDashboardActivity.this, TopUpSuccesActivity.class);
                                        // Put extra data
                                        intent.putExtra("date", transaction.getDate());
                                        intent.putExtra("amount", transaction.getAmount());
                                        intent.putExtra("userName", username); // Assuming payment method is fixed
                                        intent.putExtra("paymentMethod", "TopUp");
                                        // Start activity
                                        startActivity(intent);
                                    }

                                }
                            });
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(NewDashboardActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // kalo error
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }


}