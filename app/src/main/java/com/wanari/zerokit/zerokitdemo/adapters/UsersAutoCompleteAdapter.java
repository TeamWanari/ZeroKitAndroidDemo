package com.wanari.zerokit.zerokitdemo.adapters;

import com.wanari.zerokit.zerokitdemo.R;
import com.wanari.zerokit.zerokitdemo.rest.entities.UserJson;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class UsersAutoCompleteAdapter extends ArrayAdapter<UserJson> {

    public UsersAutoCompleteAdapter(Context context, List<UserJson> objects) {
        super(context, R.layout.item_user, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        UserJson user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }
        // Lookup view for data population
        TextView userId = (TextView) convertView.findViewById(R.id.userId);
        TextView registrationState = (TextView) convertView.findViewById(R.id.registrationState);
        // Populate the data into the template view using the data object
        userId.setText(user.getUserId());
        registrationState.setText(user.getRegistrationState());
        // Return the completed view to render on screen
        return convertView;
    }
}
