package com.filestack.android.demo.models;


/**
 * Created by shrut on 4/8/2018.
 */

public class Action {
    private String action_id;
    private String action_name;

    public Action(String action_id, String action_name) {
        this.action_id = action_id;
        this.action_name = action_name;
    }
    public Action() {

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

    @Override
    public String toString() {
        return "Action{" +
                "action_id='" + action_id + '\'' +
                ", action_name='" + action_name + '\'' +
                '}';
    }
}

