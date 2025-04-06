package com.example.robotarmh25_remote.models;

public class SelectedAction {
    private Action action;
    private int parametre;

    public SelectedAction(Action action, int parametre) {
        this.action = action;
        this.parametre = parametre;
    }

    public Action getAction() { return action; }
    public int getParametre() { return parametre; }
    public void setParametre(int parametre) { this.parametre = parametre; }

    @Override
    public String toString() {
        return action.getName()+ " (" + this.getParametre() + ")";  // This is what the ListView will display
    }
}

