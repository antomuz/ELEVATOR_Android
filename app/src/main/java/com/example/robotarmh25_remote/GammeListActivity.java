package com.example.robotarmh25_remote;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.robotarmh25_remote.adapters.GammeAdapter;
import com.example.robotarmh25_remote.models.Gamme;
import com.example.robotarmh25_remote.ui.connect.ConnectFragment;

import java.util.ArrayList;
import java.util.List;

public class GammeListActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    private ListView gammeListView;
    private Button buttonCreateGamme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamme_list);

        dbHandler = new DBHandler(this);
        gammeListView = findViewById(R.id.gammeListView);
        buttonCreateGamme = findViewById(R.id.buttonCreateGamme);


        buttonCreateGamme.setOnClickListener(v -> {
            // Navigate to CreateGammeActivity (you'll implement this next)
            startActivity(new Intent(this, CreateGammeActivity.class));
        });

        loadGammes();
    }

    private void loadGammes() {
        ArrayList<Gamme> gammes = new ArrayList<>(dbHandler.getAllGammes());
        GammeAdapter adapter = new GammeAdapter(this, gammes);
        gammeListView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGammes(); // refresh the list when returning from edit/create
    }
}
