package com.example.robotarmh25_remote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;

import com.example.robotarmh25_remote.ui.connect.ConnectFragment;
import com.example.robotarmh25_remote.ui.connect.ConnectViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;

public class MainMenuActivity extends AppCompatActivity {

    private Button buttonConnexionRobot, buttonDeconnexion;
    private Button buttonGamme, buttonScenario;
    private static final int REQ_BLE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        checkBlePermissions();
        // Link to UI
        buttonConnexionRobot = findViewById(R.id.buttonConnexionRobot);
        buttonDeconnexion = findViewById(R.id.buttonDeconnexion);
        buttonGamme = findViewById(R.id.buttonGamme);
        buttonScenario = findViewById(R.id.buttonScenario);
        Button buttonSendOrder = findViewById(R.id.buttonSendOrder);
        buttonSendOrder.setOnClickListener(v -> sendScenarioOrder(1));

        // Ensure btCon is initialized
        if (ConnectFragment.btCon == null) {
            ConnectFragment.btCon = BluetoothConnection.getInstance();
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

    /*private void sendScenarioOrder() {
        // Exemple statique de scénario à envoyer (peut être adapté dynamiquement)
        String json = "{\n" +
                "  \"header\": {\n" +
                "    \"type\": \"scenario\"\n" +
                "  },\n" +
                "  \"body\": [\n" +
                "    [\"NIVEAU_2\", \"ATTENTE_2\", \"ROULER_HORAIRE_3\", \"NIVEAU_0\"]\n" +
                "  ]\n" +
                "}";

        try {
            BluetoothConnection btCon = BluetoothConnection.getInstance();
            DataOutputStream dos = new DataOutputStream(btCon.socket_ev3_1.getOutputStream());
            dos.writeUTF(json); // ENVOIE TOUT D'UN COUP EN UTF-8
            dos.flush();
            Log.d("Bluetooth", "Scénario JSON envoyé : " + json);
            Toast.makeText(this, "Scénario envoyé", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erreur envoi : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }*/

    private void sendScenarioOrder(int scenarioId) {
        try {
            DBHandler dbHandler = new DBHandler(this);
            // Étape 1 : Construire dynamiquement le JSON
            JSONObject json = dbHandler.construireJsonScenario(scenarioId);


            // Étape 2 : Envoyer le JSON via Bluetooth
            BluetoothConnection btCon = BluetoothConnection.getInstance();
            btCon.sendJsonToLejos(json);

            Toast.makeText(this, "Scénario envoyé", Toast.LENGTH_SHORT).show();
        } catch (JSONException | InterruptedException e) {
            Toast.makeText(this, "Erreur lors de l'envoi : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // 2) demande / vérif des permissions
    private void checkBlePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {        // Android 12 +
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{ Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.BLUETOOTH_SCAN },
                        REQ_BLE);
            }
        } else {                                                     // API 21 – 30
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                        REQ_BLE);
            }
        }
    }

    // 3) callback de résultat
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Rien de spécial pour l’instant : si l’utilisateur refuse,
        // les appels Bluetooth lèveront une SecurityException déjà interceptée.
    }

}
