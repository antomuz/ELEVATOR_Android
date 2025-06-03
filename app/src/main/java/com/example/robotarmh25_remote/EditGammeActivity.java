package com.example.robotarmh25_remote;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.robotarmh25_remote.models.Action;
import com.example.robotarmh25_remote.models.SelectedAction;
import java.util.ArrayList;
import com.example.robotarmh25_remote.adapters.SelectedActionAdapter;

public class EditGammeActivity extends AppCompatActivity {

    private EditText editGammeName;
    private ListView listActions, listSelectedActions;
    private Button buttonSaveGamme;

    private DBHandler dbHandler;
    private int gammeId;

    private ArrayList<Action> storedActions;
    private ArrayList<SelectedAction> selectedActions;

    private ArrayAdapter<Action> storedAdapter;
    private ArrayAdapter<SelectedAction> selectedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gamme);

        dbHandler = new DBHandler(this);

        editGammeName = findViewById(R.id.editGammeName);
        listActions = findViewById(R.id.listActions);
        listSelectedActions = findViewById(R.id.listSelectedActions);
        buttonSaveGamme = findViewById(R.id.buttonCreateGamme); // reuse the same button ID

        gammeId = getIntent().getIntExtra("id_gamme", -1);

        if (gammeId == -1) {
            Toast.makeText(this, "Gamme introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load existing gamme data
        loadExistingGamme();

        buttonSaveGamme.setText("Sauvegarder les modifications");

        buttonSaveGamme.setOnClickListener(v -> saveGamme());

        storedActions = (ArrayList<Action>) dbHandler.getAllActions();
        storedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, storedActions);
        listActions.setAdapter(storedAdapter);

        listActions.setOnItemClickListener((parent, view, position, id) -> {
            Action action = storedActions.get(position);
            showParamDialog(action);
        });

        selectedAdapter = new SelectedActionAdapter(this, selectedActions);
        listSelectedActions.setAdapter(selectedAdapter);

        listSelectedActions.setOnItemClickListener((parent, view, position, id) -> {
            SelectedAction selectedAction = selectedActions.get(position);
            editParamDialog(selectedAction);
        });
    }

    private void loadExistingGamme() {
        editGammeName.setText(dbHandler.getGammeNameById(gammeId));
        selectedActions = dbHandler.getSelectedActionsByGammeId(gammeId);
    }

    private void showParamDialog(Action action) {
        EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setHint("Paramètre numérique");

        new android.app.AlertDialog.Builder(this)
                .setTitle("Paramètre pour : " + action.getName())
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

    private void editParamDialog(SelectedAction selectedAction) {
        EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(selectedAction.getParametre()));

        new android.app.AlertDialog.Builder(this)
                .setTitle("Modifier paramètre : " + selectedAction.getAction().getName())
                .setView(input)
                .setPositiveButton("Modifier", (dialog, which) -> {
                    String paramStr = input.getText().toString();
                    if (!paramStr.isEmpty()) {
                        int param = Integer.parseInt(paramStr);
                        selectedAction.setParametre(param);
                        selectedAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Paramètre requis", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void saveGamme() {
        String gammeName = editGammeName.getText().toString().trim();

        if (gammeName.isEmpty()) {
            editGammeName.setError("Nom requis");
            return;
        }

        if (selectedActions.isEmpty()) {
            Toast.makeText(this, "Aucune action sélectionnée", Toast.LENGTH_SHORT).show();
            return;
        }

        // 💡 Use UPDATE for edit
        boolean success = dbHandler.updateGamme(gammeId, gammeName, selectedActions);

        if (success) {
            Toast.makeText(this, "Gamme mise à jour avec succès", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Erreur lors de la mise à jour", Toast.LENGTH_LONG).show();
        }
    }
}
