package com.example.robotarmh25_remote.models;

public class Gamme {
    private int id;
    private String name;

    public Gamme(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // getters and setters
    public int getId_gamme() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return getName(); // ou getName() si c’est privé
    }
}
