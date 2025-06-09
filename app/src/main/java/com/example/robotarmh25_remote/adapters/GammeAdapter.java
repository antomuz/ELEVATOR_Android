package com.example.robotarmh25_remote.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.example.robotarmh25_remote.DBHandler;
import com.example.robotarmh25_remote.ui.activities.gamme.EditGammeActivity;
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
        Gamme gamme = gammes.get(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_gamme, parent, false);
        }

        Button edit = convertView.findViewById(R.id.btnEdit);
        Button delete = convertView.findViewById(R.id.btnDelete);

        TextView name = convertView.findViewById(R.id.gammeName);

        name.setText(gamme.getName());

        edit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditGammeActivity.class);
            intent.putExtra("id_gamme", gamme.getId_gamme());
            context.startActivity(intent);
        });


        delete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Supprimer")
                    .setMessage("Confirmer la suppression de la gamme ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        dbHandler.deleteGamme(gamme.getId_gamme());
                        gammes.remove(position);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });

        return convertView;
    }
}
