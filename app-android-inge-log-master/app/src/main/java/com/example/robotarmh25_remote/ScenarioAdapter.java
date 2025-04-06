package com.example.robotarmh25_remote.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.example.robotarmh25_remote.*;
import com.example.robotarmh25_remote.models.Scenario;

import java.util.List;

public class ScenarioAdapter extends ArrayAdapter<Scenario> {
    private Context context;
    private List<Scenario> scenarioList;
    private DBHandler dbHandler;

    public ScenarioAdapter(Context context, List<Scenario> scenarios, DBHandler dbHandler) {
        super(context, 0, scenarios);
        this.context = context;
        this.scenarioList = scenarios;
        this.dbHandler = dbHandler;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Scenario scenario = scenarioList.get(position);

        if (convertView == null) {
            try {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_scenario, parent, false);
            } catch (Exception e) {
                Log.e("Adapter", "Erreur d'inflation : " + e.getMessage());
                Toast.makeText(context, "Erreur dans item_scenario.xml", Toast.LENGTH_LONG).show();
            }
        }

        TextView name = convertView.findViewById(R.id.textScenarioName);
        TextView desc = convertView.findViewById(R.id.textScenarioDescription);
        Button edit = convertView.findViewById(R.id.buttonEditScenario);
        Button delete = convertView.findViewById(R.id.buttonDeleteScenario);

        name.setText(scenario.getName());
        desc.setText(scenario.getDescription());

        edit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditScenarioActivity.class);
            intent.putExtra("SCENARIO_ID", scenario.getId_scenario());
            context.startActivity(intent);
        });


        delete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Supprimer")
                    .setMessage("Confirmer la suppression du scénario ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        dbHandler.deleteScenarioCascade(scenario.getId_scenario());
                        scenarioList.remove(position);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });

        return convertView;
    }
}
