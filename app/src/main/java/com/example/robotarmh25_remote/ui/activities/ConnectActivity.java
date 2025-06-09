package com.example.robotarmh25_remote.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.robotarmh25_remote.R;
import com.example.robotarmh25_remote.ui.connect.ConnectFragment;

public class ConnectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);  // A layout with a FrameLayout container

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ConnectFragment())
                    .commit();
        }
    }
}
