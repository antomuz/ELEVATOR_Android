package com.example.robotarmh25_remote.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.robotarmh25_remote.DBHandler;
import com.example.robotarmh25_remote.EditGammeActivity;
import com.example.robotarmh25_remote.models.Gamme;
import com.example.robotarmh25_remote.R;
import java.util.ArrayList;

public class GammeAdapter extends BaseAdapter {

    private ArrayList<Gamme> gammes;
    private LayoutInflater inflater;
    private Context context;
    private DBHandler dbHandler;

    public GammeAdapter(Context context, ArrayList<Gamme> gammes) {
        this.context = context;
        this.gammes = gammes;
        this.inflater = LayoutInflater.from(context);
        this.dbHandler = new DBHandler(context);
    }

    @Override
    public int getCount() { return gammes.size(); }

    @Override
    public Object getItem(int position) { return gammes.get(position); }

    @Override
    public long getItemId(int position) { return gammes.get(position).getId_gamme(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_gamme, parent, false);
        }

        TextView name = convertView.findViewById(R.id.gammeName);

        Gamme gamme = gammes.get(position);
        name.setText(gamme.getName());

        return convertView;
    }
}
