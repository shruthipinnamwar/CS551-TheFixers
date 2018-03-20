package com.inducesmile.androidloginsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inducesmile.androidloginsystem.Helper.Helper;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    private EditText nameField;
    private EditText usernameField;
    private EditText emailField;
    private EditText passwordField;

    private String name;
    private String username;
    private String email;
    private String password;

    private TextView loginError;

    private RequestQueue queue;

    private LoginObject loginObject;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar){
            actionBar.hide();
        }

        queue = Volley.newRequestQueue(this);

        prefs = getSharedPreferences(Helper.SHARED_PREF, Context.MODE_PRIVATE);

        loginError = (TextView) findViewById(R.id.login_error);

        nameField = (EditText)findViewById(R.id.names);
        usernameField = (EditText)findViewById(R.id.username);
        emailField = (EditText)findViewById(R.id.email);
        passwordField = (EditText)findViewById(R.id.password);

        Button signUpButton = (Button)findViewById(R.id.create_account_button);
        assert signUpButton != null;
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Helper.isEditTextEmpty(nameField)){
                    loginError.setText(getResources().getString(R.string.name_login_error_message));
                    return;
                }
                if(Helper.isEditTextEmpty(usernameField)){
                    loginError.setText(getResources().getString(R.string.username_login_error_message));
                    return;
                }
                if(Helper.isEditTextEmpty(emailField)){
                    loginError.setText(getResources().getString(R.string.email_login_error_message));
                    return;
                }
                if(Helper.isEditTextEmpty(passwordField)){
                    loginError.setText(getResources().getString(R.string.password_login_error_message));
                    return;
                }

                name = nameField.getText().toString();
                username = usernameField.getText().toString();
                email = emailField.getText().toString();
                password = passwordField.getText().toString();

                if(name.length() <5 || username.length() < 5 || password.length() < 5){
                    loginError.setText(getResources().getString(R.string.short_input));
                    return;
                }

                if(!Helper.isValidEmail(email)){
                    loginError.setText(getResources().getString(R.string.email_login_invalid));
                    return;
                }

                postRequestToRemoteServer();
            }
        });
    }

    // make a post request to the server
    private void postRequestToRemoteServer(){
        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, Helper.PATH_TO_SERVER_REGISTRATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(SignUpActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                loginObject = gson.fromJson(response, LoginObject.class);

                if(null == loginObject){
                    loginError.setText(getResources().getString(R.string.failed_login));
                }else{
                    if(loginObject.getSuccess().equals("0")){
                        loginError.setText(getResources().getString(R.string.failed_login));
                    }else{
                        // Store the login details in shared preference
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putString(Helper.EMAIL, email);
                        edit.commit();

                        nameField.setText("");
                        usernameField.setText("");
                        emailField.setText("");
                        passwordField.setText("");

                        // redirect the user to the main page
                        Intent intentMain = new Intent(SignUpActivity.this, UserProfileActivity.class);
                        startActivity(intentMain);
                    }
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignUpActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String,String>();
                params.put(Helper.NAME, name);
                params.put(Helper.USERNAME, username);
                params.put(Helper.EMAIL, email);
                params.put(Helper.PASSWORD, password);
                return params;
            }
        };
        queue.add(stringPostRequest);
    }
}
