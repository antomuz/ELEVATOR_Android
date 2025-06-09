package com.example.robotarmh25_remote.ui.activities.scenario;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.robotarmh25_remote.DBHandler;
import com.example.robotarmh25_remote.R;
import com.example.robotarmh25_remote.models.Gamme;
import java.util.ArrayList;
import java.util.List;
import com.example.robotarmh25_remote.adapters.SelectedGammeAdapter;

public class CreateScenarioActivity extends AppCompatActivity {

    private EditText editName, editDescription;
    private ListView listAvailableGammes, listSelectedGammes;
    private Button buttonSave;

    private DBHandler dbHandler;
    private List<Gamme> allGammes;
    private ArrayList<Gamme> selectedGammes = new ArrayList<>();

    private ArrayAdapter<Gamme> availableAdapter;
    private SelectedGammeAdapter selectedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_scenario);

        editName = findViewById(R.id.editScenarioName);
        editDescription = findViewById(R.id.editScenarioDescription);
        listAvailableGammes = findViewById(R.id.listAvailableGammes);
        listSelectedGammes = findViewById(R.id.listSelectedGammes);
        buttonSave = findViewById(R.id.buttonSaveScenario);

        dbHandler = new DBHandler(this);
        allGammes = dbHandler.getAllGammes();

        availableAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allGammes);
        listAvailableGammes.setAdapter(availableAdapter);

        selectedAdapter = new SelectedGammeAdapter(this, selectedGammes);
        listSelectedGammes.setAdapter(selectedAdapter);

        listAvailableGammes.setOnItemClickListener((parent, view, position, id) -> {
            Gamme selected = allGammes.get(position);
            if (selected != null) {
                selectedGammes.add(new Gamme(selected.getId_gamme(), selected.getName())); // tu peux même réutiliser l’objet directement
                selectedAdapter.notifyDataSetChanged();
            }
        });

        buttonSave.setOnClickListener(v -> saveScenario());
    }

    private void saveScenario() {
        String name = editName.getText().toString().trim();
        String desc = editDescription.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Nom requis", Toast.LENGTH_SHORT).show();
            return;
        }

        long scenarioId = dbHandler.addScenario(name, desc);
        if (scenarioId != -1) {
            for (int i = 0; i < selectedGammes.size(); i++) {
                dbHandler.addScenarioGamme((int) scenarioId, selectedGammes.get(i).getId_gamme(), i + 1);
            }
            Toast.makeText(this, "Scénario enregistré", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Erreur à l'enregistrement", Toast.LENGTH_SHORT).show();
        }
    }
}

