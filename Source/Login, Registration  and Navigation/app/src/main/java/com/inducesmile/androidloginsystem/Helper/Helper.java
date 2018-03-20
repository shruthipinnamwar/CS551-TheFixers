package com.inducesmile.androidloginsystem.Helper;


import android.widget.EditText;

public class Helper {

    public static final String PATH_TO_SERVER_LOGIN = "path to server login";

    public static final String PATH_TO_SERVER_REGISTRATION = "path to server registration";

    public static final String PATH_TO_RESET_PASSWORD = "path to server password reset";

    public static final String NAME = "name";

    public static final String USERNAME = "username";

    public static final String EMAIL = "email";

    public static final String PASSWORD = "password";

    public static final String SHARED_PREF = "login";

    public static final String FACEBOOK_ID = "facebook_id";

    public static final String GOOGLE_ID = "google_id";

    public static final String PASSWORD_RESET_INFORMATION = "Password reset information has been sent to your email address";

    public static boolean isEditTextEmpty(EditText editText){
        if (editText.getText().toString().isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean isValidEmail(String email){
        if(email.contains("@")){
            return true;
        }
        return false;
    }
}
