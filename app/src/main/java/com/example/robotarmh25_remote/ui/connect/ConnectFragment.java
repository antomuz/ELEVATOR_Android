package com.example.robotarmh25_remote.ui.connect;

import com.example.robotarmh25_remote.BluetoothConnection;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.robotarmh25_remote.MainMenuActivity;
import com.example.robotarmh25_remote.R;
import java.util.regex.Pattern;

public class ConnectFragment extends Fragment {
    // Static instance to access the Bluetooth connection from other fragments
    public static BluetoothConnection btCon;
    private ConnectViewModel connectViewModel;
    private Context context;

    // Regex pattern for a valid MAC address (e.g. AA:BB:CC:DD:EE:FF)
    private static final Pattern MAC_ADDRESS_PATTERN =
            Pattern.compile("^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$");

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Initialize BluetoothConnection
        btCon = new BluetoothConnection();

        connectViewModel = new ViewModelProvider(this).get(ConnectViewModel.class);
        View root = inflater.inflate(R.layout.fragment_connect, container, false);

        context = getActivity();

        SharedPreferences sp = context.getSharedPreferences(getString(R.string.MyPrefs), Context.MODE_PRIVATE);

        final Button confirmButton = root.findViewById(R.id.confirmConnectButton);
        final EditText macAddressText = root.findViewById(R.id.textMacAddress);

        confirmButton.setOnClickListener(v -> {
            String macAddress = macAddressText.getText().toString().trim();
            if (!isValidMacAddress(macAddress)) {
                macAddressText.setError("Format invalide. Exemple: AA:BB:CC:DD:EE:FF");
            } else {
                SharedPreferences.Editor spEditor = sp.edit();
                spEditor.putString(getString(R.string.EV3KEY), macAddress);
                spEditor.apply();

                Toast.makeText(context, "Adresse MAC " + macAddress + " enregistrée", Toast.LENGTH_SHORT).show();

                boolean connected = btCon.connectToEV3(macAddress);
                if (connected) {
                    Toast.makeText(context, "Connexion Bluetooth réussie", Toast.LENGTH_SHORT).show();

                    // Navigate back to MainMenuActivity
                    Intent intent = new Intent(getActivity(), MainMenuActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    getActivity().finish(); // optional: close current activity
                } else {
                    Toast.makeText(context, "Erreur de connexion Bluetooth", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    /**
     * Validates the MAC address using a regex.
     * @param macAddress the MAC address string to validate.
     * @return true if the MAC address is valid, false otherwise.
     */
    private boolean isValidMacAddress(String macAddress) {
        return macAddress != null && MAC_ADDRESS_PATTERN.matcher(macAddress).matches();
    }
}
