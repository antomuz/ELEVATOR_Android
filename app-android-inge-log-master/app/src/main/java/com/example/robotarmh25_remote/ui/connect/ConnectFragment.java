package com.example.robotarmh25_remote.ui.connect;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.robotarmh25_remote.R;

public class ConnectFragment extends Fragment {

    private ConnectViewModel connectViewModel;

    Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        connectViewModel = new ViewModelProvider(this).get(ConnectViewModel.class);
        View root = inflater.inflate(R.layout.fragment_connect, container, false);

        context = this.getActivity();

        SharedPreferences sp = context.getSharedPreferences(getString(R.string.MyPrefs), Context.MODE_PRIVATE);

        final Button button = (Button) root.findViewById(R.id.confirmConnectButton);
        final EditText macAddressText = (EditText) root.findViewById(R.id.textMacAddress);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!macAddressValide(macAddressText.getText().toString())) {
                    macAddressText.setError("Format invalide");
                } else {
                    SharedPreferences.Editor speditor = sp.edit();
                    speditor.putString(getString(R.string.EV3KEY), macAddressText.getText().toString());
                    speditor.commit();

                    Toast.makeText(context, "Adresse MAC "+macAddressText.getText().toString()+" enregistrée", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    private boolean macAddressValide(String macAddress) {
        return macAddress.length() == 17;
    }
}