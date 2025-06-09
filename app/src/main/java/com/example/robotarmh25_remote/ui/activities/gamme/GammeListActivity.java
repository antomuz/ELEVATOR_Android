package com.example.robotarmh25_remote.ui.activities.gamme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.robotarmh25_remote.DBHandler;
import com.example.robotarmh25_remote.R;
import com.example.robotarmh25_remote.adapters.GammeAdapter;
import com.example.robotarmh25_remote.models.Gamme;

import java.util.ArrayList;

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
