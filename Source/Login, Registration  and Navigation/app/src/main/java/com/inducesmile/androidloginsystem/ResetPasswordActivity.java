package com.inducesmile.androidloginsystem;

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

public class ResetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ResetPasswordActivity";

    private EditText emailField;

    private String email;

    private TextView loginError;

    private LoginObject loginObject;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar){
            actionBar.hide();
        }

        queue = Volley.newRequestQueue(this);

        loginError = (TextView) findViewById(R.id.login_error);
        emailField = (EditText)findViewById(R.id.email);

        Button resetButton = (Button)findViewById(R.id.reset_button);
        assert resetButton != null;
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Helper.isEditTextEmpty(emailField)){
                    loginError.setText(getResources().getString(R.string.email_login_error_message));
                    return;
                }
                email = emailField.getText().toString();
                serverPasswordResetCall();
            }
        });
    }

    //make server call for password reset
    private void serverPasswordResetCall() {
        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, Helper.PATH_TO_RESET_PASSWORD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(ResetPasswordActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                loginObject = gson.fromJson(response, LoginObject.class);

                if (loginObject.getSuccess().equals("0")) {
                    loginError.setText(getResources().getString(R.string.invalid_email));
                    Toast.makeText(ResetPasswordActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                } else {
                    // display message notifying user that the email reset link has been send to user email address.
                    Toast.makeText(ResetPasswordActivity.this, Helper.PASSWORD_RESET_INFORMATION, Toast.LENGTH_LONG).show();
                    emailField.setText("");
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ResetPasswordActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Helper.EMAIL, email);
                return params;
            }
        };
        queue.add(stringPostRequest);
    }
}
