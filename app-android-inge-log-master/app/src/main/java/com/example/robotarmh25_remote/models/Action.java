package com.example.robotarmh25_remote.models;

public class Action {
    private int id_action;
    private String name;

    // Empty constructor
    public Action() { }

    // Full constructor
    public Action(int id_action, String name) {
        this.id_action = id_action;
        this.name = name;
    }

    public int getId_action() {
        return id_action;
    }

    public void setId_action(int id_action) {
        this.id_action = id_action;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;  // This is what the ListView will display
    }
}
