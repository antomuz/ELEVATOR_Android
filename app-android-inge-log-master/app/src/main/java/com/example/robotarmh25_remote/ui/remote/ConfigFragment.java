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
import com.example.robotarmh25_remote.data.RepositoryScenario.Scenario;
import com.example.robotarmh25_remote.ui.connect.ConnectFragment;


import java.util.ArrayList;

public class ConfigFragment extends Fragment {
    BluetoothConnection btCon;
    Context context;
    View fragmentView;
    ListView list;
    Button enregistrer;
    ImageButton left, right, lift, lower, close, open;
    Scenario scenario;
    ArrayList<String> affichage;
    private DBHandler dbHandler;
    ArrayAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_config, container, false);
        context = this.getActivity();

        //bluetooth connexion
        btCon = ConnectFragment.btCon;
        //database
        dbHandler = new DBHandler(context);

        //Liste and buttons
        list = fragmentView.findViewById(R.id.listview_item);
        enregistrer = fragmentView.findViewById(R.id.addScenarioButton);
        left = fragmentView.findViewById(R.id.addLeftButton);
        right = fragmentView.findViewById(R.id.addRightButton);
        lower = fragmentView.findViewById(R.id.addLowerButton);
        lift = fragmentView.findViewById(R.id.addLiftButton);
        close = fragmentView.findViewById(R.id.addCloseButton);
        open = fragmentView.findViewById(R.id.addOpenButton);

        //Scénario
        scenario = new Scenario();
        affichage = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, affichage);
        list.setAdapter(adapter);

        enregistrer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    dbHandler.addNewScenario(scenario);
                    Toast.makeText(context, "Scénario enregisté", Toast.LENGTH_SHORT).show();
                    affichage.clear();
                    adapter.notifyDataSetChanged();

                    //Verify the insert
                    /*HashMap<String, ArrayList<String>> test = new  HashMap<String, ArrayList<String>>();
                    test = dbHandler.getData();
                    Log.d("Base de données", test.toString());*/
                } catch (Exception e) {
                    Toast.makeText(context, "Problème rencontré: veuillez re-commencer", Toast.LENGTH_SHORT).show();
                    Log.e("Base de données", e.getMessage());
                }
            }
        });
       left.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                affichage.add("Rotation antihoraire");
                adapter.notifyDataSetChanged();
                scenario.addTask(Scenario.TypeTask.LEFT);
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                affichage.add("Rotation horaire");
                adapter.notifyDataSetChanged();
                scenario.addTask(Scenario.TypeTask.RIGHT);
            }
        });
        lower.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                affichage.add("Baisser bras");
                adapter.notifyDataSetChanged();
                scenario.addTask(Scenario.TypeTask.LOWER);
            }
        });
        lift.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                affichage.add("Lever bras");
                adapter.notifyDataSetChanged();
                scenario.addTask(Scenario.TypeTask.LIFT);
            }
        });
        open.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                affichage.add("Ouvrir pince");
                adapter.notifyDataSetChanged();
                scenario.addTask(Scenario.TypeTask.OPEN);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                affichage.add("Fermer pince");
                adapter.notifyDataSetChanged();
                scenario.addTask(Scenario.TypeTask.CLOSE);
            }
        });

        return fragmentView;
    }
}