package com.inducesmile.androidloginsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inducesmile.androidloginsystem.Helper.Helper;
import android.R;



import java.util.HashMap;
import java.util.Map;

public class LoginPageActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = LoginPageActivity.class.getSimpleName();

    private EditText emailField;

    private EditText passwordField;

    private TextView loginError;

    private String email;

    private String password;

    private RequestQueue queue;

    private LoginObject loginObject;

    private SharedPreferences prefs;

    public static boolean isLogin = false;

    /*Facebook login callback class variable*/
    private CallbackManager mCallbackManager;

    /*Google sign in class variable*/
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInOptions gso;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login_page);

        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar){
            actionBar.hide();
        }

        prefs = getSharedPreferences(Helper.SHARED_PREF, Context.MODE_PRIVATE);
        String mEmail = prefs.getString(Helper.EMAIL, "");
        String facebookLogin = prefs.getString(Helper.FACEBOOK_ID, "");
        String googleLogin = prefs.getString(Helper.GOOGLE_ID, "");
        if(!mEmail.isEmpty() || !facebookLogin.isEmpty() || !googleLogin.isEmpty()){
            isLogin = true;
        }else{
            isLogin = false;
        }

        if(isLogin){
            Intent intentMain = new Intent(LoginPageActivity.this, UserProfileActivity.class);
            startActivity(intentMain);
        }

        queue = Volley.newRequestQueue(this);

        loginError = (TextView) findViewById(R.id.login_error);

        emailField = (EditText)findViewById(R.id.email);
        passwordField = (EditText)findViewById(R.id.password);

        TextView forgetPassword = (TextView)findViewById(R.id.forgotten_password);
        TextView signUp = (TextView)findViewById(R.id.sign_up);

        assert forgetPassword != null;
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resetPasswordIntent = new Intent(LoginPageActivity.this, ResetPasswordActivity.class);
                startActivity(resetPasswordIntent);
            }
        });

        assert signUp != null;
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resetPasswordIntent = new Intent(LoginPageActivity.this, SignUpActivity.class);
                startActivity(resetPasswordIntent);
            }
        });

        /*Google login*/
        gso = ((MyApplication) getApplication()).getGoogleSignInOptions();
        mGoogleApiClient = ((MyApplication) getApplication()).getGoogleApiClient(LoginPageActivity.this, this);
        SignInButton mSignInButton = (SignInButton)findViewById(R.id.google_sign_in_button);
        assert mSignInButton != null;
        mSignInButton.setSize(SignInButton.SIZE_STANDARD);
        mSignInButton.setScopes(gso.getScopeArray());
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        //Facebook login
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton mLoginButton = (LoginButton)findViewById(R.id.fb_login_button);
        assert mLoginButton != null;
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                String userLoginId = loginResult.getAccessToken().getUserId();
                Profile mProfile = Profile.getCurrentProfile();
                String firstName = mProfile.getFirstName();
                String lastName = mProfile.getLastName();
                String userId = mProfile.getId().toString();

                SharedPreferences.Editor edits = prefs.edit();
                edits.putString(Helper.FACEBOOK_ID, userId);
                edits.apply();

                Intent intentMain = new Intent(LoginPageActivity.this, UserProfileActivity.class);
                startActivity(intentMain);

            }
            @Override
            public void onCancel() {
            }
            @Override
            public void onError(FacebookException error) {
            }
        });

        Button loginButton = (Button)findViewById(R.id.login_button);
        assert loginButton != null;
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Helper.isEditTextEmpty(emailField)){
                    // display username login error message.
                    loginError.setText(getResources().getString(R.string.username_login_error_message));
                    return;
                }
                if(Helper.isEditTextEmpty(passwordField)){
                    // display password login error message
                    loginError.setText(getResources().getString(R.string.password_login_error_message));
                    return;
                }
                email = emailField.getText().toString();
                password = passwordField.getText().toString();

                if(email.length() < 5 || password.length() < 5){
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
        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, Helper.PATH_TO_SERVER_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(LoginPageActivity.this, response.toString(), Toast.LENGTH_LONG).show();
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
                        edit.apply();

                        emailField.setText("");
                        passwordField.setText("");

                        // redirect the user to the main page
                        Intent intentMain = new Intent(LoginPageActivity.this, UserProfileActivity.class);
                        startActivity(intentMain);
                    }
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginPageActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String,String>();
                params.put(Helper.EMAIL, email);
                params.put(Helper.PASSWORD, password);
                return params;
            }
        };
        queue.add(stringPostRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "handleSignInResult:" + RC_SIGN_IN);
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount userAccount = result.getSignInAccount();

            String userId = userAccount.getId();
            String displayedUsername = userAccount.getDisplayName();
            String userEmail = userAccount.getEmail();
            String userProfilePhoto = userAccount.getPhotoUrl().toString();

            SharedPreferences.Editor googleEdit = prefs.edit();
            googleEdit.putString(Helper.GOOGLE_ID, String.valueOf(userId));
            googleEdit.apply();



            Intent intentMain = new Intent(LoginPageActivity.this, UserProfileActivity.class);
            startActivity(intentMain);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(getApplication());
    }
    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(getApplicationContext());
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    //hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}
