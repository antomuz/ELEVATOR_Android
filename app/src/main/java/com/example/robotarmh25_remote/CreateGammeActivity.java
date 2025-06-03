package com.example.robotarmh25_remote;

import android.os.Bundle;
import android.text.InputType;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.robotarmh25_remote.models.Action;
import com.example.robotarmh25_remote.models.SelectedAction;
import com.example.robotarmh25_remote.adapters.SelectedActionAdapter;

import java.util.ArrayList;

public class CreateGammeActivity extends AppCompatActivity {

    private EditText editGammeName;
    private ListView listActions, listSelectedActions;
    private Button buttonCreateGamme;

    private DBHandler dbHandler;

    private ArrayList<Action> storedActions;
    private ArrayList<SelectedAction> selectedActions;

    private ArrayAdapter<Action> storedAdapter;
    private ArrayAdapter<SelectedAction> selectedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gamme);

        dbHandler = new DBHandler(this);

        editGammeName = findViewById(R.id.editGammeName);
        listActions = findViewById(R.id.listActions);
        listSelectedActions = findViewById(R.id.listSelectedActions);
        buttonCreateGamme = findViewById(R.id.buttonCreateGamme);

        storedActions = (ArrayList<Action>) dbHandler.getAllActions();
        selectedActions = new ArrayList<>();

        storedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, storedActions);
        selectedAdapter = new SelectedActionAdapter(this, selectedActions);
        listSelectedActions.setAdapter(selectedAdapter);

        listActions.setAdapter(storedAdapter);
        listSelectedActions.setAdapter(selectedAdapter);

        listActions.setOnItemClickListener((parent, view, position, id) -> {
            Action action = storedActions.get(position);
            showParamDialog(action);
        });

        buttonCreateGamme.setOnClickListener(v -> {
            String gammeName = editGammeName.getText().toString().trim();
            if (gammeName.isEmpty()) {
                editGammeName.setError("Entrez un nom pour la gamme");
                return;
            }

            if (selectedActions.isEmpty()) {
                Toast.makeText(this, "Sélectionnez au moins une action", Toast.LENGTH_SHORT).show();
                return;
            }

            long gammeId = dbHandler.insertGamme(gammeName, selectedActions);
            if (gammeId > 0) {
                Toast.makeText(this, "Gamme créée avec succès", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showParamDialog(Action action) {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Entrez le paramètre numérique");

        new AlertDialog.Builder(this)
                .setTitle("Paramètre pour l'action : " + action.getName())
                .setView(input)
                .setPositiveButton("Ajouter", (dialog, which) -> {
                    String paramStr = input.getText().toString();
                    if (!paramStr.isEmpty()) {
                        int param = Integer.parseInt(paramStr);
                        selectedActions.add(new SelectedAction(action, param));
                        selectedAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Paramètre requis", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
}



