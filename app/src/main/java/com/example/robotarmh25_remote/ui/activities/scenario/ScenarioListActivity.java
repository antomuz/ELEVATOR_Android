package com.example.robotarmh25_remote.ui.activities.scenario;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.robotarmh25_remote.DBHandler;
import com.example.robotarmh25_remote.R;
import com.example.robotarmh25_remote.adapters.ScenarioAdapter;
import com.example.robotarmh25_remote.models.Scenario;

import java.util.List;

public class ScenarioListActivity extends AppCompatActivity {

    private ListView scenarioListView;
    private Button buttonAddScenario;
    private DBHandler dbHandler;
    private List<Scenario> scenarioList;
    private ScenarioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ScenarioDebug", "ScenarioListActivity started");

        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_scenario_list);
            Log.d("ScenarioDebug", "Layout loaded");

            scenarioListView = findViewById(R.id.scenarioListView);
            buttonAddScenario = findViewById(R.id.buttonAddScenario);

            dbHandler = new DBHandler(this);

            buttonAddScenario.setOnClickListener(v -> {
                Intent intent = new Intent(ScenarioListActivity.this, CreateScenarioActivity.class);
                startActivity(intent);
            });

        } catch (Exception e) {
            Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            scenarioList = dbHandler.getAllScenarios();
            Log.d("ScenarioDebug", "Scenarios loaded: " + scenarioList.size());
            // TEMP comment adapter
            adapter = new ScenarioAdapter(this, scenarioList, dbHandler);
            scenarioListView.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(this, "Erreur dans onResume: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
