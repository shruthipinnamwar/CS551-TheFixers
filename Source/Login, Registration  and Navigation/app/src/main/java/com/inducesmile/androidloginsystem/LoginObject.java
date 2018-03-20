package com.inducesmile.androidloginsystem;

import com.google.gson.annotations.SerializedName;

public class LoginObject {

    @SerializedName("success")
    private String success;

    public LoginObject(String success){
        this.success = success;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
