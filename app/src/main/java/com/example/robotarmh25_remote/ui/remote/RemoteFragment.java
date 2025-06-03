package com.example.robotarmh25_remote.ui.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.robotarmh25_remote.R;
import com.example.robotarmh25_remote.BluetoothConnection;
import com.example.robotarmh25_remote.ui.connect.ConnectFragment;

public class RemoteFragment extends Fragment {

    private RemoteViewModel remoteViewModel;

    BluetoothConnection btCon;

    Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        remoteViewModel =
                new ViewModelProvider(this).get(RemoteViewModel.class);
        View root = inflater.inflate(R.layout.fragment_remote, container, false);

        context = this.getActivity();

        btCon = new BluetoothConnection();

        SharedPreferences sp = context.getSharedPreferences(getString(R.string.MyPrefs), Context.MODE_PRIVATE);

        if(!btCon.initBT()){
            Toast.makeText(context, "Veuillez activer le bluetooth de votre téléphone", Toast.LENGTH_SHORT).show();
        }

        if(!btCon.connectToEV3(sp.getString(getString(R.string.EV3KEY), ""))){
            Toast.makeText(context, "Veuillez vous connecter à votre EV3", Toast.LENGTH_SHORT).show();
        }

        final Switch switchRotationAntiHoraire = (Switch) root.findViewById(R.id.switchRotationAntiHoraire);
        final Switch switchRotationHoraire = (Switch) root.findViewById(R.id.switchRotationHoraire);
        final Switch switchLever = (Switch) root.findViewById(R.id.switchLever);
        final Switch switchBaisser = (Switch) root.findViewById(R.id.switchBaisser);
        final Switch switchOuvrir = (Switch) root.findViewById(R.id.switchOuvrir);
        final Switch switchFermer = (Switch) root.findViewById(R.id.switchFermer);

        switchRotationAntiHoraire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switchRotationAntiHoraire.isChecked()) {
                    try {
                        btCon.writeMessage((byte) 1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, "Démarrage rotation Antihoraire", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        btCon.writeMessage((byte) 2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, "Arrêt rotation Antihoraire", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchRotationHoraire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switchRotationHoraire.isChecked()) {
                    try {
                        btCon.writeMessage((byte) 3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, "Démarrage rotation Horaire", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        btCon.writeMessage((byte) 4);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, "Arrêt rotation Horaire", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchLever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switchLever.isChecked()) {
                    try {
                        btCon.writeMessage((byte)5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, "Démarrage lever bras", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        btCon.writeMessage((byte) 6);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, "Arrêt lever bras", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchBaisser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switchBaisser.isChecked()) {
                    try {
                        btCon.writeMessage((byte)7);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, "Démarrage baisser bras", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        btCon.writeMessage((byte) 8);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, "Arrêt baisser bras", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchOuvrir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switchOuvrir.isChecked()) {
                    try {
                        btCon.writeMessage((byte)9);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, "Démarrage ouverture pince", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        btCon.writeMessage((byte) 10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, "Arrêt ouverture pince", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchFermer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switchFermer.isChecked()) {
                    try {
                        btCon.writeMessage((byte)11);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, "Démarrage fermeture pince", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        btCon.writeMessage((byte) 12);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, "Arrêt fermeture pince", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }
}