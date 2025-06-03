package com.example.robotarmh25_remote.models;

public class Scenario {
    private int id_scenario;
    private String name;
    private String description;

    public Scenario() {
        // empty constructor
    }

    public Scenario(int id_scenario, String name, String description) {
        this.id_scenario = id_scenario;
        this.name = name;
        this.description = description;
    }

    public int getId_scenario() {
        return id_scenario;
    }

    public void setId_scenario(int id_scenario) {
        this.id_scenario = id_scenario;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name + (description != null && !description.isEmpty() ? " - " + description : "");
    }
}