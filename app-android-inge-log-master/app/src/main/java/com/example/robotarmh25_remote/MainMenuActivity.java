package com.example.robotarmh25_remote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.robotarmh25_remote.ui.connect.ConnectFragment;
import com.example.robotarmh25_remote.ui.connect.ConnectViewModel;

public class MainMenuActivity extends AppCompatActivity {

    private Button buttonConnexionRobot, buttonDeconnexion;
    private Button buttonGamme, buttonScenario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Link to UI
        buttonConnexionRobot = findViewById(R.id.buttonConnexionRobot);
        buttonDeconnexion = findViewById(R.id.buttonDeconnexion);
        buttonGamme = findViewById(R.id.buttonGamme);
        buttonScenario = findViewById(R.id.buttonScenario);
        Button buttonSendOrder = findViewById(R.id.buttonSendOrder);
        buttonSendOrder.setOnClickListener(v -> sendScenarioOrder());

        // Ensure btCon is initialized
        if (ConnectFragment.btCon == null) {
            ConnectFragment.btCon = new BluetoothConnection();
            ConnectFragment.btCon.initBT();
        }

        // Try auto-connect if MAC address is available
        SharedPreferences sp = getSharedPreferences(getString(R.string.MyPrefs), Context.MODE_PRIVATE);
        String macAddress = sp.getString(getString(R.string.EV3KEY), "");

        if (!macAddress.isEmpty()) {
            new Thread(() -> {
                boolean connected = ConnectFragment.btCon.connectToEV3(macAddress);
                runOnUiThread(() -> {
                    if (connected) {
                        Toast.makeText(this, "Robot connecté automatiquement", Toast.LENGTH_SHORT).show();
                        buttonConnexionRobot.setText("Déconnexion Robot");
                    } else {
                        Toast.makeText(this, "Échec de la connexion automatique au robot", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        }

        // Toggle Robot connection: Navigate to ConnectActivity which hosts ConnectFragment
        buttonConnexionRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, ConnectActivity.class);
                startActivity(intent);
            }
        });

        // Example: Deconnexion (log out)
        buttonDeconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an intent to start LoginActivity
                Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
                // Clear the activity stack
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Finish current activity
            }
        });

        // Navigate to Gamme screen
        buttonGamme.setOnClickListener(v -> {
            startActivity(new Intent(this, GammeListActivity.class));
        });

        // Navigate to Scenario screen
        buttonScenario.setOnClickListener(v -> {
            startActivity(new Intent(this, ScenarioListActivity.class));
        });
    }

    private void sendScenarioOrder() {
        // Exemple statique de scénario à envoyer (peut être adapté dynamiquement)
        String json = "{\n" +
                "  \"header\": {\n" +
                "    \"type\": \"scenario\"\n" +
                "  },\n" +
                "  \"body\": [\n" +
                "    [\"NIVEAU_2\", \"ROULER_HORAIRE_5\"],\n" +
                "    [\"NIVEAU_0\", \"ATTENTE_5\"]\n" +
                "  ]\n" +
                "}";

        try {
            byte[] data = json.getBytes("UTF-8");
            BluetoothConnection btCon = BluetoothConnection.getInstance();
            for (byte b : data) {
                btCon.writeMessage(b);
            }
            Toast.makeText(this, "Scénario envoyé", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erreur envoi : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
