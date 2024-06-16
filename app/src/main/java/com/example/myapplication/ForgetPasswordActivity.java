package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private Button buttonForgot;
    private RequestQueue requestQueue;
    private ProgressBar progressBar;
    private View overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // Initialize the RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Get references to EditText, Button, ProgressBar, and overlay
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonForgot = findViewById(R.id.forgotBtn);
        progressBar = findViewById(R.id.progressBar);
        overlay = findViewById(R.id.overlay);

        // Set onClick listener for the forgot button
        buttonForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forget();
            }
        });
    }

    private void forget() {
        // Show the ProgressBar and overlay
        progressBar.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);

        // Get the email from user input
        String email = editTextEmail.getText().toString().trim();

        // Check if email is empty
        if (email.isEmpty()) {
            Toast.makeText(ForgetPasswordActivity.this, "Email must not be empty", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            return;
        }

        // Create a JSON object for the email
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ForgetPasswordActivity.this, "Error creating JSON request body", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            return;
        }

        // Create a request to the server using Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                Constants.BASE_URL + "/forgot-password",
                jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        overlay.setVisibility(View.GONE);
                        try {
                            String message = response.getString("message");
                            if (message.equals("Send Berhasil")) {
                                // Show success message
                                Toast.makeText(ForgetPasswordActivity.this, "Send Successful", Toast.LENGTH_SHORT).show();
                                // Redirect to LoginActivity
                                startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(ForgetPasswordActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ForgetPasswordActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        overlay.setVisibility(View.GONE);
                        Toast.makeText(ForgetPasswordActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
