package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextName, editTextEmail, editTextCountry, editTextNoTlp, editTextUsername, editTextPassword;
    private RequestQueue requestQueue;
    private ProgressBar progressBar;
    private View overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI components
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextCountry = findViewById(R.id.editTextCountry);
        editTextNoTlp = findViewById(R.id.editTextNoTlp);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        progressBar = findViewById(R.id.progressBar);
        overlay = findViewById(R.id.overlay);

        // Initialize RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Set onClickListener for the register button
        findViewById(R.id.registerBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });
        findViewById(R.id.textView8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private void Register() {
        // Show the ProgressBar and overlay
        progressBar.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);

        // Get the input from user
        String nama = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String country = editTextCountry.getText().toString().trim();
        String noTlp = editTextNoTlp.getText().toString().trim();
        String userName = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Check if any field is empty
        if (nama.isEmpty() || email.isEmpty() || country.isEmpty() || noTlp.isEmpty() || userName.isEmpty() || password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            return;
        }

        // Create a JSON object for the request body
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nama", nama);
            jsonBody.put("country", country);
            jsonBody.put("noTlp", noTlp);
            jsonBody.put("email", email);
            jsonBody.put("userName", userName);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(RegisterActivity.this, "Error creating JSON request body", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            return;
        }

        // Create a request to the server using Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                Constants.BASE_URL + "/register",
                jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        overlay.setVisibility(View.GONE);
                        try {
                            String message = response.getString("message");
                            if (message.equals("Registration successful")) {
                                // Save the username in SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefer", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("username", userName);
                                editor.apply();

                                // Show success message
                                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                                // Redirect to MainActivity
                                startActivity(new Intent(RegisterActivity.this, NewDashboardActivity.class));
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        overlay.setVisibility(View.GONE);
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            String responseBody = new String(error.networkResponse.data);
                            Log.e("VolleyError", "Status Code: " + statusCode + " Response Body: " + responseBody);
                            Toast.makeText(RegisterActivity.this, "Error: " + statusCode + " " + responseBody, Toast.LENGTH_LONG).show();
                        } else {
                            Log.e("VolleyError", "Error: " + error.toString());
                            if (error.getCause() != null) {
                                Log.e("VolleyError Cause", error.getCause().toString());
                            }
                            Toast.makeText(RegisterActivity.this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Add content-type header
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }
}
