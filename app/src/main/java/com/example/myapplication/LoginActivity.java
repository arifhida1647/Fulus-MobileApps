package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUserName;
    private EditText editTextPassword;
    private Button buttonLogin;
    private ProgressBar progressBar;
    private View overlay;

    private TextView btnForget;
    private RequestQueue requestQueue;

    private TextView registeLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize the RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Get references to EditText, Button, ProgressBar, and overlay
        editTextUserName = findViewById(R.id.editTextTextPersonName);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        buttonLogin = findViewById(R.id.loginBtn);
        progressBar = findViewById(R.id.progressBar);
        overlay = findViewById(R.id.overlay);
        registeLogin = findViewById(R.id.textView8);
        btnForget = findViewById(R.id.textView6);

        // Set onClick listener for the login button
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        registeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        btnForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            }
        });
    }

    private void login() {
        // Show the ProgressBar and overlay
        progressBar.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);

        // Get the username and password from user input
        String userName = editTextUserName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Check if username or password is empty
        if (userName.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Username and password must not be empty", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            return;
        }

        // Create a JSON object for the username and password
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("userName", userName);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, "Error creating JSON request body", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            return;
        }

        // Create a request to the server using Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                Constants.BASE_URL + "/login",
                jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        overlay.setVisibility(View.GONE);
                        try {
                            String message = response.getString("message");
                            if (message.equals("Login Berhasil")) {
                                // Save the username in SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefer", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("username", userName);
                                editor.apply();

                                // Show success message
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                // Redirect to MainActivity
                                startActivity(new Intent(LoginActivity.this, NewDashboardActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        overlay.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Wrong Username or Password", Toast.LENGTH_SHORT).show();
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
