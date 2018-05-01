package com.filestack.android.demo.models;

/**
 * Created by shrut on 4/11/2018.
 */

public class UserActions {

    private String action_id;
    private String action_name;
    private String img_url;
    private String timestamp;


    public UserActions(String action_id, String action_name, String img_url, String timestamp) {
        this.action_id = action_id;
        this.action_name = action_name;
        this.img_url = img_url;
        this.timestamp = timestamp;
    }
    public UserActions() {

    }

    public String getAction_id() {
        return action_id;
    }

    public void setAction_id(String action_id) {
        this.action_id = action_id;
    }

    public String getAction_name() {
        return action_name;
    }

    public void setAction_name(String action_name) {
        this.action_name = action_name;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UserActions{" +
                "action_id='" + action_id + '\'' +
                ", action_name='" + action_name + '\'' +
                ", img_url='" + img_url + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
