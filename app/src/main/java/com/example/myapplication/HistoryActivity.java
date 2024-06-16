package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView recyclerView;
    TransactionAdapter adapter;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        sharedPreferences = getSharedPreferences("MyPrefer", MODE_PRIVATE);

        recyclerView = findViewById(R.id.notification_list);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout); // Initialize swipeRefreshLayout here

        username = sharedPreferences.getString("username", "");

        getDataHistory(username);

        findViewById(R.id.buttonHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, NewDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);  // Menonaktifkan animasi transisi
            }
        });
        findViewById(R.id.buttonSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);  // Menonaktifkan animasi transisi
            }
        });
        // Setup SwipeRefreshLayout untuk melakukan refresh saat menggulir ke bawah
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataHistory(username);
                // Menghentikan indikator refresh setelah selesai
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void getDataHistory(String username) {
        String url = Constants.BASE_URL+ "/cekHistory?userName=" + username;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            List<Transaction> transactions = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject transactionObject = response.getJSONObject(i);
                                String category = transactionObject.optString("kategori", ""); // Use optString to get optional values
                                String amountString = transactionObject.optString("jumlah", "0");
                                String amount = "$ " + amountString;
                                String date = transactionObject.optString("date", ""); // Extract date

                                transactions.add(new Transaction(category, amount, username, date));
                            }

                            recyclerView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
                            adapter = new TransactionAdapter(HistoryActivity.this, transactions);

                            // Set click listener on adapter to handle item click
                            adapter.setOnItemClickListener(new TransactionAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(Transaction transaction) {
                                    // Create intent to start TopUpSuccessActivity
                                    if (transaction.getCategory().equals("Cash Out")){
                                        Intent intent = new Intent(HistoryActivity.this, TransferSuccessActivity.class);
                                        // Put extra data
                                        intent.putExtra("tujuan", "*****");
                                        intent.putExtra("date", transaction.getDate());
                                        intent.putExtra("jumlah", transaction.getAmount());
                                        intent.putExtra("userName", username); // Assuming payment method is fixed
                                        // Start activity
                                        startActivity(intent);
                                    } else if (transaction.getCategory().equals("Cash In")) {
                                        Intent intent = new Intent(HistoryActivity.this, TopUpSuccesActivity.class);
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
                            Toast.makeText(HistoryActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
}
