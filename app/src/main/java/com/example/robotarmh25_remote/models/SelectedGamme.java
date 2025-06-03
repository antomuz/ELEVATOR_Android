package com.example.robotarmh25_remote.models;

public class SelectedGamme {
    private Gamme gamme;
    private int order;

    public SelectedGamme(Gamme gamme, int order) {
        this.gamme = gamme;
        this.order = order;
    }

    public Gamme getGamme() { return gamme; }
    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }

    @Override
    public String toString() {
        return gamme.getName();
    }
}