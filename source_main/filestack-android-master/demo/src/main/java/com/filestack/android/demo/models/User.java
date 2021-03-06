package com.filestack.android.demo.models;


import java.util.List;

public class User {

    private String name;
    private String phone;
    private String profile_image;
    private String user_id;
    private String security_level;
    private String score;
    private List<UserActions> user_actions;



    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public User(String name, String phone, String profile_image, String user_id, String security_level, String score) {
        this.name = name;
        this.phone = phone;
        this.profile_image = profile_image;
        this.user_id = user_id;
        this.security_level = security_level;
        this.score = score;
        this.user_actions = user_actions;
    }

    public User() {

    }

    public List<UserActions> getuser_actions() {
        return user_actions;
    }
    public void setuser_actions(List<UserActions> user_actions) {
        this.user_actions = user_actions;
    }

    public String getSecurity_level() {
        return security_level;
    }

    public void setSecurity_level(String security_level) {
        this.security_level = security_level;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", profile_image='" + profile_image + '\'' +
                ", user_id='" + user_id + '\'' +
                ", security_level=" + security_level +
                "score='" + score + '\'' +
                ", user_actions=" + user_actions +
                '}';
    }


}
