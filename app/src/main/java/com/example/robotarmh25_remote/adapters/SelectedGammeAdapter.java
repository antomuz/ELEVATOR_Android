package com.example.robotarmh25_remote.adapters;

import android.content.Context;
import android.view.*;
import android.widget.*;
import com.example.robotarmh25_remote.R;
import com.example.robotarmh25_remote.models.Gamme;

import java.util.ArrayList;

public class SelectedGammeAdapter extends ArrayAdapter<Gamme> {

    private Context context;
    private ArrayList<Gamme> selectedGammes;

    public SelectedGammeAdapter(Context context, ArrayList<Gamme> selectedGammes) {
        super(context, 0, selectedGammes);
        this.context = context;
        this.selectedGammes = selectedGammes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Gamme gamme = selectedGammes.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_selected_gamme, parent, false);
        }

        TextView textName = convertView.findViewById(R.id.textGammeName);
        ImageButton  buttonUp = convertView.findViewById(R.id.buttonUp);
        ImageButton  buttonDown = convertView.findViewById(R.id.buttonDown);
        ImageButton  buttonDelete = convertView.findViewById(R.id.buttonDelete);

        textName.setText(gamme.getName());

        buttonUp.setOnClickListener(v -> {
            if (position > 0) {
                Gamme tmp = selectedGammes.get(position - 1);
                selectedGammes.set(position - 1, gamme);
                selectedGammes.set(position, tmp);
                notifyDataSetChanged();
            }
        });

        buttonDown.setOnClickListener(v -> {
            if (position < selectedGammes.size() - 1) {
                Gamme tmp = selectedGammes.get(position + 1);
                selectedGammes.set(position + 1, gamme);
                selectedGammes.set(position, tmp);
                notifyDataSetChanged();
            }
        });

        buttonDelete.setOnClickListener(v -> {
            selectedGammes.remove(position);
            notifyDataSetChanged();
        });

        return convertView;
    }
}
