package com.example.robotarmh25_remote.ui.remote;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.robotarmh25_remote.BluetoothConnection;
import com.example.robotarmh25_remote.DBHandler;
import com.example.robotarmh25_remote.R;
import com.example.robotarmh25_remote.ui.connect.ConnectFragment;
import com.example.robotarmh25_remote.models.Action;    // Your new model class
import com.example.robotarmh25_remote.models.Gamme;
import com.example.robotarmh25_remote.models.Scenario; // If you created a new model class

import java.util.ArrayList;
import java.util.List;

public class ConfigFragment extends Fragment {
    private BluetoothConnection btCon;
    private Context context;
    private View fragmentView;

    private ListView listView;
    private Button enregistrer;
    private ImageButton leftBtn, rightBtn, liftBtn, lowerBtn, closeBtn, openBtn;

    private DBHandler dbHandler;

    // We'll keep track of a new "Gamme" in memory for this fragment session
    private long currentGammeId = -1;            // We'll store the DB ID once we create a Gamme
    private int actionOrderCounter = 1;          // We'll increment this each time the user adds an action

    // For display in the ListView
    private ArrayList<String> displayActions;    // e.g. "LEFT", "RIGHT", etc.
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_config, container, false);
        context = getActivity();

        // bluetooth
        btCon = ConnectFragment.btCon;  // Possibly still used

        // database
        dbHandler = new DBHandler(context);

        // views
        listView = fragmentView.findViewById(R.id.listview_item);
        enregistrer = fragmentView.findViewById(R.id.addScenarioButton);

        leftBtn = fragmentView.findViewById(R.id.addLeftButton);
        rightBtn = fragmentView.findViewById(R.id.addRightButton);
        lowerBtn = fragmentView.findViewById(R.id.addLowerButton);
        liftBtn = fragmentView.findViewById(R.id.addLiftButton);
        closeBtn = fragmentView.findViewById(R.id.addCloseButton);
        openBtn = fragmentView.findViewById(R.id.addOpenButton);

        // For display
        displayActions = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, displayActions);
        listView.setAdapter(adapter);

        // 2) Set button listeners
        leftBtn.setOnClickListener(v -> addActionToCurrentGamme("LEFT"));
        rightBtn.setOnClickListener(v -> addActionToCurrentGamme("RIGHT"));
        lowerBtn.setOnClickListener(v -> addActionToCurrentGamme("LOWER"));
        liftBtn.setOnClickListener(v -> addActionToCurrentGamme("LIFT"));
        openBtn.setOnClickListener(v -> addActionToCurrentGamme("OPEN"));
        closeBtn.setOnClickListener(v -> addActionToCurrentGamme("CLOSE"));

        // 3) Save Scenario
        enregistrer.setOnClickListener(v -> {
            if (currentGammeId == -1) {
                Toast.makeText(context, "No Gamme to save!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Suppose we want to create a new Scenario each time or link to an existing one
            long newScenarioId = dbHandler.addScenario("ScenarioTemp", "Auto-created scenario");
            if (newScenarioId == -1) {
                Toast.makeText(context, "Error creating Scenario", Toast.LENGTH_SHORT).show();
                return;
            }

            // Now link the newly created gamme to the new scenario in Scenario_Gamme
            // We'll store this gamme as order 1 within that scenario, for simplicity
            long linkResult = dbHandler.addScenarioGamme((int)newScenarioId, (int)currentGammeId, 1);
            if (linkResult == -1) {
                Toast.makeText(context, "Error linking Gamme to Scenario", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Scenario created and linked!", Toast.LENGTH_SHORT).show();
                // Clear the display and reset
                displayActions.clear();
                adapter.notifyDataSetChanged();
                currentGammeId = -1;
                actionOrderCounter = 1;
            }
        });

        return fragmentView;
    }

    /**
     * Helper method to add an action (LEFT, RIGHT, etc.) to the current Gamme
     * by inserting into the 'Gamme_Action' table with the next ordre_execution.
     */
    private void addActionToCurrentGamme(String actionName) {
        if (currentGammeId == -1) {
            Toast.makeText(context, "No active Gamme. Cannot add action.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1) Look for the Action row by name
        com.example.robotarmh25_remote.models.Action existingAction = dbHandler.getActionByName(actionName);
        if (existingAction == null) {
            // If it doesn't exist, just log or toast, but do NOT insert a new action.
            Log.d("DB", "Action '" + actionName + "' does not exist in the DB.");
            Toast.makeText(context, "Action '" + actionName + "' not found in DB.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2) If found, get its ID
        int actionId = existingAction.getId_action();

        // 3) Insert a row into Gamme_Action for (currentGammeId, actionId, ordreExecution, paramValue=0)
        long result = dbHandler.addGammeAction(
                (int) currentGammeId,
                actionId,
                actionOrderCounter,
                0.0f // param
        );

        if (result == -1) {
            Toast.makeText(context, "Error inserting into Gamme_Action", Toast.LENGTH_SHORT).show();
            return;
        }

        // 4) Update UI
        displayActions.add(actionOrderCounter + ") " + actionName);
        adapter.notifyDataSetChanged();
        actionOrderCounter++;
    }
}
