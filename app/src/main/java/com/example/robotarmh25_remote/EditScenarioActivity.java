package com.example.robotarmh25_remote;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.robotarmh25_remote.adapters.GammeAdapter;
import com.example.robotarmh25_remote.adapters.SelectedGammeAdapter;
import com.example.robotarmh25_remote.models.Gamme;
import com.example.robotarmh25_remote.models.Scenario;

import java.util.ArrayList;
import java.util.List;

public class EditScenarioActivity extends AppCompatActivity {

    private EditText editName, editDescription;
    private ListView listStored, listSelected;
    private Button buttonSave;

    private DBHandler dbHandler;
    private List<Gamme> allGammes;
    private ArrayList<Gamme> selectedGammes;

    private ArrayAdapter storedAdapter;
    private SelectedGammeAdapter selectedAdapter;

    private int scenarioId;
    private Scenario currentScenario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_scenario); // reuse same layout

        dbHandler = new DBHandler(this);

        // UI references
        editName = findViewById(R.id.editScenarioName);
        editDescription = findViewById(R.id.editScenarioDescription);
        listStored = findViewById(R.id.listGammes);
        listSelected = findViewById(R.id.listSelectedGammes);
        buttonSave = findViewById(R.id.buttonSaveScenario);

        // Get scenario ID from intent
        scenarioId = getIntent().getIntExtra("SCENARIO_ID", -1);
        currentScenario = dbHandler.getScenarioById(scenarioId);
        selectedGammes = dbHandler.getGammesByScenarioId(scenarioId);

        if (currentScenario != null) {
            editName.setText(currentScenario.getName());
            editDescription.setText(currentScenario.getDescription());
        }

        // Load all gammes
        allGammes = dbHandler.getAllGammes();
        storedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allGammes);
        listStored.setAdapter(storedAdapter);

        // Setup selected gammes adapter
        selectedAdapter = new SelectedGammeAdapter(this, selectedGammes);
        listSelected.setAdapter(selectedAdapter);

        // Add onClick to add a gamme to scenario
        listStored.setOnItemClickListener((parent, view, position, id) -> {
            Gamme selectedGamme = allGammes.get(position);
            selectedGammes.add(selectedGamme);
            selectedAdapter.notifyDataSetChanged();
            Toast.makeText(EditScenarioActivity.this,
                    "Gamme ajoutée : " + selectedGamme.getName(),
                    Toast.LENGTH_SHORT).show();
        });

        // Save button
        buttonSave.setOnClickListener(v -> saveScenario());
    }

    private void saveScenario() {
        String name = editName.getText().toString().trim();
        String desc = editDescription.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Nom requis", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHandler.updateScenario(scenarioId, name, desc);
        dbHandler.clearScenarioGammes(scenarioId);

        for (int i = 0; i < selectedGammes.size(); i++) {
            dbHandler.addScenarioGamme(scenarioId, selectedGammes.get(i).getId_gamme(), i + 1);
        }

        Toast.makeText(this, "Scénario mis à jour", Toast.LENGTH_SHORT).show();
        finish();
    }
}
