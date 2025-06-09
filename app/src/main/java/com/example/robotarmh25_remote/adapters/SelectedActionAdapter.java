package com.example.robotarmh25_remote.adapters;

import android.content.Context;
import android.view.*;
import android.widget.*;
import com.example.robotarmh25_remote.R;
import com.example.robotarmh25_remote.models.SelectedAction;
import java.util.Collections;
import java.util.List;

public class SelectedActionAdapter extends ArrayAdapter<SelectedAction> {

    private List<SelectedAction> selectedActions;

    public SelectedActionAdapter(Context context, List<SelectedAction> actions) {
        super(context, 0, actions);
        this.selectedActions = actions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_selected_action, parent, false);
        }

        SelectedAction action = getItem(position);
        TextView textAction = convertView.findViewById(R.id.textAction);
        ImageButton btnUp = convertView.findViewById(R.id.btnUp);
        ImageButton btnDown = convertView.findViewById(R.id.btnDown);

        textAction.setText(action.toString());

        btnUp.setOnClickListener(v -> moveUp(position));
        btnDown.setOnClickListener(v -> moveDown(position));

        return convertView;
    }

    private void moveUp(int position) {
        if (position > 0) {
            Collections.swap(selectedActions, position, position - 1);
            notifyDataSetChanged();
        }
    }

    private void moveDown(int position) {
        if (position < selectedActions.size() - 1) {
            Collections.swap(selectedActions, position, position + 1);
            notifyDataSetChanged();
        }
    }
}
