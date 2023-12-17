package com.example.csseticketingmobileapp.activities;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.csseticketingmobileapp.R;
import com.example.csseticketingmobileapp.config.ServerConfig;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;

public class Register extends AppCompatActivity {
    EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword;
    Spinner spinnerAccountType;
    Button btnRegister;
    TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize the form input fields
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        spinnerAccountType = findViewById(R.id.spinnerAccountType);
        btnRegister = findViewById(R.id.idBTNRegister);
        loginLink = findViewById(R.id.loginLink);

        // ------------------------------------set EditText Views based on spinner selected item ----------------------------------
        // Get references to the NIC and Passport EditText fields
        final EditText editTextNIC = findViewById(R.id.editTextNIC);
        final EditText editTextPassport = findViewById(R.id.editTextPassport);

        // Set the initial visibility of NIC and Passport EditText fields
        editTextNIC.setVisibility(View.VISIBLE);
        editTextPassport.setVisibility(View.GONE);

        // Set up the spinner with a list of account types
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.account_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccountType.setAdapter(adapter);

        spinnerAccountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedAccountType = spinnerAccountType.getSelectedItem().toString();

                // Show/hide the EditText fields based on the selected account type
                if (selectedAccountType.equals("Local")) {
                    editTextNIC.setVisibility(View.VISIBLE);
                    editTextPassport.setVisibility(View.GONE);
                } else if (selectedAccountType.equals("Foreign")) {
                    editTextNIC.setVisibility(View.GONE);
                    editTextPassport.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
        // --------------------------------------------------------------------------------------------------------------------------

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the data from the form
                String fName = editTextFirstName.getText().toString();
                String lName = editTextLastName.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String accountType = spinnerAccountType.getSelectedItem().toString();

                // Call the method to add a passenger
                addPassenger(fName, lName, email, password, accountType);
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the registration page
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });
    }

    private void addPassenger( String fName, String lName, String email, String password, String accountType) {
        OkHttpClient client = new OkHttpClient();

        // Define the URL of your server's API endpoint
        String serverUrl = ServerConfig.SERVER_URL + "/passenger/add";

        // Create a JSON request body with the passenger data
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"fName\":\"" + fName + "\",\"lName\":\"" + lName + "\",\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"accountType\":\"" + accountType + "\"}";
        RequestBody requestBody = RequestBody.create(JSON, json);

        // Create a POST request
        Request request = new Request.Builder()
                .url(serverUrl)
                .post(requestBody)
                .build();

        // Send the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle request failure (e.g., network issues)
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Handle a successful response from the server
                    String responseText = response.body().string();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), responseText, Toast.LENGTH_SHORT).show();
                        }
                    });

                    Intent i = new Intent(Register.this, Home.class);
                    startActivity(i);

                } else {
                    // Handle an unsuccessful response
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Server Error when passing data...", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
