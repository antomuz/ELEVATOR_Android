package com.example.robotarmh25_remote.data.RepositoryScenario;

import android.util.Log;

import java.util.ArrayList;

public class Scenario {
    private ArrayList<TypeTask> tasks = new ArrayList<TypeTask>();

    public enum TypeTask {
        LEFT ("Left",1), RIGHT ("Right",2), LIFT ("Lift",3), LOWER ("Lower",4),
        OPEN ("Open",5), CLOSE ("Close",6);
        private String stringValue;
        private int intValue;
        private TypeTask(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }
    public Scenario(){
    }

    public ArrayList<TypeTask> getTasks(){return tasks;}
    public void setTasks(ArrayList<TypeTask> tasks) {
        this.tasks = tasks;
    }
    public TypeTask getTask(int index) {
        return tasks.get(index);
    }
    public void addTask(TypeTask task){
        try {
            tasks.add(task);
        } catch (Exception e){
            Log.e("Bluetooth", e.getMessage());
        }
    }
    public int nbTasks() {
        return tasks.size();
    }
}
